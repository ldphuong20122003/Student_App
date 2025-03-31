package com.example.phuongldph29233.student_app.Activity.Screens;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.TeacherAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.TeacherController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.Validator.Validator;
import com.example.phuongldph29233.student_app.databinding.ActivityTeacherBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class TeacherActivity extends AppCompatActivity {
    private ActivityTeacherBinding binding;
    private BranchController branchController;
    private ArrayAdapter<Branch> arrayAdapter;
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
        initControllers();
        initRecycleView();
        initUI();
        loadDataTeacher();
    }


    private void initControllers() {
        branchController = new BranchController();
        teacherController = new TeacherController();
        branchArrayList = new ArrayList<>();
        teacherArrayList = new ArrayList<>();
        originArrayList = new ArrayList<>();
    }

    private void initRecycleView() {
        teacherAdapter = new TeacherAdapter(teacherArrayList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(teacherAdapter);
    }

    private void initUI() {
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        binding.btnBack.setOnClickListener(v -> finish());
        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchTeacher(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void searchTeacher(String text) {
        ArrayList<Teacher> filteredList = new ArrayList<>();
        for (Teacher data : originArrayList) {
            if (data.getTeacherName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        teacherAdapter.searchTeacher(filteredList);
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
        EditText edt_teacherID = dialog.findViewById(R.id.edt_teacherID_add);
        EditText edt_teacherName = dialog.findViewById(R.id.edt_teacherName_add);
        EditText edt_teacherEmail = dialog.findViewById(R.id.edt_teacherEmail_add);
        EditText edt_teacherPhone = dialog.findViewById(R.id.edt_teacherPhone_add);
        Spinner spn_teacherBranch = dialog.findViewById(R.id.spn_teacherBranch);
        Button btn_add = dialog.findViewById(R.id.btn_add_teacher);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel_teacher);
        txt_title_Gv.setText("Thêm giảng viên");
        loadDataBranch();
        spn_teacherBranch.setAdapter(arrayAdapter);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = UUID.randomUUID().toString();
                String teacherID = edt_teacherID.getText().toString();
                String teacherName = edt_teacherName.getText().toString();
                String teacherEmail = edt_teacherEmail.getText().toString();
                String teacherPhone = edt_teacherPhone.getText().toString();
                Branch selectedItem = (Branch) spn_teacherBranch.getSelectedItem();
                if (teacherID.isEmpty() || teacherName.isEmpty() || teacherEmail.isEmpty() || teacherPhone.isEmpty()) {
                    Toast.makeText(TeacherActivity.this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Validator.isValidEmail(teacherEmail)) {
                    Toast.makeText(TeacherActivity.this, "Email không hợp lệ !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Validator.isValidPhone(teacherPhone)) {
                    Toast.makeText(TeacherActivity.this, "Số điện thoại không hợp lệ !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Teacher teacher = new Teacher(id, teacherID, teacherName, teacherEmail, teacherPhone, selectedItem);
                btn_add.setEnabled(false);
                addTeacher(teacher, dialog);
            }
        });
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void addTeacher(Teacher teacher, Dialog dialog) {
        teacherController.addTeacher(teacher, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TeacherActivity.this, "Thêm giảng viên thành công !!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadDataTeacher();
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

    @Override
    protected void onResume() {
        loadDataTeacher();
        super.onResume();
    }

}