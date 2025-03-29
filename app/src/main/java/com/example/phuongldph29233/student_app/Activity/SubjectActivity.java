package com.example.phuongldph29233.student_app.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import com.example.phuongldph29233.student_app.Adapter.BranchAdapter;
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
    private ArrayAdapter arrayAdapter;
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
        EditText edt_maMon_add = dialog.findViewById(R.id.edt_maMon_add);
        EditText edt_tenMon_add = dialog.findViewById(R.id.edt_tenMon_add);
        Spinner spn_chuyenNganh = dialog.findViewById(R.id.spn_chuyenNganh);
        EditText edt_soTin_add = dialog.findViewById(R.id.edt_soTin_add);
        Button btn_huy = dialog.findViewById(R.id.btn_huy_mon);
        Button btn_add = dialog.findViewById(R.id.btn_add_mon);
        txt_title_subject.setText("Thêm môn học");
        //Lấy dữ liệu branch
        loadDataBranch();
        //Gán dữ liệu vào spinner
        spn_chuyenNganh.setAdapter(arrayAdapter);

        // Sự kiện add
        btn_add.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maMon = edt_maMon_add.getText().toString().trim();
            String tenMon = edt_tenMon_add.getText().toString().trim();
            String soTin = edt_soTin_add.getText().toString().trim();

            if (maMon.isEmpty() || tenMon.isEmpty() || soTin.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                return;
            }
            Branch selectedBranch = (Branch) spn_chuyenNganh.getSelectedItem();
            Subject subject = new Subject(id, maMon, tenMon, selectedBranch, soTin);
            addSubject(subject, dialog);
        });
        btn_huy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addSubject(Subject subject, Dialog dialog) {
        subjectController.addSubject(subject, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(SubjectActivity.this, "Thêm môn học thành công !!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
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
            if (data.getTenMon().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        subjectAdapter.searchSubject(filteredList);
    }
}