package com.example.phuongldph29233.student_app.Activity.Detail;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.phuongldph29233.student_app.Activity.Screens.StudentActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.StudentClassesActivity;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DetailStudentActivity extends AppCompatActivity {
    private TextView txtMaSV, txtTenSV, txtNgaySinh, txtQueQuan, txtSdt, txtEmail, txtLopHoc, txtNgayNhapHoc, txtHeDaoTao, txtChuyenNganh;
    private ImageView btn_back_student;
    private ImageButton btn_list_student, btn_delete_student, btn_edit_student;
    private Object classObj;
    private List<Class> studentClasses = new ArrayList<>();
    private String id, maSV, tenSV, ngaySinh, queQuan, sdt, email, lopHoc, ngayNhapHoc, heDaoTao, chuyenNganh;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Branch> branchDatabaseHelper;
    private DatabaseHelper<Class> classDatabaseHelper;
    private ArrayList<Branch> branchList;
    private ArrayList<Class> classList;
    private boolean isVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_student);
        studentDatabaseHelper = new DatabaseHelper<>("Student");
        branchDatabaseHelper = new DatabaseHelper<>("Branch");
        classDatabaseHelper = new DatabaseHelper<>("Classes");
        initViews();
        getIntentExtra();
        fetchStudentClassFromDatabase();
        classList = new ArrayList<>();
        loadClass();
        branchList = new ArrayList<>();
        loadBranches();
        setupButtonListeners();
    }

    private void fetchStudentClassFromDatabase() {
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> allClasses) {
                Set<String> uniqueClassIds = new HashSet<>();
                List<Class> enrolledClasses = new ArrayList<>();

                for (Class lop : allClasses) {
                    if (lop.getDanhSachSinhVien() != null) {
                        for (Student sv : lop.getDanhSachSinhVien()) {
                            if (sv != null && id.equals(sv.getId())) {
                                if (!uniqueClassIds.contains(lop.getId())) {
                                    uniqueClassIds.add(lop.getId());
                                    enrolledClasses.add(lop);
                                }
                                break;
                            }
                        }
                    }
                }

                runOnUiThread(() -> {
                    studentClasses = enrolledClasses;
                    if (enrolledClasses.isEmpty()) {
                        if (classObj == null) {
                            txtLopHoc.setText("Chưa có lớp học");
                        }
                    } else if (enrolledClasses.size() == 1) {
                        txtLopHoc.setText(enrolledClasses.get(0).getTenLop());
                    } else {
                        txtLopHoc.setText(enrolledClasses.get(0).getTenLop() + " (+" + (enrolledClasses.size() - 1) + ")");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(DetailStudentActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
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
        btn_edit_student = findViewById(R.id.btn_edit_student);
        btn_delete_student = findViewById(R.id.btn_delete_student);
        btn_back_student = findViewById(R.id.btn_back_student);
        btn_list_student = findViewById(R.id.btn_list_student);
        txtLopHoc.setOnClickListener(v -> {
            if (studentClasses.size() > 1) {
                openStudentClassesActivity();
            } else if (studentClasses.size() == 1) {
                showSingleClassDetail(studentClasses.get(0));
            }
        });
    }

    private void showSingleClassDetail(Class lop) {
        new AlertDialog.Builder(this)
                .setTitle(lop.getTenLop())
                .setMessage("Mã lớp: " + lop.getMaLop() + "\n" +
                        "Giảng viên: " + (lop.getGiangVien() != null ? lop.getGiangVien().getTeacherName() : "N/A"))
                .setPositiveButton("OK", null)
                .show();
    }

    private void openStudentClassesActivity() {
        Intent intent = new Intent(this, StudentClassesActivity.class);
        intent.putExtra("studentId", id);
        intent.putExtra("studentName", tenSV);
        if (studentClasses != null && !studentClasses.isEmpty()) {
            ArrayList<String> classIds = new ArrayList<>();
            for (Class c : studentClasses) {
                if (c != null && c.getId() != null) {
                    classIds.add(c.getId());
                }
            }
            intent.putStringArrayListExtra("classIds", classIds);
        } else {
            intent.putStringArrayListExtra("classIds", new ArrayList<>());
        }
        startActivity(intent);
    }

    private void getIntentExtra() {
        id = (String) getIntent().getSerializableExtra("id");
        maSV = (String) getIntent().getSerializableExtra("studentID");
        tenSV = (String) getIntent().getSerializableExtra("studentName");
        ngaySinh = (String) getIntent().getSerializableExtra("studentBirthday");
        queQuan = (String) getIntent().getSerializableExtra("studentHomeTown");
        sdt = (String) getIntent().getSerializableExtra("studentPhone");
        email = (String) getIntent().getSerializableExtra("studentEmail");
        classObj = getIntent().getSerializableExtra("studentClass");

        if (classObj instanceof Class) {
            Class c = (Class) classObj;
            if (c.getId() != null && c.getTenLop() != null) {
                lopHoc = c.getTenLop();
                Log.d("DetailStudent", "Valid Class: " + c.getTenLop());
            } else {
                lopHoc = "Lớp không hợp lệ";
                Log.e("DetailStudent", "Class missing required fields");
            }
        } else {
            lopHoc = "Chưa có lớp học";
            Log.e("DetailStudent", "Invalid Class type: " +
                    (classObj != null ? classObj.getClass().getName() : "null"));
        }


        ngayNhapHoc = (String) getIntent().getSerializableExtra("studentDateJoin");
        chuyenNganh = (String) getIntent().getSerializableExtra("studentBranch");
        heDaoTao = (String) getIntent().getSerializableExtra("studentTOT");
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
        fetchStudentClassFromDatabase();
    }

    private void setupButtonListeners() {
        btn_back_student.setOnClickListener(v -> finish());
        btn_list_student.setOnClickListener(v -> {
            isVisible = !isVisible;
            if (isVisible) {
                btn_edit_student.setVisibility(View.VISIBLE);
                btn_delete_student.setVisibility(View.VISIBLE);
            } else {
                btn_edit_student.setVisibility(View.GONE);
                btn_delete_student.setVisibility(View.GONE);
            }
        });
        btn_edit_student.setOnClickListener(v -> showEditDialog());
        btn_delete_student.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void loadClass() {
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> itemList) {
                classList.clear();
                classList.addAll(itemList);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailStudentActivity.this, "Lỗi tải : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_DATA_UPDATED".equals(intent.getAction())) {
                String updateType = intent.getStringExtra("UPDATE_TYPE");
                if ("STUDENT_CLASS_UPDATE".equals(updateType)) {
                    fetchStudentClassFromDatabase();
                    loadClass();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("ACTION_DATA_UPDATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(dataUpdateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(dataUpdateReceiver);
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
        EditText edtNgayNhapHocEdit = dialog.findViewById(R.id.edt_ngayNhaphoc_add);
        EditText edtHeDaoTaoEdit = dialog.findViewById(R.id.edt_heDaotao_add);
        Spinner spnChuyenNganhEdit = dialog.findViewById(R.id.spn_chuyenNganh);
        Button btnUpdate = dialog.findViewById(R.id.btn_add_sv);
        Button btnCancel = dialog.findViewById(R.id.btn_huy_sv);
        HelperUtils.setupDatePicker(this, edtNgaySinhEdit);
        HelperUtils.setupDatePicker(this, edtNgayNhapHocEdit);
        txtTitle.setText("Chỉnh sửa sinh viên");
        btnUpdate.setText("Cập nhật");
        edtMaSVEdit.setText(maSV);
        edtTenSVEdit.setText(tenSV);
        edtNgaySinhEdit.setText(ngaySinh);
        edtQueQuanEdit.setText(queQuan);
        edtSdtEdit.setText(sdt);
        edtEmailEdit.setText(email);
        edtNgayNhapHocEdit.setText(ngayNhapHoc);
        edtHeDaoTaoEdit.setText(heDaoTao);
        ArrayAdapter<Class> dialogClassAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, classList);
        dialogClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        ArrayAdapter<Branch> dialogBranchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, branchList);
        dialogBranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnChuyenNganhEdit.setAdapter(dialogBranchAdapter);

        new Handler().postDelayed(() -> {
            int position = -1;
            for (int i = 0; i < dialogBranchAdapter.getCount(); i++) {
                Branch branch = dialogBranchAdapter.getItem(i);
                if (branch.getBranchName().trim().equalsIgnoreCase(chuyenNganh.trim())) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                spnChuyenNganhEdit.setSelection(position);
            }
        }, 250);

        btnUpdate.setOnClickListener(v -> {
            String updatedMaSV = edtMaSVEdit.getText().toString();
            String updatedTenSV = edtTenSVEdit.getText().toString();
            String updatedNgaySinh = edtNgaySinhEdit.getText().toString();
            String updatedQueQuan = edtQueQuanEdit.getText().toString();
            String updatedSdt = edtSdtEdit.getText().toString();
            String updatedEmail = edtEmailEdit.getText().toString();
            String updatedNgayNhapHoc = edtNgayNhapHocEdit.getText().toString();
            String updatedHeDaoTao = edtHeDaoTaoEdit.getText().toString();
            Branch selectedBranch = (Branch) spnChuyenNganhEdit.getSelectedItem();

            if (updatedMaSV.isEmpty() || updatedTenSV.isEmpty() || updatedNgaySinh.isEmpty() ||
                    updatedQueQuan.isEmpty() || updatedSdt.isEmpty() || updatedEmail.isEmpty() ||
                    updatedNgayNhapHoc.isEmpty() || updatedHeDaoTao.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedBranch == null || selectedBranch.getBranchName() == null || selectedBranch.getBranchID().trim().isEmpty()) {
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
                    updatedSdt, updatedEmail, updatedNgayNhapHoc,
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
                    ngayNhapHoc = updatedNgayNhapHoc;
                    heDaoTao = updatedHeDaoTao;
                    chuyenNganh = selectedBranch.getBranchName();
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