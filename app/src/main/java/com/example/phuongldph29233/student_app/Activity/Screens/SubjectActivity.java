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
import com.example.phuongldph29233.student_app.Adapter.SubjectAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.SubjectController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivitySubjectBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SubjectActivity extends AppCompatActivity {
    ActivitySubjectBinding binding;
    private BranchController branchController;
    private ArrayAdapter<Branch> arrayAdapter;
    private SubjectAdapter subjectAdapter;
    private ArrayList<Subject> subjectArrayList;
    private ArrayList<Subject> originalArrayList;
    private SubjectController subjectController;
    private ArrayList<Branch> branchList;
    private SessionManager sessionManager;
    private DatabaseHelper<Class> classDatabaseHelper;
    private String username;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        branchController = new BranchController();
        subjectController = new SubjectController();
        branchList = new ArrayList<>();
        subjectArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(subjectArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(subjectAdapter);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        try {
            classDatabaseHelper = new DatabaseHelper<>("Classes");
        } catch (Exception e) {
            Log.e("SubjectActivity", "Lỗi khởi tạo DatabaseHelper: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        binding.btnBack.setOnClickListener(v -> finish());
        if ("admin".equals(role)) {
            binding.btnAdd.setVisibility(View.VISIBLE);
            binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        } else {
            binding.btnAdd.setVisibility(View.GONE);
        }
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        loadData();
    }

    private void loadData() {
        switch (role) {
            case "teacher":
                loadTeacherSubjects();
                break;
            case "student":
                loadStudentSubjects();
                break;
            case "admin":
            default:
                loadDataSubject();
                break;
        }
    }

    private void loadDataSubject() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoData.setVisibility(View.GONE);
        subjectController.getSubject(new DatabaseHelper.DatabaseCallback<Subject>() {
            @Override
            public void onSuccess(List<Subject> itemList) {
                subjectArrayList.clear();
                originalArrayList.clear();
                subjectArrayList.addAll(itemList);
                originalArrayList.addAll(itemList);
                subjectAdapter.notifyDataSetChanged();
                if (subjectArrayList.isEmpty()) {
                    binding.txtNoData.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.txtNoData.setVisibility(View.GONE);
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtNoData.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                Toast.makeText(SubjectActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeacherSubjects() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoData.setVisibility(View.GONE);
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> classList) {
                ArrayList<Subject> teacherSubjects = new ArrayList<>();
                for (Class cls : classList) {
                    if (cls != null && cls.getGiangVien() != null && cls.getGiangVien().getTeacherID() != null
                            && cls.getGiangVien().getTeacherID().trim().equals(username.trim())
                            && cls.getMonHoc() != null) {
                        teacherSubjects.add(cls.getMonHoc());
                    }
                }
                final ArrayList<Subject> finalTeacherSubjects = teacherSubjects;
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    subjectArrayList.clear();
                    originalArrayList.clear();
                    subjectArrayList.addAll(finalTeacherSubjects);
                    originalArrayList.addAll(finalTeacherSubjects);
                    subjectAdapter.notifyDataSetChanged();
                    binding.recyclerView.setVisibility(subjectArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.txtNoData.setVisibility(subjectArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                    if (subjectArrayList.isEmpty()) {
                        binding.txtNoData.setText("Bạn chưa được gán môn học nào!");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.txtNoData.setVisibility(View.VISIBLE);
                    binding.txtNoData.setText("Lỗi tải dữ liệu: " + error);
                    Toast.makeText(SubjectActivity.this, "Lỗi tải dữ liệu môn học: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadStudentSubjects() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoData.setVisibility(View.GONE);
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> classList) {
                ArrayList<Subject> studentSubjects = new ArrayList<>();
                for (Class cls : classList) {
                    if (cls != null && cls.getDanhSachSinhVien() != null && cls.getMonHoc() != null) {
                        for (Student student : cls.getDanhSachSinhVien()) {
                            if (student != null && student.getStudentID() != null
                                    && student.getStudentID().trim().equals(username.trim())) {
                                studentSubjects.add(cls.getMonHoc());
                                break;
                            }
                        }
                    }
                }
                final ArrayList<Subject> finalStudentSubjects = studentSubjects;
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    subjectArrayList.clear();
                    originalArrayList.clear();
                    subjectArrayList.addAll(finalStudentSubjects);
                    originalArrayList.addAll(finalStudentSubjects);
                    subjectAdapter.notifyDataSetChanged();
                    binding.recyclerView.setVisibility(subjectArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.txtNoData.setVisibility(subjectArrayList.isEmpty() ? View.VISIBLE : View.GONE);
                    if (subjectArrayList.isEmpty()) {
                        binding.txtNoData.setText("Bạn chưa đăng ký môn học nào!");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.txtNoData.setVisibility(View.VISIBLE);
                    binding.txtNoData.setText("Lỗi tải dữ liệu: " + error);
                    Toast.makeText(SubjectActivity.this, "Lỗi tải dữ liệu môn học: " + error, Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(SubjectActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_subject);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_title_subject = dialog.findViewById(R.id.txt_title_subject);
        EditText edt_subjectID_add = dialog.findViewById(R.id.edt_subjectID_add);
        EditText edt_subjectName_add = dialog.findViewById(R.id.edt_subjectName_add);
        EditText edt_subjectNOC_add = dialog.findViewById(R.id.edt_subjectNOC_add);
        Spinner spn_subjectBranch = dialog.findViewById(R.id.spn_subjectBranch);
        Button btn_add = dialog.findViewById(R.id.btn_add_subject);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel_subject);
        txt_title_subject.setText("Thêm môn học");
        loadDataBranch();
        spn_subjectBranch.setAdapter(arrayAdapter);
        btn_add.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String subjectID = edt_subjectID_add.getText().toString();
            String subjectName = edt_subjectName_add.getText().toString();
            String subjectNOC = edt_subjectNOC_add.getText().toString();
            if (subjectID.isEmpty() || subjectName.isEmpty() || subjectNOC.isEmpty()) {
                Toast.makeText(SubjectActivity.this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                return;
            }
            Branch selectedBranch = (Branch) spn_subjectBranch.getSelectedItem();
            Subject subject = new Subject(id, subjectID, subjectName, selectedBranch, subjectNOC);
            addSubject(subject, dialog);
        });
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addSubject(Subject subject, Dialog dialog) {
        subjectController.addSubject(subject, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SubjectActivity.this, "Thêm môn học thành công !!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadData();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(SubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchList(String text) {
        ArrayList<Subject> filteredList = new ArrayList<>();
        for (Subject data : originalArrayList) {
            if (data.getSubjectName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        subjectAdapter.searchSubject(filteredList);
    }

    private final BroadcastReceiver localUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData();
        }
    };

    private final BroadcastReceiver globalUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_DATA_UPDATED".equals(intent.getAction())) {
                loadData();
            }
        }
    };

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
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(globalUpdateReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localUpdateReceiver);
        } catch (Exception e) {
            Log.e("SubjectActivity", "Failed to unregister receiver: " + e.getMessage());
        }
    }
}