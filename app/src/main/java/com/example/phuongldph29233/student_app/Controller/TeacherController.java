package com.example.phuongldph29233.student_app.Controller;

import android.util.Log;

import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;

import java.util.List;

public class TeacherController {
    private static final String TAG = "TeacherController";
    private final DatabaseHelper<Teacher> databaseHelper;

    public TeacherController() {
        databaseHelper = new DatabaseHelper<>("Teacher"); // Đảm bảo tên node đúng
    }

    public void getTeacher(DatabaseHelper.DatabaseCallback<Teacher> callback) {
        databaseHelper.getList(Teacher.class, new DatabaseHelper.DatabaseCallback<Teacher>() {
            @Override
            public void onSuccess(List<Teacher> itemList) {
                callback.onSuccess(itemList);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    // Các phương thức khác giữ nguyên
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