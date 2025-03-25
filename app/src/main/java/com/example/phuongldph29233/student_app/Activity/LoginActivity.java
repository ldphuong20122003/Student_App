package com.example.phuongldph29233.student_app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phuongldph29233.student_app.R;
import com.example.phuongldph29233.student_app.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
     ActivityLoginBinding binding;
     boolean isPasswordVisible= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleShowPass();
        handleLogin();
    }

    private void handleLogin() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username= binding.edtUsername.getText().toString().trim();
                String password= binding.edtPassword.getText().toString().trim();
                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,TextUtils.isEmpty(username)?"Vui lòng nhập tài khoản !!!":"Vui lòng nhập mật khẩu !!!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(username.equalsIgnoreCase("Admin")&&password.equals("123456")){
                    Intent intent= new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(LoginActivity.this,"Sai tài khoản hoặc mật khẩu",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleShowPass() {
        binding.showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPasswordVisible){
                    binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.imgShow.setImageResource(R.drawable.show_on);

                }else {
                    binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.imgShow.setImageResource(R.drawable.show_off);

                }
                isPasswordVisible=!isPasswordVisible;
                binding.edtPassword.setSelection(binding.edtPassword.getText().length());
            }
        });
    }
}