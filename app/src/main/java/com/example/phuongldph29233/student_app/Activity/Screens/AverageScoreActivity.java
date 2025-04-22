package com.example.phuongldph29233.student_app.Activity.Screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Adapter.AverageScoreAdapter;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Score;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AverageScoreActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvEmptyMessage;
    private AverageScoreAdapter adapter;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Class> classDatabaseHelper;
    private DatabaseHelper<Score> scoreDatabaseHelper;
    private ArrayList<Student> students;
    private Map<String, Class> classMap;
    private Map<String, Double> averageScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average_score);

        recyclerView = findViewById(R.id.recyclerViewAverageScores);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        students = new ArrayList<>();
        classMap = new HashMap<>();
        averageScores = new HashMap<>();
        adapter = new AverageScoreAdapter(students, classMap, averageScores);
        recyclerView.setAdapter(adapter);

        studentDatabaseHelper = new DatabaseHelper<>("Student");
        classDatabaseHelper = new DatabaseHelper<>("Classes");
        scoreDatabaseHelper = new DatabaseHelper<>("Scores");

        loadData();
    }

    private void loadData() {
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> studentList) {
                students.clear();
                students.addAll(studentList);
                Log.d("AverageScoreActivity", "Loaded " + students.size() + " students: " + students);

                runOnUiThread(() -> {
                    if (students.isEmpty()) {
                        recyclerView.setVisibility(android.view.View.GONE);
                        tvEmptyMessage.setVisibility(android.view.View.VISIBLE);
                        tvEmptyMessage.setText("Không có sinh viên nào");
                    } else {
                        recyclerView.setVisibility(android.view.View.VISIBLE);
                        tvEmptyMessage.setVisibility(android.view.View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                });

                classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
                    @Override
                    public void onSuccess(List<Class> classList) {
                        classMap.clear();
                        for (Class clazz : classList) {
                            classMap.put(clazz.getId(), clazz);
                        }
                        Log.d("AverageScoreActivity", "Loaded " + classMap.size() + " classes: " + classMap.keySet());

                        loadAverageScores();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("AverageScoreActivity", "Error loading classes: " + error);
                        runOnUiThread(() -> Toast.makeText(AverageScoreActivity.this, "Lỗi tải lớp: " + error, Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("AverageScoreActivity", "Error loading students: " + error);
                runOnUiThread(() -> {
                    recyclerView.setVisibility(android.view.View.GONE);
                    tvEmptyMessage.setVisibility(android.view.View.VISIBLE);
                    tvEmptyMessage.setText("Lỗi tải sinh viên: " + error);
                    Toast.makeText(AverageScoreActivity.this, "Lỗi tải sinh viên: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadAverageScores() {
        if (students.isEmpty()) {
            Log.d("AverageScoreActivity", "No students to load average scores");
            return;
        }

        for (Student student : students) {
            if (student.getId() == null) {
                Log.w("AverageScoreActivity", "Student ID is null: " + student);
                continue;
            }
            scoreDatabaseHelper.getAverageScore(student.getId(), new DatabaseHelper.DatabaseCallback<Double>() {
                @Override
                public void onSuccess(List<Double> result) {
                    double average = result.get(0);
                    averageScores.put(student.getId(), average);
                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        Log.d("AverageScoreActivity", "Average score for " + student.getStudentName() + ": " + average);
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e("AverageScoreActivity", "Error loading average for " + student.getId() + ": " + error);
                    averageScores.put(student.getId(), 0.0);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            });
        }
    }
}