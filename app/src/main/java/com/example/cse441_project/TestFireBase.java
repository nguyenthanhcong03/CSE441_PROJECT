package com.example.cse441_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cse441_project.model.Category;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;

public class TestFireBase extends AppCompatActivity {
    EditText edtId, edtName;
    TextView txtId, txtName;
    Button btnAdd;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_fire_base);
        edtId = findViewById(R.id.editTextCategoryId);
        edtName = findViewById(R.id.editTextName);
        btnAdd = findViewById(R.id.buttonAdd);
        txtId = findViewById(R.id.textViewId);
        txtName = findViewById(R.id.textViewName);
        btnAdd.setOnClickListener(v -> {
            String id = edtId.getText().toString();
            String name = edtName.getText().toString();
            HashMap<String, String> book = new HashMap<>();

            book.put("categoryName", name);
            firestore.collection("Categories").add(book)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {

            });



        });
        firestore.collection("Categories").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for(DocumentSnapshot documentSnapshot : task.getResult()){

                            String id1 = documentSnapshot.getId();
                            String name1 = documentSnapshot.getString("name");
                            txtId.setText(id1);
                            txtName.setText(name1);
                        }
                    }
                }
        );
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}