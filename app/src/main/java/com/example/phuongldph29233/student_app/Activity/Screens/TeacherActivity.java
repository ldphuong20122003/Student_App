package com.example.phuongldph29233.student_app.Activity.Screens;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Activity.LoginActivity;
import com.example.phuongldph29233.student_app.Adapter.TeacherAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.Validator.Validator;
import com.example.phuongldph29233.student_app.databinding.ActivityTeacherBinding;
import com.example.phuongldph29233.student_app.databinding.ActivityTeacherDetailBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TeacherActivity extends AppCompatActivity {
    private ActivityTeacherBinding listBinding;
    private ActivityTeacherDetailBinding detailBinding;
    private DatabaseHelper<Teacher> teacherDatabaseHelper;
    private DatabaseHelper<Branch> branchDatabaseHelper;
    private SessionManager sessionManager;
    private TeacherAdapter teacherAdapter;
    private ArrayList<Teacher> teacherArrayList;
    private ArrayList<Teacher> originalArrayList;
    private ArrayList<Branch> branchArrayList;
    private ArrayAdapter<Branch> branchAdapter;
    private BranchController branchController;

    private String username;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        if (username == null) username = sessionManager.getUsername();
        if (role == null) role = sessionManager.getRole();
        if (username == null || role == null) {
            Toast.makeText(this, "Lỗi: Thiếu thông tin người dùng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switch (role) {
            case "admin":
                listBinding = ActivityTeacherBinding.inflate(getLayoutInflater());
                setContentView(listBinding.getRoot());
                branchController = new BranchController();
                break;
            case "teacher":
                detailBinding = ActivityTeacherDetailBinding.inflate(getLayoutInflater());
                setContentView(detailBinding.getRoot());
                break;
            case "student":
                Toast.makeText(this, "Sinh viên không có quyền truy cập!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            default:
                Toast.makeText(this, "Vai trò không hợp lệ!", Toast.LENGTH_SHORT).show();
                finish();
                return;
        }

        try {
            teacherDatabaseHelper = new DatabaseHelper<>("Teacher");
            branchDatabaseHelper = new DatabaseHelper<>("Branches");
        } catch (Exception e) {
            Log.e("TeacherActivity", "Lỗi khởi tạo DatabaseHelper: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupViews();
        loadData();
    }

    private void setupViews() {
        switch (role) {
            case "admin":
                listBinding.btnBack.setOnClickListener(v -> finish());
                listBinding.btnAdd.setVisibility(View.VISIBLE);
                listBinding.btnAdd.setOnClickListener(v -> showDialogAdd());
                setupRecyclerViewAndSearch();
                branchArrayList = new ArrayList<>();
                branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchArrayList);
                branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                loadDataBranch();
                break;
            case "teacher":
                detailBinding.btnBack.setOnClickListener(v -> finish());
                detailBinding.btnLogout.setOnClickListener(v -> showLogoutDialog());
                break;
        }
    }

    private void setupRecyclerViewAndSearch() {
        teacherArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(teacherArrayList);
        listBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listBinding.recyclerView.setAdapter(teacherAdapter);
        listBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchTeacher(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadData() {
        switch (role) {
            case "admin":
                loadTeacherDataForAdmin();
                break;
            case "teacher":
                loadTeacherDataForTeacher();
                break;
        }
    }

    private void loadTeacherDataForAdmin() {
        listBinding.progressBar.setVisibility(View.VISIBLE);
        listBinding.txtNoData.setVisibility(View.GONE);
        teacherDatabaseHelper.getList(Teacher.class, new DatabaseHelper.DatabaseCallback<Teacher>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Teacher> list) {
                runOnUiThread(() -> {
                    listBinding.progressBar.setVisibility(View.GONE);
                    teacherArrayList.clear();
                    originalArrayList.clear();
                    teacherArrayList.addAll(list);
                    originalArrayList.addAll(list);
                    teacherAdapter.notifyDataSetChanged();
                    listBinding.recyclerView.setVisibility(teacherArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                    listBinding.txtNoData.setVisibility(teacherArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                    if (teacherArrayList.isEmpty()) {
                        listBinding.txtNoData.setText("Không có giảng viên nào trong hệ thống!");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    listBinding.progressBar.setVisibility(View.GONE);
                    listBinding.recyclerView.setVisibility(View.GONE);
                    listBinding.txtNoData.setVisibility(View.VISIBLE);
                    listBinding.txtNoData.setText("Lỗi tải dữ liệu: " + error);
                    Toast.makeText(TeacherActivity.this, "Lỗi tải dữ liệu giảng viên: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadTeacherDataForTeacher() {
        detailBinding.progressBar.setVisibility(View.VISIBLE);
        detailBinding.txtNoData.setVisibility(View.GONE);
        teacherDatabaseHelper.getList(Teacher.class, new DatabaseHelper.DatabaseCallback<Teacher>() {
            @Override
            public void onSuccess(List<Teacher> teacherList) {
                Teacher currentTeacher = null;
                for (Teacher teacher : teacherList) {
                    if (teacher.getTeacherID() != null && teacher.getTeacherID().equals(username)) {
                        currentTeacher = teacher;
                        break;
                    }
                }
                final Teacher finalCurrentTeacher = currentTeacher;
                runOnUiThread(() -> {
                    detailBinding.progressBar.setVisibility(View.GONE);
                    if (finalCurrentTeacher != null && finalCurrentTeacher.getTeacherID() != null) {
                        detailBinding.txtTeacherID.setText("Mã giảng viên: " + (finalCurrentTeacher.getTeacherID() != null ? finalCurrentTeacher.getTeacherID() : "N/A"));
                        detailBinding.txtTeacherName.setText("Tên giảng viên: " + (finalCurrentTeacher.getTeacherName() != null ? finalCurrentTeacher.getTeacherName() : "N/A"));
                        detailBinding.txtTeacherEmail.setText("Email: " + (finalCurrentTeacher.getTeacherEmail() != null ? finalCurrentTeacher.getTeacherEmail() : "N/A"));
                        detailBinding.txtTeacherPhone.setText("Số điện thoại: " + (finalCurrentTeacher.getTeacherPhone() != null ? finalCurrentTeacher.getTeacherPhone() : "N/A"));
                        detailBinding.txtTeacherBranch.setText("Chuyên ngành: " + (finalCurrentTeacher.getTeacherBranch() != null && finalCurrentTeacher.getTeacherBranch().getBranchName() != null ? finalCurrentTeacher.getTeacherBranch().getBranchName() : "N/A"));
                        detailBinding.teacherDetailLayout.setVisibility(View.VISIBLE);
                        detailBinding.txtNoData.setVisibility(View.GONE);
                    } else {
                        detailBinding.teacherDetailLayout.setVisibility(View.GONE);
                        detailBinding.txtNoData.setVisibility(View.VISIBLE);
                        detailBinding.txtNoData.setText("Không tìm thấy thông tin giảng viên!");
                        Toast.makeText(TeacherActivity.this, "Không tìm thấy thông tin giảng viên!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    detailBinding.progressBar.setVisibility(View.GONE);
                    detailBinding.teacherDetailLayout.setVisibility(View.GONE);
                    detailBinding.txtNoData.setVisibility(View.VISIBLE);
                    detailBinding.txtNoData.setText("Lỗi tải dữ liệu: " + error);
                    Toast.makeText(TeacherActivity.this, "Lỗi tải dữ liệu giảng viên: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Có", (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(TeacherActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_teacher);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView txtTitle = dialog.findViewById(R.id.txt_title_teacher);
        EditText edtTeacherID = dialog.findViewById(R.id.edt_teacherID_add);
        EditText edtTeacherName = dialog.findViewById(R.id.edt_teacherName_add);
        EditText edtTeacherEmail = dialog.findViewById(R.id.edt_teacherEmail_add);
        EditText edtTeacherPhone = dialog.findViewById(R.id.edt_teacherPhone_add);
        Spinner spnTeacherBranch = dialog.findViewById(R.id.spn_teacherBranch);
        Button btnAdd = dialog.findViewById(R.id.btn_add_teacher);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_teacher);

        txtTitle.setText("Thêm giảng viên");
        spnTeacherBranch.setAdapter(branchAdapter);

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String teacherID = edtTeacherID.getText().toString().trim();
            String teacherName = edtTeacherName.getText().toString().trim();
            String teacherEmail = edtTeacherEmail.getText().toString().trim();
            String teacherPhone = edtTeacherPhone.getText().toString().trim();
            Branch selectedBranch = (Branch) spnTeacherBranch.getSelectedItem();

            if (teacherID.isEmpty() || teacherName.isEmpty() || teacherEmail.isEmpty() || teacherPhone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Validator.isValidEmail(teacherEmail)) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Validator.isValidPhone(teacherPhone)) {
                Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedBranch == null || selectedBranch.getBranchID() == null) {
                Toast.makeText(this, "Vui lòng chọn chuyên ngành hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            Teacher teacher = new Teacher(id, teacherID, teacherName, teacherEmail, teacherPhone, selectedBranch);
            btnAdd.setEnabled(false);
            addTeacher(teacher, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addTeacher(Teacher teacher, Dialog dialog) {
        teacherDatabaseHelper.add(teacher, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TeacherActivity.this, "Thêm giảng viên thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadTeacherDataForAdmin();
                Intent intent = new Intent("ACTION_DATA_UPDATED_LOCAL");
                LocalBroadcastManager.getInstance(TeacherActivity.this).sendBroadcast(intent);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(TeacherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataBranch() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchArrayList.clear();
                branchArrayList.addAll(list);
                branchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(TeacherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchTeacher(String text) {
        ArrayList<Teacher> filteredList = new ArrayList<>();
        for (Teacher teacher : originalArrayList) {
            if (teacher.getTeacherName().toLowerCase().contains(text.toLowerCase()) ||
                    teacher.getTeacherID().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(teacher);
            }
        }
        teacherAdapter.searchTeacher(filteredList);
    }

    private final BroadcastReceiver localUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDataImmediately();
        }
    };

    private final BroadcastReceiver globalUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_DATA_UPDATED".equals(intent.getAction())) {
                refreshDataImmediately();
            }
        }
    };

    private void refreshDataImmediately() {
        runOnUiThread(() -> loadData());
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("ACTION_DATA_UPDATED");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(globalUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(globalUpdateReceiver, filter);
        }
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(localUpdateReceiver, new IntentFilter("ACTION_DATA_UPDATED_LOCAL"));
        refreshDataImmediately();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(globalUpdateReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localUpdateReceiver);
        } catch (Exception e) {
            Log.e("TeacherActivity", "Failed to unregister receiver: " + e.getMessage());
        }
    }
}