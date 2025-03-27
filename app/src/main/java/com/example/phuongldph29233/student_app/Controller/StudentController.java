package com.example.phuongldph29233.student_app.Controller;

import com.example.phuongldph29233.student_app.Domain.Student;
import com.example.phuongldph29233.student_app.Helper.DatabaseHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentController {
    private DatabaseReference myRef;
    private DatabaseHelper databaseHelper;

    public void getClasses(DatabaseHelper.DataCallback<Student> callback) {
        databaseHelper.getData(
                myRef,
                Student.class,
                callback
        );
    }

    public void addClass(Student student, DatabaseHelper.AddItemCallback callback) {
        databaseHelper.addItem(myRef, student, callback);
    }

    public void deleteClass(Student student, DatabaseHelper.DeleteItemCallback callback) {
        databaseHelper.deleteItem(myRef, student.getId(), callback);
    }

    public StudentController(DatabaseReference reference) {
        this.myRef = reference != null
                ? reference
                : FirebaseDatabase.getInstance().getReference("Students");
        this.databaseHelper = new DatabaseHelper();
    }
}