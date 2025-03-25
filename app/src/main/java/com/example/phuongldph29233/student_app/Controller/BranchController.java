package com.example.phuongldph29233.student_app.Controller;

import androidx.annotation.NonNull;

import com.example.phuongldph29233.student_app.Domain.Branch;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class BranchController {
    private final DatabaseReference myRef;

    public BranchController() {
        myRef = FirebaseDatabase.getInstance().getReference("Branch");
    }

    // Lấy danh sách Khoa từ Firebase
    public void getBranches(BranchCallback callback) {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Branch> branchArrayList = new ArrayList<>();
                for (DataSnapshot issue : snapshot.getChildren()) {
                    Branch branch = issue.getValue(Branch.class);
                    if (branch != null) {
                        branchArrayList.add(branch);
                    }
                }
                callback.onSuccess(branchArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Thêm Khoa mới vào Firebase
    public void addBranch(Branch branch, AddBranchCallback callback) {
        myRef.child(branch.getId()).setValue(branch)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void deleteBranch(Branch branch, DeleteBranchCallback callback) {
        myRef.child(branch.getId()).removeValue().addOnSuccessListener(unused -> callback.onSuccess()).addOnFailureListener(e -> callback.onFailed(e.getMessage()));
    }

    // Interface Callback
    public interface BranchCallback {
        void onSuccess(ArrayList<Branch> list);

        void onFailure(String error);
    }

    public interface AddBranchCallback {
        void onSuccess();

        void onFailure(String error);
    }

    public interface DeleteBranchCallback {
        void onSuccess();

        void onFailed(String error);
    }
}
