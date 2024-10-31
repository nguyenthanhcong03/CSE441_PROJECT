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
import com.example.cse441_project.adapter.PublisherAdapter;
import com.example.cse441_project.model.Publisher;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PublisherFragment extends Fragment {
    private RecyclerView recyclerView;
    private PublisherAdapter adapter;
    private List<Publisher> publisherList;
    private List<Publisher> fullPublisherList;
    private FirebaseFirestore db;
    private Button buttonAddPublisher;
    private EditText searchBar;
    private ActivityResultLauncher<Intent> editPublisherLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_publisher, container, false);

        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerViewPublisher);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        publisherList = new ArrayList<>();
        fullPublisherList = new ArrayList<>();

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        editPublisherLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        loadPublishers();
                    }
                }
        );

        adapter = new PublisherAdapter(publisherList, editPublisherLauncher);
        recyclerView.setAdapter(adapter);

        buttonAddPublisher = view.findViewById(R.id.buttonAddPublisher);
        buttonAddPublisher.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddPublisherActivity.class);
            editPublisherLauncher.launch(intent);
        });

        loadPublishers();
        return view;
    }

    private void filter(String text) {
        List<Publisher> filteredList = new ArrayList<>();
        for (Publisher publisher : fullPublisherList) {
            if (publisher.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(publisher);
            }
        }
        adapter.filterList(filteredList);
    }

    public ActivityResultLauncher<Intent> getEditPublisherLauncher() {
        return editPublisherLauncher;
    }

    private void loadPublishers() {
        try {
            db.collection("Publishers")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        publisherList.clear();
                        fullPublisherList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                Publisher publisher = document.toObject(Publisher.class);
                                publisher.setId(document.getId());
                                publisherList.add(publisher);
                                fullPublisherList.add(publisher);
                                Log.d("PublisherFragment", "Loaded publisher: " + publisher.getName() + " with ID: " + publisher.getId());
                            } catch (Exception e) {
                                Log.e("PublisherFragment", "Error converting document: ", e);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("PublisherFragment", "Error loading publishers: ", e);
                        if (getContext() != null) {
                            Toast.makeText(getContext(),
                                    "Không thể tải danh sách nhà xuất bản. Vui lòng thử lại sau.",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            Log.e("PublisherFragment", "Error in loadPublishers: ", e);
        }
    }
}
