package com.example.phuongldph29233.student_app.Activity.Detail;

import static com.example.phuongldph29233.student_app.R.*;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.SubjectController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityDetailSubjectBinding;

import java.util.ArrayList;
import java.util.Objects;

public class DetailSubjectActivity extends AppCompatActivity {
    private ActivityDetailSubjectBinding binding;
    private String id, subjectID, subjectName, subjectBranch, subjectNOC;
    boolean isVisible;
    private SubjectController subjectController;
    private ArrayAdapter<Branch> arrayAdapter;
    private ArrayList<Branch> branchArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailSubjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initController();
        getIntentExtra();
        initRecycleView();
        initUI();
    }

    private void initRecycleView() {
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, branchArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    private void initController() {
        subjectController = new SubjectController();
        branchArrayList = new ArrayList<>();
    }

    private void initUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVisible = !isVisible;
                if (isVisible) {
                    binding.btnEdit.setVisibility(View.VISIBLE);
                    binding.btnDelete.setVisibility(View.VISIBLE);
                } else {
                    binding.btnEdit.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                }
            }
        });
        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogDelete();
            }
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogEdit();
            }
        });
    }

    private void showDialogEdit() {
        Dialog dialog = new Dialog(DetailSubjectActivity.this);
        dialog.setContentView(layout.dialog_add_subject);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView txt_title = dialog.findViewById(R.id.txt_title_subject);
        txt_title.setText("Chỉnh sửa môn học");
        EditText edt_subjectID_add = dialog.findViewById(R.id.edt_subjectID_add);
        EditText edt_subjectName_add = dialog.findViewById(R.id.edt_subjectName_add);
        EditText edt_subjectNOC_add = dialog.findViewById(R.id.edt_subjectNOC_add);
        Spinner spn_subjectBranch = dialog.findViewById(R.id.spn_subjectBranch);
        Button btn_update = dialog.findViewById(R.id.btn_add_subject);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel_subject);
        loadDataBranchs();
        edt_subjectID_add.setText(subjectID);
        edt_subjectName_add.setText(subjectName);
        edt_subjectNOC_add.setText(subjectNOC);
        spn_subjectBranch.setAdapter(arrayAdapter);
        new Handler().postDelayed(() -> {
            spn_subjectBranch.setAdapter(arrayAdapter);
            int position = -1;
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                Branch branch = arrayAdapter.getItem(i);
                if (branch.getBranchName().trim().equalsIgnoreCase(subjectBranch.trim())) {
                    position = i;
                    break;
                }
            }

            if (position != -1) {
                spn_subjectBranch.setSelection(position);
                Log.d("DEBUG", "Đặt selection tại vị trí: " + position);
            } else {
                Log.d("DEBUG", "Không tìm thấy chuyên ngành: " + subjectBranch);
            }
        }, 500);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subjectID = edt_subjectID_add.getText().toString();
                String subjectName = edt_subjectName_add.getText().toString();
                String subjectNOC = edt_subjectNOC_add.getText().toString();
                if (subjectID.isEmpty() || subjectName.isEmpty() || subjectNOC.isEmpty()) {
                    Toast.makeText(DetailSubjectActivity.this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Branch selectedBranch = (Branch) spn_subjectBranch.getSelectedItem();
                Subject subject = new Subject(id, subjectID, subjectName, selectedBranch, subjectNOC);
                updateSubject(subject);
                dialog.dismiss();
                finish();
            }
        });
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateSubject(Subject subject) {
        subjectController.updateSubject(id, subject, new DatabaseHelper.DatabaseActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(DetailSubjectActivity.this, "Cập nhật môn học thành công !!!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailSubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void loadDataBranchs() {
        BranchController branchController = new BranchController();
        branchController.getBranches(new BranchController.BranchCallback() {
            @Override
            public void onSuccess(ArrayList<Branch> list) {
                branchArrayList.clear();
                branchArrayList.addAll(list);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DetailSubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailSubjectActivity.this);
        builder.setMessage("Bạn có chắc chắn muốn xóa không ?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                subjectController.deleteSubject(id, new DatabaseHelper.DatabaseActionCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(DetailSubjectActivity.this, "Xóa môn học thành công !!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(DetailSubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        finish();
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

    private void getIntentExtra() {
        id = (String) getIntent().getSerializableExtra("id");
        subjectID = (String) getIntent().getSerializableExtra("subjectID");
        subjectName = (String) getIntent().getSerializableExtra("subjectName");
        subjectBranch = (String) getIntent().getSerializableExtra("subjectBranch");
        subjectNOC = (String) getIntent().getSerializableExtra("subjectNOC");
        binding.txtDetailMaMon.setText(subjectID);
        binding.txtDetailTenMon.setText(subjectName);
        binding.txtDetailChuyenNganh.setText(subjectBranch);
        binding.txtDetailSoTin.setText(subjectNOC);
    }
}