package com.example.cse441_project.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookFragment extends Fragment {
    private static final String TAG = "MainActivity";
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_book, container, false);

        Button btnAddBook = view.findViewById(R.id.btnAddBook);
//        db = FirebaseFirestore.getInstance();
//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        bookList = new ArrayList<>();
//
//        fetchBooks();

        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

//    private void fetchBooks() {
//        db.collection("books")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Book book = document.toObject(Book.class);
//                                bookList.add(book);
//                            }
//                            bookAdapter = new BookAdapter(bookList);
//                            recyclerView.setAdapter(bookAdapter);
//                        } else {
//                            Log.w(TAG, "Error getting documents.", task.getException());
//                        }
//                    }
//                });
//    }
}
