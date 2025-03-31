package com.example.phuongldph29233.student_app.Activity.Detail;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Activity.Screens.ClassActivity;
import com.example.phuongldph29233.student_app.Activity.Screens.StudentListActivity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailClassActivity extends AppCompatActivity {
    private TextView txtMaLop, txtTenLop, txtKhoa, txtGiangVien, txtNamHoc,txtListSV;
    private Button btnEdit, btnDelete, btnBack;
    private String id, maLop, tenLop, khoa, giangVien, namHoc;
    private DatabaseHelper<Class> classDatabaseHelper;
    private BranchController branchController;
    private List<Student> danhSachSinhVien;
    private List<String> danhSachSinhVienIds;
    private DatabaseHelper<Student> studentDatabaseHelper;

    private TeacherController teacherController;
    private ArrayList<Branch> branchList;
    private ArrayList<Teacher> teacherList;
    private StudentSelectionAdapter studentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_class);
        classDatabaseHelper = new DatabaseHelper<>("Classes");
        studentDatabaseHelper = new DatabaseHelper<>("Student");

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
        txtListSV.setOnClickListener(view -> {
            if (danhSachSinhVienIds.isEmpty()) {
                Toast.makeText(this, "Lớp học chưa có sinh viên", Toast.LENGTH_SHORT).show();
                return;
            }
            studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
                @Override
                public void onSuccess(List<Student> allStudents) {
                    List<Student> filteredStudents = new ArrayList<>();
                    for (Student student : allStudents) {
                        if (danhSachSinhVienIds.contains(student.getId())) {
                            filteredStudents.add(student);
                        }
                    }

                    runOnUiThread(() -> {
                        if (filteredStudents.isEmpty()) {
                            Toast.makeText(DetailClassActivity.this,
                                    "Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(DetailClassActivity.this, StudentListActivity.class);
                        intent.putExtra("maLop", maLop);
                        intent.putExtra("tenLop", tenLop);
                        intent.putExtra("danhSachSinhVien", new ArrayList<>(filteredStudents));
                        startActivity(intent);
                    });
                }

                @Override
                public void onFailure(String error) {
                    Log.e("DetailClass", "Lỗi database: " + error);
                    runOnUiThread(() ->
                            Toast.makeText(DetailClassActivity.this,
                                    "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show());
                }
            });
        });
    }

    private void getIntentExtra() {
        id = (String) getIntent().getSerializableExtra("id");
        maLop = (String) getIntent().getSerializableExtra("maLop");
        tenLop = (String) getIntent().getSerializableExtra("tenLop");
        khoa = (String) getIntent().getSerializableExtra("khoa");
        giangVien = (String) getIntent().getSerializableExtra("giangVien");
        namHoc = (String) getIntent().getSerializableExtra("namHoc");
        danhSachSinhVien = (List<Student>) getIntent().getSerializableExtra("danhSachSinhVien");
        danhSachSinhVienIds = getIntent().getStringArrayListExtra("danhSachSinhVienIds");
        if (danhSachSinhVienIds == null) {
            danhSachSinhVienIds = new ArrayList<>();
        }
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
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setWindowAnimations(R.style.DialogAnimation);
        }

        TextView txtTitle = dialog.findViewById(R.id.textView);
        EditText edtMaLopEdit = dialog.findViewById(R.id.edt_maLop_add);
        EditText edtTenLopEdit = dialog.findViewById(R.id.edt_tenLop_add);
        Spinner spnKhoaEdit = dialog.findViewById(R.id.edt_khoa_add);
        Spinner spnGiangVienEdit = dialog.findViewById(R.id.edt_giangVien_add);
        EditText edtNamHocEdit = dialog.findViewById(R.id.edt_namHoc_add);
        Button btnUpdate = dialog.findViewById(R.id.btn_add_lop);
        Button btnCancel = dialog.findViewById(R.id.btn_huy_lop);
        ListView lvStudents = dialog.findViewById(R.id.lvStudents);
        Button btnSelectAll = dialog.findViewById(R.id.btnSelectAll);

        txtTitle.setText("Chỉnh sửa lớp học");
        btnUpdate.setText("Cập nhật");
        HelperUtils.setupDatePicker(this, edtNamHocEdit);

        edtMaLopEdit.setText(maLop);
        edtTenLopEdit.setText(tenLop);
        edtNamHocEdit.setText(namHoc);

        ArrayAdapter<Branch> branchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, branchList);
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnKhoaEdit.setAdapter(branchAdapter);

        new Handler().postDelayed(() -> {
            for (int i = 0; i < branchList.size(); i++) {
                if (branchList.get(i).getBranchName().equals(khoa)) {
                    spnKhoaEdit.setSelection(i);
                    break;
                }
            }
        }, 100);

        ArrayAdapter<Teacher> teacherAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, teacherList);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGiangVienEdit.setAdapter(teacherAdapter);

        new Handler().postDelayed(() -> {
            for (int i = 0; i < teacherList.size(); i++) {
                Teacher teacher = teacherList.get(i);
                if (teacher.getTeacherName().equals(giangVien) || teacher.getTeacherID().equals(giangVien)) {
                    spnGiangVienEdit.setSelection(i);
                    break;
                }
            }
        }, 100);

        studentAdapter = new StudentSelectionAdapter(this, new ArrayList<>());
        lvStudents.setAdapter(studentAdapter);

        loadStudentsForEdit(dialog);

        btnSelectAll.setOnClickListener(v -> {
            boolean allSelected = studentAdapter.getSelectedStudents().size() == studentAdapter.getCount();
            studentAdapter.selectAll(!allSelected);
            updateSelectAllButton(dialog);
        });

        btnUpdate.setOnClickListener(v -> {
            String updatedMaLop = edtMaLopEdit.getText().toString();
            String updatedTenLop = edtTenLopEdit.getText().toString();
            Branch selectedBranch = (Branch) spnKhoaEdit.getSelectedItem();
            Teacher selectedTeacher = (Teacher) spnGiangVienEdit.getSelectedItem();
            String updatedNamHoc = edtNamHocEdit.getText().toString();
            List<Student> selectedStudents = studentAdapter.getSelectedStudents();

            if (updatedMaLop.isEmpty() || updatedTenLop.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> selectedStudentIds = new ArrayList<>();
            for (Student student : selectedStudents) {
                selectedStudentIds.add(student.getId());
            }
            Class updatedClass = new Class(
                    id,
                    updatedMaLop,
                    updatedTenLop,
                    selectedBranch,
                    selectedTeacher,
                    updatedNamHoc,
                    selectedStudents
            );

            classDatabaseHelper.update(id, updatedClass, new DatabaseHelper.DatabaseActionCallback() {
                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        Toast.makeText(DetailClassActivity.this, "Cập nhật lớp học thành công!", Toast.LENGTH_SHORT).show();

                        maLop = updatedMaLop;
                        tenLop = updatedTenLop;
                        khoa = selectedBranch.getBranchName();
                        giangVien = selectedTeacher.getTeacherName();
                        namHoc = updatedNamHoc;
                        danhSachSinhVienIds = selectedStudentIds;

                        txtMaLop.setText(maLop);
                        txtTenLop.setText(tenLop);
                        txtKhoa.setText(khoa);
                        txtGiangVien.setText(giangVien);
                        txtNamHoc.setText(namHoc);

                        dialog.dismiss();
                    });
                }

                @Override
                public void onFailure(String error) {
                    runOnUiThread(() ->
                            Toast.makeText(DetailClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
                }
            });
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadStudentsForEdit(Dialog dialog) {
        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> allStudents) {
                List<Student> studentsToShow = new ArrayList<>();
                List<Student> currentClassStudents = new ArrayList<>();

                for (Student student : allStudents) {
                    if (student.getStudentClass() == null || student.getStudentClass().getMaLop().isEmpty()) {
                        studentsToShow.add(student);
                    } else if (student.getStudentClass().getId().equals(id)) {
                        currentClassStudents.add(student);
                        studentsToShow.add(student);
                    }
                }

                runOnUiThread(() -> {
                    studentAdapter.clear();
                    studentAdapter.addAll(studentsToShow);

                    // Đánh dấu các sinh viên đã chọn trước đó
                    for (int i = 0; i < studentsToShow.size(); i++) {
                        Student student = studentsToShow.get(i);
                        if (currentClassStudents.contains(student)) {
                            studentAdapter.setSelected(i, true);
                        }
                    }

                    updateSelectAllButton(dialog);
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(DetailClassActivity.this,
                                "Lỗi tải sinh viên: " + error,
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateSelectAllButton(Dialog dialog) {
        Button btnSelectAll = dialog.findViewById(R.id.btnSelectAll);
        if (btnSelectAll != null) {
            boolean allSelected = studentAdapter.getSelectedStudents().size() == studentAdapter.getCount();
            btnSelectAll.setText(allSelected ? "Bỏ chọn tất cả" : "Chọn tất cả");
        }
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
