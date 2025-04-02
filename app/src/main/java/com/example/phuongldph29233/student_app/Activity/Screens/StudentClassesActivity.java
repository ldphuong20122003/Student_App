package com.example.phuongldph29233.student_app.Activity.Screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Adapter.StudentClassesAdapter;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StudentClassesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentClassesAdapter adapter;
    private ArrayList<Class> classList;
    private String studentId, studentName, currentClass;
    private TextView txtStudentInfo;
    private Button btnBack;
    private DatabaseHelper<Class> classDatabaseHelper;
    private ArrayList<String> selectedClassIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classes);
        classDatabaseHelper = new DatabaseHelper<>("Classes");
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");
        currentClass = getIntent().getStringExtra("currentClass");
        if (getIntent().hasExtra("classIds")) {
            selectedClassIds = getIntent().getStringArrayListExtra("classIds");
        }
        if (selectedClassIds == null) {
            selectedClassIds = new ArrayList<>();
        }
        initViews();
        setupRecyclerView();
        loadClasses();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_student_classes);
        txtStudentInfo = findViewById(R.id.txt_student_info);
        btnBack = findViewById(R.id.btn_back);
        txtStudentInfo.setText("Danh sách lớp học của sinh viên: " + studentName);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        classList = new ArrayList<>();
        adapter = new StudentClassesAdapter(this, classList, currentClass, studentId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
        private void loadClasses() {
            classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
                @Override
                public void onSuccess(List<Class> allClasses) {
                    classList.clear();
                    if (selectedClassIds != null) {
                        for (Class classItem : allClasses) {
                            if (selectedClassIds.contains(classItem.getId())) {
                                classList.add(classItem);
                            }
                        }
                    } else {
                        Log.e("ERROR", "selectedClassIds is null");
                    }
                    adapter.notifyDataSetChanged();
                    if (classList.isEmpty()) {
                        Toast.makeText(StudentClassesActivity.this,
                                "Không tìm thấy lớp học",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                public void onFailure(String error) {
                    Log.e("STUDENT_ERROR", "Error loading students: " + error);
                    Toast.makeText(StudentClassesActivity.this,
                            "Lỗi tải danh sách sinh viên: " + error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

