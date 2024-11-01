package com.example.cse441_project.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.app.DatePickerDialog;

public class EditAdminProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private ImageView iconAvatarAdmin, iconPickImage, iconOpenCamera, iconPickDate, iconPickAddress;

    private EditText editTextAdminId, editTextUsername, editTextFullname, editTextDate, editTextPhone, editTextEmail;
    private RadioGroup radioGroupGender;
    private String avatarUrl;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Spinner spinnerAddress;
    private ArrayAdapter<String> addressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_profile_edit);
        setUpWindowInsets();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference("avatars");

        initializeViews();
        setButtonListeners();

        Intent intent = getIntent();
        if (intent != null) {
            editTextAdminId.setText(intent.getStringExtra("userId"));
            editTextUsername.setText(intent.getStringExtra("username"));
            editTextFullname.setText(intent.getStringExtra("fullname"));
            editTextDate.setText(intent.getStringExtra("date"));
            String selectedAddress = spinnerAddress.getSelectedItem().toString();
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
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);

        radioGroupGender = findViewById(R.id.radioGroupGender);

        spinnerAddress = findViewById(R.id.spinnerAddress);
        String[] addressOptions = {
                "Hà Nội",
                "TP Hồ Chí Minh",
                "Nghệ An",
                "Đà Nẵng",
                "Hải Phòng",
                "Cần Thơ",
                "Nha Trang",
                "Huế",
                "Đồng Nai",
                "Bình Dương",
                "Long An",
                "Thừa Thiên-Huế",
                "Kiên Giang",
                "Quảng Ninh",
                "Lâm Đồng",
                "Bắc Ninh"
        };
        addressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, addressOptions);
        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAddress.setAdapter(addressAdapter);

        iconAvatarAdmin = findViewById(R.id.iconAvatarAdminProfile);
        iconPickImage = findViewById(R.id.iconPickImage);
        iconOpenCamera = findViewById(R.id.iconOpenCamera);
        iconPickDate = findViewById(R.id.iconPickDate);
        iconPickAddress = findViewById(R.id.iconPickAddress);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    private void setButtonListeners() {
        ImageButton btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(view -> finish());

        Button btnSave = findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(v -> showConfirmationDialog());

        iconPickImage.setOnClickListener(v -> openImagePicker());
        iconOpenCamera.setOnClickListener(v -> checkCameraPermission());

        editTextDate.setOnClickListener(v -> showDatePickerDialog());
        iconPickDate.setOnClickListener(v -> showDatePickerDialog());
        iconPickAddress.setOnClickListener(v -> spinnerAddress.performClick());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editTextDate.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Quyền truy cập camera bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE) {
                Uri selectedImageUri = data.getData();
                avatarUrl = selectedImageUri.toString();
                updateAvatarImage(avatarUrl);
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    avatarUrl = getImageUri(this, imageBitmap).toString();
                    updateAvatarImage(avatarUrl);
                }
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Avatar_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    private void updateAvatarImage(String avatarUrl) {
        Glide.with(this)
                .load(avatarUrl)
                .into(iconAvatarAdmin);
    }

    private void saveProfile() {
        String adminId = editTextAdminId.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String fullname = editTextFullname.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String address = spinnerAddress.getSelectedItem().toString();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        String gender;
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedId);
            gender = selectedRadioButton.getText().toString();
        } else {
            gender = "";
        }

        if (username.isEmpty() || fullname.isEmpty() || date.isEmpty() || address.isEmpty() ||
                phone.isEmpty() || email.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (avatarUrl != null && avatarUrl.startsWith("http")) {
                updateProfileInFirestore(adminId, username, fullname, date, address, phone, email, gender, avatarUrl);
            } else {
                Uri fileUri = Uri.parse(avatarUrl);
                StorageReference fileReference = storageReference.child(adminId + ".jpg"); // Use a unique file name

                fileReference.putFile(fileUri).addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String downloadUrl = downloadUri.toString();
                        updateProfileInFirestore(adminId, username, fullname, date, address, phone, email, gender, downloadUrl);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Lấy URL ảnh thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void updateProfileInFirestore(String adminId, String username, String fullname, String date, String address, String phone, String email, String gender, String avatarUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(adminId)
                .update("username", username,
                        "fullname", fullname,
                        "birthday", date,
                        "address", address,
                        "phone", phone,
                        "email", email,
                        "gender", gender,
                        "avatarUrl", avatarUrl)
                .addOnSuccessListener(aVoid -> showNotificationDialog("Thông tin đã được lưu"))
                .addOnFailureListener(e -> Toast.makeText(this, "Lưu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
        btnBack.setText("OK");

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

}
