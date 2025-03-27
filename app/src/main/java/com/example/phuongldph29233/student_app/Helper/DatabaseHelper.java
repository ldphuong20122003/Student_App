package com.example.phuongldph29233.student_app.Helper;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class DatabaseHelper {
    public interface DataCallback<T> {
        void onSuccess(ArrayList<T> dataList);
        void onFailure(String errorMessage);
    }

    public interface AddItemCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }
}