package com.example.phuongldph29233.student_app.Domain;

public class Class {
    private String id;
    private String maLop;

    private String tenLop;
    private Branch khoa;
    private Teacher giangVien;
    private String namHoc;

    public Class() {
    }

    public Class(String id, String maLop, String tenLop, Branch khoa, Teacher giangVien, String namHoc) {
        this.id = id;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.khoa = khoa;
        this.giangVien = giangVien;
        this.namHoc = namHoc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getTenLop() {
        return tenLop;
    }

    public void setTenLop(String tenLop) {
        this.tenLop = tenLop;
    }

    public Branch getKhoa() {
        return khoa;
    }

    public void setKhoa(Branch khoa) {
        this.khoa = khoa;
    }

    public Teacher getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(Teacher giangVien) {
        this.giangVien = giangVien;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    @Override
    public String toString() {
        return "Class{" +
                "id='" + id + '\'' +
                ", maLop='" + maLop + '\'' +
                ", tenLop='" + tenLop + '\'' +
                ", khoa=" + (khoa != null ? khoa.toString() : "null") +
                ", giangVien=" + (giangVien != null ? giangVien.toString() : "null") +
                ", namHoc='" + namHoc + '\'' +
                '}';
    }
}
