package com.example.cse441_project.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPublisherActivity extends AppCompatActivity {
    private EditText editTextId, editTextName, editTextAddress;
    private String publisherId;
    private FirebaseFirestore db;
    private Button buttonSave;
    private ImageView buttonBack, iconPickCountry;
    private Spinner spinnerCountry;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_publisher_edit);

        editTextId = findViewById(R.id.editTextPublisherId);
        editTextName = findViewById(R.id.editTextPublisherName);
        editTextAddress = findViewById(R.id.editTextPublisherAddress);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBackEdit);

        iconPickCountry = findViewById(R.id.iconPickCountry);
        spinnerCountry = findViewById(R.id.spinnerCountry);

        String[] countries = {"Việt Nam", "Mỹ", "Nhật Bản", "Trung Quốc", "Hàn Quốc", "Anh", "Pháp", "Đức", "Ấn Độ", "Úc"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        if (getIntent() != null) {
            publisherId = getIntent().getStringExtra("PUBLISHER_ID");
            String publisherName = getIntent().getStringExtra("PUBLISHER_NAME");
            String publisherAddress = getIntent().getStringExtra("PUBLISHER_ADDRESS");
            String publisherCountry = getIntent().getStringExtra("PUBLISHER_COUNTRY");

            editTextId.setText(publisherId);
            editTextName.setText(publisherName);
            editTextAddress.setText(publisherAddress);
            if (publisherCountry != null) {
                int spinnerPosition = adapter.getPosition(publisherCountry);
                spinnerCountry.setSelection(spinnerPosition);
            }
        }

        buttonSave.setOnClickListener(v -> showConfirmationDialog());
        buttonBack.setOnClickListener(v -> finish());

        iconPickCountry.setOnClickListener(v -> spinnerCountry.performClick());
    }

    private void updatePublisher() {
        String name = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String country = spinnerCountry.getSelectedItem().toString().trim();

        if (name.isEmpty() || address.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Publishers").document(publisherId).update(
                "name", name,
                "address", address,
                "country", country
            ).addOnSuccessListener(aVoid -> {
                showNotificationDialog("Cập nhật thành công");
                setResult(Activity.RESULT_OK);
            }).addOnFailureListener(e -> {
                showNotificationDialog("Cập nhật thất bại");
                Toast.makeText(this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
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
        confirmMessage.setText("Bạn có chắc chắn muốn sửa nhà xuất bản không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            updatePublisher();
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
            if (message.equals("Cập nhật thành công")) {
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
