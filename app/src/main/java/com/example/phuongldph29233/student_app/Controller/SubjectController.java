package com.example.phuongldph29233.student_app.Controller;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectController {
    private final DatabaseReference myRef;

    public SubjectController() {
        myRef = FirebaseDatabase.getInstance().getReference("Subject");
    }

    public void getSubject(SubjectCallback callback) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Subject> arrayList = new ArrayList<>();
                for (DataSnapshot issue : snapshot.getChildren()) {
                    Subject subject = issue.getValue(Subject.class);
                    if (subject != null) {
                        arrayList.add(subject);
                    }
                }
                callback.onSuccess(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.getMessage());
            }
        });
    }

    public void addSubject(Subject subject, AddSubjectCallback callback) {
        myRef.child(subject.getId()).setValue(subject).addOnSuccessListener(v -> callback.onSuccess()).addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void updateSubject(String id, Subject updateSubject, AddSubjectCallback callback) {
        myRef.child(id).setValue(updateSubject).addOnSuccessListener(v -> callback.onSuccess()).addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public void deleteSubject(String id, DeleteSubjectCallback callback) {
        myRef.child(id).removeValue().addOnSuccessListener(v -> callback.onSuccess()).addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    public interface SubjectCallback {
        void onSuccess(ArrayList<Subject> list);

        void onFailed(String error);
    }

    public interface AddSubjectCallback {
        void onSuccess();

        void onFailed(String error);

    }

    public interface DeleteSubjectCallback {
        void onSuccess();

        void onFailed(String error);
    }
}
