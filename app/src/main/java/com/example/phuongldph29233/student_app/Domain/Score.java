package com.example.phuongldph29233.student_app.Domain;

import java.io.Serializable;

public class Score implements Serializable {
    private String id;
    private String studentId;
    private String classId;
    private String subjectId;
    private double progressScore;
    private double examScore;
    private double finalScore;

    public Score() {
    }

    public Score(String id, String studentId, String classId, String subjectId,
                 double progressScore, double examScore, double finalScore) {
        this.id = id;
        this.studentId = studentId;
        this.classId = classId;
        this.subjectId = subjectId;
        this.progressScore = progressScore;
        this.examScore = examScore;
        this.finalScore = finalScore;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public double getProgressScore() {
        return progressScore;
    }

    public void setProgressScore(double progressScore) {
        this.progressScore = progressScore;
    }

    public double getExamScore() {
        return examScore;
    }

    public void setExamScore(double examScore) {
        this.examScore = examScore;
    }

    public double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(double finalScore) {
        this.finalScore = finalScore;
    }
}