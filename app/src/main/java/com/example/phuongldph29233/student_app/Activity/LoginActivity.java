package com.example.phuongldph29233.student_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityLoginBinding;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    boolean isPasswordVisible = false;
    DatabaseHelper<Teacher> teacherDatabaseHelper;
    DatabaseHelper<Student> studentDatabaseHelper;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        teacherDatabaseHelper = new DatabaseHelper<>("Teacher");
        studentDatabaseHelper = new DatabaseHelper<>("Student");
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            String role = sessionManager.getRole();
            String username = sessionManager.getUsername();
            switch (role) {
                case "admin":
                    startAdminActivity(username);
                    break;
                case "teacher":
                    teacherDatabaseHelper.getList(Teacher.class, new DatabaseHelper.DatabaseCallback<Teacher>() {
                        @Override
                        public void onSuccess(List<Teacher> teacherList) {
                            for (Teacher teacher : teacherList) {
                                if (teacher.getTeacherID().equals(username)) {
                                    startTeacherActivity(username, teacher);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            startTeacherActivity(username, null);
                        }
                    });
                    break;
                case "student":
                    studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
                        @Override
                        public void onSuccess(List<Student> studentList) {
                            for (Student student : studentList) {
                                if (student.getStudentID().equals(username)) {
                                    startStudentActivity(username, student);
                                    return;
                                }
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            startStudentActivity(username, null);
                        }
                    });
                    break;
            }
            return;
        }

        handleShowPass();
        handleLogin();
    }

    private void handleLogin() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.edtUsername.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this,
                            TextUtils.isEmpty(username) ? "Vui lòng nhập tài khoản!" : "Vui lòng nhập mật khẩu!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.equalsIgnoreCase("Admin") && password.equals("123456")) {
                    sessionManager.createLoginSession(username, "admin");
                    startAdminActivity(username);
                    return;
                }
                teacherDatabaseHelper.getList(Teacher.class, new DatabaseHelper.DatabaseCallback<Teacher>() {
                    @Override
                    public void onSuccess(List<Teacher> teacherList) {
                        for (Teacher teacher : teacherList) {
                            if (teacher.getTeacherID().equals(username) && teacher.getTeacherPhone() != null && teacher.getTeacherPhone().equals(password)) {
                                sessionManager.createLoginSession(username, "teacher");
                                startTeacherActivity(username, teacher);
                                return;
                            }
                        }
                        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
                            @Override
                            public void onSuccess(List<Student> studentList) {
                                for (Student student : studentList) {
                                    if (student.getStudentID().equals(username) && student.getStudentPhone() != null && student.getStudentPhone().equals(password)) {
                                        sessionManager.createLoginSession(username, "student");
                                        startStudentActivity(username, student);
                                        return;
                                    }
                                }
                                Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(LoginActivity.this, "Lỗi lấy thông tin sinh viên: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(LoginActivity.this, "Lỗi lấy thông tin giáo viên: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void handleShowPass() {
        binding.imgShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.imgShow.setImageResource(R.drawable.show_on);
                } else {
                    binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.imgShow.setImageResource(R.drawable.show_off);
                }
                isPasswordVisible = !isPasswordVisible;
                binding.edtPassword.setSelection(binding.edtPassword.getText().length());
            }
        });
    }

    private void startAdminActivity(String username) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("role", "admin");
        startActivity(intent);
        finish();
    }

    private void startTeacherActivity(String username, Teacher teacher) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("role", "teacher");
        intent.putExtra("teacher", teacher);
        startActivity(intent);
        finish();
    }

    private void startStudentActivity(String username, Student student) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("role", "student");
        intent.putExtra("student", student);
        startActivity(intent);
        finish();
    }
}