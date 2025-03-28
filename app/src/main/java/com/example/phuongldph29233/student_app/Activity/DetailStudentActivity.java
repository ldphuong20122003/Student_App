package com.example.phuongldph29233.student_app.Activity;

import static com.example.phuongldph29233.student_app.R.*;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailStudentActivity extends AppCompatActivity {
    private EditText edtMaSV, edtTenSV, edtNgaySinh, edtQueQuan, edtSdt, edtEmail, edtLopHoc, edtNgayNhapHoc, edtHeDaoTao;
    private Spinner spnChuyenNganh;
    private Button btnEdit, btnDelete, btnBack;

    private String id, maSV, tenSV, ngaySinh, queQuan, sdt, email, lopHoc, ngayNhapHoc, heDaoTao, chuyenNganh;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Branch> branchDatabaseHelper;
    private ArrayAdapter<Branch> branchAdapter;
    private ArrayList<Branch> branchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        studentDatabaseHelper = new DatabaseHelper<>("Classes");
        branchDatabaseHelper = new DatabaseHelper<>("Branch");
        initViews();
        getIntentExtra();
        branchList = new ArrayList<>();
        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChuyenNganh.setAdapter(branchAdapter);
        loadBranches();
        setupButtonListeners();
    }

    private void initViews() {
        edtMaSV = findViewById(R.id.edt_maSV_detail);
        edtTenSV = findViewById(R.id.edt_tenSV_detail);
        edtNgaySinh = findViewById(R.id.edt_ngaySinh_detail);
        edtQueQuan = findViewById(R.id.edt_queQuan_detail);
        edtSdt = findViewById(R.id.edt_sdt_detail);
        edtEmail = findViewById(R.id.edt_email_detail);
        edtLopHoc = findViewById(R.id.edt_lopHoc_detail);
        edtNgayNhapHoc = findViewById(R.id.edt_ngayNhaphoc_detail);
        edtHeDaoTao = findViewById(R.id.edt_heDaotao_detail);
        spnChuyenNganh = findViewById(R.id.spn_chuyenNganh_detail);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);
    }

    private void getIntentExtra() {
        id =(String) getIntent().getSerializableExtra("id");
        maSV = (String)getIntent().getSerializableExtra("maSV");
        tenSV = (String)getIntent().getSerializableExtra("tenSV");
        ngaySinh = (String)getIntent().getSerializableExtra("ngaySinh");
        queQuan = (String)getIntent().getSerializableExtra("queQuan");
        sdt = (String)getIntent().getSerializableExtra("soDienThoai");
        email = (String)getIntent().getSerializableExtra("email");
        lopHoc = (String)getIntent().getSerializableExtra("lopHoc");
        ngayNhapHoc = (String)getIntent().getSerializableExtra("ngayNhapHoc");
        chuyenNganh = (String)getIntent().getSerializableExtra("chuyenNganh");
        heDaoTao = (String) getIntent().getSerializableExtra("heDaoTao");

        edtMaSV.setText(maSV);
        edtTenSV.setText(tenSV);
        edtNgaySinh.setText(ngaySinh);
        edtQueQuan.setText(queQuan);
        edtSdt.setText(sdt);
        edtEmail.setText(email);
        edtLopHoc.setText(lopHoc);
        edtNgayNhapHoc.setText(ngayNhapHoc);
        edtHeDaoTao.setText(heDaoTao);
    }

    private void setupButtonListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> showEditDialog());

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadBranches() {
        branchDatabaseHelper.getList(Branch.class, new DatabaseHelper.DatabaseCallback<Branch>() {
            @Override
            public void onSuccess(List<Branch> itemList) {
                branchList.clear();
                branchList.addAll(itemList);
                branchAdapter.notifyDataSetChanged();

                new Handler().postDelayed(() -> {
                    int position = -1;
                    for (int i = 0; i < branchAdapter.getCount(); i++) {
                        Branch branch = branchAdapter.getItem(i);
                        if (branch.getTenKhoa().trim().equalsIgnoreCase(chuyenNganh.trim())) {
                            position = i;
                            break;
                        }
                    }
                    if (position != -1) {
                        spnChuyenNganh.setSelection(position);
                    }
                }, 500);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailStudentActivity.this, "Lỗi tải chuyên ngành: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_student);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.txt_title_subject);
        EditText edtMaSVEdit = dialog.findViewById(R.id.edt_maSV_add);
        EditText edtTenSVEdit = dialog.findViewById(R.id.edt_tenSV_add);
        EditText edtNgaySinhEdit = dialog.findViewById(R.id.edt_ngaySinh_add);
        EditText edtQueQuanEdit = dialog.findViewById(R.id.edt_queQuan_add);
        EditText edtSdtEdit = dialog.findViewById(R.id.edt_sdt_add);
        EditText edtEmailEdit = dialog.findViewById(R.id.edt_email_add);
        EditText edtLopHocEdit = dialog.findViewById(R.id.edt_lopHoc_add);
        EditText edtNgayNhapHocEdit = dialog.findViewById(R.id.edt_ngayNhaphoc_add);
        EditText edtHeDaoTaoEdit = dialog.findViewById(R.id.edt_heDaotao_add);
        Spinner spnChuyenNganhEdit = dialog.findViewById(R.id.spn_chuyenNganh);
        Button btnUpdate = dialog.findViewById(R.id.btn_add_sv);
        Button btnCancel = dialog.findViewById(R.id.btn_huy_sv);

        txtTitle.setText("Chỉnh sửa sinh viên");
        btnUpdate.setText("Cập nhật");

        edtMaSVEdit.setText(maSV);
        edtTenSVEdit.setText(tenSV);
        edtNgaySinhEdit.setText(ngaySinh);
        edtQueQuanEdit.setText(queQuan);
        edtSdtEdit.setText(sdt);
        edtEmailEdit.setText(email);
        edtLopHocEdit.setText(lopHoc);
        edtNgayNhapHocEdit.setText(ngayNhapHoc);
        edtHeDaoTaoEdit.setText(heDaoTao);
        ArrayAdapter<Branch> dialogBranchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, branchList);
        dialogBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChuyenNganhEdit.setAdapter(dialogBranchAdapter);
        new Handler().postDelayed(() -> {
            int position = -1;
            for (int i = 0; i < dialogBranchAdapter.getCount(); i++) {
                Branch branch = dialogBranchAdapter.getItem(i);
                if (branch.getTenKhoa().trim().equalsIgnoreCase(chuyenNganh.trim())) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                spnChuyenNganhEdit.setSelection(position);
            }
        }, 500);

        btnUpdate.setOnClickListener(v -> {
            String updatedMaSV = edtMaSVEdit.getText().toString();
            String updatedTenSV = edtTenSVEdit.getText().toString();
            String updatedNgaySinh = edtNgaySinhEdit.getText().toString();
            String updatedQueQuan = edtQueQuanEdit.getText().toString();
            String updatedSdt = edtSdtEdit.getText().toString();
            String updatedEmail = edtEmailEdit.getText().toString();
            String updatedLopHoc = edtLopHocEdit.getText().toString();
            String updatedNgayNhapHoc = edtNgayNhapHocEdit.getText().toString();
            String updatedHeDaoTao = edtHeDaoTaoEdit.getText().toString();
            Branch selectedBranch = (Branch) spnChuyenNganhEdit.getSelectedItem();
            if (updatedMaSV.isEmpty() || updatedTenSV.isEmpty() || updatedNgaySinh.isEmpty() ||
                    updatedQueQuan.isEmpty() || updatedSdt.isEmpty() || updatedEmail.isEmpty() ||
                    updatedLopHoc.isEmpty() || updatedNgayNhapHoc.isEmpty() || updatedHeDaoTao.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Student updatedStudent = new Student(
                    id, updatedMaSV, updatedTenSV, updatedNgaySinh, updatedQueQuan,
                    updatedSdt, updatedEmail, updatedLopHoc, updatedNgayNhapHoc,
                    selectedBranch,updatedHeDaoTao
            );

            studentDatabaseHelper.update(id, updatedStudent, new DatabaseHelper.DatabaseActionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DetailStudentActivity.this, "Cập nhật sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(DetailStudentActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Bạn có chắc chắn muốn xóa sinh viên này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    studentDatabaseHelper.delete(id, new DatabaseHelper.DatabaseActionCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(DetailStudentActivity.this, "Xóa sinh viên thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(DetailStudentActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                .show();
    }
}