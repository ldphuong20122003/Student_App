package com.example.phuongldph29233.student_app.Domain;

public class Teacher {
    private String id;
    private String teacherID;
    private String teacherName;
    private String teacherEmail;
    private String teacherPhone;
    private Branch teacherBranch;

    public Teacher() {
    }

    public Teacher(String id, String teacherID, String teacherName, String teacherEmail, String teacherPhone, Branch teacherBranch) {
        this.id = id;
        this.teacherID = teacherID;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.teacherPhone = teacherPhone;
        this.teacherBranch = teacherBranch;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public Branch getTeacherBranch() {
        return teacherBranch;
    }

    public void setTeacherBranch(Branch teacherBranch) {
        this.teacherBranch = teacherBranch;
    }

    @Override
    public String toString() {
        return getTeacherName();
    }
}
