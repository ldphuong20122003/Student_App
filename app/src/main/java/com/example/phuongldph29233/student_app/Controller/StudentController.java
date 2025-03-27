package com.example.phuongldph29233.student_app.Controller;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StudentController {
    private final DatabaseReference studentRef;

    public StudentController() {
        // Khởi tạo reference tới node "Classes" trong Firebase
        this.studentRef = FirebaseDatabase.getInstance().getReference("Classes");
    }

    // Thêm lớp học mới
    public void addClass(Student student, DatabaseHelper.AddItemCallback callback) {
        // Tạo key tự động và gán ID
        String key = studentRef.push().getKey();
        student.setId(key);

        // Thêm dữ liệu
        studentRef.child(key).setValue(student)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Lấy danh sách lớp học
    public void getStudents(DatabaseHelper.DataCallback<Student> callback) {
       studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Student> students = new ArrayList<>();
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    Student classObj = classSnapshot.getValue(Student.class);
                    if (classObj != null) {
                        students.add(classObj);
                    }
                }
                callback.onSuccess(students);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}