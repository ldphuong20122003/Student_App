package com.example.phuongldph29233.student_app.Controller;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentController {
    private DatabaseHelper<Class> databaseHelper;

    public StudentController() {
        databaseHelper = new DatabaseHelper<>("Student");
    }

    public void getClasses(DatabaseHelper.DatabaseCallback<Class> callback) {
        databaseHelper.getList(Class.class, callback);
    }

    public void addClass(Class newClass, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.add(newClass, callback);
    }

    public void updateClass(String classId, Class updatedClass, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.update(classId, updatedClass, callback);
    }

    public void deleteClass(String classId, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.delete(classId, callback);
    }
}