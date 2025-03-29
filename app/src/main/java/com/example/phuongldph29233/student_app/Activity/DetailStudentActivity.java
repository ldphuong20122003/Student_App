package com.example.phuongldph29233.student_app.Activity;

import static com.example.phuongldph29233.student_app.R.*;

import android.app.DatePickerDialog;
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
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class DetailStudentActivity extends AppCompatActivity {
    private TextView txtMaSV, txtTenSV, txtNgaySinh, txtQueQuan, txtSdt, txtEmail, txtLopHoc, txtNgayNhapHoc, txtHeDaoTao, txtChuyenNganh;
    private Button btnEdit, btnDelete, btnBack;

    private String id, maSV, tenSV, ngaySinh, queQuan, sdt, email, lopHoc, ngayNhapHoc, heDaoTao, chuyenNganh;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Branch> branchDatabaseHelper;
    private ArrayList<Branch> branchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        studentDatabaseHelper = new DatabaseHelper<>("Student");
        branchDatabaseHelper = new DatabaseHelper<>("Branch");
        initViews();
        getIntentExtra();
        branchList = new ArrayList<>();
        loadBranches();
        setupButtonListeners();
    }

    private void initViews() {
        txtMaSV = findViewById(R.id.txt_maSV_detail);
        txtTenSV = findViewById(R.id.txt_tenSV_detail);
        txtNgaySinh = findViewById(R.id.txt_ngaySinh_detail);
        txtQueQuan = findViewById(R.id.txt_queQuan_detail);
        txtSdt = findViewById(R.id.txt_sdt_detail);
        txtEmail = findViewById(R.id.txt_email_detail);
        txtLopHoc = findViewById(R.id.txt_lopHoc_detail);
        txtNgayNhapHoc = findViewById(R.id.txt_ngayNhaphoc_detail);
        txtHeDaoTao = findViewById(R.id.txt_heDaotao_detail);
        txtChuyenNganh = findViewById(R.id.txt_chuyenNganh_detail);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);
        btnBack = findViewById(R.id.btn_back);
    }

    private void getIntentExtra() {
        id = (String) getIntent().getSerializableExtra("id");
        maSV = (String) getIntent().getSerializableExtra("maSV");
        tenSV = (String) getIntent().getSerializableExtra("tenSV");
        ngaySinh = (String) getIntent().getSerializableExtra("ngaySinh");
        queQuan = (String) getIntent().getSerializableExtra("queQuan");
        sdt = (String) getIntent().getSerializableExtra("soDienThoai");
        email = (String) getIntent().getSerializableExtra("email");
        lopHoc = (String) getIntent().getSerializableExtra("lopHoc");
        ngayNhapHoc = (String) getIntent().getSerializableExtra("ngayNhapHoc");
        chuyenNganh = (String) getIntent().getSerializableExtra("chuyenNganh");
        heDaoTao = (String) getIntent().getSerializableExtra("heDaoTao");

        txtMaSV.setText(maSV);
        txtTenSV.setText(tenSV);
        txtNgaySinh.setText(ngaySinh);
        txtQueQuan.setText(queQuan);
        txtSdt.setText(sdt);
        txtEmail.setText(email);
        txtLopHoc.setText(lopHoc);
        txtNgayNhapHoc.setText(ngayNhapHoc);
        txtHeDaoTao.setText(heDaoTao);
        txtChuyenNganh.setText(chuyenNganh);
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
        HelperUtils.setupDatePicker(this,edtNgaySinhEdit);
        HelperUtils.setupDatePicker(this,edtNgayNhapHocEdit);
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
            if (selectedBranch == null || selectedBranch.getTenKhoa() == null || selectedBranch.getMaKhoa().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn chuyên ngành hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!HelperUtils.isValidPhoneNumber(updatedSdt)) {
                edtSdtEdit.setError("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
                return;
            }
            if (!HelperUtils.isValidEmail(email)) {
                edtEmailEdit.setError("Email không hợp lệ");
                return;
            }
            Student updatedStudent = new Student(
                    id, updatedMaSV, updatedTenSV, updatedNgaySinh, updatedQueQuan,
                    updatedSdt, updatedEmail, updatedLopHoc, updatedNgayNhapHoc,
                    selectedBranch, updatedHeDaoTao
            );

            studentDatabaseHelper.update(id, updatedStudent, new DatabaseHelper.DatabaseActionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(DetailStudentActivity.this, "Cập nhật sinh viên thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    maSV = updatedMaSV;
                    tenSV = updatedTenSV;
                    ngaySinh = updatedNgaySinh;
                    queQuan = updatedQueQuan;
                    sdt = updatedSdt;
                    email = updatedEmail;
                    lopHoc = updatedLopHoc;
                    ngayNhapHoc = updatedNgayNhapHoc;
                    heDaoTao = updatedHeDaoTao;
                    chuyenNganh = selectedBranch.getTenKhoa();
                    txtMaSV.setText(maSV);
                    txtTenSV.setText(tenSV);
                    txtNgaySinh.setText(ngaySinh);
                    txtQueQuan.setText(queQuan);
                    txtSdt.setText(sdt);
                    txtEmail.setText(email);
                    txtLopHoc.setText(lopHoc);
                    txtNgayNhapHoc.setText(ngayNhapHoc);
                    txtHeDaoTao.setText(heDaoTao);
                    txtChuyenNganh.setText(chuyenNganh);
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