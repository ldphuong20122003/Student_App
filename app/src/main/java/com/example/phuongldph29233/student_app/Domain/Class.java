package com.example.phuongldph29233.student_app.Domain;

public class Class {
    private String id;
    private String maLop;

    private String tenLop;
    private String khoa;
    private String giangVien;
    private String namHoc;

    public Class() {
    }

    public Class(String id, String maLop, String tenLop, String khoa, String giangVien, String namHoc) {
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

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }

    public String getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(String giangVien) {
        this.giangVien = giangVien;
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }
}
