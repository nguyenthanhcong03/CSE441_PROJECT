package com.example.cse441_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;

public class BookDetailActivity extends AppCompatActivity {
    TextView txtBookName, txtBookAuthor, txtBookDescription, txtBookCategory, txtBookQuantity, txtBookPublisher, txtBookPublishYear;
    ImageButton btnEditBook, btnDeleteBook;
    ImageView imageViewBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        txtBookName = findViewById(R.id.txtBookName);
        txtBookAuthor = findViewById(R.id.txtBookAuthor);
        txtBookDescription = findViewById(R.id.txtBookDescription);
        txtBookCategory = findViewById(R.id.txtBookCategory);
        txtBookQuantity = findViewById(R.id.txtBookQuantity);
        txtBookPublishYear = findViewById(R.id.txtBookPublishYear);
        txtBookPublisher = findViewById(R.id.txtBookPublisher);
        imageViewBook = findViewById(R.id.imageViewBook);

        btnEditBook = findViewById(R.id.btnEditBook);
        btnDeleteBook = findViewById(R.id.btnDeleteBook);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String bookName = intent.getStringExtra("bookName");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        String bookDescription = intent.getStringExtra("bookDescription");
        String bookCategory = intent.getStringExtra("bookCategory");
        int bookQuantity = intent.getIntExtra("bookQuantity", 0);
        String bookPublisher = intent.getStringExtra("bookPublisher");
        int bookPublishYear = intent.getIntExtra("bookPublishYear", 0);
        String image = intent.getStringExtra("bookImage"); // Nhận URI ảnh


        // Hiển thị thông tin sách
        txtBookName.setText("Tên sách: " + bookName);
        txtBookAuthor.setText("Tác giả: " + bookAuthor);
        txtBookDescription.setText("Mô tả: " + bookDescription);
        txtBookCategory.setText("Danh mục: " + bookCategory);
        txtBookQuantity.setText("Số lượng: " + bookQuantity);
        txtBookPublishYear.setText("Năm xuất bản: " + bookPublishYear);
        txtBookPublisher.setText("Nhà xuất bản: " + bookPublisher);
        // Sử dụng Glide để tải ảnh vào ImageView
        Glide.with(this).load(image).into(imageViewBook);


        btnEditBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);


                startActivity(intent);
            }
        });


    }
}
