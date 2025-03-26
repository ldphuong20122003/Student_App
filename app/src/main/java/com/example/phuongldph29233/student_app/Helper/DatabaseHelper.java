package com.example.phuongldph29233.student_app.Helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class DatabaseHelper {
    public <T> void getData(
            DatabaseReference reference,
            Class<T> dataType,
            DataCallback<T> callback
    ) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<T> dataList = new ArrayList<>();
                for (DataSnapshot issue : snapshot.getChildren()) {
                    T data = issue.getValue(dataType);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                callback.onSuccess(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public <T> void addItem(
            DatabaseReference reference,
            T item,
            AddItemCallback callback
    ) {
        reference.push().setValue(item)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public <T> void updateItem(
            DatabaseReference reference,
            String itemId,
            T updatedItem,
            UpdateItemCallback callback
    ) {
        reference.child(itemId).setValue(updatedItem)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void deleteItem(
            DatabaseReference reference,
            String itemId,
            DeleteItemCallback callback
    ) {
        reference.child(itemId).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface DataCallback<T> {
        void onSuccess(ArrayList<T> dataList);
        void onFailure(String errorMessage);
    }

    public interface AddItemCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface UpdateItemCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface DeleteItemCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}