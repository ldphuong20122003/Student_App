package com.example.phuongldph29233.student_app.Domain;

import java.io.Serializable;

public class Student implements Serializable {
    private String id;
    private String studentID;
    private String studentName;
    private String studentBirthday;
    private String studentHomeTown;
    private String studentPhone;
    private String studentEmail;
    private Class studentClass;

    private String studentDateJoin;
    private Branch studentBranch;
    private String studentTOT;

    public Student() {
    }

    public Student(String id, String studentID, String studentName, String studentBirthday, String studentHomeTown, String studentPhone, String studentEmail, Class studentClass, String studentDateJoin, Branch studentBranch, String studentTOT) {
        this.id = id;
        this.studentID = studentID;
        this.studentName = studentName;
        this.studentBirthday = studentBirthday;
        this.studentHomeTown = studentHomeTown;
        this.studentPhone = studentPhone;
        this.studentEmail = studentEmail;
        this.studentClass = studentClass;
        this.studentDateJoin = studentDateJoin;
        this.studentBranch = studentBranch;
        this.studentTOT = studentTOT;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentBirthday() {
        return studentBirthday;
    }

    public void setStudentBirthday(String studentBirthday) {
        this.studentBirthday = studentBirthday;
    }

    public String getStudentHomeTown() {
        return studentHomeTown;
    }

    public void setStudentHomeTown(String studentHomeTown) {
        this.studentHomeTown = studentHomeTown;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        this.studentPhone = studentPhone;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public Class getStudentClass() {
        return studentClass;
    }

    // Trong Student.java
    public void setStudentClass(Class studentClass) {

        if (studentClass != null) {

            this.studentClass = new Class(

                    studentClass.getId(),

                    studentClass.getMaLop(),

                    studentClass.getTenLop(),

                    studentClass.getKhoa(),  // Giữ nguyên thông tin khoa

                    studentClass.getGiangVien(),  // Giữ nguyên thông tin giảng viên

                    studentClass.getNamHoc(),

                    studentClass.getDanhSachSinhVien()  // Giữ nguyên danh sách sinh viên nếu cần

            );

        } else {

            this.studentClass = null;

        }

    }

    public String getStudentDateJoin() {
        return studentDateJoin;
    }

    public void setStudentDateJoin(String studentDateJoin) {
        this.studentDateJoin = studentDateJoin;
    }

    public Branch getStudentBranch() {
        return studentBranch;
    }

    public void setStudentBranch(Branch studentBranch) {
        this.studentBranch = studentBranch;
    }

    public String getStudentTOT() {
        return studentTOT;
    }

    public void setStudentTOT(String studentTOT) {
        this.studentTOT = studentTOT;
    }
}
