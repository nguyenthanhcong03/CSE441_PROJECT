package com.example.cse441_project.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.R;

public class ChangePasswordActivity extends AppCompatActivity {
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_change_password);

        // Thiết lập toggle cho các trường mật khẩu
        setupPasswordVisibilityToggle(findViewById(R.id.edtPassword), findViewById(R.id.ivTogglePasswordVisibilityCurrent));
        setupPasswordVisibilityToggle(findViewById(R.id.edtNewPass), findViewById(R.id.ivTogglePasswordVisibilityNew));
        setupPasswordVisibilityToggle(findViewById(R.id.editTextText), findViewById(R.id.ivTogglePasswordVisibilityConfirm));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnSave = findViewById(R.id.button);
        btnSave.setOnClickListener(v -> showSuccessDialog());

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