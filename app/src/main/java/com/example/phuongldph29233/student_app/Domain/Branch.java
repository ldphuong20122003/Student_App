package com.example.phuongldph29233.student_app.Domain;

public class Branch {
    private String id;
    private String maKhoa;
    private String tenKhoa;

    public Branch() {
    }

    public Branch(String id, String maKhoa, String tenKhoa) {
        this.id = id;
        this.maKhoa = maKhoa;
        this.tenKhoa = tenKhoa;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    @Override
    public String toString() {
        return getTenKhoa(); // Hiển thị tên chuyên ngành trong log và Spinner
    }
}
