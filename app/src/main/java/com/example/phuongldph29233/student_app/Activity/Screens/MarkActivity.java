package com.example.phuongldph29233.student_app.Activity.Screens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.databinding.ActivityMarkBinding;

public class MarkActivity extends AppCompatActivity {
    ActivityMarkBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}