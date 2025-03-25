package com.example.phuongldph29233.student_app.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.BranchAdapter;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityBranchBinding;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class BranchActivity extends AppCompatActivity {
    private ActivityBranchBinding binding;
    private BranchController branchController;
    private BranchAdapter adapter;
    private ArrayList<Branch> branchList;
    private ArrayList<Branch> originalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBranchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Khởi tạo Controller
        branchController = new BranchController();

        // Cấu hình RecyclerView
        branchList = new ArrayList<>();
        originalList = new ArrayList<>();
        adapter = new BranchAdapter(branchList);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        // Load dữ liệu từ Firebase
        loadDataBranch();

        // Bắt sự kiện click
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAdd.setOnClickListener(v -> showDialogAdd());

        // Bắt sự kiện tìm kiếm
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

    private void loadDataBranch() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.txtNoData.setVisibility(View.GONE);

        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchList.clear();
                originalList.clear();
                branchList.addAll(list);
                originalList.addAll(list);
                adapter.notifyDataSetChanged();

                if (branchList.isEmpty()) {
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
                Toast.makeText(BranchActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_branch);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edt_maKhoa_add = dialog.findViewById(R.id.edt_maKhoa_add);
        EditText edt_tenKhoa_add = dialog.findViewById(R.id.edt_tenKhoa_add);
        Button btn_huy = dialog.findViewById(R.id.btn_huy_khoa);
        Button btn_add = dialog.findViewById(R.id.btn_add_khoa);

        btn_add.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maKhoa = edt_maKhoa_add.getText().toString().trim();
            String tenKhoa = edt_tenKhoa_add.getText().toString().trim();

            if (maKhoa.isEmpty() || tenKhoa.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                return;
            }

            Branch branch = new Branch(id, maKhoa, tenKhoa);
            addBranch(branch, dialog);
        });

        btn_huy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addBranch(Branch branch, Dialog dialog) {
        branchController.addBranch(branch, new BranchController.AddBranchCallback() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess() {
                Toast.makeText(BranchActivity.this, "Thêm Khoa thành công !!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                adapter.notifyDataSetChanged();
                loadDataBranch();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(BranchActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void searchList(String text) {
        ArrayList<Branch> filteredList = new ArrayList<>();
        for (Branch data : originalList) {
            if (data.getTenKhoa().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(data);
            }
        }
        adapter.searchBranch(filteredList);
    }
}
