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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.example.phuongldph29233.student_app.databinding.ActivityDetailClassBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DetailClassActivity extends AppCompatActivity {
    private ActivityDetailClassBinding binding;
    private String id, maLop, tenLop, khoa, giangVien, namHoc;
    private DatabaseHelper<Class> classDatabaseHelper;
    private BranchController branchController;
    private List<Student> danhSachSinhVien;
    private List<String> danhSachSinhVienIds;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private boolean isVisible;
    private TeacherController teacherController;
    private ArrayList<Branch> branchList;
    private ArrayList<Teacher> teacherList;
    private StudentSelectionAdapter studentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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

        binding.btnDanhSachSV.setOnClickListener(view -> {
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
        binding.txtMaLopDetail.setText(maLop);
        binding.txtTenLopDetail.setText(tenLop);
        binding.txtKhoaDetail.setText(khoa);
        binding.txtGiangVienDetail.setText(giangVien);
        binding.txtNamHocDetail.setText(namHoc);
    }

    private void setupButtonListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnList.setOnClickListener(v -> {
            isVisible = !isVisible;
            if (isVisible) {
                binding.btnEdit.setVisibility(View.VISIBLE);
                binding.btnDelete.setVisibility(View.VISIBLE);
            } else {
                binding.btnEdit.setVisibility(View.GONE);
                binding.btnDelete.setVisibility(View.GONE);
            }
        });
        binding.btnEdit.setOnClickListener(v -> showEditDialog());
        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
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
            List<Student> selectedStudents = studentAdapter.getSelectedStudents();
            List<Student> selectableStudents = studentAdapter.getSelectedStudents();
            boolean allSelected = selectedStudents.size() == selectableStudents.size();
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
            updateClassAndStudents(updatedMaLop, updatedTenLop, selectedBranch, selectedTeacher,
                    updatedNamHoc, selectedStudents, selectedStudentIds, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateClassAndStudents(String updatedMaLop, String updatedTenLop, Branch selectedBranch,
                                        Teacher selectedTeacher, String updatedNamHoc,
                                        List<Student> selectedStudents, List<String> selectedStudentIds,
                                        Dialog dialog) {
        Class simplifiedClass = new Class(
                id,
                updatedMaLop,
                updatedTenLop,
                null,
                null,
                updatedNamHoc,
                null
        );

        Class updatedClass = new Class(
                id,
                updatedMaLop,
                updatedTenLop,
                selectedBranch,
                selectedTeacher,
                updatedNamHoc,
                selectedStudents
        );

        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> allStudents) {
                List<Student> studentsToAdd = new ArrayList<>();
                List<Student> studentsToRemove = new ArrayList<>();

                for (Student student : selectedStudents) {
                    boolean isNewStudent = true;
                    for (String existingId : danhSachSinhVienIds) {
                        if (student.getId().equals(existingId)) {
                            isNewStudent = false;
                            break;
                        }
                    }
                    if (isNewStudent) {
                        studentsToAdd.add(student);
                    }
                }
                for (Student student : allStudents) {
                    if (danhSachSinhVienIds.contains(student.getId()) &&
                            !selectedStudentIds.contains(student.getId())) {
                        studentsToRemove.add(student);
                    }
                }

                classDatabaseHelper.update(id, updatedClass, new DatabaseHelper.DatabaseActionCallback() {
                    @Override
                    public void onSuccess() {
                        for (Student student : studentsToRemove) {
                            student.setStudentClass(null);
                            studentDatabaseHelper.update(student.getId(), student, new DatabaseHelper.DatabaseActionCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("DetailClass", "Đã xóa sinh viên " + student.getStudentName() + " khỏi lớp");
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e("DetailClass", "Lỗi xóa sinh viên khỏi lớp: " + error);
                                }
                            });
                        }

                        for (Student student : studentsToAdd) {
                            student.setStudentClass(simplifiedClass);
                            studentDatabaseHelper.update(student.getId(), student, new DatabaseHelper.DatabaseActionCallback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("DetailClass", "Đã thêm sinh viên " + student.getStudentName() + " vào lớp");
                                }

                                @Override
                                public void onFailure(String error) {
                                    Log.e("DetailClass", "Lỗi thêm sinh viên vào lớp: " + error);
                                }
                            });
                        }

                        runOnUiThread(() -> {
                            Toast.makeText(DetailClassActivity.this, "Cập nhật lớp học thành công!", Toast.LENGTH_SHORT).show();

                            maLop = updatedMaLop;
                            tenLop = updatedTenLop;
                            khoa = selectedBranch.getBranchName();
                            giangVien = selectedTeacher.getTeacherName();
                            namHoc = updatedNamHoc;
                            danhSachSinhVienIds = selectedStudentIds;

                            binding.txtMaLopDetail.setText(maLop);
                            binding.txtTenLopDetail.setText(tenLop);
                            binding.txtKhoaDetail.setText(khoa);
                            binding.txtGiangVienDetail.setText(giangVien);
                            binding.txtNamHocDetail.setText(namHoc);
                            sendRefreshBroadcast();
                            dialog.dismiss();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() ->
                                Toast.makeText(DetailClassActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(DetailClassActivity.this, "Lỗi tải sinh viên: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void sendRefreshBroadcast() {
        try {
            Intent intent = new Intent("ACTION_DATA_UPDATED");
            intent.setPackage(getPackageName());
            intent.putExtra("UPDATE_TYPE", "STUDENT_CLASS_UPDATE");
            sendBroadcast(intent);
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(new Intent("ACTION_DATA_UPDATED_LOCAL"));
        } catch (Exception e) {
            Log.e("BroadcastError", "Failed to send broadcast", e);
        }
    }

    private void loadStudentsForEdit(Dialog dialog) {
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> classes) {
                java.util.Map<String, Integer> studentClassCount = new java.util.HashMap<>();
                Set<String> currentClassStudentIds = new HashSet<>(danhSachSinhVienIds);
                for (Class aClass : classes) {
                    if (aClass.getDanhSachSinhVien() != null) {
                        for (Student student : aClass.getDanhSachSinhVien()) {
                            String studentId = student.getId();
                            studentClassCount.put(studentId,
                                    studentClassCount.getOrDefault(studentId, 0) + 1);
                        }
                    }
                }

                studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
                    @Override
                    public void onSuccess(List<Student> allStudents) {
                        List<Student> studentsToShow = new ArrayList<>();
                        List<Student> currentClassStudents = new ArrayList<>();
                        Set<String> maxClassStudentIds = new HashSet<>();

                        for (Student student : allStudents) {
                            int classCount = studentClassCount.getOrDefault(student.getId(), 0);
                            if (currentClassStudentIds.contains(student.getId())) {
                                classCount--;
                            }
                            studentsToShow.add(student);
                            if (classCount >= 3 && !currentClassStudentIds.contains(student.getId())) {
                                maxClassStudentIds.add(student.getId());
                            }
                            if (currentClassStudentIds.contains(student.getId())) {
                                currentClassStudents.add(student);
                            }
                        }

                        runOnUiThread(() -> {
                            studentAdapter.clear();
                            studentAdapter.addAll(studentsToShow);
                            studentAdapter.setMaxClassStudentIds(maxClassStudentIds);
                            for (int i = 0; i < studentsToShow.size(); i++) {
                                Student student = studentsToShow.get(i);
                                if (currentClassStudents.contains(student)) {
                                    studentAdapter.setSelected(i, true);
                                }
                            }
                            int eligibleCount = studentsToShow.size() - maxClassStudentIds.size();
                            if (maxClassStudentIds.size() > 0) {
                                Toast.makeText(DetailClassActivity.this,
                                        "Có " + eligibleCount + " sinh viên có thể chọn và " +
                                                maxClassStudentIds.size() + " sinh viên đã đạt giới hạn 3 lớp.",
                                        Toast.LENGTH_SHORT).show();
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

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(DetailClassActivity.this,
                                "Lỗi tải danh sách lớp học: " + error,
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateSelectAllButton(Dialog dialog) {
        Button btnSelectAll = dialog.findViewById(R.id.btnSelectAll);
        if (btnSelectAll != null) {
            List<Student> selectedStudents = studentAdapter.getSelectedStudents();
            List<Student> selectableStudents = studentAdapter.getSelectedStudents();
            boolean allSelected = selectedStudents.size() == selectableStudents.size();
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
                            Toast.makeText(DetailClassActivity.this, "Xóa lớp học thành công!", Toast.LENGTH_SHORT).show();
                            updateStudentsAfterClassDeletion();
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

    private void updateStudentsAfterClassDeletion() {
        if (danhSachSinhVienIds == null || danhSachSinhVienIds.isEmpty()) {
            return;
        }

        studentDatabaseHelper.getList(Student.class, new DatabaseHelper.DatabaseCallback<Student>() {
            @Override
            public void onSuccess(List<Student> students) {
                for (Student student : students) {
                    if (danhSachSinhVienIds.contains(student.getId())) {
                        student.setStudentClass(null);
                        studentDatabaseHelper.update(student.getId(), student, new DatabaseHelper.DatabaseActionCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("DetailClass", "Cập nhật sinh viên sau khi xóa lớp thành công");
                            }

                            @Override
                            public void onFailure(String error) {
                                Log.e("DetailClass", "Lỗi cập nhật sinh viên sau khi xóa lớp: " + error);
                            }
                        });
                    }
                }
                sendRefreshBroadcast();
            }

            @Override
            public void onFailure(String error) {
                Log.e("DetailClass", "Lỗi tải sinh viên sau khi xóa lớp: " + error);
            }
        });
    }
}