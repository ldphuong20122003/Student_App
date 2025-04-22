package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
        Student student = items.get(position);
        Log.d("AverageScoreAdapter", "Binding student at position " + position + ": " + student);

        holder.binding.txtStudentID.setText(student.getStudentID() != null ? student.getStudentID() : "N/A");
        holder.binding.txtStudentName.setText(student.getStudentName() != null ? student.getStudentName() : "N/A");

        // Hiển thị tên lớp
//        Class clazz = classMap.get(student.getClass());
//        String className = clazz != null && clazz.getTenLop() != null ? clazz.getTenLop() : "Chưa có lớp";
//        holder.binding.txtClassName.setText(student.getStudentClass().getTenLop());
//        Log.d("AverageScoreAdapter", "Class for student " + student.getStudentID() + ": " + student.getStudentClass().getTenLop());

        // Hiển thị điểm trung bình và xếp loại
        Double average = averageScores.get(student.getId());
        if (average != null && average != 0.0) {
            holder.binding.txtAverageScore.setText(String.format("%.1f", average));
            holder.binding.txtRanking.setText(getRanking(average));
        } else {
            holder.binding.txtAverageScore.setText("Chưa có");
            holder.binding.txtRanking.setText("Chưa xếp loại");
        }
        Log.d("AverageScoreAdapter", "Average score for " + student.getStudentID() + ": " + average);

        // Sự kiện click để mở StudentDetailActivity
//        holder.binding.getRoot().setOnClickListener(v -> {
//            Intent intent = new Intent(context, StudentDetailActivity.class);
//            intent.putExtra("id", student.getId());
//            intent.putExtra("studentID", student.getStudentId());
//            intent.putExtra("studentName", student.getStudentName());
//            intent.putExtra("studentBirthday", student.getStudentBirthday());
//            intent.putExtra("studentHomeTown", student.getStudentHomeTown());
//            intent.putExtra("studentPhone", student.getStudentPhone());
//            intent.putExtra("studentEmail", student.getStudentEmail());
//            intent.putExtra("studentClass", clazz);
//            intent.putExtra("studentDateJoin", student.getStudentDateJoin());
//            intent.putExtra("studentBranch", student.getStudentBranch());
//            intent.putExtra("studentTOT", student.getStudentTOT());
//            Log.d("AverageScoreAdapter", "Opening StudentDetailActivity for student: " + student.getStudentId());
//            context.startActivity(intent);
//        });
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
        return items.size();
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