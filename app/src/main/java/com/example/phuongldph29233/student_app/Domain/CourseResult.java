package com.example.phuongldph29233.student_app.Domain;

public class CourseResult {
    private String studentId;
    private Subject subjectId;
    private float processScore;
    private float examScore;
    private float finalScore;
    public CourseResult() {}

    public CourseResult(String studentId, Subject subjectId) {
        this.studentId = studentId;
        this.subjectId = subjectId;
    }
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Subject getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Subject subjectId) {
        this.subjectId = subjectId;
    }

    public float getProcessScore() {
        return processScore;
    }

    public void setProcessScore(float processScore) {
        this.processScore = processScore;
    }

    public float getExamScore() {
        return examScore;
    }

    public void setExamScore(float examScore) {
        this.examScore = examScore;
    }

    public float getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(float finalScore) {
        this.finalScore = finalScore;
    }

    public void calculateFinalScore() {
        this.finalScore = (float) (processScore * 0.3 + examScore * 0.7);
    }
}