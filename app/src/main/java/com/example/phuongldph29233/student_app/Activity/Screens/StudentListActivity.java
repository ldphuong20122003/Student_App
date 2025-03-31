package com.example.phuongldph29233.student_app.Activity.Screens;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Adapter.StudentTableAdapter;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentTableAdapter adapter;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private List<String> studentIds;
    private String maLop, tenLop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        maLop = getIntent().getStringExtra("maLop");
        tenLop = getIntent().getStringExtra("tenLop");
        List<Student> students = (List<Student>) getIntent().getSerializableExtra("danhSachSinhVien");
        if (students == null || students.isEmpty()) {
            finish();
            return;
        }
        RecyclerView recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        StudentTableAdapter adapter = new StudentTableAdapter(this, students);
        recyclerView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Danh sách SV - " + tenLop + " (" + maLop + ")");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        studentDatabaseHelper = new DatabaseHelper<>("Students");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}