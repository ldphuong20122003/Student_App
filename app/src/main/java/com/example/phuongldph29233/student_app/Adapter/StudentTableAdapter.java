package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.R;

import java.util.List;

public class StudentTableAdapter extends RecyclerView.Adapter<StudentTableAdapter.StudentViewHolder> {
    private Context context;
    private List<Student> studentList;

    public StudentTableAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_table, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.tvStt.setText(String.valueOf(position + 1));
        holder.tvMaSV.setText(student.getStudentID());
        holder.tvTenSV.setText(student.getStudentName());
        holder.tvNgaySinh.setText(student.getStudentBirthday());
        holder.tvQueQuan.setText(student.getStudentHomeTown());
        if (holder.tvStt != null) {
            holder.tvStt.setText(String.valueOf(position + 1));
        }
//        holder.tvKhoa.setText(student.getKhoa() != null ? student.getKhoa().getTenKhoa() : "");
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateDataSet(List<Student> newList) {
        studentList.clear();
        studentList.addAll(newList);
        notifyDataSetChanged();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvStt, tvMaSV, tvTenSV, tvNgaySinh, tvQueQuan, tvKhoa;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tvStt);
            tvMaSV = itemView.findViewById(R.id.tvMaSV);
            tvTenSV = itemView.findViewById(R.id.tvTenSV);
            tvNgaySinh = itemView.findViewById(R.id.tvNgaySinh);
            tvQueQuan = itemView.findViewById(R.id.tvQueQuan);
//            tvKhoa = itemView.findViewById(R.id.tvKhoa);
        }
    }
}
