package com.example.phuongldph29233.student_app.Domain;

import com.google.firebase.database.IgnoreExtraProperties;

// Lớp Admin
@IgnoreExtraProperties
public class Admin extends Users {
    public Admin() {
    }

    public Admin(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email);
    }

    @Override
    public String getRole() {
        return "admin";
    }
}
