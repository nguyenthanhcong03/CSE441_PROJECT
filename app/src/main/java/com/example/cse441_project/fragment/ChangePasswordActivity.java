package com.example.cse441_project.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.MainActivity;
import com.example.cse441_project.R;
import com.example.cse441_project.auth.Constants;
import com.example.cse441_project.auth.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePasswordActivity extends AppCompatActivity {
    private AlertDialog dialog;
    EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Button buttonSave;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_change_password);
        edtOldPassword = findViewById(R.id.edtPassword);
        edtNewPassword = findViewById(R.id.edtNewPass);
        edtConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        buttonSave = findViewById(R.id.buttonSave);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        String userId = preferenceManager.getString("userId");
        buttonSave.setOnClickListener(v -> {
            if (validateForm()) {
                changePassword(userId);
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }

        });

        setupPasswordVisibilityToggle(findViewById(R.id.edtPassword), findViewById(R.id.ivTogglePasswordVisibilityCurrent));
        setupPasswordVisibilityToggle(findViewById(R.id.edtNewPass), findViewById(R.id.ivTogglePasswordVisibilityNew));
        setupPasswordVisibilityToggle(findViewById(R.id.editTextConfirmPassword), findViewById(R.id.ivTogglePasswordVisibilityConfirm));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView imageViewBack = findViewById(R.id.btnBack);
        imageViewBack.setOnClickListener(v -> finish());
    }

    private void setupPasswordVisibilityToggle(final EditText editText, final ImageView imageView) {
        imageView.setOnClickListener(v -> {
            if (editText.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imageView.setImageResource(R.drawable.icon_eye_hidden);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                imageView.setImageResource(R.drawable.icon_eye_show);
            }
            editText.setSelection(editText.getText().length());
        });
    }

    // Hàm xác thực các trường input
    private boolean validateForm() {
        boolean valid = true;

        String password = edtOldPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edtOldPassword.setError("Mật khẩu không được để trống");
            valid = false;
        }

        String newPassword = edtNewPassword.getText().toString();
        if (TextUtils.isEmpty(newPassword)) {
            edtNewPassword.setError("Mật khẩu mới không được để trống");
            valid = false;
        } else if (newPassword.length() < 8) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 8 ký tự");
            valid = false;
        } else if (!newPassword.matches(".*\\d.*")) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 1 ký tự số");
            valid = false;
        } else if (!newPassword.matches(".*[!@#$%^&*()_+=|<>?{}\\[\\]~-].*")) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 1 ký tự đặc biệt");
            valid = false;
        } else if (!newPassword.matches(".*[A-Z].*")) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 1 ký tự in hoa");
            valid = false;
        } else if (!newPassword.matches(".*[a-z].*")) {
            edtNewPassword.setError("Mật khẩu mới phải có ít nhất 1 ký tự viết thường");
            valid = false;
        } else {
            edtNewPassword.setError(null);
        }

        String confirmPassword = edtConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Xác nhận mật khẩu không được để trống");
            valid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            edtConfirmPassword.setError("Xác nhận mật khẩu không khớp");
            valid = false;
        } else {
            edtConfirmPassword.setError(null);
        }

        return valid;
    }

    // Hàm đổi mật khẩu
    private void changePassword(String userId) {
        String newPassword = edtNewPassword.getText().toString().trim();
        String password = edtOldPassword.getText().toString().trim();

        firestore.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null){
                        DocumentSnapshot userDoc = task.getResult();

                        String storedPasswordHash = userDoc.getString("password");

                        if(!BCrypt.verifyer().verify(password.toCharArray(), storedPasswordHash).verified){
                            edtOldPassword.setError("Mật khẩu không khớp");
                            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (BCrypt.verifyer().verify(newPassword.toCharArray(), storedPasswordHash).verified) {
                            
                            edtNewPassword.setError("Mật khẩu mới không được giống mật khẩu cũ");
                            Toast.makeText(this, "Mật khẩu mới không được giống mật khẩu cũ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put(Constants.KEY_PASSWORD, BCrypt.withDefaults().hashToString(12, newPassword.toCharArray())); // Hash the new password

                        userDoc.getReference().update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    showSuccessDialog();
                                    startActivity(new Intent(ChangePasswordActivity.this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    
                                    Toast.makeText(this, "Có lỗi xảy ra khi đổi mật khẩu", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        
                        Toast.makeText(this, "Không tìm thấy người dùng", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // Hàm thông báo
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.quanly_dialog_notification, null);

        TextView tvTitle = customLayout.findViewById(R.id.titleDialog);
        tvTitle.setText("Thông báo");

        TextView tvMessage = customLayout.findViewById(R.id.contentDialog);
        tvMessage.setText("Đổi mật khẩu thành công");

        builder.setView(customLayout);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        Button btnBack = customLayout.findViewById(R.id.btnBack);
        btnBack.setText("Quay lại");
        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 3000);
    }
}
