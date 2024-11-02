package com.example.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.model.BorrowedBook;

import java.util.List;

public class BorrowBookAdapter extends RecyclerView.Adapter<BorrowBookAdapter.ViewHolder> {

    private List<BorrowedBook> borrowedBooksList;

    // Constructor
    public BorrowBookAdapter(List<BorrowedBook> borrowedBooksList) {
        this.borrowedBooksList = borrowedBooksList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_borrow_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BorrowedBook borrowedBook = borrowedBooksList.get(position);
        holder.textViewBorrowCode.setText(borrowedBook.getId());
        holder.textViewStudentCode.setText(borrowedBook.getStudentId());
        holder.textViewBookId.setText(borrowedBook.getBookId());
        holder.textViewStudentName.setText(borrowedBook.getStudentName());
    }

    @Override
    public int getItemCount() {
        return borrowedBooksList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBorrowCode, textViewStudentCode, textViewBookId, textViewStudentName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBorrowCode = itemView.findViewById(R.id.textViewBorrowCode);
            textViewStudentCode = itemView.findViewById(R.id.textViewStudentCode);
            textViewStudentName = itemView.findViewById(R.id.textViewStudentName);
            textViewBookId = itemView.findViewById(R.id.textViewBookId);
        }
    }
}

