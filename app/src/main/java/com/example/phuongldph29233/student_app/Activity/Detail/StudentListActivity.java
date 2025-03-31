package com.example.phuongldph29233.student_app.Activity.Detail;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
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
        setContentView(R.layout.table_student);

        maLop = getIntent().getStringExtra("maLop");
        tenLop = getIntent().getStringExtra("tenLop");
        studentIds = getIntent().getStringArrayListExtra("danhSachSinhVienIds");

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        toolbar.setTitle("Danh sách SV - " + tenLop + " (" + maLop + ")");
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StudentTableAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        studentDatabaseHelper = new DatabaseHelper<>("Students");
        loadStudents();
    }

    private void loadStudents() {
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> allStudents) {
                List<Student> filteredStudents = new ArrayList<>();
                for (Student student : allStudents) {
                    if (studentIds.contains(student.getId())) {
                        filteredStudents.add(student);
                    }
                }

                runOnUiThread(() -> {
                    if (filteredStudents.isEmpty()) {
                        Toast.makeText(StudentListActivity.this,
                                "Không tìm thấy sinh viên", Toast.LENGTH_SHORT).show();
                    }
                    adapter.updateDataSet(filteredStudents);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(StudentListActivity.this,
                                "Lỗi tải sinh viên: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}