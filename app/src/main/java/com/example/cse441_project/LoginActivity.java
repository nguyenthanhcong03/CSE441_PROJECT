package com.example.cse441_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.auth.Constants;
import com.example.cse441_project.auth.PreferenceManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginActivity extends AppCompatActivity {
    EditText editTextPassword, editTextUsername;
    Button btnLogin;
    //
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();



    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        btnLogin = findViewById(R.id.buttonLogin);

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

        editTextPassword.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[2].getBounds().width())) {
                    // Xử lý khi nhấn vào icon
                    togglePasswordVisibility(editTextPassword);
                }
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }else {
                loginUser(username, password);

            }

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

    //Ham login

    private void loginUser(String username, String password) {
            firestore.collection("Users")
                    .whereEqualTo(Constants.KEY_USERNAME, username)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);

                            String storedPasswordHash = document.getString(Constants.KEY_PASSWORD);
                            String userId = document.getId(); // Lấy userId từ tài liệu
                            String role = document.getString("role");

                            // Kiểm tra mật khẩu sau khi băm
                            if (storedPasswordHash != null) {
                                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedPasswordHash);
                                if (result.verified) {
                                    // Lưu userId vào SharedPreferences
                                    PreferenceManager preferenceManager = new PreferenceManager(this);
                                    preferenceManager.putString(Constants.KEY_USER_ID, userId);

                                    // Chuyển đến các Activity tương ứng
                                    if ("Admin".equals(role)) {
                                        Toast.makeText(this, "Admin", Toast.LENGTH_SHORT).show();
                                         startActivity(new Intent(this, MainActivity.class));
                                    } else if ("Student".equals(role)) {
                                        Toast.makeText(this, "Student", Toast.LENGTH_SHORT).show();
                                         startActivity(new Intent(this, StudentActivity.class));
                                    } else {
                                        Toast.makeText(this, "Role không hợp lệ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }


//                        // Kiểm tra mật khẩu với bcrypt
//                        if (BCrypt.checkpw(password, storedPasswordHash)) {
//                            // Xác thực thành công
//                            if ("Admin".equals(role)) {
//                                startActivity(new Intent(this, AdminActivity.class));
//                            } else if ("Student".equals(role)) {
//                                startActivity(new Intent(this, StudentActivity.class));
//                            } else {
//                                Toast.makeText(this, "Role không hợp lệ", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(this, "Tên đăng nhập không tồn tại", Toast.LENGTH_SHORT).show();
//                    }
}