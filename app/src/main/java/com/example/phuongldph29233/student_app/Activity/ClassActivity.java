package com.example.phuongldph29233.student_app.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityClassBinding;

public class ClassActivity extends AppCompatActivity {
    ActivityClassBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v->finish());
       binding.btnAdd.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               showDialog();
           }
       });
    }

    private void showDialog() {
        Dialog dialog= new Dialog(ClassActivity.this);
        dialog.setContentView(R.layout.dialog_add_class);
        dialog.show();

    }


}