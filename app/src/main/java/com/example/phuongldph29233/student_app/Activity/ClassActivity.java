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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.phuongldph29233.student_app.Adapter.ClassAdapter;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityClassBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClassActivity extends AppCompatActivity {
    ActivityClassBinding binding;
    private DatabaseHelper<Class> databaseHelper;
    private ClassAdapter classAdapter;
    private ArrayList<Class> classArrayList;
    private ArrayList<Class> originalArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityClassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper<>("Classes");
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDataClass() {
        databaseHelper.getList(Class.class, new DatabaseHelper.DatabaseCallback<Class>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSuccess(List<Class> list) {
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
            }

            @Override
            public void onFailure(String error) {
                binding.recyclerView.setVisibility(View.GONE);
                Toast.makeText(ClassActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogAdd() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_class);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Ánh xạ view
        TextView txtTitle = dialog.findViewById(R.id.textView);
        EditText edtMaLop = dialog.findViewById(R.id.edt_maLop_add);
        EditText edtTenLop = dialog.findViewById(R.id.edt_tenLop_add);
        EditText edtKhoa = dialog.findViewById(R.id.edt_khoa_add);
        EditText edtGiangVien = dialog.findViewById(R.id.edt_giangVien_add);
        EditText edtNamHoc = dialog.findViewById(R.id.edt_namHoc_add);
        Button btnHuy = dialog.findViewById(R.id.btn_huy_lop);
        Button btnAdd = dialog.findViewById(R.id.btn_add_lop);

        txtTitle.setText("Thêm lớp học");

        btnAdd.setOnClickListener(v -> {
            String id = UUID.randomUUID().toString();
            String maLop = edtMaLop.getText().toString().trim();
            String tenLop = edtTenLop.getText().toString().trim();
            String khoa = edtKhoa.getText().toString().trim();
            String giangVien = edtGiangVien.getText().toString().trim();
            String namHoc = edtNamHoc.getText().toString().trim();

            if (maLop.isEmpty() || tenLop.isEmpty() || khoa.isEmpty() ||
                    giangVien.isEmpty() || namHoc.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Class newClass = new Class(id, maLop, tenLop, khoa, giangVien, namHoc);
            addClass(newClass, dialog);
        });

        btnHuy.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void addClass(Class newClass, Dialog dialog) {
        databaseHelper.add(newClass, new DatabaseHelper.DatabaseActionCallback() {
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