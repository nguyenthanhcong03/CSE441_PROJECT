package com.example.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.model.Book;

import java.util.ArrayList;
import java.util.List;
//import com.bumptech.glide.Glide;

public class BookAdapter  {
//    private List<Book> bookList;
//    public static class BookViewHolder extends RecyclerView.ViewHolder {
//        public ImageView bookImage;
//        public TextView bookName, bookAuthor, bookQuantity, bookPublishYear, bookCategory;
//
//        public BookViewHolder(View view) {
//            super(view);
//            bookImage = view.findViewById(R.id.imgBook);
//            bookName = view.findViewById(R.id.txtName);
//            bookAuthor = view.findViewById(R.id.txtAuthor);
//            bookQuantity = view.findViewById(R.id.txtQuantity);
//            bookPublishYear = view.findViewById(R.id.txtPubishYear);
//            bookCategory = view.findViewById(R.id.txtCategory);
//        }
//    }
//
//    public BookAdapter(List<Book> books) {
//        this.bookList = books;
//    }
//
//    @NonNull
//    @Override
//    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_book, parent, false);
//        return new BookViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
//        Book book = bookList.get(position);
//        holder.bookName.setText(book.getName());
//        holder.bookAuthor.setText(book.getAuthor());
//
//        // Sử dụng Glide để tải hình ảnh
//        Glide.with(holder.bookImage.getContext())
//                .load(book.getImage())
//                .into(holder.bookImage);
//    }
//
//    @Override
//    public int getItemCount() {
//        return bookList.size();
//    }
}
