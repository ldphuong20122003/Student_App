package com.example.phuongldph29233.student_app.Controller;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Class;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassController {
    private final DatabaseReference classesRef;

    public ClassController() {
        this.classesRef = FirebaseDatabase.getInstance().getReference("Classes");
    }

    // Lấy danh sách lớp học
    public void getClasses(ClassCallback callback) {
        classesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Class> classList = new ArrayList<>();
                for (DataSnapshot classSnapshot : snapshot.getChildren()) {
                    Class classObj = classSnapshot.getValue(Class.class);
                    if (classObj != null) {
                        classObj.setId(classSnapshot.getKey()); // Gán ID từ Firebase
                        classList.add(classObj);
                    }
                }
                callback.onSuccess(classList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    // Thêm lớp học mới
    public void addClass(Class newClass, ClassActionCallback callback) {
        String key = classesRef.push().getKey();
        newClass.setId(key);
        classesRef.child(key)
                .setValue(newClass)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    // Cập nhật lớp học
    public void updateClass(String classId, Class updatedClass, ClassActionCallback callback) {
        classesRef.child(classId)
                .setValue(updatedClass)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    // Xóa lớp học
    public void deleteClass(String classId, ClassActionCallback callback) {
        classesRef.child(classId)
                .removeValue()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    // Interface callback
    public interface ClassCallback {
        void onSuccess(ArrayList<Class> classList);
        void onFailed(String error);
    }

    public interface ClassActionCallback {
        void onSuccess();
        void onFailed(String error);
    }
}