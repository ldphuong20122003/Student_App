package com.example.phuongldph29233.student_app.Domain;

public class Teacher {
    private String id;
    private String maGV;
    private String tenGV;
    private String email;
    private String soDT;
    private Branch khoa;

    public Teacher() {
    }

    public Teacher(String id, String maGV, String tenGV, String email, String soDT, Branch khoa) {
        this.id = id;
        this.maGV = maGV;
        this.tenGV = tenGV;
        this.email = email;
        this.soDT = soDT;
        this.khoa = khoa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaGV() {
        return maGV;
    }

    public void setMaGV(String maGV) {
        this.maGV = maGV;
    }

    public String getTenGV() {
        return tenGV;
    }

    public void setTenGV(String tenGV) {
        this.tenGV = tenGV;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public Branch getKhoa() {
        return khoa;
    }

    public void setKhoa(Branch khoa) {
        this.khoa = khoa;
    }

    @Override
    public String toString() {
        return getTenGV();
    }
}
