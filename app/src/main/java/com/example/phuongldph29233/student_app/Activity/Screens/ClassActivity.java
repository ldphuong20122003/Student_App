package com.example.phuongldph29233.student_app.Activity.Screens;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.ClassAdapter;
import com.example.phuongldph29233.student_app.Adapter.StudentSelectionAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.TeacherController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityClassBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassActivity extends AppCompatActivity {
    ActivityClassBinding binding;
    private DatabaseHelper<Class> databaseHelper;
    private ClassAdapter classAdapter;
    private ArrayList<Class> classArrayList;
    private ArrayList<Class> originalArrayList;
    private ArrayAdapter<Branch> branchAdapter;
    private ArrayAdapter<Teacher> teacherAdapter;
    private ArrayList<Branch> branchList;
    private ArrayList<Teacher> teacherList;
    private BranchController branchController;
    private TeacherController teacherController;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Class> classDatabaseHelper;
    private ArrayList<Student> studentsWithoutClass;
    private StudentSelectionAdapter studentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding = ActivityClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper = new DatabaseHelper<>("Classes");
        studentDatabaseHelper = new DatabaseHelper<>("Student");
        studentsWithoutClass = new ArrayList<>();
        classArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        classAdapter = new ClassAdapter(classArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(classAdapter);
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        loadDataClass();
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
        initializeComponents();
        setupListeners();
        loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        loadDataClass();
    }

    private void initializeComponents() {
        branchList = new ArrayList<>();
        teacherList = new ArrayList<>();
        branchController = new BranchController();
        teacherController = new TeacherController();
        branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
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

    private void loadData() {
        loadBranches();
        loadTeachers();
    }

    private void loadBranches() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @SuppressLint("NotifyDataSetChanged")
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
                runOnUiThread(() ->
                        Toast.makeText(ClassActivity.this, "Lỗi tải khoa: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadTeachers() {
        teacherController.getTeacher(new DatabaseHelper.DatabaseCallback<Teacher>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Teacher> itemList) {
                runOnUiThread(() -> {
                    teacherList.clear();
                    teacherList.addAll(itemList);
                    teacherAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(ClassActivity.this, "Lỗi tải giảng viên: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadDataClass() {
        databaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Class> list) {
                runOnUiThread(() -> {
                    classArrayList.clear();
                    originalArrayList.clear();
                    classArrayList.addAll(list);
                    originalArrayList.addAll(list);
                    classAdapter.notifyDataSetChanged();

                    if (classArrayList.isEmpty()) {
                        binding.recyclerView.setVisibility(View.GONE);
                    } else {
                        binding.recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    binding.recyclerView.setVisibility(View.GONE);
                    Toast.makeText(ClassActivity.this, "Lỗi tải dữ liệu lớp: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_class);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        Window window = dialog.getWindow();
//        if (window != null) {
//            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            window.setWindowAnimations(R.style.DialogAnimation);
//        }
        TextView txtTitle = dialog.findViewById(R.id.textView);
        EditText edtMaLop = dialog.findViewById(R.id.edt_maLop_add);
        EditText edtTenLop = dialog.findViewById(R.id.edt_tenLop_add);
        Spinner spnKhoaAdd = dialog.findViewById(R.id.edt_khoa_add);
        Spinner spnGiangVienAdd = dialog.findViewById(R.id.edt_giangVien_add);
        EditText edtNamHoc = dialog.findViewById(R.id.edt_namHoc_add);
        Button btnHuy = dialog.findViewById(R.id.btn_huy_lop);
        Button btnAdd = dialog.findViewById(R.id.btn_add_lop);
        HelperUtils.setupDatePicker(this, edtNamHoc);
//        spnKhoaAdd.setAdapter(branchAdapter);
//        spnGiangVienAdd.setAdapter(teacherAdapter);
//        txtTitle.setText("Thêm lớp học");
//        if (txtTitle == null || edtMaLop == null || edtTenLop == null || spnKhoaAdd == null ||
//                spnGiangVienAdd == null || edtNamHoc == null || btnHuy == null || btnAdd == null) {
//            Toast.makeText(this, "Lỗi hiển thị dialog", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        runOnUiThread(() -> {
//
//        });

//        ListView lvStudents = dialog.findViewById(R.id.lvStudents);
//        Button btnSelectAll = dialog.findViewById(R.id.btnSelectAll);

//        studentAdapter = new StudentSelectionAdapter(this, new ArrayList<>());
//        lvStudents.setAdapter(studentAdapter);

//        loadStudentsWithoutClass();

//        btnSelectAll.setOnClickListener(v -> {
//            boolean selectAll = studentAdapter.getSelectedStudents().size() != studentsWithoutClass.size();
//            studentAdapter.selectAll(selectAll);
//            btnSelectAll.setText(selectAll ? "Bỏ chọn tất cả" : "Chọn tất cả");
//        });

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maLop = edtMaLop.getText().toString().trim();
            String tenLop = edtTenLop.getText().toString().trim();
            Branch khoa = (Branch) spnKhoaAdd.getSelectedItem();
            Teacher giangVien = (Teacher) spnGiangVienAdd.getSelectedItem();
            String namHoc = edtNamHoc.getText().toString().trim();

            if (maLop.isEmpty() || tenLop.isEmpty() || namHoc.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (khoa == null || khoa.getBranchName() == null || khoa.getBranchID().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn khoa hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (giangVien == null || giangVien.getTeacherName() == null || giangVien.getTeacherID().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn giảng viên hợp lệ", Toast.LENGTH_SHORT).show();
            }

//            List<Student> selectedStudents = studentAdapter.getSelectedStudents();
//            if (selectedStudents.isEmpty()) {
//                Toast.makeText(this, "Vui lòng chọn ít nhất một sinh viên", Toast.LENGTH_SHORT).show();
//                return;
//            }

//            Class newClass = new Class(id, maLop, tenLop, khoa, giangVien, namHoc, selectedStudents);
//            addClass(newClass, dialog);
        });

        btnHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addClass(Class classes, Dialog dialog) {
        databaseHelper.add(classes, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(ClassActivity.this, "Thêm lớp học thành công", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadDataClass();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentsWithoutClass() {
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> students) {
                List<Student> filteredStudents = new ArrayList<>();
                for (Student student : students) {
                    if (student.getStudentClass() == null || student.getStudentClass().getMaLop().isEmpty()) {
                        filteredStudents.add(student);
                    }
                }

                runOnUiThread(() -> {
                    studentAdapter.clear();
                    studentAdapter.addAll(filteredStudents);
                    studentAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(ClassActivity.this,
                                "Lỗi tải sinh viên: " + error,
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

//    private void saveClassWithStudents(Class newClass, Dialog dialog) {
//        databaseHelper.add(newClass, new DatabaseHelper.DatabaseActionCallback() {
//            @Override
//            public void onSuccess() {
//                updateStudentsClass(newClass, newClass.getDanhSachSinhVien(), dialog);
//            }
//
//            @Override
//            public void onFailure(String error) {
//                runOnUiThread(() ->
//                        Toast.makeText(ClassActivity.this,
//                                "Lỗi khi tạo lớp: " + error,
//                                Toast.LENGTH_SHORT).show());
//            }
//        });
//    }

//    private void updateStudentsClass(Class newClass, List<Student> students, Dialog dialog) {
//        AtomicInteger successCount = new AtomicInteger();
//        int totalStudents = students.size();
//
//        if (totalStudents == 0) {
//            dialog.dismiss();
//            loadDataClass();
//            return;
//        }
//
//        for (Student student : students) {
//            student.setStudentClass(newClass);
//
//            studentDatabaseHelper.update(student.getId(), student, new DatabaseHelper.DatabaseActionCallback() {
//                @Override
//                public void onSuccess() {
//                    if (successCount.incrementAndGet() == totalStudents) {
//                        runOnUiThread(() -> {
//                            Toast.makeText(ClassActivity.this,
//                                    "Tạo lớp thành công với " + totalStudents + " sinh viên",
//                                    Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            loadDataClass();
//                        });
//                    }
//                }
//
//                @Override
//                public void onFailure(String error) {
//                    if (successCount.incrementAndGet() == totalStudents) {
//                        runOnUiThread(() -> {
//                            Toast.makeText(ClassActivity.this,
//                                    "Tạo lớp thành công nhưng có lỗi với một số sinh viên",
//                                    Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            loadDataClass();
//                        });
//                    }
//                }
//            });
//        }
//    }

    private void searchList(String text) {
        ArrayList<Class> filteredList = new ArrayList<>();
        for (Class data : originalArrayList) {
            if (data.getTenLop().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        classAdapter.searchClass(filteredList);
    }
}