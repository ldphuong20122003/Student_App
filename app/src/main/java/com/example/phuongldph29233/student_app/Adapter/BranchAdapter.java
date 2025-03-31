package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.databinding.ViewHolderBranchBinding;

import java.util.ArrayList;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {
    ArrayList<Branch> items;
    Context context;

    public BranchAdapter(ArrayList<Branch> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public BranchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolderBranchBinding binding = ViewHolderBranchBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.txtTenKhoa.setText(items.get(position).getTenKhoa());
        holder.binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BranchController branchController = new BranchController();
                Branch selectedBranch = items.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có chắc chắn muốn xóa không ?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        branchController.deleteBranch(selectedBranch, new BranchController.DeleteBranchCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(context, "Xóa khoa thành công !!!", Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailed(String error) {
                                Toast.makeText(context, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolderBranchBinding binding;


        public ViewHolder(ViewHolderBranchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
