package com.example.cse441_project.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditPublisherActivity extends AppCompatActivity {
    private EditText editTextId, editTextName, editTextAddress, editTextCountry;
    private String publisherId;
    private FirebaseFirestore db;
    private Button buttonSave;
    private ImageView buttonBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_publisher_edit);

        editTextId = findViewById(R.id.editTextPublisherId);
        editTextName = findViewById(R.id.editTextPublisherName);
        editTextAddress = findViewById(R.id.editTextPublisherAddress);
        editTextCountry = findViewById(R.id.editTextPublisherCountry);
        buttonSave = findViewById(R.id.buttonSave);
        buttonBack = findViewById(R.id.buttonBackEdit);

        db = FirebaseFirestore.getInstance();

        if (getIntent() != null) {
            publisherId = getIntent().getStringExtra("PUBLISHER_ID");

            editTextId.setText(getIntent().getStringExtra("PUBLISHER_ID"));
            editTextName.setText(getIntent().getStringExtra("PUBLISHER_NAME"));
            editTextAddress.setText(getIntent().getStringExtra("PUBLISHER_ADDRESS"));
            editTextCountry.setText(getIntent().getStringExtra("PUBLISHER_COUNTRY"));
        }

        buttonSave.setOnClickListener(v -> showConfirmationDialog());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void updatePublisher() {
        String name = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();

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
