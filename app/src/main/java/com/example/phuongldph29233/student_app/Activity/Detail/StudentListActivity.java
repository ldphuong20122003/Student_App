package com.example.phuongldph29233.student_app.Activity.Detail;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.R;

public class StudentListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
//    private StudentTableAdapter adapter;
//    private List<Student> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table_student);

        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        studentList.add(new Student("SV001", "Nguyễn Văn A", "01/01/2000", "Hà Nội", new Branch("CNTT", "Công nghệ thông tin")));
//        studentList.add(new Student("SV002", "Trần Thị B", "15/05/2001", "TP.HCM", new Branch("KT", "Kế toán")));
        // Thêm các sinh viên khác...

//        adapter = new StudentTableAdapter(this, studentList);
//        recyclerView.setAdapter(adapter);
    }
}