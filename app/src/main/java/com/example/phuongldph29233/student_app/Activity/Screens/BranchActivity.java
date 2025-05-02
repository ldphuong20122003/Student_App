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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Activity.LoginActivity;
import com.example.phuongldph29233.student_app.Adapter.BranchAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityBranchBinding;
import com.example.phuongldph29233.student_app.databinding.ActivityBranchDetailBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class BranchActivity extends AppCompatActivity {
    private ActivityBranchBinding listBinding;
    private ActivityBranchDetailBinding detailBinding;
    private DatabaseHelper<Branch> branchDatabaseHelper;
    private DatabaseHelper<Class> classDatabaseHelper;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private SessionManager sessionManager;
    private BranchAdapter branchAdapter;
    private ArrayList<Branch> branchList;
    private ArrayList<Branch> originalBranchList;
    private String username;
    private String role;
    private BranchController branchController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        branchController = new BranchController();
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
                listBinding = ActivityBranchBinding.inflate(getLayoutInflater());
                setContentView(listBinding.getRoot());
                break;
            case "teacher":
            case "student":
                detailBinding = ActivityBranchDetailBinding.inflate(getLayoutInflater());
                setContentView(detailBinding.getRoot());
                break;
            default:
                Toast.makeText(this, "Vai trò không hợp lệ!", Toast.LENGTH_SHORT).show();
                finish();
                return;
        }

        try {
            branchDatabaseHelper = new DatabaseHelper<>("Branches");
            classDatabaseHelper = new DatabaseHelper<>("Classes");
            studentDatabaseHelper = new DatabaseHelper<>("Student");
        } catch (Exception e) {
            Log.e("BranchActivity", "Lỗi khởi tạo DatabaseHelper: " + e.getMessage());
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
                break;
            case "teacher":
                detailBinding.btnBack.setOnClickListener(v -> finish());
                break;
            case "student":
                detailBinding.btnBack.setOnClickListener(v -> finish());
                break;
        }
    }

    private void setupRecyclerViewAndSearch() {
        branchList = new ArrayList<>();
        originalBranchList = new ArrayList<>();
        branchAdapter = new BranchAdapter(branchList);
        listBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listBinding.recyclerView.setAdapter(branchAdapter);
        listBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBranches(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadData() {
        switch (role) {
            case "admin":
                loadAllBranches();
                break;
            case "teacher":
                loadTeacherBranch();
                break;
            case "student":
                loadStudentBranch();
                break;
        }
    }

    private void loadAllBranches() {
        listBinding.progressBar.setVisibility(View.VISIBLE);
        listBinding.txtNoData.setVisibility(View.GONE);

        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchList.clear();
                originalBranchList.clear();
                branchList.addAll(list);
                originalBranchList.addAll(list);
                branchAdapter.notifyDataSetChanged();

                if (branchList.isEmpty()) {
                    listBinding.txtNoData.setVisibility(View.VISIBLE);
                    listBinding.recyclerView.setVisibility(View.GONE);
                } else {
                    listBinding.recyclerView.setVisibility(View.VISIBLE);
                    listBinding.txtNoData.setVisibility(View.GONE);
                }

                listBinding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                listBinding.progressBar.setVisibility(View.GONE);
                listBinding.txtNoData.setVisibility(View.VISIBLE);
                listBinding.recyclerView.setVisibility(View.GONE);
                Toast.makeText(BranchActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeacherBranch() {
        detailBinding.txtNoData.setVisibility(View.GONE);
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> classList) {
                Branch selectedBranch = null;
                for (Class cls : classList) {
                    if (cls != null && cls.getGiangVien() != null && cls.getGiangVien().getTeacherID() != null
                            && cls.getGiangVien().getTeacherID().trim().equals(username.trim())) {
                        selectedBranch = cls.getKhoa();
                        break;
                    }
                }
                final Branch finalSelectedBranch = selectedBranch;
                runOnUiThread(() -> {
                    if (finalSelectedBranch != null && finalSelectedBranch.getBranchID() != null) {
                        detailBinding.txtBranchID.setText("Mã khoa: " + finalSelectedBranch.getBranchID());
                        detailBinding.txtBranchName.setText("Tên khoa: " + finalSelectedBranch.getBranchName());
                        detailBinding.txtNoData.setVisibility(View.GONE);
                    } else {
                        detailBinding.txtNoData.setVisibility(View.VISIBLE);
                        detailBinding.txtNoData.setText("Bạn chưa được gán lớp học nào thuộc khoa nào!");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    detailBinding.txtNoData.setVisibility(View.VISIBLE);
                    detailBinding.txtNoData.setText("Lỗi tải dữ liệu: " + error);
                });
            }
        });
    }

    private void loadStudentBranch() {
        detailBinding.txtNoData.setVisibility(View.GONE);
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> studentList) {
                Branch selectedBranch = null;
                for (Student student : studentList) {
                    if (student != null && student.getStudentID() != null
                            && student.getStudentID().trim().equals(username.trim())) {
                        selectedBranch = student.getStudentBranch();
                        break;
                    }
                }
                final Branch finalSelectedBranch = selectedBranch;
                runOnUiThread(() -> {
                    if (finalSelectedBranch != null && finalSelectedBranch.getBranchID() != null) {
                        detailBinding.txtBranchID.setText("Mã khoa: " + finalSelectedBranch.getBranchID());
                        detailBinding.txtBranchName.setText("Tên khoa: " + finalSelectedBranch.getBranchName());
                        detailBinding.txtNoData.setVisibility(View.GONE);
                    } else {
                        detailBinding.txtNoData.setVisibility(View.VISIBLE);
                        detailBinding.txtNoData.setText("Bạn chưa được gán vào khoa nào!");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    detailBinding.txtNoData.setVisibility(View.VISIBLE);
                    detailBinding.txtNoData.setText("Lỗi tải dữ liệu: " + error);
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
                    Intent intent = new Intent(BranchActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_branch);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtBranchID = dialog.findViewById(R.id.edt_branchID_add);
        EditText edtBranchName = dialog.findViewById(R.id.edt_branchName_add);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel_branch);
        Button btnAdd = dialog.findViewById(R.id.btn_add_branch);

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String branchID = edtBranchID.getText().toString().trim();
            String branchName = edtBranchName.getText().toString().trim();

            if (branchID.isEmpty() || branchName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Branch branch = new Branch(id, branchID, branchName);
            addBranch(branch, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addBranch(Branch branch, Dialog dialog) {
        branchDatabaseHelper.add(branch, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(BranchActivity.this, "Thêm khoa thành công!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadAllBranches();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(BranchActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchBranches(String text) {
        ArrayList<Branch> filteredList = new ArrayList<>();
        for (Branch branch : originalBranchList) {
            if (branch.getBranchName().toLowerCase().contains(text.toLowerCase()) ||
                    branch.getBranchID().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(branch);
            }
        }
        branchAdapter.updateList(filteredList);
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
            Log.e("BranchActivity", "Failed to unregister receiver: " + e.getMessage());
        }
    }
}