package com.example.phuongldph29233.student_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Activity.Screens.AverageScoreActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.BranchActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.ClassActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.StudentActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.SubjectActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.TeacherActivity;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        String username = sessionManager.getUsername();
        String role = sessionManager.getRole();
        switch (role) {
            case "admin":
                binding.cardStudent.setVisibility(View.VISIBLE);
                binding.cardClass.setVisibility(View.VISIBLE);
                binding.cardBranch.setVisibility(View.VISIBLE);
                binding.cardSubject.setVisibility(View.VISIBLE);
                binding.cardTeacher.setVisibility(View.VISIBLE);
                binding.cardMath.setVisibility(View.VISIBLE);
                break;
            case "teacher":
                binding.cardStudent.setVisibility(View.VISIBLE);
                binding.cardClass.setVisibility(View.VISIBLE);
                binding.cardBranch.setVisibility(View.VISIBLE);
                binding.cardTeacher.setVisibility(View.VISIBLE);
                binding.cardMath.setVisibility(View.VISIBLE);
                binding.cardSubject.setVisibility(View.VISIBLE);
                break;
            case "student":
                binding.cardStudent.setVisibility(View.VISIBLE);
                binding.cardClass.setVisibility(View.VISIBLE);
                binding.cardBranch.setVisibility(View.VISIBLE);
                binding.cardMath.setVisibility(View.VISIBLE);
                binding.cardSubject.setVisibility(View.VISIBLE);
                binding.cardTeacher.setVisibility(View.GONE);
                break;
            default:
                Toast.makeText(this, "Vai trò không hợp lệ!", Toast.LENGTH_SHORT).show();
                sessionManager.logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return;
        }

        handleItem();
        handleLogout();
    }

    private void handleItem() {
        String role = sessionManager.getRole();
        String username = sessionManager.getUsername();

        binding.cardStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StudentActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        binding.cardClass.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ClassActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        binding.cardBranch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BranchActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        binding.cardSubject.setOnClickListener(v -> {
            if (role.equals("admin")) {
                startActivity(new Intent(MainActivity.this, SubjectActivity.class));
            }
        });

        binding.cardTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        binding.cardMath.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AverageScoreActivity.class);
            intent.putExtra("role", role);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }

    private void handleLogout() {
        binding.btnLogout.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });
    }
}