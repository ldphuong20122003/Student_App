package com.example.phuongldph29233.student_app.Domain;

public class Branch {
    private String id;
    private String branchID;
    private String branchName;

    public Branch() {
    }

    public Branch(String id, String branchID, String branchName) {
        this.id = id;
        this.branchID = branchID;
        this.branchName = branchName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBranchID() {
        return branchID;
    }

    public void setBranchID(String branchID) {
        this.branchID = branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    @Override
    public String toString() {
        return getBranchName(); // Hiển thị tên chuyên ngành trong log và Spinner
    }
}
