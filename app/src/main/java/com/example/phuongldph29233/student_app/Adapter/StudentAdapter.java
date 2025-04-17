package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Activity.Detail.DetailStudentActivity;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.AdapterHelper;
import com.example.phuongldph29233.student_app.databinding.ViewHolderStudentBinding;

import java.io.Serializable;
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
            binding.txtStudentID.setText(studentItem.getStudentID());
            binding.txtStudentName.setText(studentItem.getStudentName());
            binding.txtStudentHomeTown.setText(studentItem.getStudentHomeTown());
            binding.txtStudentTOT.setText(studentItem.getStudentTOT());
            binding.txtStudentBranch.setText(studentItem.getStudentBranch().toString());
            binding.cardViewStudent.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onItemClick(studentItem);
                } else {
                    Intent intent = new Intent(context, DetailStudentActivity.class);
                    intent.putExtra("id", studentItem.getId());
                    intent.putExtra("studentID", studentItem.getStudentID());
                    intent.putExtra("studentName", studentItem.getStudentName());
                    intent.putExtra("studentBirthday", studentItem.getStudentBirthday());
                    intent.putExtra("studentHomeTown", studentItem.getStudentHomeTown());
                    intent.putExtra("studentPhone", studentItem.getStudentPhone());
                    intent.putExtra("studentEmail", studentItem.getStudentEmail());

                    if (studentItem.getStudentClass() != null &&
                            !studentItem.getStudentClass().toString().isEmpty() &&
                            !studentItem.getStudentClass().toString().equals("sdweqe")) {

                        if (studentItem.getStudentClass() instanceof Class) {
                            intent.putExtra("studentClass", (Class) studentItem.getStudentClass());
                        } else {
                            Log.e("StudentAdapter", "Invalid Class object: " + studentItem.getStudentClass());
                            intent.putExtra("studentClass", (Serializable) null);
                        }
                    } else {
                        intent.putExtra("studentClass", (Serializable) null);
                    }

                    intent.putExtra("studentDateJoin", studentItem.getStudentDateJoin());

                    // Nếu studentBranch cũng là đối tượng, sửa tương tự
                    intent.putExtra("studentBranch", studentItem.getStudentBranch().toString());

                    intent.putExtra("studentTOT", studentItem.getStudentTOT());
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