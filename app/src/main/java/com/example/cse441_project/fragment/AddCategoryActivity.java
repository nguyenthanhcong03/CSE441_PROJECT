package com.example.cse441_project.fragment;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {
    private ImageButton btnCloseTab;
    private Button btnSave;
    EditText edtName;

    ProgressDialog pd;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
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

                uploadData(name);


//                // Kiểm tra tất cả các thông tin và ảnh
//                if (!name.isEmpty()) {
//
//                    // Hiển thị ProgressDialog trong khi upload
//                    ProgressDialog progressDialog = new ProgressDialog(AddCategoryActivity.this);
//                    progressDialog.setTitle("Đang tải lên...");
//                    progressDialog.show();
//
//                    // Định dạng tên tệp ảnh để tránh trùng lặp
//                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
//                    Date date = new Date();
//                    String fileFormat = simpleDateFormat.format(date);
//
//                    // Thiết lập đường dẫn ảnh trên Firebase Storage
//                    storageReference = FirebaseStorage.getInstance().getReference("images/" + fileFormat);
//                    storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    // Nhận được URL của ảnh sau khi upload thành công
//                                    String imageUrl = uri.toString();
//
//                                    String id = UUID.randomUUID().toString();
//
//                                    Map<String, Object> doc = new HashMap<>();
//                                    doc.put("id", id);
//                                    doc.put("name", name);
//                                    doc.put("image", imageUrl);
//
//                                    db.collection("Categories").document(id).set(doc)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        // Tắt ProgressDialog và hiển thị thông báo thành công
//                                                        progressDialog.dismiss();
//                                                        Toast.makeText(AddCategoryActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
//                                                        // Refresh dữ liệu trong CategoryFragment (nếu có)
//                                                        CategoryFragment.getInstance().docDulieu();
//                                                        finish();
//                                                    } else {
//                                                        // Tắt ProgressDialog và hiển thị lỗi
//                                                        progressDialog.dismiss();
//                                                        Log.d(TAG, "Lỗi khi thêm danh mục", task.getException());
//                                                    }
//
//                                                }
//                                            });
//                                }
//                            });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@androidx.annotation.NonNull Exception e) {
//                            // Tắt ProgressDialog và hiển thị lỗi khi upload thất bại
//                            progressDialog.dismiss();
//                            Toast.makeText(AddCategoryActivity.this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    // Thông báo lỗi nếu thiếu thông tin hoặc ảnh
//                    Toast.makeText(AddCategoryActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }

    private void uploadData(String name) {
        if (!name.isEmpty()) {
            pd.setTitle("Đang thêm danh mục");
            pd.show();

            String id = UUID.randomUUID().toString();

            // Kiểm tra id tồn tại
            db.collection("Categories").document(id).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().exists()) {
                            // Nếu id chưa tồn tại, thì thêm dữ liệu vào Firestore
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("id", id);
                            doc.put("name", name);
                            doc.put("search", name.toLowerCase());

                            db.collection("Categories").document(id).set(doc)
                                    .addOnCompleteListener(task1 -> {
                                        pd.dismiss();
                                        Toast.makeText(AddCategoryActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                        CategoryFragment.getInstance().docDulieu(); // Refresh dữ liệu
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        pd.dismiss();
                                        Toast.makeText(AddCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Nếu ID đã tồn tại, gọi lại hàm uploadData với một ID mới
                            uploadData(name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(AddCategoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddCategoryActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

}