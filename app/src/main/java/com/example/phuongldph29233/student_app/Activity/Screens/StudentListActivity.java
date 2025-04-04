package com.example.phuongldph29233.student_app.Activity.Screens;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Adapter.StudentTableAdapter;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.List;

public class StudentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentTableAdapter adapter;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private String maLop, tenLop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        maLop = getIntent().getStringExtra("maLop");
        tenLop = getIntent().getStringExtra("tenLop");
        List<Student> students = (List<Student>) getIntent().getSerializableExtra("danhSachSinhVien");
        if (students == null || students.isEmpty()) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentTableAdapter(this, students);
        adapter.setOnItemLongClickListener(position -> {
            Student student = adapter.getStudentAtPosition(position);
            if (student != null) {
                showPopupMenu(position, student);
            }
            return true;
        });
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("Danh sách lớp " + tenLop + " " + maLop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        studentDatabaseHelper = new DatabaseHelper<>("Students");
    }

    private void showPopupMenu(int position, Student student) {
        View view = recyclerView.findViewHolderForAdapterPosition(position).itemView;
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.student_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId(); // Lấy ID của item
            if (itemId == R.id.action_edit) {
                Toast.makeText(this, "Chỉnh sửa: " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_delete) {
                Toast.makeText(this, "Xóa: " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_view) {
                Toast.makeText(this, "Xem chi tiết: " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}