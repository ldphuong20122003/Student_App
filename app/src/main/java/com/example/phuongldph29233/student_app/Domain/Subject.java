package com.example.phuongldph29233.student_app.Domain;

public class Subject {
    private String id;
    private String maMon;
    private String tenMon;
    private Branch chuyenNganh;
    private String soTinChi;

    public Subject(String id, String maMon, String tenMon, Branch chuyenNganh, String soTinChi) {
        this.id = id;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.chuyenNganh = chuyenNganh;
        this.soTinChi = soTinChi;
    }

    public Subject() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public Branch getChuyenNganh() {
        return chuyenNganh;
    }

    public void setChuyenNganh(Branch chuyenNganh) {
        this.chuyenNganh = chuyenNganh;
    }

    public String getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(String soTinChi) {
        this.soTinChi = soTinChi;
    }
}
