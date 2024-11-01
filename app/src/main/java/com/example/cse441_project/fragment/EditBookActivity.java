package com.example.cse441_project.fragment;

import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.example.cse441_project.model.Category;
import com.example.cse441_project.model.Publisher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
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

public class EditBookActivity extends AppCompatActivity {
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

    // Khai báo các biến cho Category và Publisher
    ArrayList<String> categoryNames = new ArrayList<>();
    HashMap<String, String> categoryMap = new HashMap<>();
    String selectedCategoryId;

    ArrayList<String> publisherNames = new ArrayList<>();
    HashMap<String, String> publisherMap = new HashMap<>();
    String selectedPublisherId;

    String pId, pName, pCategory, pAuthor, pDescription, pPublisher, pPublishYear, pQuantity, pImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pId = bundle.getString("pId");
            pName = bundle.getString("pName");
            pAuthor = bundle.getString("pAuthor");
            pCategory = bundle.getString("pCategory");
            pDescription = bundle.getString("pDescription");
            pPublisher = bundle.getString("pPublisher");
            pPublishYear = bundle.getString("pPublishYear");
            pQuantity = bundle.getString("pQuantity");
            pImage = bundle.getString("pImage");


            edtName.setText(pName);
            edtAuthor.setText(pAuthor);
            edtDescription.setText(pDescription);
            edtPublishYear.setText(pPublishYear);
            edtQuantity.setText(pQuantity);
            // Thiết lập hình ảnh
            Glide.with(this)
                    .load(pImage) // Đường dẫn hình ảnh
                    .into(imageViewBook); // ImageView nơi hình ảnh sẽ được hiển thị

        }

        pd = new ProgressDialog(this);
        db = FirebaseFirestore.getInstance();

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraintent = new Intent(ACTION_IMAGE_CAPTURE);
                if (ActivityCompat.checkSelfPermission(EditBookActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditBookActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
                    return;
                }
                startActivityForResult(cameraintent, CAM_REQ);
            }
        });

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra quyền truy cập lưu trữ
                if (ActivityCompat.checkSelfPermission(EditBookActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // Nếu quyền chưa được cấp, yêu cầu quyền
                    ActivityCompat.requestPermissions(EditBookActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, IMG_REQ);
                    return; // Kết thúc hàm nếu quyền chưa được cấp
                }

                // Nếu quyền đã được cấp, mở thư viện ảnh
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, IMG_REQ);
            }
        });


        loadCategories();

        // Tải dữ liệu Publisher từ Firestore và đổ vào Spinner
        db.collection("Publishers").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String publisherId = document.getId();
                    String publisherName = document.getString("name");

                    publisherNames.add(publisherName);
                    publisherMap.put(publisherName, publisherId);
                }

                ArrayAdapter<String> publisherAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, publisherNames);
                publisherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPublisher.setAdapter(publisherAdapter);
            } else {
                Toast.makeText(this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategoryName = categoryNames.get(position);
                if (selectedCategoryName.equals("Thêm danh mục mới")) {
                    // Hiển thị dialog để thêm danh mục mới
                    showAddCategoryDialog();

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

        // Xử lý chọn Publisher từ Spinner
        spinnerPublisher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPublisherName = publisherNames.get(position);
                selectedPublisherId = publisherMap.get(selectedPublisherName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPublisherId = null;
            }
        });

        btnCloseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //BookFragment.getInstance().docDulieu();
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = getIntent().getExtras();
                if (bundle1 != null) {
                    //update
                    String id = pId;
                    String name = edtName.getText().toString().trim();
                    String author = edtAuthor.getText().toString().trim();
                    String description = edtDescription.getText().toString().trim();
                    String publishYear = edtPublishYear.getText().toString().trim();
                    String quantity = edtQuantity.getText().toString().trim();


                    updateData(id, name, author, selectedCategoryId, description, selectedPublisherId, publishYear, quantity);
                } else {
                    String name = edtName.getText().toString().trim();

                    //uploadData(name);
                }
            }
        });

    }

    private void updateData(String id, String name, String author, String selectedCategoryId, String description, String selectedPublisherId, String publishYear, String quantity) {
        if (!name.isEmpty() && !author.isEmpty() && !selectedCategoryId.isEmpty() && !description.isEmpty()
                && !selectedPublisherId.isEmpty() && !publishYear.isEmpty() && !quantity.isEmpty()) {
            pd.setTitle("Đang cập nhật...");
            pd.show();

            if (imageUri != null) {
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
                                // Sau khi đã có URL của ảnh, tiến hành cập nhật dữ liệu lên Firestore
                                updateFirestoreData(id, name, author, selectedCategoryId, description, selectedPublisherId, publishYear, quantity, imageUrl);
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(EditBookActivity.this, "Lỗi khi lấy URL ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(EditBookActivity.this, "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Nếu không có ảnh mới, chỉ cập nhật dữ liệu
                updateFirestoreData(id, name, author, selectedCategoryId, description, selectedCategoryId, publishYear, quantity, null);
            }

        } else {
            Toast.makeText(EditBookActivity.this, "Vui lòng nhập đầy đủ thông tin và chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFirestoreData(String id, String name, String author, String selectedCategoryId, String description, String selectedPublisherId, String publishYear, String quantity, String imageUrl) {
        // Tạo một Map để chứa dữ liệu cần cập nhật
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("name", name);
        updateData.put("author", author);
        updateData.put("description", description);
        updateData.put("categoryId", selectedCategoryId);
        updateData.put("publisherId", selectedPublisherId);
        updateData.put("publishYear", Integer.parseInt(publishYear));
        updateData.put("quantity", Integer.parseInt(quantity));

        // Chỉ thêm trường "image" nếu imageUrl không null
        if (imageUrl != null) {
            updateData.put("image", imageUrl);
        }

        // Thực hiện cập nhật Firestore với các dữ liệu đã chọn
        db.collection("Books").document(id)
                .update(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        Toast.makeText(EditBookActivity.this, "Đã cập nhật", Toast.LENGTH_SHORT).show();
                        BookFragment.getInstance().docDulieu();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(EditBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                                        Toast.makeText(EditBookActivity.this, "Thêm danh mục thành công!", Toast.LENGTH_SHORT).show();
                                        loadCategories();

                                        // Thêm danh mục mới vào danh sách và cập nhật Spinner
                                        categoryNames.add(name); // Thêm danh mục mới vào danh sách
                                        categoryMap.put(name, id); // Lưu ID của danh mục

                                        spinnerCategory.setSelection(categoryNames.indexOf(name));

                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(EditBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Nếu ID đã tồn tại, gọi lại hàm uploadData với một ID mới
                            addNewCategory(name);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(EditBookActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm danh mục mới");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCategoryName = input.getText().toString();
            if (!newCategoryName.isEmpty()) {
                addNewCategory(newCategoryName); // Gọi hàm thêm danh mục mới
            } else {
                Toast.makeText(this, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    // Chuyển đổi Bitmap sang Uri
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
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

}