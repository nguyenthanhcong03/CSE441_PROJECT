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

import com.example.cse441_project.MainActivity;
import com.example.cse441_project.R;
import com.example.cse441_project.adapter.RuleAdapter;
import com.example.cse441_project.model.Rule;
import com.example.cse441_project.model.Rule;

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

public class RuleFragment extends Fragment {
    private static RuleFragment instance;
    static ProgressDialog pd;
    static FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<Rule> list;
    private RuleAdapter ruleAdapter;
    private Button btnAddRule;

    private EditText edtSearch;
    private TextView txtNoResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_rule, container, false);
        instance = this;

        pd = new ProgressDialog(getActivity());
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        btnAddRule = view.findViewById(R.id.btnAddRule);
        edtSearch = view.findViewById(R.id.edtSearch);
        txtNoResults = view.findViewById(R.id.txtNoResults);

        list = new ArrayList<>();
        // Khởi tạo Adapter và gán cho RecyclerView
        ruleAdapter = new RuleAdapter(getActivity(), list);
        recyclerView.setAdapter(ruleAdapter);

        docDulieu();

        // Button mở giao diện thêm quy định
        btnAddRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddRuleActivity.class);
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
        db.collection("Rules")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear(); // Xóa dữ liệu cũ
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Rule ruleData = document.toObject(Rule.class);
                                list.add(ruleData);
                            }
                            ruleAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu mới
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void deleteData(int index) {
        pd.setTitle("Đang xóa");
        pd.show();

        db.collection("Rules").document(list.get(index).getId())
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

    public static RuleFragment getInstance() {
        return instance;
    }

    private void search(String query) {
        list.clear();
        txtNoResults.setVisibility(View.GONE); // Ẩn TextView ban đầu
        recyclerView.setVisibility(View.VISIBLE);
        // Truy vấn Firestore để tìm sách theo tên chứa từ khóa tìm kiếm
        db.collection("Rules")
                .whereGreaterThanOrEqualTo("name", query)  // Điều kiện tìm kiếm tên bắt đầu từ từ khóa
                .whereLessThanOrEqualTo("name", query + "\uf8ff")  // Giới hạn phạm vi tìm kiếm
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear(); // Xóa dữ liệu cũ
//                        QuerySnapshot documents = task.getResult();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Rule rule = document.toObject(Rule.class);
                            list.add(rule); // Thêm sách vào danh sách
                        }
                        // Kiểm tra nếu bookList rỗng sau khi truy vấn
                        if (list.isEmpty()) {
                            txtNoResults.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            txtNoResults.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        ruleAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    } else {
                        // Xử lý lỗi
                    }
                });
    }
}
