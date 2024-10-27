package com.example.cse441_project;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    EditText editTextPassword;
    Drawable[] drawable;
    Drawable icon;
    boolean isPasswordVisible = false; // Biến để theo dõi trạng thái hiển thị mật khẩu

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        editTextPassword = findViewById(R.id.editTextPassword);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thay đổi icon khi EditText không trống
                if (s.length() > 0) {
                    editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                } else {
                    editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0); // Ẩn icon khi trống
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Xử lý khi nhấn vào icon
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
}