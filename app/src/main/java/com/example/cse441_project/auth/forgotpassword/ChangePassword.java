package com.example.cse441_project.auth.forgotpassword;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.LoginActivity;
import com.example.cse441_project.R;
import com.example.cse441_project.auth.Constants;
import com.example.cse441_project.auth.PreferenceManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePassword extends AppCompatActivity {
    EditText editTextCurrentKey, editTextPassword, editTextConfirmPassword;
    ProgressBar loadingProgressBar;
    View overlayView;
    Button btnSubmit;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        editTextCurrentKey = findViewById(R.id.editTextCurrentKey);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextComfirmPassword);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        overlayView = findViewById(R.id.overlayView);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSubmit = findViewById(R.id.buttonSubmit);
        btnSubmit.setOnClickListener(v -> {

            if (validateForm()) {

                changePassword(email);
            } else {
                Toast.makeText(this, "Vui long nhap day du thong tin", Toast.LENGTH_SHORT).show();
            }

        });

        //An hien mat khau
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thay đổi icon khi EditText không trống
                updatePasswordIcon(s, editTextPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //An hien mat khau
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Thay đổi icon khi EditText không trở
                updatePasswordIcon(s, editTextConfirmPassword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Nhap mk khi cham vao nhap thi mat khau duoc hien thi che dau
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
    }

    //Overflow loading
    private void showLoadingOverlay() {
        overlayView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingOverlay() {
        overlayView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.GONE);
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

    //Validate
    private boolean validateForm() {
        boolean valid = true;

        // Kiểm tra Password
        String password = editTextPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Mật khẩu không được để trống");
            valid = false;
        } else if (password.length() < 8) {
            editTextPassword.setError("Mật khẩu phải có ít nhất 8 ký tự");
            valid = false;
        } else if (!password.matches(".*\\d.*")) {
            editTextPassword.setError("Mật khẩu phải có ít nhất 1 ký tự số");
            valid = false;
        } else if (!password.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*")) {
            editTextPassword.setError("Mật khẩu phải có ít nhất 1 ký tự đặc biệt");
            valid = false;
        } else if (!password.matches(".*[A-Z].*")) {
            editTextPassword.setError("Mật khẩu phải có ít nhất 1 ký tự in hoa");
            valid = false;
        } else if (!password.matches(".*[a-z].*")) {
            editTextPassword.setError("Mật khẩu phải có ít nhất 1 ký tự viết thường");
            valid = false;
        } else {
            editTextPassword.setError(null);
        }

        // Kiểm tra Confirm Password
        String confirmPassword = editTextConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
            valid = false;
        } else if (!confirmPassword.equals(password)) {
            editTextConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            valid = false;
        } else {
            editTextConfirmPassword.setError(null);
        }

        return valid;
    }
    //Ham doi mat khau
    private void changePassword(String email) {
        showLoadingOverlay();
        // Get the entered currentKey and new password
        String enteredCurrentKey = editTextCurrentKey.getText().toString().trim();
        String newPassword = editTextPassword.getText().toString().trim();

        // Firestore query to get user document by email
        firestore.collection("Users")
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot userDoc = task.getResult().getDocuments().get(0);
                        String storedCurrentKey = userDoc.getString(Constants.KEY_CURRENT_KEY);
                        String storedPasswordHash = userDoc.getString("password");

                        // Check if the entered currentKey matches the stored one
                        if (!enteredCurrentKey.equals(storedCurrentKey)) {
                            hideLoadingOverlay();
                            editTextCurrentKey.setError("Current key hợp lệ");
                            Toast.makeText(this, "Current key không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Check if the new password is different from the old password
                        if (BCrypt.verifyer().verify(newPassword.toCharArray(), storedPasswordHash).verified) {
                            hideLoadingOverlay();
                            editTextPassword.setError("Mật khẩu mới không được giống mật khẩu cũ");
                            Toast.makeText(this, "Mật khẩu mới không được giống mật khẩu cũ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update password and clear currentKey
                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put(Constants.KEY_PASSWORD, BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())); // Hash the new password
                        updates.put(Constants.KEY_CURRENT_KEY, ""); // Clear currentKey

                        userDoc.getReference().update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    hideLoadingOverlay();
                                    Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ChangePassword.this, LoginActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    hideLoadingOverlay();
                                    Toast.makeText(this, "Có lỗi xảy ra khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        hideLoadingOverlay();
                        Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}