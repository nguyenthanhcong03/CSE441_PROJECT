package com.example.cse441_project.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddRuleActivity extends AppCompatActivity {

    private ImageButton btnCloseTab;
    private Button btnSave;
    EditText edtName, edtContent;

    ProgressDialog pd;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_rule);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pd = new ProgressDialog(this);

        db = FirebaseFirestore.getInstance();

        // Ánh xạ id
        btnCloseTab = findViewById(R.id.btnCloseTab);
        btnSave = findViewById(R.id.btnSave);
        edtName = findViewById(R.id.edtName);
        edtContent = findViewById(R.id.edtContent);


        btnCloseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Button Lưu
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String content = edtContent.getText().toString();
                uploadData(name, content);
            }
        });
    }

    private void uploadData(String name, String content) {
        if (!name.isEmpty() && !content.isEmpty()) {
            pd.setTitle("Đang thêm quy định");
            pd.show();

            String id = UUID.randomUUID().toString();
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", id);
            doc.put("name", name);
            doc.put("content", content);

            db.collection("Rules").document(id).set(doc)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                            pd.dismiss();
                            Toast.makeText(AddRuleActivity.this, "Thêm quy định thành công!", Toast.LENGTH_SHORT).show();
                            // Refresh dữ liệu trong RuleFragment (nếu có)
                            RuleFragment.getInstance().docDulieu();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddRuleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(AddRuleActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }


}