package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.databinding.ItemAverageScoreBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AverageScoreAdapter extends RecyclerView.Adapter<AverageScoreAdapter.ViewHolder> {
    private ArrayList<Student> items;
    private Context context;
    private Map<String, Class> classMap;
    private Map<String, Double> averageScores;

    public AverageScoreAdapter(ArrayList<Student> items, Map<String, Class> classMap, Map<String, Double> averageScores) {
        this.items = items != null ? items : new ArrayList<>();
        this.classMap = classMap != null ? classMap : new HashMap<>();
        this.averageScores = averageScores != null ? averageScores : new HashMap<>();
        Log.d("AverageScoreAdapter", "Initialized with " + this.items.size() + " students");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemAverageScoreBinding binding = ItemAverageScoreBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (position < 0 || position >= items.size()) {
            Log.e("AverageScoreAdapter", "Invalid position: " + position);
            return;
        }
        Student student = items.get(position);
        holder.binding.txtSTT.setText(String.valueOf(position + 1));
        if (student == null) {
            Log.e("AverageScoreAdapter", "Student at position " + position + " is null");
            return;
        }
        Log.d("AverageScoreAdapter", "Binding student at position " + position + ": " + student);

        holder.binding.txtStudentID.setText(student.getStudentID() != null ? student.getStudentID() : "N/A");
        holder.binding.txtStudentName.setText(student.getStudentName() != null ? student.getStudentName() : "N/A");

//        String classId = student.getClassId();
//        Class clazz = classId != null ? classMap.get(classId) : null;
//        String className = clazz != null && clazz.getTenLop() != null ? clazz.getTenLop() : "Chưa có lớp";
//        holder.binding.txtClassName.setText(className);
//        Log.d("AverageScoreAdapter", "Class for student " + student.getStudentID() + ": " + className);

        String studentId = student.getId();
        Double average = studentId != null ? averageScores.get(studentId) : null;
        if (average != null && average != 0.0) {
            holder.binding.txtAverageScore.setText(String.format("%.1f", average));
            holder.binding.txtRanking.setText(getRanking(average));
        } else {
            holder.binding.txtAverageScore.setText("Chưa có");
            holder.binding.txtRanking.setText("Chưa xếp loại");
        }
        Log.d("AverageScoreAdapter", "Average score for " + student.getStudentID() + ": " + average);
    }

    private String getRanking(double average) {
        if (average >= 9.0) return "Xuất sắc";
        if (average >= 8.0) return "Giỏi";
        if (average >= 7.0) return "Khá";
        if (average >= 5.0) return "Trung bình";
        return "Yếu";
    }

    @Override
    public int getItemCount() {
        int size = items.size();
        Log.d("AverageScoreAdapter", "getItemCount: " + size);
        return size;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(ArrayList<Student> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
        Log.d("AverageScoreAdapter", "Updated data with " + items.size() + " students");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemAverageScoreBinding binding;

        public ViewHolder(ItemAverageScoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}