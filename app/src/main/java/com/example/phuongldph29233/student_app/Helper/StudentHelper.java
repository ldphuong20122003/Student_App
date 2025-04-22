package com.example.phuongldph29233.student_app.Helper;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Score;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StudentHelper<T> {
    private final DatabaseReference databaseReference;

    public StudentHelper(String path) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference(path);
    }

    public void getStudentScores(String studentId, String classId, String subjectId, Class<T> clazz, DatabaseHelper.DatabaseGetCallback<T> callback) {
        DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("Scores");
        scoresRef.orderByChild("studentId").equalTo(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String scoreClassId = ds.child("classId").getValue(String.class);
                            String scoreSubjectId = ds.child("subjectId").getValue(String.class);
                            if (scoreClassId != null && scoreClassId.equals(classId) &&
                                    scoreSubjectId != null && scoreSubjectId.equals(subjectId)) {
                                T score = ds.getValue(clazz);
                                if (score != null) {
                                    try {
                                        Method setIdMethod = score.getClass().getMethod("setId", String.class);
                                        setIdMethod.invoke(score, ds.getKey());
                                    } catch (Exception e) {
                                    }
                                    callback.onSuccess(score);
                                    return;
                                }
                            }
                        }
                        callback.onFailure("Không tìm thấy điểm cho sinh viên này");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void getAverageScore(String studentId, DatabaseHelper.DatabaseCallback<Double> callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Double> finalScores = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Score score = snapshot.getValue(Score.class);
                    if (score != null && studentId.equals(score.getStudentId())) {
                        finalScores.add(score.getFinalScore());
                    }
                }
                if (finalScores.isEmpty()) {
                    callback.onSuccess(List.of(0.0));
                    return;
                }
                double average = finalScores.stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0);
                callback.onSuccess(List.of(average));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }


}