package com.example.phuongldph29233.student_app.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.TeacherController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailClassActivity extends AppCompatActivity {
    private TextView txtMaLop, txtTenLop, txtKhoa, txtGiangVien, txtNamHoc,txtListSV;
    private Button btnEdit, btnDelete, btnBack;
    private String id, maLop, tenLop, khoa, giangVien, namHoc;
    private DatabaseHelper<Class> classDatabaseHelper;
    private BranchController branchController;

    private TeacherController teacherController;
    private ArrayList<Branch> branchList;
    private ArrayList<Teacher> teacherList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_class);
        classDatabaseHelper = new DatabaseHelper<>("Classes");
        branchController = new BranchController();
        teacherController = new TeacherController();
        initViews();
        getIntentExtra();
        branchList = new ArrayList<>();
        loadBranches();
        teacherList = new ArrayList<>();
        loadTeachers();
        setupButtonListeners();
    }

    private void initViews() {
        txtMaLop = findViewById(R.id.txt_maLop_detail);
        txtTenLop = findViewById(R.id.txt_tenLop_detail);
        txtKhoa = findViewById(R.id.txt_khoa_detail);
        txtGiangVien = findViewById(R.id.txt_giangVien_detail);
        txtNamHoc = findViewById(R.id.txt_namHoc_detail);
        txtListSV = findViewById(R.id.btn_danhSachSV);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);
        txtListSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailClassActivity.this, ClassActivity.class));
            }
        });
    }

    private void getIntentExtra() {
        id = (String) getIntent().getSerializableExtra("id");
        maLop = (String) getIntent().getSerializableExtra("maLop");
        tenLop = (String) getIntent().getSerializableExtra("tenLop");
        khoa = (String) getIntent().getSerializableExtra("khoa");
        giangVien = (String) getIntent().getSerializableExtra("giangVien");
        namHoc = (String) getIntent().getSerializableExtra("namHoc");

        txtMaLop.setText(maLop);
        txtTenLop.setText(tenLop);
        txtKhoa.setText(khoa);
        txtGiangVien.setText(giangVien);
        txtNamHoc.setText(namHoc);
    }

    private void setupButtonListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> showEditDialog());
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadBranches() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchList.clear();
                branchList.addAll(list);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadTeachers() {
        teacherController.getTeacher(new DatabaseHelper.DatabaseCallback<Teacher>() {
            @Override
            public void onSuccess(List<Teacher> itemList) {
                teacherList.clear();
                teacherList.addAll(itemList);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_class);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView txtTitle = dialog.findViewById(R.id.textView);
        EditText edtMaLopEdit = dialog.findViewById(R.id.edt_maLop_add);
        EditText edtTenLopEdit = dialog.findViewById(R.id.edt_tenLop_add);
        Spinner spnKhoaEdit = dialog.findViewById(R.id.edt_khoa_add);
        Spinner spnGiangVienEdit = dialog.findViewById(R.id.edt_giangVien_add);
        EditText edtNamHocEdit = dialog.findViewById(R.id.edt_namHoc_add);
        Button btnUpdate = dialog.findViewById(R.id.btn_add_lop);
        Button btnCancel = dialog.findViewById(R.id.btn_huy_lop);
        HelperUtils.setupDatePicker(this, edtNamHocEdit);
        txtTitle.setText("Chỉnh sửa lớp học");
        btnUpdate.setText("Cập nhật");

        edtMaLopEdit.setText(maLop);
        edtTenLopEdit.setText(tenLop);

        ArrayAdapter<Branch> dialogBranchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, branchList);
        dialogBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnKhoaEdit.setAdapter(dialogBranchAdapter);

        new Handler().postDelayed(() -> {
            int position = -1;
            for (int i = 0; i < dialogBranchAdapter.getCount(); i++) {
                Branch branch = dialogBranchAdapter.getItem(i);
                if (branch.getTenKhoa().trim().equalsIgnoreCase(khoa.trim())) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                spnKhoaEdit.setSelection(position);
            }
        }, 500);

        ArrayAdapter<Teacher> dialogTeacherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, teacherList);
        dialogTeacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGiangVienEdit.setAdapter(dialogTeacherAdapter);

        new Handler().postDelayed(() -> {
            int position = -1;
            for (int i = 0; i < dialogTeacherAdapter.getCount(); i++) {
                Teacher teacher = dialogTeacherAdapter.getItem(i);
                if (teacher.getTenGV().trim().equalsIgnoreCase(giangVien.trim()) || teacher.getMaGV().trim().equalsIgnoreCase(giangVien.trim())) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                spnGiangVienEdit.setSelection(position);
            }
        }, 500);
        edtNamHocEdit.setText(namHoc);

        btnUpdate.setOnClickListener(v -> {
            String updatedMaLop = edtMaLopEdit.getText().toString();
            String updatedTenLop = edtTenLopEdit.getText().toString();
            Branch selectedBranch = (Branch) spnKhoaEdit.getSelectedItem();
            Teacher selectedTeacher = (Teacher) spnGiangVienEdit.getSelectedItem();
            String updatedNamHoc = edtNamHocEdit.getText().toString();

            if (updatedMaLop.isEmpty() || updatedTenLop.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Class updateClass = new Class(
                    id, updatedMaLop, updatedTenLop, selectedBranch, selectedTeacher,
                    updatedNamHoc
            );

            classDatabaseHelper.update(id, updateClass, new DatabaseHelper.DatabaseActionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DetailClassActivity.this, "Cập nhật lớp học thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    maLop = updatedMaLop;
                    tenLop = updatedTenLop;
                    khoa = selectedBranch.getTenKhoa();
                    giangVien = selectedTeacher.getTenGV();
                    namHoc = updatedNamHoc;

                    txtMaLop.setText(maLop);
                    txtTenLop.setText(tenLop);
                    txtKhoa.setText(khoa);
                    txtGiangVien.setText(giangVien);
                    txtNamHoc.setText(namHoc);
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(DetailClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());

    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn xóa lớp học?")
                .setPositiveButton("Có", (dialog, which) -> {
                    classDatabaseHelper.delete(id, new DatabaseHelper.DatabaseActionCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(DetailClassActivity.this, "Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(DetailClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
