package com.example.phuongldph29233.student_app.Activity.Screens;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.ClassAdapter;
import com.example.phuongldph29233.student_app.Adapter.StudentAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityStudentBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class StudentActivity extends AppCompatActivity {
    ActivityStudentBinding binding;
    private DatabaseHelper<Student> databaseHelper;
    private ArrayAdapter<Branch> branchAdapter;
    private ArrayAdapter<Class> classAdapter;
    private StudentAdapter studentAdapter;
    private BranchController branchController;
    private ArrayList<Student> studentArrayList;
    private ArrayList<Branch> branchList;
    private ArrayList<Student> originalArrayList;
    private DatabaseHelper<Class> classDatabaseHelper;
    private ArrayList<Class> classList;
    private static final Class EMPTY_CLASS = new Class("", "", "Chưa có lớp học", null, null, "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper<>("Student");
        classDatabaseHelper = new DatabaseHelper<>("Classes");
        classList = new ArrayList<>();
        branchController = new BranchController();
        studentArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        branchList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentArrayList);
        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(studentAdapter);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        loadDataBranch();
        loadDataClass();
        loadDataStudent();
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataStudent();
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

    private void loadDataStudent() {
        databaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> list) {
                studentArrayList.clear();
                originalArrayList.clear();

                for (Student student : list) {
                    // Xử lý khi studentClass null
                    if (student.getStudentClass() == null) {
                        student.setStudentClass(EMPTY_CLASS);
                    } else {
                        // Kiểm tra lớp có tồn tại
                        boolean classExists = false;
                        for (Class cls : classList) {
                            if (cls != null && cls.getMaLop() != null &&
                                    cls.getMaLop().equals(student.getStudentClass().getMaLop())) {
                                classExists = true;
                                break;
                            }
                        }
                        if (!classExists) {
                            student.setStudentClass(EMPTY_CLASS);
                        }
                    }
                    studentArrayList.add(student);
                }

                originalArrayList.addAll(studentArrayList);
                runOnUiThread(() -> {
                    studentAdapter.notifyDataSetChanged();
                    binding.recyclerView.setVisibility(studentArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    binding.recyclerView.setVisibility(View.GONE);
                    Toast.makeText(StudentActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateStudentClass(String studentId, Class newClass) {
        databaseHelper.get(studentId, Student.class, new DatabaseHelper.DatabaseGetCallback<Student>() {
            @Override
            public void onSuccess(Student student) {
                if (student != null) {
                    student.setStudentClass(newClass);
                    databaseHelper.update(studentId, student, new DatabaseHelper.DatabaseActionCallback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(String error) {
                        }
                    });
                }
            }

            @Override
            public void onFailure(String error) {
            }
        });
    }

    private void loadDataClass() {
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> itemList) {
                classList.add(EMPTY_CLASS);
                classList.addAll(itemList);
                classAdapter.notifyDataSetChanged();
                loadDataStudent();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(StudentActivity.this, "Lỗi tải : " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataBranch() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchList.clear();
                branchList.addAll(list);
                branchAdapter.notifyDataSetChanged();
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
        HelperUtils.setupPhoneNumberValidation(edtSDT);
        EditText edtMail = dialog.findViewById(R.id.edt_email_add);
        HelperUtils.setupEmailValidation(edtMail);
        Spinner edtLophoc = dialog.findViewById(R.id.edt_lopHoc_add);

        EditText edtNgayNhapHoc = dialog.findViewById(R.id.edt_ngayNhaphoc_add);
        EditText edtHeDaotao = dialog.findViewById(R.id.edt_heDaotao_add);
        Spinner spn_chuyenNganh = dialog.findViewById(R.id.spn_chuyenNganh);
        Button btnHuy = dialog.findViewById(R.id.btn_huy_sv);
        Button btnAdd = dialog.findViewById(R.id.btn_add_sv);
        HelperUtils.setupDatePicker(this, edtNgaysinh);
        HelperUtils.setupDatePicker(this, edtNgayNhapHoc);
        txtTitle.setText("Thêm sinh viên");
        spn_chuyenNganh.setAdapter(branchAdapter);
        edtLophoc.setAdapter(classAdapter);

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maSV = edtMaSV.getText().toString().trim();
            String tenSV = edtTenSV.getText().toString().trim();
            String ngaySinh = edtNgaysinh.getText().toString().trim();
            String queQuan = edtQue.getText().toString().trim();
            String soDienThoai = edtSDT.getText().toString().trim();
            String email = edtMail.getText().toString().trim();
//            String lopHoc = edtLophoc.getText().toString().trim();
            Class lopHoc = (Class) edtLophoc.getSelectedItem();
            String ngayNhapHoc = edtNgayNhapHoc.getText().toString().trim();
            String heDaoTao = edtHeDaotao.getText().toString().trim();
            Branch selectedBranch = (Branch) spn_chuyenNganh.getSelectedItem();

            if (maSV.isEmpty() || tenSV.isEmpty() || heDaoTao.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedBranch == null || selectedBranch.getBranchName() == null || selectedBranch.getBranchID().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn chuyên ngành hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!HelperUtils.isValidPhoneNumber(soDienThoai)) {
                edtSDT.setError("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
                return;
            }
            if (!HelperUtils.isValidEmail(email)) {
                edtMail.setError("Email không hợp lệ");
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

    private BroadcastReceiver localUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDataImmediately();
        }
    };

    private BroadcastReceiver globalUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("ACTION_DATA_UPDATED".equals(intent.getAction())) {
                refreshDataImmediately();
            }
        }
    };

    private void refreshDataImmediately() {
        runOnUiThread(() -> {
            loadDataClass();
            new Handler().postDelayed(this::loadDataStudent, 200); // Sau đó load sinh viên
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
        // Hủy đăng ký receivers
        try {
            unregisterReceiver(globalUpdateReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localUpdateReceiver);
        } catch (Exception e) {
            Log.e("ReceiverError", "Failed to unregister", e);
        }
    }


    private void searchList(String text) {
        ArrayList<Student> filteredList = new ArrayList<>();
        for (Student data : originalArrayList) {
            if (data.getStudentID().toLowerCase().contains(text.toLowerCase()) || data.getStudentName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        studentAdapter.searchStudent(filteredList);
    }
}