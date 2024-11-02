package com.example.cse441_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.cse441_project.model.Book;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class StudentViewBookDetail extends AppCompatActivity {
    private TextView textViewBookName, textViewBookAuthor, textViewBookDescription, textViewPublisher;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView imageViewBook;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_view_book_detail);

        textViewBookName = findViewById(R.id.book_title);
        textViewBookAuthor = findViewById(R.id.book_author);
        textViewBookDescription = findViewById(R.id.book_description);
        textViewPublisher = findViewById(R.id.book_publisher);
        imageViewBook = findViewById(R.id.book_cover);

        // Lấy đối tượng Book từ Intent
        Book book = (Book) getIntent().getSerializableExtra("book");
        if (book != null) {
            // Hiển thị thông tin của sách
            textViewBookName.setText(book.getName());
            textViewBookAuthor.setText("Tác giả: "+book.getAuthor());
            textViewBookDescription.setText(book.getDescription()); // Đảm bảo book có phương thức getDescription()
            textViewPublisher.setText("NXB: "+book.getPublisherId());

            //Hiển thị hình ảnh của sách
            String imageUrl = book.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(imageViewBook);
            } else {
                imageViewBook.setImageResource(R.drawable.img_default); // Ảnh placeholder
            }

        }
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }


