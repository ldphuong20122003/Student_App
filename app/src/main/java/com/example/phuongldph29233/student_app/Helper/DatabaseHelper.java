package com.example.phuongldph29233.student_app.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper<T> {
    private DatabaseReference databaseReference;

    public DatabaseHelper(String path) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference(path);
    }

    // Lấy danh sách các đối tượng
    public void getList(Class<T> clazz, DatabaseCallback<T> callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> itemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    T item = itemSnapshot.getValue(clazz);
                    if (item != null) {
                        // Nếu đối tượng có phương thức setId, hãy gọi nó
                        try {
                            itemSnapshot.getKey();
                            item.getClass().getMethod("setId", String.class).invoke(item, itemSnapshot.getKey());
                        } catch (Exception e) {
                            // Ignore if setId method doesn't exist
                        }
                        itemList.add(item);
                    }
                }
                callback.onSuccess(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Thêm đối tượng mới
    public void add(T item, DatabaseActionCallback callback) {
        String key = databaseReference.push().getKey();

        // Nếu đối tượng có phương thức setId, hãy gọi nó
        try {
            item.getClass().getMethod("setId", String.class).invoke(item, key);
        } catch (Exception e) {
            // Ignore if setId method doesn't exist
        }

        databaseReference.child(key)
                .setValue(item)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Cập nhật đối tượng
    public void update(String id, T updatedItem, DatabaseActionCallback callback) {
        databaseReference.child(id)
                .setValue(updatedItem)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void delete(String id, DatabaseActionCallback callback) {
        databaseReference.child(id)
                .removeValue()
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface DatabaseCallback<T> {
        void onSuccess(List<T> itemList);
        void onFailure(String error);
    }

    // Interface callback cho các hành động thêm, sửa, xóa
    public interface DatabaseActionCallback {
        void onSuccess();
        void onFailure(String error);
    }
}