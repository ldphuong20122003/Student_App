package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.databinding.ViewHolderBranchBinding;

import java.util.ArrayList;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {
    private ArrayList<Branch> items;
    private Context context;
    private DatabaseHelper<Branch> branchDatabaseHelper;

    public BranchAdapter(ArrayList<Branch> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolderBranchBinding binding = ViewHolderBranchBinding.inflate(LayoutInflater.from(context), parent, false);
        try {
            branchDatabaseHelper = new DatabaseHelper<>("Branches");
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi khởi tạo cơ sở dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Branch selectedBranch = items.get(position); // Biến final để sử dụng trong lambda
        holder.binding.txtBranchName.setText("Tên khoa: " + selectedBranch.getBranchName());
        holder.binding.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Bạn có chắc chắn muốn xóa khoa " + selectedBranch.getBranchName() + "?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                branchDatabaseHelper.delete(String.valueOf(selectedBranch), new DatabaseHelper.DatabaseActionCallback() {
                    @Override
                    public void onSuccess() {
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, items.size());
                        Toast.makeText(context, "Xóa khoa thành công!", Toast.LENGTH_SHORT).show();
                        // Gửi broadcast để làm mới dữ liệu
                        Intent intent = new Intent("ACTION_DATA_UPDATED_LOCAL");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(context, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchBranch(ArrayList<Branch> searchBranch) {
        items = searchBranch;
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<Branch> newList) {
        items = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolderBranchBinding binding;

        public ViewHolder(ViewHolderBranchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}