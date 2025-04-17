package com.example.phuongldph29233.student_app.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Domain.Score;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.R;

import java.util.List;
import java.util.Map;

public class StudentTableAdapter extends RecyclerView.Adapter<StudentTableAdapter.StudentViewHolder> {
    private Context context;
    private List<Student> studentList;
    private OnItemLongClickListener longClickListener;
    private Map<String, Score> studentScores;
    public interface OnItemLongClickListener {
        boolean onItemLongClick(int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }
//    public StudentTableAdapter(Context context, List<Student> studentList) {
//        this.context = context;
//        this.studentList = studentList;
//    }

    public StudentTableAdapter(Context context, List<Student> studentList, Map<String, Score> studentScores) {
        this.context = context;
        this.studentList = studentList;
        this.studentScores = studentScores;
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
        Score score = studentScores.get(student.getId());
        if (score != null) {
            holder.tvDiemQT.setText(String.format("%.1f", score.getProgressScore()));
            holder.tvDiemThi.setText(String.format("%.1f", score.getExamScore()));
            holder.tvDiemTK.setText(String.format("%.1f", score.getFinalScore()));
        } else {
            holder.tvDiemQT.setText("0.0");
            holder.tvDiemThi.setText("0.0");
            holder.tvDiemTK.setText("0.0");
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                return longClickListener.onItemLongClick(position);
            }
            return false;
        });
        holder.tvKhoa.setText(student.getStudentBranch() != null ? student.getStudentBranch().getBranchName() : "");

//        holder.tvKhoa.setText(student.getKhoa() != null ? student.getKhoa().getTenKhoa() : "");
    }

    public Student getStudentAtPosition(int position) {
        return studentList.get(position);
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
        TextView tvStt, tvMaSV, tvTenSV, tvNgaySinh, tvQueQuan, tvKhoa, tvDiemQT, tvDiemThi, tvDiemTK;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStt = itemView.findViewById(R.id.tvStt);
            tvMaSV = itemView.findViewById(R.id.tvMaSV);
            tvTenSV = itemView.findViewById(R.id.tvTenSV);
            tvNgaySinh = itemView.findViewById(R.id.tvNgaySinh);
            tvQueQuan = itemView.findViewById(R.id.tvQueQuan);
            tvKhoa = itemView.findViewById(R.id.tvKhoa);
            tvDiemQT = itemView.findViewById(R.id.tvProgressScore);
            tvDiemThi = itemView.findViewById(R.id.tvExamScore);
            tvDiemTK = itemView.findViewById(R.id.tvFinalScore);
//            tvKhoa = itemView.findViewById(R.id.tvKhoa);
        }
    }
}
