package com.example.cse441_project.fragment;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.adapter.BookAdapter;
import com.example.cse441_project.model.Book;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookFragment extends Fragment {
    TextView txtName, txtAuthor, txtCategory, txtQuantity, txtPublishYear;
//    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//    private RecyclerView recyclerView;
//    private BookAdapter bookAdapter;;
//    List<Book> listBook;

    FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private List<Book> list;
    private BookAdapter bookAdapter;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_book, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Button btnAddBook = view.findViewById(R.id.btnAddBook);


        list = new ArrayList<>();
        // Khởi tạo Adapter và gán cho RecyclerView
        bookAdapter = new BookAdapter(getActivity(), list);
        recyclerView.setAdapter(bookAdapter);

//        ghiDulieu();

        docDulieu();


        // Button mở giao diện thêm sách
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

//    private void ghiDulieu() {
//        CollectionReference cities = firestore.collection("cities");
//
//        Map<String, Object> data1 = new HashMap<>();
//        data1.put("name", "San Francisco");
//        data1.put("country", "USA");
//        data1.put("population", 860000);
//        cities.document("SF").set(data1);
//
//        Map<String, Object> data2 = new HashMap<>();
//        data2.put("name", "Los Angeles");
//        data2.put("country", "USA");
//        data2.put("population", 3900000);
//        cities.document("LA").set(data2);
//        Map<String, Object> data3 = new HashMap<>();
//        data3.put("name", "Washington D.C.");
//        data3.put("country", "USA");
//        data3.put("population", 680000);
//        cities.document("DC").set(data3);
//
//        Map<String, Object> data4 = new HashMap<>();
//        data4.put("name", "Tokyo");
//        data4.put("country", "Japan");
//        data4.put("population", 9000000);
//        cities.document("TOK").set(data4);
//
//        Map<String, Object> data5 = new HashMap<>();
//        data5.put("name", "Beijing");
//        data5.put("country", "China");
//        data5.put("population", 21500000);
//        cities.document("BJ").set(data5);
//
//
//    }

    private void docDulieu () {
        firestore.collection("Books")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Book bookData = document.toObject(Book.class);
                                list.add(bookData);
                            }
                            bookAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
