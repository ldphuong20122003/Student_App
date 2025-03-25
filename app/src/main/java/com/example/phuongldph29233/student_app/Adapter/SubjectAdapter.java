package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Activity.DetailSubjectActivity;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.databinding.ViewHolderBranchBinding;
import com.example.phuongldph29233.student_app.databinding.ViewHolderSubjectBinding;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> {
    ArrayList<Subject> items;
    Context context;

    public SubjectAdapter(ArrayList<Subject> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SubjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolderSubjectBinding binding = ViewHolderSubjectBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.txtMaMon.setText(items.get(position).getMaMon());
        holder.binding.txtTenMon.setText(items.get(position).getTenMon());
        holder.binding.txtSoTin.setText("Số tín: " + items.get(position).getSoTinChi());
        holder.binding.cardViewSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailSubjectActivity.class);
                intent.putExtra("id", items.get(position).getId());
                intent.putExtra("maMon", items.get(position).getMaMon());
                intent.putExtra("tenMon", items.get(position).getTenMon());
                intent.putExtra("chuyenNganh", items.get(position).getChuyenNganh().toString());
                intent.putExtra("soTin", items.get(position).getSoTinChi());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchSubject(ArrayList<Subject> searchSubject) {
        items = searchSubject;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolderSubjectBinding binding;

        public ViewHolder(ViewHolderSubjectBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
