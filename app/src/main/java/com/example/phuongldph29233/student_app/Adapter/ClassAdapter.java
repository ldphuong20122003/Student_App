package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Activity.DetailClassActivity;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Helper.AdapterHelper;
import com.example.phuongldph29233.student_app.databinding.ViewHolderClassBinding;

import java.util.List;

public class ClassAdapter extends AdapterHelper<Class, ViewHolderClassBinding> {

    public ClassAdapter(List<Class> items) {
        super(items);
    }

    public ClassAdapter(List<Class> items, OnItemActionListener<Class> listener) {
        super(items, listener);
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolderClassBinding binding = ViewHolderClassBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ClassViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ((ClassViewHolder) holder).bind(items.get(position));
    }

    public class ClassViewHolder extends BaseViewHolder {
        public ClassViewHolder(ViewHolderClassBinding binding) {
            super(binding);
        }

        @Override
        public void bind(Class classItem) {
        binding.txtMaLop.setText(classItem.getMaLop());
        binding.txtTenLop.setText(classItem.getTenLop());
        binding.txtKhoa.setText(classItem.getKhoa());
        binding.txtGiangvien.setText(classItem.getGiangVien());
        binding.txtNamhoc.setText(classItem.getNamHoc());
            binding.cardViewClass.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onItemClick(classItem);
                }else {
                    Intent intent = new Intent(context, DetailClassActivity.class);
                    intent.putExtra("id", classItem.getId());
                    intent.putExtra("maLop", classItem.getMaLop());
                    intent.putExtra("tenLop", classItem.getTenLop());
                    intent.putExtra("khoa", classItem.getKhoa());
                    intent.putExtra("giangVien", classItem.getGiangVien());
                   intent.putExtra("namHoc",classItem.getNamHoc());
                    context.startActivity(intent);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void searchClass(List<Class> searchClass) {
        updateDataSet(searchClass);
    }
}