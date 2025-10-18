package ru.mirea.zhemaytisvs.fitmotiv.domain.entities;

public class ExerciseAnalysis {
    private String exerciseName;
    private double correctnessScore;
    private String feedback;
    private boolean isCorrect;

    public ExerciseAnalysis(String exerciseName, double correctnessScore, String feedback, boolean isCorrect) {
        this.exerciseName = exerciseName;
        this.correctnessScore = correctnessScore;
        this.feedback = feedback;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public String getExerciseName() { return exerciseName; }
    public double getCorrectnessScore() { return correctnessScore; }
    public String getFeedback() { return feedback; }
    public boolean isCorrect() { return isCorrect; }
}