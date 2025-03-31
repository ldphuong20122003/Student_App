package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Activity.Detail.DetailSubjectActivity;
import com.example.phuongldph29233.student_app.Domain.Subject;
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
        holder.binding.txtSubjectID.setText(items.get(position).getSubjectID());
        holder.binding.txtSubjectName.setText(items.get(position).getSubjectName());
        holder.binding.txtSubjectNOC.setText("Số tín: " + items.get(position).getSubjectNOC());
        holder.binding.cardViewSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailSubjectActivity.class);
                intent.putExtra("id", items.get(position).getId());
                intent.putExtra("subjectID", items.get(position).getSubjectID());
                intent.putExtra("subjectName", items.get(position).getSubjectName());
                intent.putExtra("subjectBranch", items.get(position).getSubjectBranch().toString());
                intent.putExtra("subjectNOC", items.get(position).getSubjectNOC());
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
