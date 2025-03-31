package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Activity.Detail.DetailTeacherActivity;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.databinding.ViewHolderTeacherBinding;

import java.util.ArrayList;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {
    ArrayList<Teacher> items;
    Context context;

    public TeacherAdapter(ArrayList<Teacher> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public TeacherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolderTeacherBinding binding = ViewHolderTeacherBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull TeacherAdapter.ViewHolder holder, int position) {
        holder.binding.txtTeacherID.setText(items.get(position).getTeacherID());
        String teacherName = items.get(position).getTeacherName();
        if (teacherName.length() > 12) {
            teacherName = teacherName.substring(0, 12) + "...";
        }
        holder.binding.txtTeacherName.setText(teacherName);
        holder.binding.txtTeacherEmail.setText(items.get(position).getTeacherEmail());
        holder.binding.cardViewTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailTeacherActivity.class);
                intent.putExtra("id", items.get(position).getId());
                intent.putExtra("teacherID", items.get(position).getTeacherID());
                intent.putExtra("teacherName", items.get(position).getTeacherName());
                intent.putExtra("teacherEmail", items.get(position).getTeacherEmail());
                intent.putExtra("teacherPhone", items.get(position).getTeacherPhone());
                intent.putExtra("teacherBranch", items.get(position).getTeacherBranch().toString());
                context.startActivity(intent);
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchTeacher(ArrayList<Teacher> searchTeacher) {
        items = searchTeacher;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolderTeacherBinding binding;

        public ViewHolder(ViewHolderTeacherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
