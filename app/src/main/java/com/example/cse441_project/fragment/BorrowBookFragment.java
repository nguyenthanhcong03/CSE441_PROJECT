package com.example.cse441_project.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.adapter.BorrowBookAdapter;
import com.example.cse441_project.model.BorrowedBook;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BorrowBookFragment extends Fragment {
    private RecyclerView recyclerViewBorrowBook;
    private BorrowBookAdapter adapter;
    private List<BorrowedBook> borrowedBooksList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_borrow_book, container, false);
        recyclerViewBorrowBook = view.findViewById(R.id.recyclerViewBorrowBook);
        recyclerViewBorrowBook.setLayoutManager(new LinearLayoutManager(getContext()));
        borrowedBooksList = new ArrayList<>();
        // Lấy dữ liệu từ bảng BorrowedBooks
        db.collection("BorrowedBooks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BorrowedBook borrowedBook = document.toObject(BorrowedBook.class);
                            if (borrowedBook != null){
                                // Lấy studentId để lấy tên sinh viên từ bảng Users
                                String studentId = borrowedBook.getStudentId();
                                db.collection("Users").document(studentId).get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                String studentName = userDoc.getString("fullname");
                                                borrowedBook.setStudentName(studentName);
                                            }
                                            borrowedBooksList.add(borrowedBook);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.d("Firestore", "Error getting student name: ", e));
                            }
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
        adapter = new BorrowBookAdapter(borrowedBooksList);
        recyclerViewBorrowBook.setAdapter(adapter);
        return view;
    }
}
