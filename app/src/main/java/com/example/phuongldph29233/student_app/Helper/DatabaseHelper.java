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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper<T> {
    private final DatabaseReference databaseReference;

    public DatabaseHelper(String path) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference(path);
    }

    public void getList(Class<T> clazz, DatabaseCallback<T> callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> itemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    T item = itemSnapshot.getValue(clazz);
                    if (item != null) {
                        try {
                            item.getClass().getMethod("setId", String.class).invoke(item, itemSnapshot.getKey());
                        } catch (Exception e) {
                        }
                        itemList.add(item);
                    }
                }
                callback.onSuccess(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void add(T item, DatabaseActionCallback callback) {
        String key = databaseReference.push().getKey();
        try {
            item.getClass().getMethod("setId", String.class).invoke(item, key);
        } catch (Exception e) {
        }

        databaseReference.child(key)
                .setValue(item)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void update(String id, T updatedItem, DatabaseActionCallback callback) {
        databaseReference.child(id)
                .setValue(updatedItem)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void delete(String id, DatabaseActionCallback callback) {
        databaseReference.child(id)
                .removeValue()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void get(String id, Class<T> clazz, DatabaseGetCallback<T> callback) {
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    T item = snapshot.getValue(clazz);
                    if (item != null) {
                        try {
                            Method setIdMethod = item.getClass().getMethod("setId", String.class);
                            setIdMethod.invoke(item, snapshot.getKey());
                        } catch (Exception e) {
                        }
                        callback.onSuccess(item);
                    } else {
                        callback.onFailure("Không thể chuyển đổi dữ liệu");
                    }
                } else {
                    callback.onFailure("Không tìm thấy đối tượng với ID: " + id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public void getStudentClasses(String studentId, Class<T> clazz, DatabaseCallback<T> callback) {
        DatabaseReference studentClassRef = FirebaseDatabase.getInstance().getReference("StudentClasses");
        studentClassRef.orderByChild("studentId").equalTo(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<T> classes = new ArrayList<>();
                        List<String> classIds = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String classId = ds.child("classId").getValue(String.class);
                            if (classId != null) {
                                classIds.add(classId);
                            }
                        }

                        if (classIds.isEmpty()) {
                            callback.onSuccess(classes);
                            return;
                        }

                        DatabaseReference classesRef = FirebaseDatabase.getInstance().getReference("Classes");
                        for (String classId : classIds) {
                            classesRef.child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot classSnapshot) {
                                    T cls = classSnapshot.getValue(clazz);
                                    if (cls != null) {
                                        try {
                                            Method setIdMethod = cls.getClass().getMethod("setId", String.class);
                                            setIdMethod.invoke(cls, classSnapshot.getKey());
                                        } catch (Exception e) {
                                        }
                                        classes.add(cls);
                                    }

                                    if (classes.size() == classIds.size()) {
                                        callback.onSuccess(classes);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    callback.onFailure(error.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void addStudentToClass(String studentId, String classId, DatabaseActionCallback callback) {
        DatabaseReference studentClassRef = FirebaseDatabase.getInstance().getReference("StudentClasses");
        studentClassRef.orderByChild("studentId").equalTo(studentId)
                .orderByChild("classId").equalTo(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            callback.onFailure("Sinh viên đã có trong lớp này");
                        } else {
                            String key = studentClassRef.push().getKey();
                            Map<String, Object> studentClass = new HashMap<>();
                            studentClass.put("studentId", studentId);
                            studentClass.put("classId", classId);

                            studentClassRef.child(key).setValue(studentClass)
                                    .addOnSuccessListener(aVoid -> callback.onSuccess())
                                    .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void removeStudentFromClass(String studentId, String classId, DatabaseActionCallback callback) {
        DatabaseReference studentClassRef = FirebaseDatabase.getInstance().getReference("StudentClasses");
        studentClassRef.orderByChild("studentId").equalTo(studentId)
                .orderByChild("classId").equalTo(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> callback.onSuccess())
                                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                            }
                        } else {
                            callback.onFailure("Không tìm thấy bản ghi");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    public void getStudentsInClass(String classId, Class<T> clazz, DatabaseCallback<T> callback) {
        DatabaseReference studentClassRef = FirebaseDatabase.getInstance().getReference("StudentClasses");
        studentClassRef.orderByChild("classId").equalTo(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<T> students = new ArrayList<>();
                        List<String> studentIds = new ArrayList<>();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String studentId = ds.child("studentId").getValue(String.class);
                            if (studentId != null) {
                                studentIds.add(studentId);
                            }
                        }

                        if (studentIds.isEmpty()) {
                            callback.onSuccess(students);
                            return;
                        }

                        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("Students");
                        for (String studentId : studentIds) {
                            studentsRef.child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot studentSnapshot) {
                                    T student = studentSnapshot.getValue(clazz);
                                    if (student != null) {
                                        try {
                                            Method setIdMethod = student.getClass().getMethod("setId", String.class);
                                            setIdMethod.invoke(student, studentSnapshot.getKey());
                                        } catch (Exception e) {
                                        }
                                        students.add(student);
                                    }

                                    if (students.size() == studentIds.size()) {
                                        callback.onSuccess(students);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    callback.onFailure(error.getMessage());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    // Phương thức mới: Lấy điểm của sinh viên trong một lớp và môn học
    public void getStudentScores(String studentId, String classId, String subjectId, Class<T> clazz, DatabaseGetCallback<T> callback) {
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

    public void getAverageScore(String studentId, DatabaseCallback<Double> callback) {
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

    public interface DatabaseGetCallback<T> {
        void onSuccess(T item);
        void onFailure(String error);
    }

    public interface DatabaseCallback<T> {
        void onSuccess(List<T> itemList);
        void onFailure(String error);
    }

    public interface DatabaseActionCallback {
        void onSuccess();
        void onFailure(String error);
    }
}