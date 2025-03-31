package com.example.phuongldph29233.student_app.Activity.Detail;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phuongldph29233.student_app.Activity.Screens.TeacherActivity;
import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.TeacherController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Teacher;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.Validator.Validator;
import com.example.phuongldph29233.student_app.databinding.ActivityDetailTeacherBinding;

import java.util.ArrayList;
import java.util.Objects;

public class DetailTeacherActivity extends AppCompatActivity {
    private ActivityDetailTeacherBinding binding;
    private String id, teacherID, teacherName, teacherPhone, teacherEmail, teacherBranch;
    private ArrayAdapter<Branch> arrayAdapter;
    private ArrayList<Branch> branchArrayList;
    private boolean isVisible = false;
    private TeacherController teacherController;
    private BranchController branchController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailTeacherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initControllers();
        getIntentExtra();
        initRecycleView();
        initUI();
    }

    private void initControllers() {
        teacherController = new TeacherController();
        branchArrayList = new ArrayList<>();
        branchController = new BranchController();

    }

    private void initUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnList.setOnClickListener(v -> showMoreAction());
        binding.btnDelete.setOnClickListener(v -> showDialogDelete());
        binding.btnEdit.setOnClickListener(v -> showDialogEdit());
    }

    private void initRecycleView() {
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void loadDataBranchs() {
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchArrayList.clear();
                branchArrayList.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailTeacherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogEdit() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_teacher);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_title_teacher = dialog.findViewById(R.id.txt_title_teacher);
        EditText edt_teacherID = dialog.findViewById(R.id.edt_teacherID_add);
        EditText edt_teacherName = dialog.findViewById(R.id.edt_teacherName_add);
        EditText edt_teacherEmail = dialog.findViewById(R.id.edt_teacherEmail_add);
        EditText edt_teacherPhone = dialog.findViewById(R.id.edt_teacherPhone_add);
        Spinner spn_teacherBranch = dialog.findViewById(R.id.spn_teacherBranch);
        Button btn_update = dialog.findViewById(R.id.btn_add_teacher);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel_teacher);
        txt_title_teacher.setText("Chỉnh sửa giảng viên");
        edt_teacherID.setText(teacherID);
        edt_teacherName.setText(teacherName);
        edt_teacherEmail.setText(teacherEmail);
        edt_teacherPhone.setText(teacherPhone);
        loadDataBranchs();
        spn_teacherBranch.setAdapter(arrayAdapter);
        new Handler().postDelayed(() -> {
            spn_teacherBranch.setAdapter(arrayAdapter);
            int position = -1;
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                Branch branch = arrayAdapter.getItem(i);
                if (branch.getTenKhoa().trim().equalsIgnoreCase(teacherBranch.toLowerCase())) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                spn_teacherBranch.setSelection(position);
                Log.d("DEBUG", "Đặt selection tại vị trí: " + position);
            } else {
                Log.d("DEBUG", "Không tìm thấy chuyên ngành: " + teacherBranch);
            }
        }, 500);
        btn_update.setOnClickListener(v -> {
            String maGV = edt_teacherID.getText().toString();
            String tenGV = edt_teacherName.getText().toString();
            String email = edt_teacherEmail.getText().toString();
            String soDT = edt_teacherPhone.getText().toString();
            Branch selectedBranch = (Branch) spn_teacherBranch.getSelectedItem();
            if (teacherID.isEmpty() || teacherName.isEmpty() || teacherEmail.isEmpty() || teacherPhone.isEmpty()) {
                Toast.makeText(DetailTeacherActivity.this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Validator.isValidEmail(teacherEmail)) {
                Toast.makeText(DetailTeacherActivity.this, "Email không hợp lệ !!!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Validator.isValidPhone(teacherPhone)) {
                Toast.makeText(DetailTeacherActivity.this, "Số điện thoại không hợp lệ !!!", Toast.LENGTH_SHORT).show();
                return;
            }
            Teacher teacher = new Teacher(id, maGV, tenGV, email, soDT, selectedBranch);
            btn_update.setEnabled(false);
            updateTeacher(teacher, dialog);


        });
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void updateTeacher(Teacher teacher, Dialog dialog) {
        teacherController.updateTeacher(id, teacher, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(DetailTeacherActivity.this, "Cập nhật dữ liệu thành công !!!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailTeacherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có chắc chắn muốn xóa giảng viên này ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                teacherController.deleteTeacher(id, new DatabaseHelper.DatabaseActionCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DetailTeacherActivity.this, "Xóa giảng viên thành công !!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(DetailTeacherActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showMoreAction() {
        isVisible = !isVisible;
        if (isVisible) {
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.btnDelete.setVisibility(View.VISIBLE);
        } else {
            binding.btnEdit.setVisibility(View.GONE);
            binding.btnDelete.setVisibility(View.GONE);
        }
    }

    private void getIntentExtra() {
        id = (String) getIntent().getSerializableExtra("id");
        teacherID = (String) getIntent().getSerializableExtra("teacherID");
        teacherName = (String) getIntent().getSerializableExtra("teacherName");
        teacherPhone = (String) getIntent().getSerializableExtra("teacherPhone");
        teacherEmail = (String) getIntent().getSerializableExtra("teacherEmail");
        teacherBranch = (String) getIntent().getSerializableExtra("teacherBranch");
        binding.txtDetailMaGV.setText(teacherID);
        binding.txtDetailTenGV.setText(teacherName);
        binding.txtDetailEmail.setText(teacherEmail);
        binding.txtDetailSoDT.setText(teacherPhone);
        binding.txtDetailChuyenNganh.setText(teacherBranch);
    }
}