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
    private List<Student> students;
    private SparseBooleanArray selectedStudents;

    public StudentSelectionAdapter(Context context, List<Student> students) {
        super(context, 0, students);
        this.students = students;
        this.selectedStudents = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_student, parent, false);
        }

        Student student = getItem(position);
        CheckBox cbStudent = convertView.findViewById(R.id.cbStudent);
        TextView tvStudentCode = convertView.findViewById(R.id.tvStudentCode);
        TextView tvStudentName = convertView.findViewById(R.id.tvStudentName);
        cbStudent.setChecked(selectedStudents.get(position, false));

        cbStudent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectedStudents.put(position, isChecked);
        });

        return convertView;
    }

    public List<Student> getSelectedStudents() {
        List<Student> selected = new ArrayList<>();
        for (int i = 0; i < selectedStudents.size(); i++) {
            if (selectedStudents.valueAt(i)) {
                selected.add(students.get(selectedStudents.keyAt(i)));
            }
        }
        return selected;
    }

    public void selectAll(boolean select) {
        for (int i = 0; i < getCount(); i++) {
            selectedStudents.put(i, select);
        }
        notifyDataSetChanged();
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
        selectedStudents.clear();
    }
}
