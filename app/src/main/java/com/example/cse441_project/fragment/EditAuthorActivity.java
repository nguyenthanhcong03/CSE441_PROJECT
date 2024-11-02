package com.example.cse441_project.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditAuthorActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PICK_IMAGE = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private EditText editTextAuthorId, editTextAuthorName, editTextAuthorBirthday;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale;
    private Button buttonSave;
    private ImageView iconAvatarAuthor, buttonBack, iconPickImage, iconOpenCamera, iconPickDate, iconPickNationality;

    private FirebaseFirestore db;
    private String authorId, avatarUrl;
    Spinner spinnerNationality;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_author_edit);

        editTextAuthorId = findViewById(R.id.editTextAuthorId);
        editTextAuthorName = findViewById(R.id.editTextAuthorName);
        editTextAuthorBirthday = findViewById(R.id.editTextAuthorBirthday);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        buttonSave = findViewById(R.id.buttonAdd);
        buttonBack = findViewById(R.id.btnBackEditAuthor);
        iconAvatarAuthor = findViewById(R.id.iconAvatarAuthor);

        iconPickImage = findViewById(R.id.iconPickImageAuthor);
        iconOpenCamera = findViewById(R.id.iconOpenCameraAuthor);
        iconPickDate  = findViewById(R.id.iconPickDate);
        iconPickNationality = findViewById(R.id.iconPickNationality);

        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        spinnerNationality = findViewById(R.id.spinnerNationality);
        String[] countries = {"Việt Nam", "Mỹ", "Nhật Bản", "Trung Quốc", "Hàn Quốc", "Anh", "Pháp", "Đức", "Ấn Độ", "Úc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNationality.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        authorId = getIntent().getStringExtra("authorId");
        editTextAuthorId.setText(authorId);

        String authorName = getIntent().getStringExtra("authorName");
        String authorGender = getIntent().getStringExtra("authorGender");
        String authorBirthday = getIntent().getStringExtra("authorBirthday");
        String authorNationality = getIntent().getStringExtra("authorNationality");
        String authorAvatarUrl = getIntent().getStringExtra("authorAvatarUrl");

        editTextAuthorId.setText(authorId);
        editTextAuthorName.setText(authorName);
        editTextAuthorBirthday.setText(authorBirthday);
        Glide.with(this).load(authorAvatarUrl).into(iconAvatarAuthor);

        if (authorNationality != null) {
            int spinnerPosition = adapter.getPosition(authorNationality);
            spinnerNationality.setSelection(spinnerPosition);
        }

        if ("Nam".equals(authorGender)) {
            radioMale.setChecked(true);
        } else {
            radioFemale.setChecked(true);
        }

        buttonSave.setOnClickListener(v -> showConfirmationDialog());
        buttonBack.setOnClickListener(v -> finish());

        iconPickImage.setOnClickListener(v -> openImagePicker());
        iconOpenCamera.setOnClickListener(v -> checkCameraPermission());
        editTextAuthorBirthday.setOnClickListener(v -> showDatePickerDialog());
        iconPickDate.setOnClickListener(v -> showDatePickerDialog());
        iconPickNationality.setOnClickListener(v -> spinnerNationality.performClick());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editTextAuthorBirthday.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
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

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGE && data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Load the image into a Bitmap
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        avatarUrl = saveImageToFile(bitmap); // Save to file and get the URL
                        updateAvatarImage(avatarUrl);
                    } catch (IOException e) {
                        Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_CODE_CAMERA) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    avatarUrl = saveImageToFile(imageBitmap);
                    updateAvatarImage(avatarUrl);
                }
            }
        }
    }

    private String saveImageToFile(Bitmap bitmap) {
        String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
        File directory = new File(getExternalFilesDir(null), "Avatars");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        File file = new File(directory, fileName);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void updateAvatarImage(String avatarUrl) {
        Glide.with(this)
                .load(avatarUrl)
                .into(iconAvatarAuthor);
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
        confirmMessage.setText("Bạn có chắc chắn muốn sửa thông tin tác giả không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            updateAuthor();
        });

        btnConfirmNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateAuthor() {
        String name = editTextAuthorName.getText().toString().trim();
        String gender = radioMale.isChecked() ? "Nam" : "Nữ";
        String birthday = editTextAuthorBirthday.getText().toString().trim();
        String nationality = spinnerNationality.getSelectedItem().toString().trim();

        if (name.isEmpty() || birthday.isEmpty() || nationality.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("gender", gender);
        updates.put("birthday", birthday);
        updates.put("nationality", nationality);

        if (avatarUrl != null) {
            updates.put("avatarUrl", avatarUrl);
        }

        db.collection("Authors").document(authorId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    showNotificationDialog("Cập nhật tác giả thành công");
                    setResult(Activity.RESULT_OK);
                })
                .addOnFailureListener(e -> {
                    showNotificationDialog("Cập nhật tác giả thất bại: " + e.getMessage());
                });
    }


    private void showNotificationDialog(String message) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.quanly_dialog_notification);
        dialog.setCancelable(false);

        TextView contentDialog = dialog.findViewById(R.id.contentDialog);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        contentDialog.setText(message);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            dialog.dismiss();
            if (message.contains("thành công")) {
                finish();
            }
        }, 3000);

        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            handler.removeCallbacksAndMessages(null);
            finish();
        });

        dialog.show();
    }
}
