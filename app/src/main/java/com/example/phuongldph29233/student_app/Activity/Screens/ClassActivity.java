package com.example.phuongldph29233.student_app.Activity.Screens;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Activity.Detail.DetailClassActivity;
import com.example.phuongldph29233.student_app.Adapter.ClassAdapter;
import com.example.phuongldph29233.student_app.Adapter.StudentSelectionAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.SubjectController;
import com.example.phuongldph29233.student_app.Controller.TeacherController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.Helper.SessionManager;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityClassBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassActivity extends AppCompatActivity {
    private ActivityClassBinding binding;
    private DatabaseHelper<Class> databaseHelper;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Class> classDatabaseHelper;
    private SessionManager sessionManager;
    private ClassAdapter classAdapter;
    private ArrayList<Class> classArrayList;
    private ArrayList<Class> originalArrayList;
    private ArrayAdapter<Branch> branchAdapter;
    private ArrayAdapter<Teacher> teacherAdapter;
    private ArrayAdapter<Subject> subjectAdapter;
    private ArrayList<Branch> branchList;
    private ArrayList<Teacher> teacherList;
    private ArrayList<Subject> subjectList;
    private BranchController branchController;
    private TeacherController teacherController;
    private SubjectController subjectController;
    private ArrayList<Student> studentsWithoutClass;
    private StudentSelectionAdapter studentAdapter;
    private String username;
    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize SessionManager and retrieve user info
        sessionManager = new SessionManager(this);
        username = getIntent().getStringExtra("username");
        role = getIntent().getStringExtra("role");
        if (username == null) username = sessionManager.getUsername();
        if (role == null) role = sessionManager.getRole();

        // Validate user info
        if (username == null || role == null || !Arrays.asList("admin", "teacher", "student").contains(role)) {
            Toast.makeText(this, "Lỗi: Thông tin người dùng không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize DatabaseHelpers
        try {
            databaseHelper = new DatabaseHelper<>("Classes");
            studentDatabaseHelper = new DatabaseHelper<>("Student");
            classDatabaseHelper = new DatabaseHelper<>("Classes");
        } catch (Exception e) {
            Log.e("ClassActivity", "Lỗi khởi tạo DatabaseHelper: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize lists and adapters
        classArrayList = new ArrayList<>();
        originalArrayList = new ArrayList<>();
        studentsWithoutClass = new ArrayList<>();
        classAdapter = new ClassAdapter(classArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(classAdapter);

        // Initialize components for admin role
        if ("admin".equals(role)) {
            branchList = new ArrayList<>();
            teacherList = new ArrayList<>();
            subjectList = new ArrayList<>();
            branchController = new BranchController();
            teacherController = new TeacherController();
            subjectController = new SubjectController();
            branchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchList);
            branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            teacherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
            teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList);
            subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        // Setup views based on role
        setupViews();
        loadData();
    }

    private void setupViews() {
        binding.btnBack.setOnClickListener(v -> finish());
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

        switch (role) {
            case "admin":
                binding.btnAdd.setVisibility(View.VISIBLE);
                binding.btnAdd.setOnClickListener(v -> showDialogAdd());
                break;
            case "teacher":
            case "student":
                binding.btnAdd.setVisibility(View.GONE);
                break;
        }
    }

    private void loadData() {
        if ("admin".equals(role)) {
            loadBranches();
            loadTeachers();
            loadSubjects();
        }
        loadDataClass();
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

    private void loadSubjects() {
        subjectController.getSubject(new DatabaseHelper.DatabaseCallback<Subject>() {
            @Override
            public void onSuccess(List<Subject> itemList) {
                runOnUiThread(() -> {
                    subjectList.clear();
                    subjectList.addAll(itemList);
                    subjectAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(ClassActivity.this, "Lỗi tải môn học: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadDataClass() {
        databaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Class> list) {
                Log.d("ClassActivity", "Số lớp học gốc: " + list.size());
                for (Class cls : list) {
                    Log.d("ClassActivity", "Lớp: " + cls.getTenLop() +
                            ", TeacherID: " + (cls.getGiangVien() != null ? cls.getGiangVien().getTeacherID() : "null"));
                }
                runOnUiThread(() -> {
                    classArrayList.clear();
                    originalArrayList.clear();

                    switch (role) {
                        case "admin":
                            Log.d("ClassActivity", "Admin: Số lớp trước lọc: " + list.size());
                            for (Class cls : list) {
                                if (cls != null && cls.getId() != null) {
                                    classArrayList.add(cls);
                                }
                            }
                            Log.d("ClassActivity", "Admin: Số lớp sau lọc: " + classArrayList.size());
                            break;
                        case "teacher":
                            Log.d("ClassActivity", "Teacher: Username = " + username);
                            for (Class cls : list) {
                                if (cls == null) {
                                    Log.d("ClassActivity", "Bỏ qua lớp: Lớp null");
                                    continue;
                                }
                                if (cls.getGiangVien() == null) {
                                    Log.d("ClassActivity", "Bỏ qua lớp: " + cls.getTenLop() + ", Teacher null");
                                    continue;
                                }
                                if (cls.getGiangVien().getTeacherID() == null) {
                                    Log.d("ClassActivity", "Bỏ qua lớp: " + cls.getTenLop() + ", TeacherID null");
                                    continue;
                                }
                                if (cls.getGiangVien().getTeacherID().trim().equals(username.trim())) {
                                    classArrayList.add(cls);
                                    Log.d("ClassActivity", "Lớp được thêm: " + cls.getTenLop() + ", TeacherID: " + cls.getGiangVien().getTeacherID());
                                } else {
                                    Log.d("ClassActivity", "Bỏ qua lớp: " + cls.getTenLop() + ", TeacherID: " + cls.getGiangVien().getTeacherID() + " không khớp với " + username);
                                }
                            }
                            Log.d("ClassActivity", "Teacher: Số lớp sau lọc: " + classArrayList.size());
                            break;
                        case "student":
                            Log.d("ClassActivity", "Student: Username = " + username);
                            for (Class cls : list) {
                                if (cls != null && cls.getDanhSachSinhVien() != null) {
                                    for (Student student : cls.getDanhSachSinhVien()) {
                                        if (student != null && student.getStudentID() != null
                                                && student.getStudentID().trim().equals(username.trim())) {
                                            classArrayList.add(cls);
                                            Log.d("ClassActivity", "Lớp được thêm: " + cls.getTenLop());
                                            break;
                                        }
                                    }
                                }
                            }
                            Log.d("ClassActivity", "Student: Số lớp sau lọc: " + classArrayList.size());
                            break;
                    }

                    originalArrayList.addAll(classArrayList);
                    classAdapter.notifyDataSetChanged();
                    binding.recyclerView.setVisibility(classArrayList.isEmpty() ? View.GONE : View.VISIBLE);
                    if (classArrayList.isEmpty()) {
                        String message;
                        switch (role) {
                            case "admin":
                                message = "Hệ thống chưa có lớp học nào!";
                                break;
                            case "teacher":
                                message = "Bạn chưa được gán lớp học nào với mã " + username + "!";
                                break;
                            case "student":
                                message = "Bạn chưa tham gia lớp học nào với mã " + username + "!";
                                break;
                            default:
                                message = "Không có lớp học nào!";
                        }
                        Toast.makeText(ClassActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Log.e("ClassActivity", "Lỗi tải dữ liệu lớp: " + error);
                runOnUiThread(() -> {
                    binding.recyclerView.setVisibility(View.GONE);
                    Toast.makeText(ClassActivity.this, "Lỗi tải dữ liệu lớp: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showDialogAdd() {
        if (!"admin".equals(role)) {
            Toast.makeText(this, "Chỉ admin có thể thêm lớp học!", Toast.LENGTH_SHORT).show();
            return;
        }

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
        EditText edtMaLop = dialog.findViewById(R.id.edt_maLop_add);
        EditText edtTenLop = dialog.findViewById(R.id.edt_tenLop_add);
        Spinner spnKhoaAdd = dialog.findViewById(R.id.edt_khoa_add);
        Spinner spnGiangVienAdd = dialog.findViewById(R.id.edt_giangVien_add);
        Spinner spnMonHocAdd = dialog.findViewById(R.id.edt_Monhoc_add);
        EditText edtNamHoc = dialog.findViewById(R.id.edt_namHoc_add);
        Button btnHuy = dialog.findViewById(R.id.btn_huy_lop);
        Button btnAdd = dialog.findViewById(R.id.btn_add_lop);
        ListView lvStudents = dialog.findViewById(R.id.lvStudents);
        Button btnSelectAll = dialog.findViewById(R.id.btnSelectAll);

        if (txtTitle == null || edtMaLop == null || edtTenLop == null || spnKhoaAdd == null ||
                spnGiangVienAdd == null || spnMonHocAdd == null || edtNamHoc == null ||
                btnHuy == null || btnAdd == null || lvStudents == null || btnSelectAll == null) {
            Toast.makeText(this, "Lỗi hiển thị dialog", Toast.LENGTH_SHORT).show();
            return;
        }

        runOnUiThread(() -> {
            txtTitle.setText("Thêm lớp học");
            spnKhoaAdd.setAdapter(branchAdapter);
            spnMonHocAdd.setAdapter(subjectAdapter);
            spnGiangVienAdd.setAdapter(teacherAdapter);
        });

        studentAdapter = new StudentSelectionAdapter(this, new ArrayList<>());
        lvStudents.setAdapter(studentAdapter);
        loadStudentsWithoutClass();

        btnSelectAll.setOnClickListener(v -> {
            boolean selectAll = studentAdapter.getSelectedStudents().size() != studentsWithoutClass.size();
            studentAdapter.selectAll(selectAll);
            btnSelectAll.setText(selectAll ? "Bỏ chọn tất cả" : "Chọn tất cả");
        });

        HelperUtils.setupDatePicker(this, edtNamHoc);

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maLop = edtMaLop.getText().toString().trim();
            String tenLop = edtTenLop.getText().toString().trim();
            Branch khoa = (Branch) spnKhoaAdd.getSelectedItem();
            Subject selectedSubject = (Subject) spnMonHocAdd.getSelectedItem();
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

            if (selectedSubject == null || selectedSubject.getSubjectName() == null || selectedSubject.getSubjectID().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn môn học hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (giangVien == null || giangVien.getTeacherName() == null || giangVien.getTeacherID().trim().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn giảng viên hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Student> selectedStudents = studentAdapter.getSelectedStudents();
            Class newClass = new Class(id, maLop, tenLop, khoa, selectedSubject, giangVien, namHoc, selectedStudents);
            addClassAndUpdateStudents(newClass, selectedStudents, dialog);
        });

        btnHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addClassAndUpdateStudents(Class newClass, List<Student> selectedStudents, Dialog dialog) {
        Class simplifiedClass = new Class(
                newClass.getId(),
                newClass.getMaLop(),
                newClass.getTenLop(),
                null,
                null,
                null,
                newClass.getNamHoc(),
                null
        );

        databaseHelper.add(newClass, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                AtomicInteger successCount = new AtomicInteger();
                int totalStudents = selectedStudents.size();

                if (totalStudents == 0) {
                    runOnUiThread(() -> {
                        sendRefreshBroadcast();
                        Toast.makeText(ClassActivity.this, "Thêm lớp thành công", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadDataClass();
                    });
                    return;
                }

                for (Student student : selectedStudents) {
                    student.setStudentClass(simplifiedClass);
                    studentDatabaseHelper.update(student.getId(), student,
                            new DatabaseHelper.DatabaseActionCallback() {
                                @Override
                                public void onSuccess() {
                                    if (successCount.incrementAndGet() == totalStudents) {
                                        runOnUiThread(() -> {
                                            removeUpdatedStudentsFromList(selectedStudents);
                                            sendRefreshBroadcast();
                                            Toast.makeText(ClassActivity.this,
                                                    "Thêm lớp và cập nhật " + totalStudents + " sinh viên thành công",
                                                    Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            loadDataClass();
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(String error) {
                                    runOnUiThread(() -> {
                                        databaseHelper.delete(newClass.getId(), new DatabaseHelper.DatabaseActionCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(ClassActivity.this,
                                                        "Đã hủy tạo lớp do lỗi cập nhật sinh viên",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(String rollbackError) {
                                                Log.e("ClassActivity", "Rollback failed: " + rollbackError);
                                            }
                                        });
                                        Toast.makeText(ClassActivity.this,
                                                "Lỗi cập nhật sinh viên: " + error + ". Đã hủy tạo lớp.",
                                                Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                }
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(ClassActivity.this,
                                "Lỗi thêm lớp: " + error,
                                Toast.LENGTH_SHORT).show());
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

    private void removeUpdatedStudentsFromList(List<Student> updatedStudents) {
        Iterator<Student> iterator = studentsWithoutClass.iterator();
        while (iterator.hasNext()) {
            Student student = iterator.next();
            for (Student updated : updatedStudents) {
                if (student.getStudentID().equals(updated.getStudentID())) {
                    iterator.remove();
                    break;
                }
            }
        }
        studentAdapter.notifyDataSetChanged();
    }

    private void loadStudentsWithoutClass() {
        classDatabaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @Override
            public void onSuccess(List<Class> classes) {
                java.util.Map<String, Integer> studentClassCount = new java.util.HashMap<>();
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
                    public void onSuccess(List<Student> students) {
                        List<Student> allStudents = new ArrayList<>();
                        Set<String> maxClassStudentIds = new HashSet<>();

                        for (Student student : students) {
                            int classCount = studentClassCount.getOrDefault(student.getId(), 0);
                            allStudents.add(student);
                            if (classCount >= 3) {
                                maxClassStudentIds.add(student.getId());
                            }
                        }

                        runOnUiThread(() -> {
                            studentsWithoutClass.clear();
                            studentsWithoutClass.addAll(allStudents);
                            studentAdapter.clear();
                            studentAdapter.addAll(allStudents);
                            studentAdapter.setMaxClassStudentIds(maxClassStudentIds);

                            int eligibleCount = allStudents.size() - maxClassStudentIds.size();
                            if (eligibleCount == 0) {
                                Toast.makeText(ClassActivity.this,
                                        "Tất cả sinh viên đã có đủ 3 lớp học.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (maxClassStudentIds.size() > 0) {
                                Toast.makeText(ClassActivity.this,
                                        "Có " + eligibleCount + " sinh viên có thể chọn và " +
                                                maxClassStudentIds.size() + " sinh viên đã đạt giới hạn 3 lớp.",
                                        Toast.LENGTH_SHORT).show();
                            }
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

            @Override
            public void onFailure(String error) {
                runOnUiThread(() ->
                        Toast.makeText(ClassActivity.this,
                                "Lỗi tải danh sách lớp học: " + error,
                                Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void searchList(String text) {
        ArrayList<Class> filteredList = new ArrayList<>();
        for (Class data : originalArrayList) {
            if (data.getTenLop().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        classAdapter.searchClass(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}