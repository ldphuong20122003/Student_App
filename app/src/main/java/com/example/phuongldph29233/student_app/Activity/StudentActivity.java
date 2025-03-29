package com.example.phuongldph29233.student_app.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.ClassAdapter;
import com.example.phuongldph29233.student_app.Adapter.StudentAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityStudentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StudentActivity extends AppCompatActivity {
    ActivityStudentBinding binding;
    private DatabaseHelper<Student> databaseHelper;
    private ArrayAdapter arrayAdapter;
    private StudentAdapter studentAdapter;
    private BranchController branchController;
    private ArrayList<Student> studentArrayList;
    private ArrayList<Branch> branchList;
    private ArrayList<Student> originalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper<>("Student");
        branchController = new BranchController();
        studentArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        branchList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentArrayList);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(studentAdapter);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        loadDataStudent();
        loadDataBranch();
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadDataStudent();
    }
    private void loadDataStudent() {
        databaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Student> list) {
                studentArrayList.clear();
                originalArrayList.clear();
                studentArrayList.addAll(list);
                originalArrayList.addAll(list);
                studentAdapter.notifyDataSetChanged();

                if (studentArrayList.isEmpty()) {
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(String error) {
                binding.recyclerView.setVisibility(View.GONE);
                Toast.makeText(StudentActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataBranch() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchList.clear();
                branchList.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(StudentActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_student);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txtTitle = dialog.findViewById(R.id.txt_title_subject);
        EditText edtMaSV = dialog.findViewById(R.id.edt_maSV_add);
        EditText edtTenSV = dialog.findViewById(R.id.edt_tenSV_add);
        EditText edtNgaysinh = dialog.findViewById(R.id.edt_ngaySinh_add);
        EditText edtQue = dialog.findViewById(R.id.edt_queQuan_add);
        EditText edtSDT = dialog.findViewById(R.id.edt_sdt_add);
        EditText edtMail = dialog.findViewById(R.id.edt_email_add);
        EditText edtLophoc = dialog.findViewById(R.id.edt_lopHoc_add);
        EditText edtNgayNhapHoc = dialog.findViewById(R.id.edt_ngayNhaphoc_add);
        EditText edtHeDaotao = dialog.findViewById(R.id.edt_heDaotao_add);
        Spinner spn_chuyenNganh = dialog.findViewById(R.id.spn_chuyenNganh);
        Button btnHuy = dialog.findViewById(R.id.btn_huy_sv);
        Button btnAdd = dialog.findViewById(R.id.btn_add_sv);
        txtTitle.setText("Thêm sinh viên");
        spn_chuyenNganh.setAdapter(arrayAdapter);
        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maSV = edtMaSV.getText().toString().trim();
            String tenSV = edtTenSV.getText().toString().trim();
            String ngaySinh = edtNgaysinh.getText().toString().trim();
            String queQuan = edtQue.getText().toString().trim();
            String soDienThoai = edtSDT.getText().toString().trim();
            String email = edtMail.getText().toString().trim();
            String lopHoc = edtLophoc.getText().toString().trim();
            String ngayNhapHoc = edtNgayNhapHoc.getText().toString().trim();
            String heDaoTao = edtHeDaotao.getText().toString().trim();
            Branch selectedBranch = (Branch) spn_chuyenNganh.getSelectedItem();
            if (maSV.isEmpty() || tenSV.isEmpty() || lopHoc.isEmpty() || heDaoTao.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedBranch == null || selectedBranch.getTenKhoa() == null || selectedBranch.getMaKhoa().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn chuyên ngành hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            Student student = new Student(id, maSV, tenSV, ngaySinh, queQuan, soDienThoai, email, lopHoc, ngayNhapHoc, selectedBranch, heDaoTao);
            addStudent(student, dialog);
        });
        btnHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addStudent(Student student, Dialog dialog) {
        databaseHelper.add(student, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(StudentActivity.this, "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadDataStudent();
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(StudentActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchList(String text) {
        ArrayList<Student> filteredList = new ArrayList<>();
        for (Student data : originalArrayList) {
            if (data.getMaSV().toLowerCase().contains(text.toLowerCase()) || data.getTenSV().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        studentAdapter.searchStudent(filteredList);
    }
}