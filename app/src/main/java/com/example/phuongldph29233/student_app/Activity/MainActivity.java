package com.example.phuongldph29233.student_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Activity.Screens.BranchActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.ClassActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.MarkActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.StudentActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.SubjectActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.TeacherActivity;
import com.example.phuongldph29233.student_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleItem();
    }

    private void handleItem() {
        binding.cardStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        });
        binding.cardClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ClassActivity.class);
                startActivity(intent);
            }
        });
        binding.cardBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BranchActivity.class);
                startActivity(intent);
            }
        });
        binding.cardSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SubjectActivity.class));
            }
        });
        binding.cardTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TeacherActivity.class));
            }
        });
        binding.cardMath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MarkActivity.class));
            }
        });
    }
}