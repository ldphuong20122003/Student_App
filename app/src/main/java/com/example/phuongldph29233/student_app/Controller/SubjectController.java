package com.example.phuongldph29233.student_app.Controller;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectController {
    private final DatabaseHelper<Subject> databaseHelper;

    public SubjectController() {
        databaseHelper = new DatabaseHelper<>("Subject");
    }

    public void getSubject(DatabaseHelper.DatabaseCallback<Subject> callback) {
        databaseHelper.getList(Subject.class, callback);

    }

    public void addSubject(Subject subject, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.add(subject, callback);
    }

    public void updateSubject(String id, Subject updateSubject, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.update(id, updateSubject, callback);
    }

    public void deleteSubject(String id, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.delete(id, callback);
    }


}
