package com.example.phuongldph29233.student_app.Adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.R;

import java.util.ArrayList;
import java.util.List;

public class StudentSelectionAdapter extends ArrayAdapter<Student> {
    private final List<Student> students;
    private final SparseBooleanArray selectedItems;

    private final LayoutInflater inflater;
    private boolean selectAllMode = false;
    public StudentSelectionAdapter(Context context, List<Student> students) {
        super(context, 0, students);
        this.students = new ArrayList<>(students);
        this.selectedItems = new SparseBooleanArray();
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_student, parent, false);
            holder = new ViewHolder();
            holder.cbStudent = convertView.findViewById(R.id.cbStudent);
            holder.tvStudentCode = convertView.findViewById(R.id.tvStudentCode);
            holder.tvStudentName = convertView.findViewById(R.id.tvStudentName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student student = getItem(position);
        if (student != null) {
            holder.tvStudentCode.setText(student.getStudentID());
            holder.tvStudentName.setText(student.getStudentName());

            // QUAN TRỌNG: Đặt trạng thái checkbox từ selectedItems
            holder.cbStudent.setChecked(selectedItems.get(position, false));

            // Xử lý sự kiện click trên toàn bộ item
            convertView.setOnClickListener(v -> {
                toggleSelection(position);
                holder.cbStudent.setChecked(selectedItems.get(position, false));
            });

            // Xử lý sự kiện click trên checkbox
            holder.cbStudent.setOnClickListener(v -> {
                toggleSelection(position);
            });
        }

        return convertView;
    }

    private static class ViewHolder {
        CheckBox cbStudent;
        TextView tvStudentCode;
        TextView tvStudentName;
    }

    public void toggleSelection(int position) {
        selectedItems.put(position, !selectedItems.get(position, false));
        notifyDataSetChanged();
    }

    public boolean isSelected(int position) {
        return selectedItems.get(position, false);
    }

    public List<Student> getSelectedStudents() {
        List<Student> selected = new ArrayList<>();
        for (int i = 0; i < students.size(); i++) {
            if (isSelected(i)) {
                selected.add(students.get(i));
            }
        }
        return selected;
    }



    public void selectAll(boolean select) {
        selectAllMode = select;
        for (int i = 0; i < getCount(); i++) {
            selectedItems.put(i, select);
        }
        notifyDataSetChanged();
    }

    public boolean isSelectAllMode() {
        return selectAllMode;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Student getItem(int position) {
        return students.get(position);
    }

    public void clear() {
        students.clear();
        notifyDataSetChanged();
    }

    public void clearSelection() {
        notifyDataSetChanged();
    }

    public void setSelected(int position, boolean selected) {
        notifyDataSetChanged();
    }

    public void setSelectedStudents(List<Student> studentsToSelect) {
        clearSelection();
        for (Student student : studentsToSelect) {
            int position = students.indexOf(student);
            if (position >= 0) {
            }
        }
        notifyDataSetChanged();
    }

    public void addAll(List<Student> newStudents) {
        students.addAll(newStudents);
        notifyDataSetChanged();
    }

    public void updateStudents(List<Student> newStudents) {
        students.clear();
        students.addAll(newStudents);
        notifyDataSetChanged();
    }
}