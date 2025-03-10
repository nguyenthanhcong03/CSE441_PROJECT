package com.example.cse441_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class BookDetailActivity extends AppCompatActivity {
    TextView txtName, txtAuthor, txtDescription, txtCategory, txtQuantity, txtPublisher, txtPublishYear, txtBorrowedBooks, txtAvailableBooks;
    ImageButton btnDelete, btnEdit, btnCloseTab;
    ImageView imageViewBook;

    String pId, pName, pCategory, pAuthor, pDescription, pPublisher, pPublishYear, pQuantity, pImage;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        txtName = findViewById(R.id.txtName);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtDescription = findViewById(R.id.txtDescription);
        txtCategory = findViewById(R.id.txtCategory);
        txtQuantity = findViewById(R.id.txtQuantity);
        txtPublishYear = findViewById(R.id.txtPublishYear);
        txtPublisher = findViewById(R.id.txtPublisher);
        imageViewBook = findViewById(R.id.imageViewBook);
        txtBorrowedBooks = findViewById(R.id.txtBorrowedBooks);
        txtAvailableBooks = findViewById(R.id.txtAvailableBooks);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnCloseTab = findViewById(R.id.btnCloseTab);

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


            txtName.setText(pName);
            txtAuthor.setText(pAuthor);
            txtDescription.setText(pDescription);
            txtPublishYear.setText(pPublishYear);
            txtQuantity.setText(pQuantity);

            // Thiết lập hình ảnh
            Glide.with(this).load(pImage).into(imageViewBook);

            fetchCategoryName(pCategory);
            fetchPublisherName(pPublisher);
            fetchQuantity(pId, Integer.parseInt(pQuantity));

        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BookFragment.getInstance().deleteData(position);
                new AlertDialog.Builder(BookDetailActivity.this)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc chắn muốn xóa cuốn sách này không?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            // Thực hiện xóa sách
                            deleteBook();
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });

        btnCloseTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void fetchCategoryName(String categoryId) {
        db.collection("Categories").document(categoryId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String categoryName = documentSnapshot.getString("name");
                        txtCategory.setText(categoryName);  // Cập nhật tên Category
                    }
                })
                .addOnFailureListener(e -> {
                    txtCategory.setText("Không xác định");
                });
    }

    private void fetchPublisherName(String publisherId) {
        db.collection("Publishers").document(publisherId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String publisherName = documentSnapshot.getString("name");
                        txtPublisher.setText(publisherName);  // Cập nhật tên Publisher
                    }
                })
                .addOnFailureListener(e -> {
                    txtPublisher.setText("Không xác định");
                });
    }

    private void fetchQuantity(String bookId, int totalQuantity) {
        db.collection("BorrowedBooks")
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("status", "1")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int borrowedCount = queryDocumentSnapshots.size();  // Số sách đang mượn

                    // Tính số sách còn lại
                    int availableCount = totalQuantity - borrowedCount;

                    // Hiển thị số sách đang mượn và số sách còn lại
                    txtBorrowedBooks.setText(borrowedCount + "");
                    txtAvailableBooks.setText(availableCount + "");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookDetailActivity.this, "Lỗi khi lấy dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteBook() {
        db.collection("Books").document(pId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BookDetailActivity.this, "Đã xóa sách thành công", Toast.LENGTH_SHORT).show();
                    BookFragment.getInstance().docDulieu();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookDetailActivity.this, "Lỗi khi xóa sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
