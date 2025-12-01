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
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ImageClassifierHelper {

    private static final String TAG = "ImageClassifier";
    private static final String MODEL_FILE = "mobilenet_v1_1.0_224_quantized.tflite";
    private static final String LABEL_FILE = "labels.txt";

    private static final int IMAGE_SIZE = 224;
    private static final int IMAGE_MEAN = 0;
    private static final int IMAGE_STD = 255;
    private static final int MAX_RESULTS = 5;
    private static final float THRESHOLD = 0.1f;

    private Interpreter interpreter;
    private List<String> labels;
    private ImageProcessor imageProcessor;
    private TensorImage inputImageBuffer;

    public ImageClassifierHelper(Context context) throws IOException {
        try {
            // Загрузка модели
            MappedByteBuffer modelBuffer = loadModelFile(context);

            // Создание интерпретатора
            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(4);
            interpreter = new Interpreter(modelBuffer, options);

            // Загрузка меток
            labels = FileUtil.loadLabels(context, LABEL_FILE);

            // Создание процессора изображений для модели MobileNet
            imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(IMAGE_SIZE, IMAGE_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))
                    .build();

            // Создание буфера для входного изображения
            inputImageBuffer = new TensorImage(DataType.FLOAT32);

            Log.d(TAG, "Model loaded successfully. Labels count: " + labels.size());

        } catch (IOException e) {
            Log.e(TAG, "Error loading model or labels", e);
            throw e;
        }
    }

    // Исправленный метод загрузки модели
    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(MODEL_FILE);
             FileInputStream fileInputStream = new FileInputStream(inputStream.toString())) {

            FileChannel fileChannel = fileInputStream.getChannel();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

        } catch (Exception e) {
            // Альтернативный способ загрузки
            Log.w(TAG, "Standard method failed, trying alternative...", e);
            return loadModelFileAlternative(context);
        }
    }

    // Альтернативный метод загрузки модели
    private MappedByteBuffer loadModelFileAlternative(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(MODEL_FILE)) {
            // Читаем все байты
            byte[] modelData = new byte[inputStream.available()];
            inputStream.read(modelData);

            // Создаем ByteBuffer из массива байтов
            ByteBuffer buffer = ByteBuffer.allocateDirect(modelData.length);
            buffer.put(modelData);
            buffer.rewind();

            // Преобразуем в MappedByteBuffer
            // Вместо MappedByteBuffer используем ByteBuffer напрямую
            // TensorFlow Lite может работать с ByteBuffer
            java.nio.ByteBuffer byteBuffer = java.nio.ByteBuffer.allocateDirect(modelData.length);
            byteBuffer.put(modelData);
            byteBuffer.rewind();

            // Для совместимости с методом map
            return (MappedByteBuffer) byteBuffer;
        }
    }

    // Простой метод загрузки без FileChannel
    private MappedByteBuffer loadModelFileSimple(Context context) throws IOException {
        AssetManager assetManager = context.getAssets();

        try (InputStream inputStream = assetManager.open(MODEL_FILE)) {
            // Читаем все байты модели
            int fileSize = inputStream.available();
            byte[] modelData = new byte[fileSize];
            inputStream.read(modelData);

            // Создаем ByteBuffer напрямую
            ByteBuffer buffer = ByteBuffer.allocateDirect(fileSize);
            buffer.put(modelData);
            buffer.position(0);

            // Возвращаем как MappedByteBuffer
            return (MappedByteBuffer) buffer.asReadOnlyBuffer();
        }
    }

    public List<ExerciseClassification> classifyImage(Bitmap bitmap) {
        if (interpreter == null || labels == null) {
            Log.e(TAG, "Classifier not initialized");
            return Collections.emptyList();
        }

        try {
            // Подготовка изображения
            inputImageBuffer.load(bitmap);
            inputImageBuffer = imageProcessor.process(inputImageBuffer);

            // Подготовка вывода
            int numClasses = labels.size();
            float[][] output = new float[1][numClasses];

            // Выполнение инференса
            interpreter.run(inputImageBuffer.getBuffer(), output);

            // Получение результатов
            float[] probabilities = output[0];

            // Фильтрация и сортировка результатов
            return getTopResults(probabilities);

        } catch (Exception e) {
            Log.e(TAG, "Error classifying image", e);
            return Collections.emptyList();
        }
    }

    private List<ExerciseClassification> getTopResults(float[] probabilities) {
        // Используем PriorityQueue для получения топ-N результатов
        PriorityQueue<ExerciseClassification> pq = new PriorityQueue<>(
                MAX_RESULTS,
                Comparator.comparing(ExerciseClassification::getConfidence)
        );

        for (int i = 0; i < Math.min(probabilities.length, labels.size()); i++) {
            float confidence = probabilities[i];

            // Фильтрация по порогу
            if (confidence > THRESHOLD) {
                String label = labels.get(i);

                if (pq.size() < MAX_RESULTS) {
                    pq.offer(new ExerciseClassification(label, confidence));
                } else if (pq.peek() != null && confidence > pq.peek().getConfidence()) {
                    pq.poll();
                    pq.offer(new ExerciseClassification(label, confidence));
                }
            }
        }

        // Конвертация в список и сортировка по убыванию confidence
        List<ExerciseClassification> results = new ArrayList<>(pq);
        results.sort((a, b) -> Float.compare(b.getConfidence(), a.getConfidence()));

        Log.d(TAG, "Found " + results.size() + " results");
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

        public String getLabel() { return label; }
        public float getConfidence() { return confidence; }

        @Override
        public String toString() {
            return String.format("%s (%.2f%%)", label, confidence * 100);
        }
    }
}