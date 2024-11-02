package com.example.cse441_project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.adapter.AuthorAdapter;
import com.example.cse441_project.model.Author;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AuthorFragment extends Fragment {
    private RecyclerView recyclerView;
    private AuthorAdapter authorAdapter;
    private List<Author> authorList;
    private FirebaseFirestore db;
    private Button buttonAddAuthor;
    private EditText searchBarAuthor;
    private ActivityResultLauncher<Intent> authorActivityLauncher;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_author, container, false);

        // Khai báo các view, recycle view và khởi tạo firebase
        recyclerView = view.findViewById(R.id.recycleViewAuthor);
        buttonAddAuthor = view.findViewById(R.id.buttonAddAuthor);
        searchBarAuthor = view.findViewById(R.id.searchBarAuthor);

        db = FirebaseFirestore.getInstance();

        // Đăng ký load lại dữ liệu authors
        authorActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fetchAuthors();
                    }
                }
        );

        // Đổ dữ liệu vào recycle view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        authorList = new ArrayList<>();
        authorAdapter = new AuthorAdapter(authorList, authorActivityLauncher);
        recyclerView.setAdapter(authorAdapter);
        fetchAuthors();

        // Setup các sự kiện
        buttonAddAuthor.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddAuthorActivity.class);
            authorActivityLauncher.launch(intent);
        });
        searchBarAuthor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterAuthors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    // Hàm load lại dữ liệu authors
    private void fetchAuthors() {
        authorList.clear();

        db.collection("Authors")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Author author = document.toObject(Author.class);
                        authorList.add(author);
                    }
                    authorAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FetchAuthorsError", "Lỗi tải tác giả: ", e);
                });
    }

    // Hàm lọc authors
    private void filterAuthors(String text) {
        List<Author> filteredList = new ArrayList<>();
        for (Author author : authorList) {
            if (author.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(author);
            }
        }
        authorAdapter.updateList(filteredList);
    }

}
