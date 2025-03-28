package com.example.phuongldph29233.student_app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    @Override
    public void onBindViewHolder(@NonNull TeacherAdapter.ViewHolder holder, int position) {
        holder.binding.txtMaGv.setText(items.get(position).getMaGV());
        holder.binding.txtTenGv.setText(items.get(position).getTenGV());
        holder.binding.txtEmail.setText(items.get(position).getEmail());

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
