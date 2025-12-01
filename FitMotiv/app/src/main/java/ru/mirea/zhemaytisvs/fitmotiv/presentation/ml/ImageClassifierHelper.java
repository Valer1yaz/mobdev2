package ru.mirea.zhemaytisvs.fitmotiv.presentation.ml;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ImageClassifierHelper {

    private static final String TAG = "ImageClassifierHelper";
    private static final String MODEL_FILE = "exercise_model.tflite";
    private static final String LABEL_FILE = "labels.txt";

    // Размеры для модели (стандарт для MobileNet)
    private static final int IMAGE_WIDTH = 224;
    private static final int IMAGE_HEIGHT = 224;
    private static final int CHANNELS = 3;

    // Нормализация для модели
    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;

    // Параметры классификации
    private static final int MAX_RESULTS = 5;
    private static final float CONFIDENCE_THRESHOLD = 0.3f;

    private Interpreter interpreter;
    private List<String> labels;
    private ImageProcessor imageProcessor;
    private TensorImage inputImageBuffer;
    private ByteBuffer inputBuffer;

    public ImageClassifierHelper(Context context) throws IOException {
        try {
            // Загрузка модели
            ByteBuffer modelBuffer = loadModelFile(context);

            // Создание интерпретатора
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(4);
            interpreter = new Interpreter(modelBuffer, options);

            // Загрузка меток
            labels = FileUtil.loadLabels(context, LABEL_FILE);

            // Создание процессора изображений
            imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(IMAGE_WIDTH, IMAGE_HEIGHT, ResizeOp.ResizeMethod.BILINEAR))
                    //.add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                    .build();

            // Подготовка буфера для входных данных
            inputImageBuffer = new TensorImage(DataType.UINT8);
            inputBuffer = ByteBuffer.allocateDirect(IMAGE_WIDTH * IMAGE_HEIGHT * CHANNELS * 4);
            inputBuffer.order(ByteOrder.nativeOrder());

            Log.d(TAG, "Model loaded successfully. Labels count: " + labels.size());

        } catch (IOException e) {
            Log.e(TAG, "Error loading model or labels", e);
            throw e;
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during initialization", e);
            throw new IOException("Failed to initialize TensorFlow Lite", e);
        }
    }

    private ByteBuffer loadModelFile(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(MODEL_FILE)) {
            // Читаем все байты модели
            byte[] modelData = new byte[inputStream.available()];
            int bytesRead = inputStream.read(modelData);

            if (bytesRead != modelData.length) {
                throw new IOException("Failed to read complete model file");
            }

            // Создаем ByteBuffer
            ByteBuffer buffer = ByteBuffer.allocateDirect(modelData.length);
            buffer.order(ByteOrder.nativeOrder());
            buffer.put(modelData);
            buffer.rewind();

            return buffer;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load model file: " + MODEL_FILE, e);
            throw new IOException("Model file not found or corrupted: " + MODEL_FILE, e);
        }
    }

    public List<ExerciseClassification> classifyImage(Bitmap bitmap) {
        if (interpreter == null || labels == null || labels.isEmpty()) {
            Log.e(TAG, "Classifier not properly initialized");
            return Collections.emptyList();
        }

        try {
            // Подготовка изображения
            Bitmap processedBitmap = Bitmap.createScaledBitmap(bitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);
            inputImageBuffer.load(processedBitmap);

            // Обработка изображения
            inputImageBuffer = imageProcessor.process(inputImageBuffer);

            // Подготовка вывода для квантованной модели
            byte[][] output = new byte[1][labels.size()]; // Используем byte, а не float

            // Выполнение инференса
            interpreter.run(inputImageBuffer.getBuffer(), output);

            // Преобразование byte[] в float[] (масштабирование)
            float[] probabilities = new float[output[0].length];
            for (int i = 0; i < output[0].length; i++) {
                probabilities[i] = (output[0][i] & 0xff) / 255.0f;
            }

            return getTopResults(probabilities);

        } catch (Exception e) {
            Log.e(TAG, "Error during image classification", e);
            return Collections.emptyList();
        }
    }

    private List<ExerciseClassification> getTopResults(float[] probabilities) {
        // Используем PriorityQueue для получения топ-N результатов
        PriorityQueue<ExerciseClassification> pq = new PriorityQueue<>(
                MAX_RESULTS,
                Comparator.comparing(ExerciseClassification::getConfidence)
        );

        for (int i = 0; i < probabilities.length; i++) {
            float confidence = probabilities[i];

            // Фильтрация по порогу уверенности
            if (confidence >= CONFIDENCE_THRESHOLD && i < labels.size()) {
                String label = labels.get(i);

                if (pq.size() < MAX_RESULTS) {
                    pq.offer(new ExerciseClassification(label, confidence));
                } else if (pq.peek() != null && confidence > pq.peek().getConfidence()) {
                    pq.poll();
                    pq.offer(new ExerciseClassification(label, confidence));
                }
            }
        }

        // Конвертация в список и сортировка по убыванию уверенности
        List<ExerciseClassification> results = new ArrayList<>(pq);
        results.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));

        Log.d(TAG, "Found " + results.size() + " valid results");
        return results;
    }

    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }

    public static class ExerciseClassification {
        private String label;
        private float confidence;

        public ExerciseClassification(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }

        public String getLabel() {
            return label;
        }

        public float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return String.format("%s (%.2f%%)", label, confidence * 100);
        }
    }
}