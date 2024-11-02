package com.example.cse441_project.fragment.student;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cse441_project.R;
import com.example.cse441_project.adapter.CustomSpinnerAdapter;
import com.example.cse441_project.adapter.StudentViewBookAdapter;
import com.example.cse441_project.databinding.FragmentStudentViewBookBinding;
import com.example.cse441_project.databinding.FragmentViewBookBinding;
import com.example.cse441_project.model.Book;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FragmentViewBook extends Fragment {
    private FragmentStudentViewBookBinding binding;
    private SearchView searchView;
    private Spinner spinnerCategory;
    private RecyclerView recyclerView;
    private StudentViewBookAdapter bookAdapter;
    private List<Book> bookList = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private List<String> categoryList;
    private Map<String, String> categoryMap;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentViewBookBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        recyclerView = binding.recyclerViewAllBooks;
        searchView = view.findViewById(R.id.searchView);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);

        /// Thiết lập RecyclerView với GridLayoutManager 2 cột
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        // Khởi tạo adapter cho RecyclerView
        bookAdapter = new StudentViewBookAdapter(getContext(), bookList);
        recyclerView.setAdapter(bookAdapter);

        // Khởi tạo danh sách và map cho Spinner
        categoryList = new ArrayList<>();
        categoryMap = new HashMap<>();
        categoryList.add("Chọn danh mục"); // Giá trị mặc định

        // Lấy dữ liệu từ Firestore
        loadCategories();
        loadBooks();


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

        //
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCategoryName = (String) parent.getItemAtPosition(position);

                String selectedCategoryId = categoryMap.get(selectedCategoryName);

                if (selectedCategoryName == null || selectedCategoryName.equals("Chọn danh mục")) {
                    // Handle case where selectedCategoryName is not found in categoryMap
                    loadBooks();
                    return;
                }

                loadBooksByCategory(selectedCategoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally handle case where nothing is selected
                loadBooks();
            }
        });

        return view;
    }

    // Hàm để load danh mục từ Firestore
    private void loadCategories() {
        firestore.collection("Categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String categoryId = document.getId();
                            String categoryName = document.getString("name");
                            categoryMap.put(categoryName, categoryId);
                            categoryList.add(categoryName);;
                        }

                        // Thiết lập adapter cho Spinner
                        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(requireContext(), R.layout.category_item_selected, categoryList);
                        spinnerCategory.setAdapter(customSpinnerAdapter);
                    }else {
                        Toast.makeText(requireContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //Hàm search theo category
    private void loadBooksByCategory(String categoryId) {
        firestore.collection("Books")
                .whereEqualTo("categoryId", categoryId) // Querying by categoryId
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear(); // Clear old book list
                        Set<String> bookNamesSet = new HashSet<>(); // Track unique book names

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);

                            // Check if the book name is already in the set
                            if (book != null && !bookNamesSet.contains(book.getName())) {
                                bookList.add(book); // Add book to the list
                                bookNamesSet.add(book.getName()); // Add book name to the set
                            }
                        }
                        // Notify the adapter of the data change
                        bookAdapter.notifyDataSetChanged();
                    }
                });
    }


    // Hàm để load sách từ Firestore và cập nhật RecyclerView
    @SuppressLint("NotifyDataSetChanged")
    private void loadBooks() {
        firestore.collection("Books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookList.clear(); // Clear the old list
                        Set<String> bookNamesSet = new HashSet<>(); // Set to track unique book names

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Book book = document.toObject(Book.class);

                            // Check if the book name is already in the set
                            if (book != null && !bookNamesSet.contains(book.getName())) {
                                bookList.add(book); // Add book to the list
                                bookNamesSet.add(book.getName()); // Add book name to the set
                            }
                        }
                        // Notify the adapter of the data change
                        bookAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void filterBooks(String query) {
        // If query is empty, show all books
        if (query.isEmpty()) {
            loadBooks();
        } else {
            String lowerCaseQuery = query.toLowerCase();
            List<Book> filteredList = new ArrayList<>();

            for (Book book : bookList) {
                if (book.getName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(book);
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(requireContext(), "Không có kết quả tìm kiếm", Toast.LENGTH_SHORT).show();
            }
            // Update adapter with the filtered list
            bookAdapter.updateBookList(filteredList);
        }
    }

}
