package com.example.phuongldph29233.student_app.Controller;

import com.example.phuongldph29233.student_app.Domain.Class;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClassController {
    private DatabaseReference myRef;
    private DatabaseHelper databaseHelper;

    public void getClasses(DatabaseHelper.DataCallback<Class> callback) {
        databaseHelper.getData(
                myRef,
                Class.class,
                callback
        );
    }
    public void addClass(Class newClass, DatabaseHelper.AddItemCallback callback) {
        databaseHelper.addItem(myRef, newClass, callback);
    }

    public void deleteClass(Class classroom, DatabaseHelper.DeleteItemCallback callback) {
        databaseHelper.deleteItem(myRef, classroom.getId(), callback);
    }

    public ClassController(DatabaseReference reference) {
        this.myRef = reference != null
                ? reference
                : FirebaseDatabase.getInstance().getReference("classes");
        this.databaseHelper = new DatabaseHelper();
    }
}