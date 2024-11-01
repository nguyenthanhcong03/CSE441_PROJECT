package com.example.cse441_project.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditCategoryActivity extends AppCompatActivity {

    EditText edtName;
    Button btnSave;
    ImageButton btnCloseTab;

    ProgressDialog pd;

    FirebaseFirestore db;

    String pId, pName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtName = findViewById(R.id.edtName);
        btnSave = findViewById(R.id.btnSave);
        btnCloseTab = findViewById(R.id.btnCloseTab);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pId = bundle.getString("pId");
            pName = bundle.getString("pName");

            edtName.setText(pName);
        }

        pd = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null) {
                    //update
                    String id = pId;
                    String name = edtName.getText().toString().trim();
                    updateData(id, name);
                } else {
                    String name = edtName.getText().toString().trim();

                    //uploadData(name);
                }
            }
        });

        btnCloseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //BookFragment.getInstance().docDulieu();
                finish();
            }
        });
    }

    private void updateData(String id, String name) {
        if (!name.isEmpty()) {
            pd.setTitle("Đang cập nhật...");
            pd.show();

            db.collection("Categories").document(id)
                    .update("name", name)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            Toast.makeText(EditCategoryActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                            CategoryFragment.getInstance().docDulieu();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(EditCategoryActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }
}