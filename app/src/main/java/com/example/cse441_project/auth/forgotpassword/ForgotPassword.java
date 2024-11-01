package com.example.cse441_project.auth.forgotpassword;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.LoginActivity;
import com.example.cse441_project.MainActivity;
import com.example.cse441_project.R;
import com.example.cse441_project.StudentActivity;
import com.example.cse441_project.auth.Constants;
import com.example.cse441_project.auth.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ForgotPassword extends AppCompatActivity {
    EditText editTextEmail;
    Button btnSendEmail;
    TextView textViewBack;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        editTextEmail = findViewById(R.id.editTextEmail);
        btnSendEmail = findViewById(R.id.buttonSubmit);
        textViewBack = findViewById(R.id.textViewBack);
        textViewBack.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
            startActivity(intent);
        });
        btnSendEmail.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lý nhap email", Toast.LENGTH_SHORT).show();
            } else {
                sendResetPasswordLink(email);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }




    //Ham sinh currentKEy

    // Hàm tạo currentKey 8 ký tự ngẫu nhiên
    private String generateCurrentKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        key.append(chars.charAt(random.nextInt(26))); // 1 chữ cái viết hoa
        key.append(chars.charAt(26 + random.nextInt(26))); // 1 chữ cái viết thường
        key.append(chars.charAt(52 + random.nextInt(10))); // 1 số
        key.append(chars.charAt(62 + random.nextInt(8))); // 1 ký tự đặc biệt
        for (int i = 4; i < 8; i++) { // các ký tự còn lại
            key.append(chars.charAt(random.nextInt(chars.length())));
        }
        return key.toString();
    }

    //Ham sendEmail
    public void sendResetPasswordLink(String email) {
        firestore.collection("Users")
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Tạo currentKey với 8 ký tự ngẫu nhiên
                        String currentKey = generateCurrentKey();

                        // Lưu currentKey vào trường của người dùng
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        document.getReference().update("currentKey", currentKey)
                                .addOnSuccessListener(aVoid -> {
                                    // Gửi email với currentKey
                                    sendEmailWithCurrentKey(email, currentKey);
                                    PreferenceManager preferenceManager = new PreferenceManager(this);
                                    preferenceManager.putString(Constants.KEY_EMAIL, email);

                                })
                                .addOnFailureListener(e -> {
                                    // Xử lý lỗi nếu không lưu được currentKey
                                    Toast.makeText(ForgotPassword.this, "Mã xác nhận không hợp lệ hoặc lỗi", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        // Xử lý lỗi khi email không tồn tại
                        editTextEmail.setError("Email không tồn tại");
                    }
                });
    }


    //Ham sendEmail
    public void sendEmailWithCurrentKey(String recipientEmail, String currentKey) {
        String username = "azlibrary2024@zohomail.com";
        String passwordMail = "DatTo20032003@";
        String smtpHost = "smtp.zoho.com";
        int smtpPort = 587;

        String subject = "Yêu cầu đặt lại mật khẩu";
        String body = "Kính gửi,\n\n"
                + "Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n"
                + "Vui lòng sử dụng mã sau để đặt lại mật khẩu: " + currentKey + "\n\n"
                + "Nếu bạn không yêu cầu thay đổi này, vui lòng liên hệ với chúng tôi ngay lập tức.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ AZ Library";

        new SendEmailTask(this, recipientEmail, subject, body, username, passwordMail, smtpHost, smtpPort).execute();
    }




}