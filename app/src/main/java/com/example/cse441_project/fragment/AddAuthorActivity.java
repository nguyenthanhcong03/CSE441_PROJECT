package com.example.cse441_project.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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

import com.example.cse441_project.R;
import com.example.cse441_project.model.Author;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.util.Calendar;

public class AddAuthorActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextAuthorName, editTextAuthorBirthday;
    private RadioGroup radioGroupGender;
    private Button buttonAdd;
    private ImageView buttonBack, iconAvatarAuthor, iconPickImage, iconPickDate, iconPickNationality;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    Spinner spinnerNationality;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_author_add);

        editTextAuthorName = findViewById(R.id.editTextAuthorName);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        editTextAuthorBirthday = findViewById(R.id.editTextAuthorBirthday);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonBack = findViewById(R.id.buttonBackAdd);

        iconAvatarAuthor = findViewById(R.id.iconAvatarAuthor);
        iconPickImage = findViewById(R.id.iconPickImage);
        iconPickDate  = findViewById(R.id.iconPickDate);
        iconPickNationality = findViewById(R.id.iconPickNationality);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        buttonAdd.setOnClickListener(v -> showConfirmationDialog());
        buttonBack.setOnClickListener(v -> finish());

        iconPickImage.setOnClickListener(v -> openFileChooser());

        editTextAuthorBirthday.setOnClickListener(v -> {
            showDatePickerDialog();
        });

        spinnerNationality = findViewById(R.id.spinnerNationality);
        String[] countries = {"Việt Nam", "Mỹ", "Nhật Bản", "Trung Quốc", "Hàn Quốc", "Anh", "Pháp", "Đức", "Ấn Độ", "Úc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNationality.setAdapter(adapter);

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
                    editTextAuthorBirthday.setText(date); // Set the selected date to EditText
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveAuthor() {
        String name = editTextAuthorName.getText().toString().trim();
        String gender = getSelectedGender();
        String birthday = editTextAuthorBirthday.getText().toString().trim();
        String nationality = spinnerNationality.getSelectedItem().toString();

        if (name.isEmpty() || gender == null || imageUri == null) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String authorId = db.collection("Authors").document().getId();

        StorageReference fileReference = storage.getReference("avatars").child(System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = fileReference.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
            String avatarUrl = uri.toString();

            Author author = new Author(authorId, name, gender, birthday, nationality, avatarUrl);

            db.collection("Authors")
                    .document(authorId)
                    .set(author)
                    .addOnSuccessListener(aVoid -> {
                        showNotificationDialog("Thêm tác giả thành công");
                        setResult(Activity.RESULT_OK);
                    })
                    .addOnFailureListener(e -> {
                        showNotificationDialog("Thêm tác giả thất bại");
                        Log.d("AddAuthorError", "Lưu thất bại: " + e.getMessage());
                    });
        })).addOnFailureListener(e -> {
            showNotificationDialog("Lưu ảnh thất bại");
            Log.d("AddAuthorError", "Lưu ảnh thất bại: " + e.getMessage());
        });
    }


    private String getSelectedGender() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radioMale) {
            return "Nam";
        } else if (selectedId == R.id.radioFemale) {
            return "Nữ";
        } else {
            return null;
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            iconAvatarAuthor.setImageURI(imageUri);
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

        confirmTitle.setText("Thêm tác giả mới");
        confirmMessage.setText("Bạn có chắc chắn muốn thêm tác giả không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            saveAuthor();
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

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            dialog.dismiss();
            if (message.equals("Thêm tác giả thành công")) {
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
