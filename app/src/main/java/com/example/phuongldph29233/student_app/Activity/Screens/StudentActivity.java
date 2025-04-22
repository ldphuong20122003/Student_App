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
import android.os.Handler;
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
import com.example.phuongldph29233.student_app.Adapter.StudentAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityStudentBinding;
import com.example.phuongldph29233.student_app.databinding.ActivityStudentListNewBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class StudentActivity extends AppCompatActivity {
    private ActivityStudentBinding studentBinding;
    private ActivityStudentListNewBinding listBinding;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Class> classDatabaseHelper;
    private SessionManager sessionManager;
    private StudentAdapter studentAdapter;
    private ArrayList<Student> studentArrayList;
    private ArrayList<Student> originalArrayList;
    private List<Class> studentClasses;
    private ArrayAdapter<Branch> branchAdapter;
    private BranchController branchController;
    private ArrayList<Branch> branchList;
    private String username;
    private String role;
    private static final Class EMPTY_CLASS = new Class("", "", "Chưa có lớp học", null, null, null, "");

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
            case "student":
                studentBinding = ActivityStudentBinding.inflate(getLayoutInflater());
                setContentView(studentBinding.getRoot());
                break;
            case "teacher":
            case "admin":
                listBinding = ActivityStudentListNewBinding.inflate(getLayoutInflater());
                setContentView(listBinding.getRoot());
                break;
            default:
                Toast.makeText(this, "Vai trò không hợp lệ!", Toast.LENGTH_SHORT).show();
                finish();
                return;
        }
        try {
            studentDatabaseHelper = new DatabaseHelper<>("Student");
            classDatabaseHelper = new DatabaseHelper<>("Classes");
        } catch (Exception e) {
            Log.e("StudentActivity", "Lỗi khởi tạo DatabaseHelper: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setupViews();
        loadData();
    }

    private void setupViews() {
        switch (role) {
            case "student":
                studentBinding.btnBack.setOnClickListener(v -> finish());
                studentBinding.txtLopHocDetail.setOnClickListener(v -> handleClassClick());
                break;
            case "teacher":
                listBinding.btnBack.setOnClickListener(v -> finish());
                listBinding.btnLogout.setOnClickListener(v -> showLogoutDialog());
                listBinding.btnAdd.setVisibility(View.GONE);
                setupRecyclerViewAndSearch();
                break;
            case "admin":
                listBinding.btnBack.setOnClickListener(v -> finish());
                listBinding.btnLogout.setOnClickListener(v -> showLogoutDialog());
                listBinding.btnAdd.setVisibility(View.VISIBLE);
                listBinding.btnAdd.setOnClickListener(v -> showDialogAdd());
                setupRecyclerViewAndSearch();
                branchController = new BranchController();
                branchList = new ArrayList<>();
                branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
                branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                loadDataBranch();
                break;
        }
    }

    private void setupRecyclerViewAndSearch() {
        studentArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentArrayList);
        listBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listBinding.recyclerView.setAdapter(studentAdapter);
        listBinding.edtSearch.addTextChangedListener(new TextWatcher() {
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

    private void loadData() {
        switch (role) {
            case "student":
                loadStudentDataForStudent();
                fetchStudentClasses();
                break;
            case "teacher":
                loadStudentDataForTeacher();
                break;
            case "admin":
                loadStudentDataForAdmin();
                break;
        }
    }

    private void loadStudentDataForStudent() {
        studentBinding.progressBar.setVisibility(View.VISIBLE);
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> studentList) {
                runOnUiThread(() -> {
                    studentBinding.progressBar.setVisibility(View.GONE);
                    Student currentStudent = null;
                    for (Student student : studentList) {
                        if (student.getStudentID() != null && student.getStudentID().equals(username)) {
                            currentStudent = student;
                            break;
                        }
                    }

                    if (currentStudent == null) {
                        Toast.makeText(StudentActivity.this, "Không tìm thấy thông tin sinh viên!", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    studentBinding.txtMaSVDetail.setText("Mã SV: " + (currentStudent.getStudentID() != null ? currentStudent.getStudentID() : "N/A"));
                    studentBinding.txtTenSVDetail.setText("Tên SV: " + (currentStudent.getStudentName() != null ? currentStudent.getStudentName() : "N/A"));
                    studentBinding.txtNgaySinhDetail.setText("Ngày sinh: " + (currentStudent.getStudentBirthday() != null ? currentStudent.getStudentBirthday() : "N/A"));
                    studentBinding.txtQueQuanDetail.setText("Quê quán: " + (currentStudent.getStudentHomeTown() != null ? currentStudent.getStudentHomeTown() : "N/A"));
                    studentBinding.txtSdtDetail.setText("SĐT: " + (currentStudent.getStudentPhone() != null ? currentStudent.getStudentPhone() : "N/A"));
                    studentBinding.txtEmailDetail.setText("Email: " + (currentStudent.getStudentEmail() != null ? currentStudent.getStudentEmail() : "N/A"));
                    studentBinding.txtNgayNhaphocDetail.setText("Ngày nhập học: " + (currentStudent.getStudentDateJoin() != null ? currentStudent.getStudentDateJoin() : "N/A"));
                    studentBinding.txtHeDaotaoDetail.setText("Hệ đào tạo: " + (currentStudent.getStudentTOT() != null ? currentStudent.getStudentTOT() : "N/A"));
                    studentBinding.txtChuyenNganhDetail.setText("Chuyên ngành: " + (currentStudent.getStudentBranch() != null && currentStudent.getStudentBranch().getBranchName() != null ? currentStudent.getStudentBranch().getBranchName() : "N/A"));
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    studentBinding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(StudentActivity.this, "Lỗi tải dữ liệu sinh viên: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void fetchStudentClasses() {
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> allClasses) {
                runOnUiThread(() -> {
                    studentClasses = new ArrayList<>();
                    for (Class lop : allClasses) {
                        List<Student> students = lop.getDanhSachSinhVien();
                        if (students != null) {
                            for (Student sv : students) {
                                if (sv != null && sv.getStudentID() != null && sv.getStudentID().equals(username)) {
                                    studentClasses.add(lop);
                                    break;
                                }
                            }
                        }
                    }
                    if (studentClasses.isEmpty()) {
                        studentBinding.txtLopHocDetail.setText("Lớp học: Chưa có lớp học");
                    } else if (studentClasses.size() == 1) {
                        studentBinding.txtLopHocDetail.setText("Lớp học: " + studentClasses.get(0).getTenLop());
                    } else {
                        studentBinding.txtLopHocDetail.setText("Lớp học: " + studentClasses.get(0).getTenLop() + " (+" + (studentClasses.size() - 1) + ")");
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    studentBinding.txtLopHocDetail.setText("Lớp học: Lỗi tải dữ liệu");
                    Toast.makeText(StudentActivity.this, "Lỗi tải lớp học: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadStudentDataForTeacher() {
        listBinding.progressBar.setVisibility(View.VISIBLE);
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> classList) {
                Set<String> studentIds = new HashSet<>();
                List<String> studentIdList = new ArrayList<>();
                for (Class lop : classList) {
                    if (lop.getGiangVien() != null && lop.getGiangVien().getTeacherID() != null && lop.getGiangVien().getTeacherID().equals(username)) {
                        List<Student> students = lop.getDanhSachSinhVien();
                        if (students != null) {
                            for (Student sv : students) {
                                if (sv != null && sv.getStudentID() != null && !studentIds.contains(sv.getStudentID())) {
                                    studentIds.add(sv.getStudentID());
                                    studentIdList.add(sv.getStudentID());
                                }
                            }
                        }
                    }
                }

                if (studentIdList.isEmpty()) {
                    runOnUiThread(() -> {
                        listBinding.progressBar.setVisibility(View.GONE);
                        listBinding.recyclerView.setVisibility(View.GONE);
                        Toast.makeText(StudentActivity.this, "Không có sinh viên trong lớp của bạn!", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
                    @Override
                    public void onSuccess(List<Student> studentList) {
                        List<Student> teacherStudents = new ArrayList<>();
                        for (Student student : studentList) {
                            if (student != null && student.getStudentID() != null && studentIds.contains(student.getStudentID())) {
                                if (student.getStudentClass() == null) {
                                    student.setStudentClass(EMPTY_CLASS);
                                }
                                teacherStudents.add(student);
                            }
                        }

                        runOnUiThread(() -> {
                            listBinding.progressBar.setVisibility(View.GONE);
                            studentArrayList.clear();
                            originalArrayList.clear();
                            studentArrayList.addAll(teacherStudents);
                            originalArrayList.addAll(teacherStudents);
                            studentAdapter.notifyDataSetChanged();
                            listBinding.recyclerView.setVisibility(studentArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                            if (studentArrayList.isEmpty()) {
                                Toast.makeText(StudentActivity.this, "Không tìm thấy thông tin sinh viên trong lớp!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            listBinding.progressBar.setVisibility(View.GONE);
                            listBinding.recyclerView.setVisibility(View.GONE);
                            Toast.makeText(StudentActivity.this, "Lỗi tải dữ liệu sinh viên: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    listBinding.progressBar.setVisibility(View.GONE);
                    listBinding.recyclerView.setVisibility(View.GONE);
                    Toast.makeText(StudentActivity.this, "Lỗi tải dữ liệu lớp học: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadStudentDataForAdmin() {
        listBinding.progressBar.setVisibility(View.VISIBLE);
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> list) {
                runOnUiThread(() -> {
                    listBinding.progressBar.setVisibility(View.GONE);
                    studentArrayList.clear();
                    originalArrayList.clear();
                    for (Student student : list) {
                        if (student != null && student.getStudentID() != null) {
                            if (student.getStudentClass() == null) {
                                student.setStudentClass(EMPTY_CLASS);
                            }
                            studentArrayList.add(student);
                        }
                    }
                    originalArrayList.addAll(studentArrayList);
                    studentAdapter.notifyDataSetChanged();
                    listBinding.recyclerView.setVisibility(studentArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                    if (studentArrayList.isEmpty()) {
                        Toast.makeText(StudentActivity.this, "Không có sinh viên nào trong hệ thống!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    listBinding.progressBar.setVisibility(View.GONE);
                    listBinding.recyclerView.setVisibility(View.GONE);
                    Toast.makeText(StudentActivity.this, "Lỗi tải dữ liệu sinh viên: " + error, Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(StudentActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void handleClassClick() {
        if (studentClasses == null) return;
        if (studentClasses.size() > 1) {
            Intent intent = new Intent(StudentActivity.this, StudentClassesActivity.class);
            intent.putExtra("studentId", username);
            intent.putExtra("studentName", studentBinding.txtTenSVDetail.getText().toString().replace("Tên SV: ", ""));
            ArrayList<String> classIds = new ArrayList<>();
            for (Class c : studentClasses) {
                if (c != null && c.getId() != null) {
                    classIds.add(c.getId());
                }
            }
            intent.putStringArrayListExtra("classIds", classIds);
            startActivity(intent);
        } else if (studentClasses.size() == 1) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(studentClasses.get(0).getTenLop())
                    .setMessage("Mã lớp: " + studentClasses.get(0).getMaLop() + "\n" +
                            "Giảng viên: " + (studentClasses.get(0).getGiangVien() != null ? studentClasses.get(0).getGiangVien().getTeacherName() : "N/A"))
                    .setPositiveButton("OK", null)
                    .show();
        }
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
        EditText edtNgayNhapHoc = dialog.findViewById(R.id.edt_ngayNhaphoc_add);
        EditText edtHeDaotao = dialog.findViewById(R.id.edt_heDaotao_add);
        Spinner spnChuyenNganh = dialog.findViewById(R.id.spn_chuyenNganh);
        Button btnHuy = dialog.findViewById(R.id.btn_huy_sv);
        Button btnAdd = dialog.findViewById(R.id.btn_add_sv);
        HelperUtils.setupDatePicker(this, edtNgaysinh);
        HelperUtils.setupDatePicker(this, edtNgayNhapHoc);
        txtTitle.setText("Thêm sinh viên");
        spnChuyenNganh.setAdapter(branchAdapter);

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maSV = edtMaSV.getText().toString().trim();
            String tenSV = edtTenSV.getText().toString().trim();
            String ngaySinh = edtNgaysinh.getText().toString().trim();
            String queQuan = edtQue.getText().toString().trim();
            String soDienThoai = edtSDT.getText().toString().trim();
            String email = edtMail.getText().toString().trim();
            String ngayNhapHoc = edtNgayNhapHoc.getText().toString().trim();
            String heDaoTao = edtHeDaotao.getText().toString().trim();
            Branch selectedBranch = (Branch) spnChuyenNganh.getSelectedItem();

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
            Student student = new Student(id, maSV, tenSV, ngaySinh, queQuan, soDienThoai, email, ngayNhapHoc, selectedBranch, heDaoTao);
            addStudent(student, dialog);
        });
        btnHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addStudent(Student student, Dialog dialog) {
        studentDatabaseHelper.add(student, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(StudentActivity.this, "Thêm sinh viên thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadStudentDataForAdmin();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(StudentActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataBranch() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                runOnUiThread(() -> {
                    branchList.clear();
                    branchList.addAll(list);
                    branchAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> Toast.makeText(StudentActivity.this, "Lỗi tải chuyên ngành: " + error, Toast.LENGTH_SHORT).show());
            }
        });
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
        runOnUiThread(() -> {
            switch (role) {
                case "student":
                    loadStudentDataForStudent();
                    fetchStudentClasses();
                    break;
                case "teacher":
                    loadStudentDataForTeacher();
                    break;
                case "admin":
                    loadStudentDataForAdmin();
                    break;
            }
        });
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
            Log.e("StudentActivity", "Failed to unregister receiver: " + e.getMessage());
        }
    }
}