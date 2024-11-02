package com.example.cse441_project.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.adapter.BorrowBookAdapter;
import com.example.cse441_project.model.Book;
import com.example.cse441_project.model.BorrowedBook;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BorrowBookFragment extends Fragment {
    private RecyclerView recyclerViewBorrowBook;
    private BorrowBookAdapter adapter;
    private List<BorrowedBook> borrowedBooksList;
    private SearchView searchView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_borrow_book, container, false);
        recyclerViewBorrowBook = view.findViewById(R.id.recyclerViewBorrowBook);
        recyclerViewBorrowBook.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView = view.findViewById(R.id.searchView);
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

        //
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filterBooks(newText);
                return true;
            }
        });
        return view;
    }

    private void loadBorrowBooks() {
        db.collection("BorrowedBooks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        borrowedBooksList.clear(); // Clear the old list
                        

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            BorrowedBook borrowedBook = document.toObject(BorrowedBook.class);

                            // Check if the borrowedBook name is already in the set
                            if (borrowedBook != null ) {
                                borrowedBooksList.add(borrowedBook); // Add borrowedBook to the list
                                
                            }
                        }
                        // Notify the adapter of the data change
                        adapter.notifyDataSetChanged();
                    }
                });
    }


    private void filterBooks(String query) {
        // If query is empty, show all borrowedBooks
        if (query.isEmpty()) {
            loadBorrowBooks();
        } else {

            List<BorrowedBook> filteredList = new ArrayList<>();

            for (BorrowedBook borrowedBook : borrowedBooksList) {
                if (borrowedBook.getId().contains(query)) {
                    filteredList.add(borrowedBook);
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "Không có kết quả tìm kiếm", Toast.LENGTH_SHORT).show();
            }
            // Update adapter with the filtered list
            adapter.updateDocument(filteredList);
        }
    }
}
