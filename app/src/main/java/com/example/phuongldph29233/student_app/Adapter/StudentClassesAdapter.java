package com.example.phuongldph29233.student_app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Activity.Screens.StudentListActivity;
import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;

public class StudentClassesAdapter extends RecyclerView.Adapter<StudentClassesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Class> classList;
    private String currentClass;
    private String studentId;
    private DatabaseHelper<Student> studentDatabaseHelper;

    public StudentClassesAdapter(Context context, ArrayList<Class> classList, String currentClass, String studentId) {
        this.context = context;
        this.classList = classList;
        this.currentClass = currentClass;
        this.studentId = studentId;
        this.studentDatabaseHelper = new DatabaseHelper<>("Student");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Class classItem = classList.get(position);
        holder.txt_stt.setText(String.valueOf(position + 1));
        holder.txtClassName.setText(classItem.getTenLop());
        holder.txtClassCode.setText(classItem.getMaLop());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudentListActivity.class);
            intent.putExtra("maLop", classItem.getMaLop());
            intent.putExtra("tenLop", classItem.getTenLop());
            intent.putExtra("subjectId", classItem.getMonHoc() != null ? classItem.getMonHoc().getSubjectID() : "");
            intent.putExtra("monHoc", classItem.getMonHoc() != null ? classItem.getMonHoc().toString() : "");
            ArrayList<Student> students = new ArrayList<>();
            if (classItem.getDanhSachSinhVien() != null) {
                students.addAll(classItem.getDanhSachSinhVien());
            }
            intent.putExtra("danhSachSinhVien", students);

            context.startActivity(intent);
        });
    }

    private void updateStudentClass(Class newClass) {
        studentDatabaseHelper.get(studentId, Student.class, new DatabaseHelper.DatabaseGetCallback<Student>() {
            @Override
            public void onSuccess(Student student) {
                student.setStudentClass(newClass);

                studentDatabaseHelper.update(studentId, student, new DatabaseHelper.DatabaseActionCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context,
                                "Đã cập nhật lớp học cho sinh viên thành công!",
                                Toast.LENGTH_SHORT).show();
                        currentClass = newClass.getTenLop();
                        notifyDataSetChanged();
                        Intent intent = new Intent("ACTION_DATA_UPDATED");
                        intent.putExtra("UPDATE_TYPE", "STUDENT_CLASS_UPDATE");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(context,
                                "Lỗi cập nhật lớp học: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(context,
                        "Lỗi tải thông tin sinh viên: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtClassName, txtClassCode, txt_stt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtClassName = itemView.findViewById(R.id.txt_class_name);
            txtClassCode = itemView.findViewById(R.id.txt_class_code);
            txt_stt = itemView.findViewById(R.id.txt_stt);
        }
    }
}