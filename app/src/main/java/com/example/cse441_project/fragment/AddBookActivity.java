package com.example.cse441_project.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.R;
import com.example.cse441_project.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.content.ContentValues.TAG;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;


import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import lombok.NonNull;

public class AddBookActivity extends AppCompatActivity {
    private Spinner spinnerCategory, spinnerPublisher;
    private ArrayAdapter<String> categoryAdapter, publisherAdapter;
    private List<String> categoryList, publisherList;
    private ImageButton btnCloseTab, btnOpenCamera, btnOpenGallery;
    private Button btnSave;
    private ImageView imageViewBook;
    EditText edtName, edtAuthor, edtDescription, edtPublishYear, edtQuantity;

    private final int CAM_REQ = 1000;
    private final int IMG_REQ = 2000;
    Uri imageUri;
    ProgressDialog pd;
    StorageReference storageReference;
    FirebaseFirestore db;

    ArrayList<String> categoryNames = new ArrayList<>();
    HashMap<String, String> categoryMap = new HashMap<>();
    String selectedCategoryId;

    ArrayList<String> publisherNames = new ArrayList<>();
    HashMap<String, String> publisherMap = new HashMap<>();
    String selectedPublisherId;

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

        pd = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();

        // Ánh xạ id
        btnCloseTab = findViewById(R.id.btnCloseTab);
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        btnSave = findViewById(R.id.btnSave);
        imageViewBook = findViewById(R.id.imageViewBook);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerPublisher = findViewById(R.id.spinnerPublisher);
        edtName = findViewById(R.id.edtName);
        edtAuthor = findViewById(R.id.edtAuthor);
        edtDescription = findViewById(R.id.edtDescription);
        edtPublishYear = findViewById(R.id.edtPublishYear);
        edtQuantity = findViewById(R.id.edtQuantity);

        categoryList = new ArrayList<>();
        publisherList = new ArrayList<>();

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

        loadCategories();
//        // Tải dữ liệu Category từ Firestore và đổ vào Spinner
//        db.collection("Categories").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String categoryId = document.getId();
//                    String categoryName = document.getString("name");
//
//                    categoryNames.add(categoryName);
//                    categoryMap.put(categoryName, categoryId);
//                }
//
//                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
//                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinnerCategory.setAdapter(categoryAdapter);
//            } else {
//                Toast.makeText(this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
//            }
//        });

