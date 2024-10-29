package com.example.cse441_project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

public class AddBookActivity extends AppCompatActivity {
    private Spinner spinnerCategory, spinnerPublisher;
    private ArrayAdapter<String> categoryAdapter, publisherAdapter;
    private List<String> categoryList, publisherList;
    private ImageButton btnCloseTab, btnOpenCamera, btnOpenGallery;
    private ImageView imageViewBook;

    private final int CAM_REQ = 1000;
    private final int IMG_REQ = 2000;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnCloseTab = findViewById(R.id.btnCloseTab);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        imageViewBook = findViewById(R.id.imageViewBook);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerPublisher = findViewById(R.id.spinnerPublisher);
        categoryList = new ArrayList<>();
        publisherList = new ArrayList<>();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraintent = new Intent(ACTION_IMAGE_CAPTURE);
                if (ActivityCompat.checkSelfPermission(AddBookActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddBookActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
                    return;
                }
                startActivityForResult(cameraintent, CAM_REQ);
            }
        });

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra quyền truy cập lưu trữ
                if (ActivityCompat.checkSelfPermission(AddBookActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(AddBookActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMG_REQ);
                    return; // Kết thúc hàm nếu quyền chưa được cấp
                }

                // Nếu quyền đã được cấp, mở thư viện ảnh
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, IMG_REQ);
            }
        });

        // Thiết lập văn bản mặc định cho Spinner
        //categoryList.add("Chọn danh mục"); // Văn bản mặc định

        // Tạo Adapter cho Spinner
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        publisherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, publisherList);
        publisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPublisher.setAdapter(publisherAdapter);

        // Lấy danh mục từ Firestore
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Categories") // Thay đổi tên collection nếu cần
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String categoryName = document.getString("name"); // Thay đổi tên trường nếu cần
                                categoryList.add(categoryName); // Thêm tên danh mục vào danh sách
                            }
                            categoryAdapter.notifyDataSetChanged(); // Cập nhật Adapter
                        } else {
                            Log.d("TAG", "Error getting categories: ", task.getException());
                        }
                    }
                });

        // Lấy nhà xuất bản từ Firestore
        firestore.collection("Categories") // Thay đổi tên collection nếu cần
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String publisherName = document.getString("name"); // Thay đổi tên trường nếu cần
                                publisherList.add(publisherName); // Thêm tên nhà xuất bản vào danh sách
                            }
                            publisherAdapter.notifyDataSetChanged(); // Cập nhật Adapter
                        } else {
                            Log.d("TAG", "Error getting publishers: ", task.getException());
                        }
                    }
                });

//        Đặt chọn mặc định cho Spinner
//        spinnerCategory.setSelection(0);
//        spinnerPublisher.setSelection(0);

        btnCloseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAM_REQ) {
                Bitmap camBitmap = (Bitmap) data.getExtras().get("data");
                imageViewBook.setImageBitmap(camBitmap);
            }
            if (requestCode == IMG_REQ) {
                imageUri = data.getData();
                imageViewBook.setImageURI(imageUri);
            }
        }
    }
}