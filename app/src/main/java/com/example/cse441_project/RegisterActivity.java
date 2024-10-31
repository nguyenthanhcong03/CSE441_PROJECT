package com.example.cse441_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextPassword, editTextConfirmPassword;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextComfirmPassword);
        //An hien mat khau
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thay đổi icon khi EditText không trống
                updatePasswordIcon(s, editTextPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //An hien mat khau
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thay đổi icon khi EditText không trở
                updatePasswordIcon(s, editTextConfirmPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Gọi hàm togglePasswordVisibility khi nhấn vào icon
                    togglePasswordVisibility(editTextPassword);
                    return true;
                }
            }
            return false;
        });


        editTextConfirmPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextConfirmPassword.getRight() - editTextConfirmPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Gọi hàm togglePasswordVisibility khi nhấn vào icon
                    togglePasswordVisibility(editTextConfirmPassword);
                    return true;
                }
            }
            return false;
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //Ham Thay đổi icon khi EditText không trống
    private void updatePasswordIcon(CharSequence s, EditText editTextPassword) {
        if (s.length() > 0) {
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
        } else {
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Ẩn icon khi trống
        }
    }

    private void togglePasswordVisibility(EditText editTextPassword) {
        if (editTextPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
            // Hiện mật khẩu
            editTextPassword.setTransformationMethod(null);
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0); // Icon hiện
        } else {
            // Ẩn mật khẩu
            editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0); // Icon ẩn
        }
        // Đặt con trỏ về cuối EditText
        editTextPassword.setSelection(editTextPassword.length());
    }


}