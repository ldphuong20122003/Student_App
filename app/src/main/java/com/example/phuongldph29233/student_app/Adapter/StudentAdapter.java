package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Activity.Detail.DetailClassActivity;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.AdapterHelper;
import com.example.phuongldph29233.student_app.databinding.ViewHolderStudentBinding;

import java.util.List;

public class StudentAdapter extends AdapterHelper<Student, ViewHolderStudentBinding> {

    public StudentAdapter(List<Student> items) {
        super(items);
    }

    public StudentAdapter(List<Student> items, OnItemActionListener<Student> listener) {
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
        holder.bind(items.get(position));
    }

    public class StudentViewHolder extends BaseViewHolder {
        public StudentViewHolder(ViewHolderStudentBinding binding) {
            super(binding);
        }

        @Override
        public void bind(Student studentItem) {
//            binding.txtMaLop.setText(studentItem.getId());
//            binding.txtKhoa.setText(studentItem.getMaSV());
//            binding.txtGiangvien.setText(studentItem.getTenSV());
//            binding.txtNamhoc.setText(studentItem.getNgaySinh());
            binding.cardViewStudent.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onItemClick(studentItem);
                } else {
                    Intent intent = new Intent(context, DetailClassActivity.class);
//                    intent.putExtra("id", studentItem.getId());
//s                    intent.putExtra("tenLop", classItem.getTenLop());
//                    intent.putExtra("khoa", classItem.getKhoa());
//                    intent.putExtra("giangVien", classItem.getGiangVien());
//                    intent.putExtra("namHoc",classItem.getNamHoc());
                    context.startActivity(intent);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchStudent(List<Student> searchStudent) {
        updateDataSet(searchStudent);
    }
}