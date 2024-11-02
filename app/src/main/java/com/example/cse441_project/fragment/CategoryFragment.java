package com.example.cse441_project.fragment;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.adapter.CategoryAdapter;
import com.example.cse441_project.model.Category;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    private static CategoryFragment instance;
    private ProgressDialog pd;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<Category> list;
    private CategoryAdapter categoryAdapter;
    private Button btnAddCategory;

    private EditText edtSearch;
    private TextView txtNoResults;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_category, container, false);
        instance = this;

        pd = new ProgressDialog(getActivity());
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        edtSearch = view.findViewById(R.id.edtSearch);
        txtNoResults = view.findViewById(R.id.txtNoResults);

        list = new ArrayList<>();
        // Khởi tạo Adapter và gán cho RecyclerView
        categoryAdapter = new CategoryAdapter(getActivity(), list);
        recyclerView.setAdapter(categoryAdapter);

        docDulieu();

        // Button mở giao diện thêm danh mục
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddCategoryActivity.class);
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
        db.collection("Categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Category categoryData = document.toObject(Category.class);
                                list.add(categoryData);
                            }
                            categoryAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void deleteData(int index) {
        pd.setTitle("Đang xóa");
        pd.show();

        db.collection("Categories").document(list.get(index).getId())
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
        db.collection("Categories")
                .whereGreaterThanOrEqualTo("name", query)  // Điều kiện tìm kiếm tên bắt đầu từ từ khóa
                .whereLessThanOrEqualTo("name", query + "\uf8ff")  // Giới hạn phạm vi tìm kiếm
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear(); // Xóa dữ liệu cũ
                        QuerySnapshot documents = task.getResult();
                        for (QueryDocumentSnapshot document : documents) {
                            Category category = document.toObject(Category.class);
                            list.add(category); // Thêm sách vào danh sách
                        }
                        // Kiểm tra nếu bookList rỗng sau khi truy vấn
                        if (list.isEmpty()) {
                            txtNoResults.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            txtNoResults.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        categoryAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    } else {
                        // Xử lý lỗi
                    }
                });
    }

    public static CategoryFragment getInstance() {
        return instance;
    }
}
