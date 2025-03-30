package com.example.phuongldph29233.student_app.Domain;

public class Student {
    private String id;
    private String maSV;
    private String tenSV;
    private String ngaySinh;
    private String queQuan;
    private String soDienThoai;
    private String email;
    private Class lopHoc;

    private String ngayNhapHoc;
    private Branch chuyenNganh;
    private String heDaoTao;

    public Student() {
    }

    public Student(String id, String maSV, String tenSV, String ngaySinh, String queQuan, String soDienThoai, String email, Class lopHoc, String ngayNhapHoc, Branch chuyenNganh, String heDaoTao) {
        this.id = id;
        this.maSV = maSV;
        this.tenSV = tenSV;
        this.ngaySinh = ngaySinh;
        this.queQuan = queQuan;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.lopHoc = lopHoc;
        this.ngayNhapHoc = ngayNhapHoc;
        this.chuyenNganh = chuyenNganh;
        this.heDaoTao = heDaoTao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Class getLopHoc() {
        return lopHoc;
    }

    public void setLopHoc(Class lopHoc) {
        this.lopHoc = lopHoc;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getTenSV() {
        return tenSV;
    }

    public void setTenSV(String tenSV) {
        this.tenSV = tenSV;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getQueQuan() {
        return queQuan;
    }

    public void setQueQuan(String queQuan) {
        this.queQuan = queQuan;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNgayNhapHoc() {
        return ngayNhapHoc;
    }

    public void setNgayNhapHoc(String ngayNhapHoc) {
        this.ngayNhapHoc = ngayNhapHoc;
    }

    public Branch getChuyenNganh() {
        return chuyenNganh;
    }

    public void setChuyenNganh(Branch chuyenNganh) {
        this.chuyenNganh = chuyenNganh;
    }

    public String getHeDaoTao() {
        return heDaoTao;
    }

    public void setHeDaoTao(String heDaoTao) {
        this.heDaoTao = heDaoTao;
    }


}
