package com.example.cse441_project.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cse441_project.R;
import com.example.cse441_project.model.Publisher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;

public class AddPublisherActivity extends AppCompatActivity {
    private EditText editTextPublisherName, editTextPublisherAddress, editTextPublisherCountry;
    private Button buttonAdd;
    private FirebaseFirestore db;
    private ImageView buttonBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_publisher_add);

        editTextPublisherName = findViewById(R.id.editTextPublisherName);
        editTextPublisherAddress = findViewById(R.id.editTextPublisherAddress);
        editTextPublisherCountry = findViewById(R.id.editTextPublisherCountry);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonBack = findViewById(R.id.buttonBackAdd);

        db = FirebaseFirestore.getInstance();

        buttonAdd.setOnClickListener(v -> showConfirmationDialog());
        buttonBack.setOnClickListener(v -> finish());
    }

    private void savePublisher() {
        String name = editTextPublisherName.getText().toString().trim();
        String address = editTextPublisherAddress.getText().toString().trim();
        String country = editTextPublisherCountry.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || country.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference newPublisherRef = db.collection("Publishers").document();
        String newId = "PUB" + newPublisherRef.getId().substring(0, 5);

        Publisher publisher = new Publisher();
        publisher.setId(newId);
        publisher.setName(name);
        publisher.setAddress(address);
        publisher.setCountry(country);

        db.collection("Publishers")
                .document(newId)
                .set(publisher)
                .addOnSuccessListener(aVoid -> {
                    showNotificationDialog("Thêm nhà xuất bản thành công");
                    setResult(Activity.RESULT_OK);
                })
                .addOnFailureListener(e -> {
                    showNotificationDialog("Thêm nhà xuất bản thất bại");
                    Log.d("AddPublisherError", "Lưu thất bại: " + e.getMessage());
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

        confirmTitle.setText("Thêm NXB mới");
        confirmMessage.setText("Bạn có chắc chắn muốn thêm nhà xuất bản không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            savePublisher();
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
            if (message.equals("Thêm nhà xuất bản thành công")) {
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
