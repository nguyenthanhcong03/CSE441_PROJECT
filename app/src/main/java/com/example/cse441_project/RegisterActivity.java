package com.example.cse441_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.auth.Constants;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import at.favre.lib.crypto.bcrypt.BCrypt;


public class RegisterActivity extends AppCompatActivity {
    EditText editTextPassword, editTextConfirmPassword,
            editTextFullname, editTextUsername, editTextGmail;
    ImageButton btnBack;
    Button btnRegister;
    ProgressBar loadingProgressBar;
    View overlayView;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    //    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextComfirmPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextFullname = findViewById(R.id.editTextFullname);
        editTextGmail = findViewById(R.id.editTextGmail);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        overlayView = findViewById(R.id.overlayView);
        btnBack = findViewById(R.id.btnBack);
        //
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //Dang ky
        btnRegister = findViewById(R.id.buttonSubmit);
        btnRegister.setOnClickListener(v -> {

            if (validateForm()) {

                registerUser();
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
        // Kiểm tra Fullname
        String fullname = editTextFullname.getText().toString();
        if (TextUtils.isEmpty(fullname)) {
            editTextFullname.setError("Họ và tên không được để trống");
            valid = false;
        } else if (!fullname.matches("[a-zA-ZÀ-ỹ\\s]+")) {
            editTextFullname.setError("Họ và tên không được chứa chữ số hoặc ký tự đặc biệt");
            valid = false;
        } else {
            editTextFullname.setError(null);
        }
        // Kiểm tra Username
        String username = editTextUsername.getText().toString();
        if (TextUtils.isEmpty(username)) {
            editTextUsername.setError("Tên đăng nhập không được để trống");
            valid = false;
        } else if (!username.matches("[a-zA-Z0-9]+")) {
            editTextUsername.setError("Tên đăng nhập không được chứa ký tự đặc biệt");
            valid = false;
        } else {
            editTextUsername.setError(null);
        }

        // Kiểm tra Email
        String email = editTextGmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editTextGmail.setError("Email không được để trống");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextGmail.setError("Email không hợp lệ");
            valid = false;
        } else {
            editTextGmail.setError(null);
        }

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

    //Register
    private void registerUser() {
        // Hiển thị ProgressBar
        showLoadingOverlay();

        // Lấy dữ liệu người dùng từ các trường EditText
        String fullname = editTextFullname.getText().toString();
        String username = editTextUsername.getText().toString();
        String email = editTextGmail.getText().toString();
        String password = editTextPassword.getText().toString();

        // Tạo truy vấn để kiểm tra username trong Firestore
        Task<QuerySnapshot> queryUsernameTask = firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_USERNAME, username)
                .get();

        // Tạo truy vấn để kiểm tra email trong Firestore
        Task<QuerySnapshot> queryEmailTask = firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get();

        // Chạy cả hai truy vấn
        Tasks.whenAllComplete(queryUsernameTask, queryEmailTask).addOnCompleteListener(task -> {
            boolean usernameExists = false;
            boolean emailExists = false;

            // Kiểm tra kết quả truy vấn username
            if (queryUsernameTask.isSuccessful() && queryUsernameTask.getResult() != null) {
                usernameExists = !queryUsernameTask.getResult().isEmpty();
            }

            // Kiểm tra kết quả truy vấn email
            if (queryEmailTask.isSuccessful() && queryEmailTask.getResult() != null) {
                emailExists = !queryEmailTask.getResult().isEmpty();
            }

            // Thông báo nếu username hoặc email đã tồn tại
            if (usernameExists) {
                editTextUsername.setError("Tên đăng nhập đã tồn tại");
                Toast.makeText(this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show();
                hideLoadingOverlay();
            } else if (emailExists) {
                editTextGmail.setError("Email đã tồn tại");
                Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                hideLoadingOverlay();
            } else {
                // Nếu không tồn tại, tiếp tục đăng ký
                HashMap<String, String> user = new HashMap<>();
                user.put(Constants.KEY_NAME, fullname);
                user.put(Constants.KEY_USERNAME, username);
                user.put(Constants.KEY_EMAIL, email);

                // Hash mật khẩu trước khi thêm vào Firestore
                String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                user.put(Constants.KEY_PASSWORD, hashedPassword);
                user.put(Constants.KEY_ROLE, "Student");
                user.put(Constants.KEY_ADDRESS, "");
                user.put(Constants.KEY_PHONE, "");
                user.put(Constants.KEY_GENDER, "");
                user.put(Constants.KEY_BIRTHDAY, "");
                user.put(Constants.KEY_CURRENT_KEY, "");
                user.put(Constants.KEY_STATUS, "");
                // Đăng dữ liệu với Firestore
                firestore.collection("Users")
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            hideLoadingOverlay();
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                            // Chuyển đến màn hình đăng nhập
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            hideLoadingOverlay();
                            Toast.makeText(this, "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
