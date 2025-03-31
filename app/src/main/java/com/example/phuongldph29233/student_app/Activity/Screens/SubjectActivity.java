package com.example.phuongldph29233.student_app.Activity.Screens;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Activity.Detail.DetailSubjectActivity;
import com.example.phuongldph29233.student_app.Adapter.SubjectAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.SubjectController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Khởi tạo controller
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

        //function
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());
        loadDataSubject();
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

    private void showDialogAdd() {
        //Tạo Dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_subject);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //Ánh xạ sự kiện
        TextView txt_title_subject = dialog.findViewById(R.id.txt_title_subject);
        EditText edt_subjectID_add = dialog.findViewById(R.id.edt_subjectID_add);
        EditText edt_subjectName_add = dialog.findViewById(R.id.edt_subjectName_add);
        EditText edt_subjectNOC_add = dialog.findViewById(R.id.edt_subjectNOC_add);
        Spinner spn_subjectBranch = dialog.findViewById(R.id.spn_subjectBranch);
        Button btn_add = dialog.findViewById(R.id.btn_add_subject);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel_subject);
        txt_title_subject.setText("Thêm môn học");
        //Lấy dữ liệu branch
        loadDataBranch();
        //Gán dữ liệu vào spinner
        spn_subjectBranch.setAdapter(arrayAdapter);

        // Sự kiện add
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
                loadDataSubject();
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

    @Override
    protected void onResume() {
        loadDataSubject();
        super.onResume();
    }
}