package com.example.cse441_project.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_author, container, false);

        recyclerView = view.findViewById(R.id.recycleViewAuthor);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        authorList = new ArrayList<>();
        authorAdapter = new AuthorAdapter(authorList);
        recyclerView.setAdapter(authorAdapter);

        db = FirebaseFirestore.getInstance();
        fetchAuthors();

        return view;
    }

    private void fetchAuthors() {
        db.collection("Authors")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Author author = document.toObject(Author.class);
                            authorList.add(author);
                        }
                        authorAdapter.notifyDataSetChanged();
                    } else {

                    }
                });
    }
}
