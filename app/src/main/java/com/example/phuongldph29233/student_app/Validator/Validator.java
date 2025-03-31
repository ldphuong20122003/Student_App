package com.example.phuongldph29233.student_app.Validator;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isValidEmail(String email) {

        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && Patterns.PHONE.matcher(phone).matches();
    }
}
