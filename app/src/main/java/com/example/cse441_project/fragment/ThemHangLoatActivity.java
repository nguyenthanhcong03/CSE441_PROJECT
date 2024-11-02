//package com.example.cse441_project.fragment;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.cse441_project.R;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ThemHangLoatActivity extends AppCompatActivity {
//
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private Map<String, String> categoryMap = new HashMap<>();
//    private Map<String, String> publisherMap = new HashMap<>();
//    private ActivityResultLauncher<Intent> filePickerLauncher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_add_book);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Tải thể loại và nhà xuất bản từ Firestore
//        loadCategoriesAndPublishers();
//
//        // Thiết lập launcher để chọn file CSV
//        filePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                Uri csvUri = result.getData().getData();
//                if (csvUri != null) {
//                    addBooksFromCSV(csvUri);
//                }
//            }
//        });
//
//        // Nút "Chọn file CSV" và xử lý khi nhấn
//        Button selectCSVButton = findViewById(R.id.btnSelectCSV);
//        selectCSVButton.setOnClickListener(v -> openFileChooser());
//    }
//
//    // Mở bộ chọn file để chọn file CSV
//    private void openFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.setType("text/csv");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        filePickerLauncher.launch(intent);
//    }
//
//    // Phương thức đọc file CSV từ URI và thêm sách vào Firestore
//    private void addBooksFromCSV(Uri csvUri) {
//        try {
//            InputStream inputStream = getContentResolver().openInputStream(csvUri);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] data = line.split(",");
//                String image = data[0];
//                String categoryName = data[1];
//                String publisherName = data[2];
//                String name = data[3];
//                String author = data[4];
//                String description = data[5];
//                int publishYear = Integer.parseInt(data[6]);
//                int quantity = Integer.parseInt(data[7]);
//
//                // Thêm thể loại và nhà xuất bản nếu chưa tồn tại
//                String categoryId = addCategoryIfNotExists(categoryName);
//                String publisherId = addPublisherIfNotExists(publisherName, "Địa chỉ mặc định", "Quốc gia mặc định");
//
//                // Thêm sách vào Firestore
//                Map<String, Object> book = new HashMap<>();
//                book.put("image", image);
//                book.put("categoryId", categoryId);
//                book.put("publisherId", publisherId);
//                book.put("name", name);
//                book.put("author", author);
//                book.put("description", description);
//                book.put("publishYear", publishYear);
//                book.put("quantity", quantity);
//
//                db.collection("Books").add(book)
//                        .addOnSuccessListener(documentReference ->
//                                Toast.makeText(this, "Thêm sách thành công: " + name, Toast.LENGTH_SHORT).show())
//                        .addOnFailureListener(e ->
//                                Toast.makeText(this, "Lỗi khi thêm sách: " + e.getMessage(), Toast.LENGTH_SHORT).show());
//            }
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Lỗi đọc file CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    // Phương thức tải thể loại và nhà xuất bản từ Firestore (giống trước đó)
//    private void loadCategoriesAndPublishers() {
//        db.collection("Categories").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String categoryId = document.getId();
//                    String categoryName = document.getString("name");
//                    categoryMap.put(categoryName, categoryId);
//                }
//            }
//        });
//
//        db.collection("Publishers").get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                for (QueryDocumentSnapshot document : task.getResult()) {
//                    String publisherId = document.getId();
//                    String publisherName = document.getString("name");
//                    publisherMap.put(publisherName, publisherId);
//                }
//            }
//        });
//    }
//
//    // Thêm mới thể loại và nhà xuất bản nếu chưa tồn tại (giống trước đó)
//    private String addCategoryIfNotExists(String categoryName) {
//        if (categoryMap.containsKey(categoryName)) {
//            return categoryMap.get(categoryName);
//        }
//
//        Map<String, Object> categoryData = new HashMap<>();
//        categoryData.put("name", categoryName);
//
//        String newCategoryId = db.collection("Categories").document().getId();
//        db.collection("Categories").document(newCategoryId).set(categoryData)
//                .addOnSuccessListener(aVoid -> categoryMap.put(categoryName, newCategoryId));
//
//        return newCategoryId;
//    }
//
//    private String addPublisherIfNotExists(String publisherName, String address, String country) {
//        if (publisherMap.containsKey(publisherName)) {
//            return publisherMap.get(publisherName);
//        }
//
//        Map<String, Object> publisherData = new HashMap<>();
//        publisherData.put("name", publisherName);
//        publisherData.put("address", address);
//        publisherData.put("country", country);
//
//        String newPublisherId = db.collection("Publishers").document().getId();
//        db.collection("Publishers").document(newPublisherId).set(publisherData)
//                .addOnSuccessListener(aVoid -> publisherMap.put(publisherName, newPublisherId));
//
//        return newPublisherId;
//    }
//}
