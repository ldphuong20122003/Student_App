package com.example.phuongldph29233.student_app.Helper;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper<T> {
    private final DatabaseReference databaseReference;

    public DatabaseHelper(String path) {
        this.databaseReference = FirebaseDatabase.getInstance().getReference(path);
    }


    public void getList(Class<T> clazz, DatabaseCallback<T> callback) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<T> itemList = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    T item = itemSnapshot.getValue(clazz);
                    if (item != null) {
                        try {
                            itemSnapshot.getKey();
                            item.getClass().getMethod("setId", String.class).invoke(item, itemSnapshot.getKey());
                        } catch (Exception e) {
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

    public void add(T item, DatabaseActionCallback callback) {
        String key = databaseReference.push().getKey();
        try {
            item.getClass().getMethod("setId", String.class).invoke(item, key);
        } catch (Exception e) {
        }

        databaseReference.child(key)
                .setValue(item)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

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

    public void get(String id, Class<T> clazz, DatabaseGetCallback<T> callback) {
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    T item = snapshot.getValue(clazz);
                    if (item != null) {
                        try {
                            Method setIdMethod = item.getClass().getMethod("setId", String.class);
                            setIdMethod.invoke(item, snapshot.getKey());
                        } catch (Exception e) {
                        }
                        callback.onSuccess(item);
                    } else {
                        callback.onFailure("Không thể chuyển đổi dữ liệu");
                    }
                } else {
                    callback.onFailure("Không tìm thấy đối tượng với ID: " + id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    // Interface callback mới cho phương thức get
    public interface DatabaseGetCallback<T> {
        void onSuccess(T item);
        void onFailure(String error);
    }

    public interface DatabaseCallback<T> {
        void onSuccess(List<T> itemList);

        void onFailure(String error);
    }

    public interface DatabaseActionCallback {
        void onSuccess();

        void onFailure(String error);
    }
}