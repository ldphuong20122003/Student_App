package com.example.phuongldph29233.student_app.Domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Class implements Serializable {
    private String id;
    private String maLop;
    private String tenLop;
    private Branch khoa;
    private Teacher giangVien;
    private String namHoc;
    private final List<Student> danhSachSinhVien;

    public Class() {
        this.danhSachSinhVien = new ArrayList<>();
    }

    public Class(String id, String maLop, String tenLop, Branch khoa, Teacher giangVien, String namHoc) {
        this();
        this.id = id;
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.khoa = khoa;
        this.giangVien = giangVien;
        this.namHoc = namHoc;
    }

    public Class(String id, String maLop, String tenLop, Branch khoa, Teacher giangVien, String namHoc, List<Student> danhSachSinhVien) {
        this(id, maLop, tenLop, khoa, giangVien, namHoc);
        if (danhSachSinhVien != null) {
            this.danhSachSinhVien.addAll(danhSachSinhVien);
        }
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

    public List<Student> getDanhSachSinhVien() {
        return danhSachSinhVien;
    }

    public void setDanhSachSinhVien(List<Student> danhSachSinhVien) {
        this.danhSachSinhVien.clear();
        if (danhSachSinhVien != null) {
            this.danhSachSinhVien.addAll(danhSachSinhVien);
        }
    }

    public void xoaSinhVien(Student sinhVien) {
        this.danhSachSinhVien.remove(sinhVien);
    }

    public void themSinhVien(Student sinhVien) {
        if (sinhVien != null) {
            this.danhSachSinhVien.add(sinhVien);
        }
    }

    public String getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(String namHoc) {
        this.namHoc = namHoc;
    }

    @Override
    public String toString() {
        return getTenLop();
    }
}
