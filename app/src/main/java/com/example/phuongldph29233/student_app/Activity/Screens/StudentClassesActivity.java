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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_classes);

        // Initialize database helper
        classDatabaseHelper = new DatabaseHelper<>("Classes");

        // Get intent data
        studentId = getIntent().getStringExtra("studentId");
        studentName = getIntent().getStringExtra("studentName");
        currentClass = getIntent().getStringExtra("currentClass");

        // Initialize views
        initViews();

        // Set up RecyclerView
        setupRecyclerView();

        // Load class data
        loadClasses();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_student_classes);
        txtStudentInfo = findViewById(R.id.txt_student_info);
        btnBack = findViewById(R.id.btn_back);

        // Set student info text
        txtStudentInfo.setText("Danh sách lớp học của sinh viên: " + studentName);

        // Set back button click listener
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

                // Tạo một DatabaseHelper để truy vấn sinh viên
                DatabaseHelper<Student> studentDatabaseHelper = new DatabaseHelper<>("Student"); // Đổi thành "Students" nếu cần
                studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
                    @Override
                    public void onSuccess(List<Student> allStudents) {
                        // Tạo map để lưu trữ các lớp có sinh viên
                        Set<String> classesWithStudents = new HashSet<>();

                        for (Student student : allStudents) {
                            // Kiểm tra cách lấy classId từ student
                            // Giả sử student có phương thức getClassId() trả về id của lớp
                            if (student.getStudentClass().getMaLop() != null && !student.getStudentClass().getMaLop().isEmpty()) {
                                classesWithStudents.add(student.getStudentClass().getMaLop());
                            }
                            // Hoặc nếu bạn dùng student.getStudentClass() thì:
                            // if (student.getStudentClass() != null && student.getStudentClass().getId() != null) {
                            //     classesWithStudents.add(student.getStudentClass().getId());
                            // }
                        }

                        // Lọc các lớp có trong danh sách classesWithStudents
                        for (Class classItem : allClasses) {
                            if (classesWithStudents.contains(classItem.getId())) {
                                classList.add(classItem);
                            }
                        }

                        // Kiểm tra log để debug
                        Log.d("CLASS_LIST", "Classes with students: " + classList.size());
                        for (Class c : classList) {
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.e("STUDENT_ERROR", "Error loading students: " + error);
                        Toast.makeText(StudentClassesActivity.this,
                                "Lỗi tải danh sách sinh viên: " + error,
                                Toast.LENGTH_SHORT).show();
                        // Vẫn hiển thị tất cả lớp nếu không tải được sinh viên
                        classList.addAll(allClasses);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("CLASS_ERROR", "Error loading classes: " + error);
                Toast.makeText(StudentClassesActivity.this,
                        "Lỗi tải danh sách lớp: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}