package com.example.cse441_project.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditAdminProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private ImageView iconAvatarAdmin, iconPickImage;

    private EditText editTextAdminId, editTextUsername, editTextFullname, editTextDate, editTextAddress, editTextPhone, editTextEmail;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale;
    private String userId;
    private String avatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_profile_edit);
        setUpWindowInsets();

        initializeViews();
        setButtonListeners();

        Intent intent = getIntent();
        if (intent != null) {
            editTextAdminId.setText(intent.getStringExtra("userId"));
            editTextUsername.setText(intent.getStringExtra("username"));
            editTextFullname.setText(intent.getStringExtra("fullname"));
            editTextDate.setText(intent.getStringExtra("date"));
            editTextAddress.setText(intent.getStringExtra("address"));
            editTextPhone.setText(intent.getStringExtra("phone"));
            editTextEmail.setText(intent.getStringExtra("email"));
            avatarUrl = intent.getStringExtra("avatarUrl");

            if (avatarUrl != null) {
                updateAvatarImage(avatarUrl);
            }

            String gender = intent.getStringExtra("gender");
            if (gender != null) {
                switch (gender) {
                    case "Nam":
                        radioGroupGender.check(R.id.radioMale);
                        break;
                    case "Nữ":
                        radioGroupGender.check(R.id.radioFemale);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void setUpWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        editTextAdminId = findViewById(R.id.editTextAdminId);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextFullname = findViewById(R.id.editTextFullname);
        editTextDate = findViewById(R.id.editTextDate);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        iconAvatarAdmin = findViewById(R.id.iconAvatarAdminProfile);
        iconPickImage = findViewById(R.id.iconPickImage);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void setButtonListeners() {
        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(view -> finish());

        iconPickImage.setOnClickListener(v -> openImagePicker());

        Button btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(v -> showConfirmationDialog());
    }

    private void saveProfile() {
        String adminId = editTextAdminId.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String fullname = editTextFullname.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        String gender = "";
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            gender = selectedRadioButton.getText().toString();
        }

        if (username.isEmpty() || fullname.isEmpty() || date.isEmpty() || address.isEmpty() ||
                phone.isEmpty() || email.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Users").document(adminId).update(
                    "username", username,
                    "fullname", fullname,
                    "birthday", date,
                    "address", address,
                    "phone", phone,
                    "email", email,
                    "gender", gender
            ).addOnSuccessListener(aVoid -> {
                if (avatarUrl != null) {
                    db.collection("Users").document(adminId)
                            .update("avatarUrl", avatarUrl)
                            .addOnSuccessListener(aVoid2 -> {
                                showNotificationDialog("Thông tin đã được lưu");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Lưu ảnh đại diện thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            });
                } else {
                    showNotificationDialog("Thông tin đã được lưu");
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Lưu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void showConfirmationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.quanly_dialog_confirmation);
        dialog.setCancelable(true);

        TextView confirmTitle = dialog.findViewById(R.id.confirmTitle);
        TextView confirmMessage = dialog.findViewById(R.id.confirmMessage);
        Button btnConfirmYes = dialog.findViewById(R.id.btnConfirmYes);
        Button btnConfirmNo = dialog.findViewById(R.id.btnConfirmNo);

        confirmTitle.setText("Xác nhận");
        confirmMessage.setText("Bạn có chắc chắn muốn lưu thông tin không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            saveProfile();
        });

        btnConfirmNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showNotificationDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.quanly_dialog_notification);
        dialog.setCancelable(false);

        TextView contentDialog = dialog.findViewById(R.id.contentDialog);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        contentDialog.setText(message);

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            avatarUrl = selectedImageUri.toString();
            updateAvatarImage(avatarUrl);
        }
    }

    private void updateAvatarImage(String avatarUrl) {
        Glide.with(this)
                .load(avatarUrl)
                .into(iconAvatarAdmin);
    }
}
