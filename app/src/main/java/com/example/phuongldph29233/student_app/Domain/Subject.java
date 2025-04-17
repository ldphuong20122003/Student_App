package com.example.phuongldph29233.student_app.Domain;

import java.io.Serializable;

public class Subject implements Serializable {
    private String id;
    private String subjectID;
    private String subjectName;
    private Branch subjectBranch;
    private String subjectNOC;


    public Subject() {
    }

    public Subject(String id, String subjectID, String subjectName, Branch subjectBranch, String subjectNOC) {
        this.id = id;
        this.subjectID = subjectID;
        this.subjectName = subjectName;
        this.subjectBranch = subjectBranch;
        this.subjectNOC = subjectNOC;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Branch getSubjectBranch() {
        return subjectBranch;
    }

    public void setSubjectBranch(Branch subjectBranch) {
        this.subjectBranch = subjectBranch;
    }

    public String getSubjectNOC() {
        return subjectNOC;
    }

    public void setSubjectNOC(String subjectNOC) {
        this.subjectNOC = subjectNOC;
    }
    public String toString() {
        return getSubjectName();
    }
}