        loadPublishers();

//        // Tải dữ liệu Publisher từ Firestore và đổ vào Spinner
//        db.collection("Publishers").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String publisherId = document.getId();
//                    String publisherName = document.getString("name");
//
//                    publisherNames.add(publisherName);
//                    publisherMap.put(publisherName, publisherId);
//                }
//
//                ArrayAdapter<String> publisherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, publisherNames);
//                publisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinnerPublisher.setAdapter(publisherAdapter);
//            } else {
//                Toast.makeText(this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
//            }
//        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategoryName = categoryNames.get(position);
                if (selectedCategoryName.equals("Thêm danh mục mới")) {
                    // Hiển thị dialog để thêm danh mục mới
                    showAddCategoryDialog();
                    // Đặt lại selectedCategoryId để cho phép chọn lại
                    spinnerCategory.setSelection(0);

                } else {
                    // Lưu ID của danh mục đã chọn
                    selectedCategoryId = categoryMap.get(selectedCategoryName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategoryId = null;
            }
        });


        spinnerPublisher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPublisherName = publisherNames.get(position);
                if (selectedPublisherName.equals("Thêm nhà xuất bản mới")) {
                    // Hiển thị dialog để thêm nhà xuất bản mới
                    showAddPublisherDialog();
                    spinnerPublisher.setSelection(0);

                } else {
                    // Lưu ID của nhà xuất bản đã chọn
                    selectedPublisherId = publisherMap.get(selectedPublisherName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPublisherId = null;
            }
        });


        btnCloseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtName.getText().toString();
                String author = edtAuthor.getText().toString();
                String description = edtDescription.getText().toString();
                String publishYear = edtPublishYear.getText().toString();
                String quantity = edtQuantity.getText().toString();

                uploadData(name, author, selectedCategoryId, description, selectedPublisherId, publishYear, quantity);

            }
        });
    }

    private void uploadData(String name, String author, String categoryId, String description, String publisherId, String publishYear, String quantity) {
        if (!name.isEmpty() && !author.isEmpty() && !categoryId.isEmpty() && !description.isEmpty()
                && !publisherId.isEmpty() && !publishYear.isEmpty() && !quantity.isEmpty() && imageUri != null) {
            pd.setTitle("Đang thêm sách");
            pd.show();

            // Định dạng tên tệp ảnh để tránh trùng lặp
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
            Date date = new Date();
            String fileFormat = simpleDateFormat.format(date);

            // Thiết lập đường dẫn ảnh trên Firebase Storage
            storageReference = FirebaseStorage.getInstance().getReference("images/" + fileFormat);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Nhận được URL của ảnh sau khi upload thành công
                            String imageUrl = uri.toString();

//                            String id = UUID.randomUUID().toString();
                            String id = generateId();
                            Map<String, Object> doc = new HashMap<>();

                            doc.put("id", id);
                            doc.put("name", name);
                            doc.put("author", author);
                            doc.put("categoryId", selectedCategoryId);
                            doc.put("description", description);
                            doc.put("publisherId", selectedPublisherId);
                            doc.put("publishYear", Integer.parseInt(publishYear));
                            doc.put("quantity", Integer.parseInt(quantity));
                            doc.put("image", imageUrl);

                            db.collection("Books").document(id).set(doc)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                            pd.dismiss();
                                            Toast.makeText(AddBookActivity.this, "Thêm sách thành công!", Toast.LENGTH_SHORT).show();
                                            BookFragment.getInstance().docDulieu();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@androidx.annotation.NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@androidx.annotation.NonNull Exception e) {
                    // Tắt ProgressDialog và hiển thị lỗi khi upload thất bại
                    pd.dismiss();
                    Toast.makeText(AddBookActivity.this, "Tải ảnh lên thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Thông báo lỗi nếu thiếu thông tin hoặc ảnh
            Toast.makeText(AddBookActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAM_REQ) {
                Bitmap camBitmap = (Bitmap) data.getExtras().get("data");
                imageViewBook.setImageBitmap(camBitmap);
                // Chuyển Bitmap sang Uri và lưu vào imageUri
                imageUri = getImageUri(getApplicationContext(), camBitmap);
            }
            if (requestCode == IMG_REQ && data != null && data.getData() != null) {
                imageUri = data.getData();
                imageViewBook.setImageURI(imageUri);
            }
        }
    }

    // Chuyển đổi Bitmap sang Uri
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void loadCategories() {
        // Tải dữ liệu Category từ Firestore và đổ vào Spinner
        db.collection("Categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                categoryNames.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                categoryMap.clear(); // Xóa bản đồ category

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String categoryId = document.getId();
                    String categoryName = document.getString("name");

                    categoryNames.add(categoryName);
                    categoryMap.put(categoryName, categoryId);
                }

                // Thêm mục "Thêm danh mục mới" vào cuối danh sách
                categoryNames.add("Thêm danh mục mới");

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryNames);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
            } else {
                Toast.makeText(this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPublishers() {
        // Tải dữ liệu Publisher từ Firestore và đổ vào Spinner
        db.collection("Publishers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                publisherNames.clear(); // Xóa dữ liệu cũ trước khi thêm mới
                publisherMap.clear();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String publisherId = document.getId();
                    String publisherName = document.getString("name");

                    publisherNames.add(publisherName);
                    publisherMap.put(publisherName, publisherId);
                }

                // Thêm mục "Thêm nhà xuất bản mới" vào cuối danh sách
                publisherNames.add("Thêm nhà xuất bản mới");

                ArrayAdapter<String> publisherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, publisherNames);
                publisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPublisher.setAdapter(publisherAdapter);
            } else {
                Toast.makeText(this, "Lỗi khi tải nhà xuất bản", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addNewCategory(String name) {
        if (!name.isEmpty()) {
            String id = UUID.randomUUID().toString();

            // Kiểm tra id tồn tại
            db.collection("Categories").document(id).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().exists()) {
                            // Nếu id chưa tồn tại, thì thêm dữ liệu vào Firestore
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("id", id);
                            doc.put("name", name);

                            db.collection("Categories").document(id).set(doc)
                                    .addOnCompleteListener(task1 -> {
                                        Toast.makeText(AddBookActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                        loadCategories();

                                        // Thêm danh mục mới vào danh sách và cập nhật Spinner
                                        categoryNames.add(name); // Thêm danh mục mới vào danh sách
                                        categoryMap.put(name, id); // Lưu ID của danh mục

                                        spinnerCategory.setSelection(categoryNames.indexOf(name));

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Nếu ID đã tồn tại, gọi lại hàm uploadData với một ID mới
                            addNewCategory(name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddBookActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewPublisher(String name, String address, String country) {
        if (!name.isEmpty() && !address.isEmpty() && !country.isEmpty()) {
            String id = UUID.randomUUID().toString();

            // Kiểm tra id tồn tại
            db.collection("Publishers").document(id).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().exists()) {
                            // Nếu id chưa tồn tại, thì thêm dữ liệu vào Firestore
                            Map<String, Object> doc = new HashMap<>();
                            doc.put("id", id);
                            doc.put("name", name);
                            doc.put("address", address);
                            doc.put("country", country);

                            db.collection("Publishers").document(id).set(doc)
                                    .addOnCompleteListener(task1 -> {
                                        Toast.makeText(AddBookActivity.this, "Thêm nhà xuất bản thành công!", Toast.LENGTH_SHORT).show();
                                        loadPublishers();

                                        // Thêm nhà xuất bản mới vào danh sách và cập nhật Spinner
                                        publisherNames.add(name); // Thêm nhà xuất bản mới vào danh sách
                                        publisherMap.put(name, id); // Lưu ID của nhà xuất bản

                                        spinnerPublisher.setSelection(publisherNames.indexOf(name));

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Nếu ID đã tồn tại, gọi lại hàm uploadData với một ID mới
                            addNewPublisher(name, address, country);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(AddBookActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateId() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder(5);
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int index = random.nextInt(characters.length());
            id.append(characters.charAt(index));
        }
        return id.toString();
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Tạo một TextView để hiển thị tiêu đề
        TextView titleView = new TextView(this);
        titleView.setText("Thêm danh mục mới");
        titleView.setGravity(Gravity.CENTER); // Căn giữa tiêu đề
        titleView.setTextSize(20); // Đặt kích thước chữ cho tiêu đề
        titleView.setPadding(16, 20, 16, 16); // Thêm padding cho tiêu đề

        // Tạo một LinearLayout để chứa các EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32); // Thêm padding cho layout

        // Tạo EditText cho tên nhà xuất bản
        EditText inputName = new EditText(this);
        inputName.setHint("Tên danh mục");
        layout.addView(inputName); // Thêm EditText vào layout

        // Đặt layout và title vào AlertDialog
        builder.setCustomTitle(titleView);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCategoryName = inputName.getText().toString();
            if (!newCategoryName.isEmpty()) {
                addNewCategory(newCategoryName); // Gọi hàm thêm danh mục mới
            } else {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showAddPublisherDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Tạo một TextView để hiển thị tiêu đề
        TextView titleView = new TextView(this);
        titleView.setText("Thêm nhà xuất bản mới");
        titleView.setGravity(Gravity.CENTER); // Căn giữa tiêu đề
        titleView.setTextSize(20); // Đặt kích thước chữ cho tiêu đề
        titleView.setPadding(16, 20, 16, 16); // Thêm padding cho tiêu đề

        // Tạo một LinearLayout để chứa các EditText
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32); // Thêm padding cho layout

        // Tạo EditText cho tên nhà xuất bản
        EditText inputName = new EditText(this);
        inputName.setHint("Tên nhà xuất bản");
        layout.addView(inputName); // Thêm EditText vào layout

        // Tạo EditText cho địa chỉ
        EditText inputAddress = new EditText(this);
        inputAddress.setHint("Địa chỉ");
        layout.addView(inputAddress); // Thêm EditText vào layout

        // Tạo EditText cho quốc gia
        EditText inputCountry = new EditText(this);
        inputCountry.setHint("Quốc gia");
        layout.addView(inputCountry); // Thêm EditText vào layout

        // Đặt layout và title vào AlertDialog
        builder.setCustomTitle(titleView);
        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newPublisherName = inputName.getText().toString();
            String newPublisherAddress = inputAddress.getText().toString();
            String newPublisherCountry = inputCountry.getText().toString();
            if (!newPublisherName.isEmpty() && !newPublisherAddress.isEmpty() && !newPublisherCountry.isEmpty()) {
                addNewPublisher(newPublisherName, newPublisherAddress, newPublisherCountry); // Gọi hàm thêm danh mục mới
            } else {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}