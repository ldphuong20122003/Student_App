package com.example.phuongldph29233.student_app.Activity.Screens;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phuongldph29233.student_app.Adapter.StudentTableAdapter;
import com.example.phuongldph29233.student_app.Domain.Score;
import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentTableAdapter adapter;
    private DatabaseHelper<Student> studentDatabaseHelper;
    private DatabaseHelper<Score> scoreDatabaseHelper;
    private String maLop, tenLop, subjectId;
    private List<Student> students;
    private Map<String, Score> studentScores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        maLop = getIntent().getStringExtra("maLop");
        tenLop = getIntent().getStringExtra("tenLop");
        subjectId = getIntent().getStringExtra("subjectId");
        List<Student> students = (List<Student>) getIntent().getSerializableExtra("danhSachSinhVien");
        if (students == null || students.isEmpty()) {
            finish();
            return;
        }
        studentScores = new HashMap<>();
        recyclerView = findViewById(R.id.recyclerViewStudents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentTableAdapter(this, students);
        adapter.setOnItemLongClickListener(position -> {
            Student student = adapter.getStudentAtPosition(position);
            if (student != null) {
                showPopupMenu(position, student);
            }
            return true;
        });
        recyclerView.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("Danh sách lớp " + tenLop + " " + maLop);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        studentDatabaseHelper = new DatabaseHelper<>("Students");
        scoreDatabaseHelper = new DatabaseHelper<>("Scores");
        loadStudentScores();
    }

    private void loadStudentScores() {
        for (Student student : students) {
            scoreDatabaseHelper.getStudentScores(
                    student.getId(), maLop, subjectId, Score.class,
                    new DatabaseHelper.DatabaseGetCallback<Score>() {
                        @Override
                        public void onSuccess(Score score) {
                            studentScores.put(student.getId(), score);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String error) {
                            studentScores.put(student.getId(), null);
                            adapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void showPopupMenu(int position, Student student) {
        View view = recyclerView.findViewHolderForAdapterPosition(position).itemView;
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.student_options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_edit) {
                showEditScoresDialog(position, student);
                return true;
            } else if (itemId == R.id.action_delete) {
                Toast.makeText(this, "Xóa: " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_view) {
                Toast.makeText(this, "Xem chi tiết: " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        popup.show();
    }

    private void showEditScoresDialog(int position, Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_scores, null);
        builder.setView(dialogView);

        EditText etProgressScore = dialogView.findViewById(R.id.etProgressScore);
        EditText etExamScore = dialogView.findViewById(R.id.etExamScore);
        EditText etFinalScore = dialogView.findViewById(R.id.etFinalScore);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Hiển thị điểm hiện tại (nếu có)
        Score existingScore = studentScores.get(student.getId());
        if (existingScore != null) {
            etProgressScore.setText(String.format("%.1f", existingScore.getProgressScore()));
            etExamScore.setText(String.format("%.1f", existingScore.getExamScore()));
            etFinalScore.setText(String.format("%.1f", existingScore.getFinalScore()));
        } else {
            etProgressScore.setText("0.0");
            etExamScore.setText("0.0");
            etFinalScore.setText("0.0");
        }

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            try {
                double progressScore = Double.parseDouble(etProgressScore.getText().toString());
                double examScore = Double.parseDouble(etExamScore.getText().toString());
                double finalScore = Double.parseDouble(etFinalScore.getText().toString());

                // Kiểm tra điểm hợp lệ
                if (progressScore < 0 || progressScore > 10 ||
                        examScore < 0 || examScore > 10 ||
                        finalScore < 0 || finalScore > 10) {
                    Toast.makeText(this, "Điểm phải từ 0 đến 10", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Tạo hoặc cập nhật điểm
                Score score = new Score();
                score.setStudentId(student.getId());
                score.setClassId(maLop);
                score.setSubjectId(subjectId);
                score.setProgressScore(progressScore);
                score.setExamScore(examScore);
                score.setFinalScore(finalScore);

                if (existingScore != null) {
                    // Cập nhật điểm hiện có
                    score.setId(existingScore.getId());
                    scoreDatabaseHelper.update(score.getId(), score, new DatabaseHelper.DatabaseActionCallback() {
                        @Override
                        public void onSuccess() {
                            studentScores.put(student.getId(), score);
                            adapter.notifyItemChanged(position);
                            Toast.makeText(StudentListActivity.this,
                                    "Đã cập nhật điểm cho " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(StudentListActivity.this,
                                    "Lỗi khi cập nhật điểm: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Thêm điểm mới
                    scoreDatabaseHelper.add(score, new DatabaseHelper.DatabaseActionCallback() {
                        @Override
                        public void onSuccess() {
                            studentScores.put(student.getId(), score);
                            adapter.notifyItemChanged(position);
                            Toast.makeText(StudentListActivity.this,
                                    "Đã thêm điểm cho " + student.getStudentName(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(StudentListActivity.this,
                                    "Lỗi khi thêm điểm: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập điểm hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}