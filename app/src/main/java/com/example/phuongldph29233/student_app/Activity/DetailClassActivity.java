package com.example.phuongldph29233.student_app.Activity;

import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;

public class DetailClassActivity extends AppCompatActivity {
    private TextView txtMaLop, txtTenLop, txtKhoa, txtGiangVien, txtNamHoc;
    private Button btnEdit, btnDelete, btnBack;
    private String maLop,tenLop,khoa,giangVien,namHoc;
    private DatabaseHelper<Class> classDatabaseHelper;
    private DatabaseHelper<Branch> branchDatabaseHelper;
}
