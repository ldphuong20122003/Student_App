package com.example.phuongldph29233.student_app.Adapter;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Activity.DetailClassActivity;
import com.example.phuongldph29233.student_app.Activity.DetailStudentActivity;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.AdapterHelper;
import com.example.phuongldph29233.student_app.databinding.ViewHolderClassBinding;
import com.example.phuongldph29233.student_app.databinding.ViewHolderStudentBinding;

import java.util.List;

public class ClassStudentAdapter extends AdapterHelper<Student, ViewHolderStudentBinding> {

    public ClassStudentAdapter(List<Student> items) {
        super(items);
    }

    public ClassStudentAdapter(List<Student> items, OnItemActionListener<Student> listener) {
        super(items, listener);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolderStudentBinding binding = ViewHolderStudentBinding.inflate(LayoutInflater.from(context), parent, false);
        return new StudentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ((StudentViewHolder) holder).bind(items.get(position));
    }

    public class StudentViewHolder extends BaseViewHolder {
        public StudentViewHolder(ViewHolderStudentBinding binding) {
            super(binding);
        }

        @Override
        public void bind(Student studentItem) {
            binding.txtMaSV.setText(studentItem.getMaSV());
            binding.txtTenSV.setText(studentItem.getTenSV());
            binding.txtLopHoc.setText(studentItem.getLopHoc().toString());
            binding.txtHeDaotao.setText(studentItem.getHeDaoTao());
            binding.cardViewStudent.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onItemClick(studentItem);
                }else {
                    Intent intent = new Intent(context, DetailStudentActivity.class);
                    intent.putExtra("id", studentItem.getId());
                    intent.putExtra("maSV", studentItem.getMaSV());
                    intent.putExtra("tenSV", studentItem.getTenSV());
                    intent.putExtra("ngaySinh", studentItem.getNgaySinh());
                    intent.putExtra("queQuan", studentItem.getQueQuan());
                    intent.putExtra("soDienThoai", studentItem.getSoDienThoai());
                    intent.putExtra("email", studentItem.getEmail());
                    intent.putExtra("lopHoc", studentItem.getLopHoc().toString());
                    intent.putExtra("ngayNhapHoc", studentItem.getNgayNhapHoc());
                    intent.putExtra("chuyenNganh", studentItem.getChuyenNganh().toString());
                    intent.putExtra("heDaoTao",studentItem.getHeDaoTao());
                    context.startActivity(intent);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchClassStudent(List<Student> searchStudent) {
        updateDataSet(searchStudent);
    }
}
