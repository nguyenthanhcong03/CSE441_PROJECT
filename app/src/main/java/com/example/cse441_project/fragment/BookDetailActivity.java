package com.example.cse441_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.R;

public class BookDetailActivity extends AppCompatActivity {
    TextView txtBookName, txtBookAuthor, txtBookCategory, txtBookQuantity, txtBookPublishYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        txtBookName = findViewById(R.id.txtBookName);
        txtBookAuthor = findViewById(R.id.txtBookAuthor);
        txtBookCategory = findViewById(R.id.txtBookCategory);
        txtBookQuantity = findViewById(R.id.txtBookQuantity);
        txtBookPublishYear = findViewById(R.id.txtBookPublishYear);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        String bookName = intent.getStringExtra("bookName");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        String bookCategory = intent.getStringExtra("bookCategory");
        int bookQuantity = intent.getIntExtra("bookQuantity", 0);
        int bookPublishYear = intent.getIntExtra("bookPublishYear", 0);

        // Hiển thị thông tin sách
        txtBookName.setText("Tên sách: " + bookName);
        txtBookAuthor.setText("Tác giả: " + bookAuthor);
        txtBookCategory.setText("Danh mục: " + bookCategory);
        txtBookQuantity.setText("Số lượng: " + bookQuantity);
        txtBookPublishYear.setText("Năm xuất bản: " + bookPublishYear);
    }
}
