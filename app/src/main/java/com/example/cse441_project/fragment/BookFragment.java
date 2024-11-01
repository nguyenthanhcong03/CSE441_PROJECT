package com.example.cse441_project.fragment;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import com.example.cse441_project.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
    private static BookFragment instance;

    private ProgressDialog pd;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<Book> list;
    private BookAdapter bookAdapter;
    private Button btnAddBook;

    private EditText edtSearch;
    private TextView txtNoResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_book, container, false);
        instance = this;

        pd = new ProgressDialog(getActivity());
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnAddBook = view.findViewById(R.id.btnAddBook);
        edtSearch = view.findViewById(R.id.edtSearch);
        txtNoResults = view.findViewById(R.id.txtNoResults);

        list = new ArrayList<>();
        // Khởi tạo Adapter và gán cho RecyclerView
        bookAdapter = new BookAdapter(getActivity(), list);
        recyclerView.setAdapter(bookAdapter);

        docDulieu();

        // Button mở giao diện thêm sách
        btnAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddBookActivity.class);
                startActivity(intent);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String query = charSequence.toString().trim();
                if (!query.isEmpty()) {
                    search(query);
                } else {
                    docDulieu(); // Xóa dữ liệu khi từ khóa rỗng
                    txtNoResults.setVisibility(View.GONE); // Ẩn
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    public void docDulieu() {
        db.collection("Books")
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

    public void deleteData(int index) {
        pd.setTitle("Đang xóa");
        pd.show();

        db.collection("Books").document(list.get(index).getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                        docDulieu();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void search(String query) {
        list.clear();
        txtNoResults.setVisibility(View.GONE); // Ẩn TextView ban đầu
        recyclerView.setVisibility(View.VISIBLE);
        // Truy vấn Firestore để tìm sách theo tên chứa từ khóa tìm kiếm
        db.collection("Books")
                .whereGreaterThanOrEqualTo("name", query)  // Điều kiện tìm kiếm tên bắt đầu từ từ khóa
                .whereLessThanOrEqualTo("name", query + "\uf8ff")  // Giới hạn phạm vi tìm kiếm
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear(); // Xóa dữ liệu cũ
                        QuerySnapshot documents = task.getResult();
                        for (QueryDocumentSnapshot document : documents) {
                            Book book = document.toObject(Book.class);
                            list.add(book); // Thêm sách vào danh sách
                        }
                        // Kiểm tra nếu bookList rỗng sau khi truy vấn
                        if (list.isEmpty()) {
                            txtNoResults.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            txtNoResults.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        bookAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    } else {
                        // Xử lý lỗi
                    }
                });
    }

    public static BookFragment getInstance() {
        return instance;
    }
}