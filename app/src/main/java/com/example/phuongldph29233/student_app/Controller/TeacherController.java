package com.example.phuongldph29233.student_app.Controller;

import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;

public class TeacherController {
    private final DatabaseHelper<Teacher> databaseHelper;

    public TeacherController() {
        databaseHelper = new DatabaseHelper<>("Teacher");
    }

    public void getTeacher(DatabaseHelper.DatabaseCallback<Teacher> callback) {
        databaseHelper.getList(Teacher.class, callback);
    }

    public void addTeacher(Teacher teacher, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.add(teacher, callback);
    }

    public void updateTeacher(String id, Teacher teacher, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.update(id, teacher, callback);
    }

    public void deleteTeacher(String id, DatabaseHelper.DatabaseActionCallback callback) {
        databaseHelper.delete(id, callback);
    }
}
