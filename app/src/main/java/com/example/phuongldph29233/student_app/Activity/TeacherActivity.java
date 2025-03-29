package com.example.phuongldph29233.student_app.Activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.TeacherAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.TeacherController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.Helper.HelperUtils;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityTeacherBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TeacherActivity extends AppCompatActivity {
    ActivityTeacherBinding binding;
    private BranchController branchController;
    private ArrayAdapter arrayAdapter;
    private ArrayList<Branch> branchArrayList;
    private TeacherController teacherController;
    private ArrayList<Teacher> teacherArrayList;
    private ArrayList<Teacher> originArrayList;
    private TeacherAdapter teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        branchController = new BranchController();
        teacherController = new TeacherController();
        branchArrayList = new ArrayList<>();
        teacherArrayList = new ArrayList<>();
        originArrayList = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(teacherArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(teacherAdapter);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        binding.btnBack.setOnClickListener(v -> finish());
        loadDataTeacher();

    }

    private void loadDataTeacher() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoData.setVisibility(View.GONE);
        teacherController.getTeacher(new DatabaseHelper.DatabaseCallback<Teacher>() {
            @Override
            public void onSuccess(List<Teacher> itemList) {
                teacherArrayList.clear();
                originArrayList.clear();
                teacherArrayList.addAll(itemList);
                originArrayList.addAll(itemList);
                teacherAdapter.notifyDataSetChanged();
                if (teacherArrayList.isEmpty()) {
                    binding.txtNoData.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                } else {
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.txtNoData.setVisibility(View.GONE);
                    Log.d("TAG", "onSuccess: Co du lieu");
                }
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.txtNoData.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                Toast.makeText(TeacherActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_teacher);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_title_Gv = dialog.findViewById(R.id.txt_title_teacher);
        EditText ed_maGv_add = dialog.findViewById(R.id.edt_maGV_add);
        EditText ed_tenGv_add = dialog.findViewById(R.id.edt_tenGV_add);
        EditText ed_emailGv_add = dialog.findViewById(R.id.edt_emailGv_add);
        HelperUtils.setupEmailValidation(ed_emailGv_add);
        EditText ed_soDtGv_add = dialog.findViewById(R.id.edt_soDTGv_add);
        HelperUtils.setupPhoneNumberValidation(ed_soDtGv_add);
        Spinner spn_chuyenNganh = dialog.findViewById(R.id.spn_chuyenNganhgv);
        Button btn_addGv = dialog.findViewById(R.id.btn_add_GV);
        Button btn_huyGv = dialog.findViewById(R.id.btn_huy_GV);
        txt_title_Gv.setText("Thêm giảng viên");
        loadDataBranch();
        spn_chuyenNganh.setAdapter(arrayAdapter);
        btn_addGv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = UUID.randomUUID().toString();
                String maGV = ed_maGv_add.getText().toString();
                String tenGV = ed_tenGv_add.getText().toString();
                String emailGV = ed_emailGv_add.getText().toString();
                String soDTGV = ed_soDtGv_add.getText().toString();
                Branch selectedItem = (Branch) spn_chuyenNganh.getSelectedItem();
                if (maGV.isEmpty() || tenGV.isEmpty() || emailGV.isEmpty() || soDTGV.isEmpty()) {
                    Toast.makeText(TeacherActivity.this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!HelperUtils.isValidPhoneNumber(soDTGV)) {
                    ed_soDtGv_add.setError("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0");
                    return;
                }
                if (!HelperUtils.isValidEmail(emailGV)) {
                    ed_emailGv_add.setError("Email không hợp lệ");
                    return;
                }
                Teacher teacher = new Teacher(id, maGV, tenGV, emailGV, soDTGV, selectedItem);
                addTeacher(teacher, dialog);
            }
        });
        btn_huyGv.setOnClickListener(v -> dialog.dismiss());
        dialog.show();


    }

    private void addTeacher(Teacher teacher, Dialog dialog) {
        teacherController.addTeacher(teacher, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TeacherActivity.this, "Thêm giảng viên thành công !!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(TeacherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}