package com.example.phuongldph29233.student_app.Activity;

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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.phuongldph29233.student_app.Controller.BranchController;
import com.example.phuongldph29233.student_app.Controller.SubjectController;
import com.example.phuongldph29233.student_app.Domain.Branch;
import com.example.phuongldph29233.student_app.Domain.Subject;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityDetailSubjectBinding;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class DetailSubjectActivity extends AppCompatActivity {
    ActivityDetailSubjectBinding binding;
    String id, maMon, tenMon, chuyenNganh, soTin;
    boolean isVisible;
    private SubjectController subjectController;
    private ArrayAdapter arrayAdapter;
    private ArrayList<Branch> branchArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailSubjectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        subjectController = new SubjectController();
        branchArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, branchArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Function
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
        EditText edt_maMon_add = dialog.findViewById(R.id.edt_maMon_add);
        EditText edt_tenMon_add = dialog.findViewById(R.id.edt_tenMon_add);
        EditText edt_soTin_add = dialog.findViewById(R.id.edt_soTin_add);
        Spinner spn_chuyenNganh = dialog.findViewById(R.id.spn_chuyenNganh);
        Button btn_update = dialog.findViewById(R.id.btn_add_mon);
        Button btn_cancel = dialog.findViewById(R.id.btn_huy_mon);
        loadDataBranchs();
        edt_maMon_add.setText(maMon);
        edt_tenMon_add.setText(tenMon);
        edt_soTin_add.setText(soTin);
        spn_chuyenNganh.setAdapter(arrayAdapter);
        new Handler().postDelayed(() -> {
            spn_chuyenNganh.setAdapter(arrayAdapter);
            int position = -1;
            for (int i = 0; i < arrayAdapter.getCount(); i++) {
                Branch branch = (Branch) arrayAdapter.getItem(i);
                Log.d("DEBUG", "So sánh: '" + branch.getTenKhoa().trim() + "' với '" + chuyenNganh.trim() + "'");
                if (branch.getTenKhoa().trim().equalsIgnoreCase(chuyenNganh.trim())) {
                    position = i;
                    break;
                }
            }

            if (position != -1) {
                spn_chuyenNganh.setSelection(position);
                Log.d("DEBUG", "Đặt selection tại vị trí: " + position);
            } else {
                Log.d("DEBUG", "Không tìm thấy chuyên ngành: " + chuyenNganh);
            }
        }, 500);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String maMon = edt_maMon_add.getText().toString();
                String tenMon = edt_tenMon_add.getText().toString();
                String soTin = edt_soTin_add.getText().toString();
                if (maMon.isEmpty() || tenMon.isEmpty() || soTin.isEmpty()) {
                    Toast.makeText(DetailSubjectActivity.this, "Vui lòng nhập đầy đủ thông tin !!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Branch selectedBranch = (Branch) spn_chuyenNganh.getSelectedItem();
                Subject subject = new Subject(id, maMon, tenMon, selectedBranch, soTin);
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
        maMon = (String) getIntent().getSerializableExtra("maMon");
        tenMon = (String) getIntent().getSerializableExtra("tenMon");
        chuyenNganh = (String) getIntent().getSerializableExtra("chuyenNganh");
        soTin = (String) getIntent().getSerializableExtra("soTin");
        binding.txtDetailMaMon.setText(maMon);
        binding.txtDetailTenMon.setText(tenMon);
        binding.txtDetailChuyenNganh.setText(chuyenNganh);
        binding.txtDetailSoTin.setText(soTin);
    }
}