package com.example.cse441_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.fragment.BookDetailActivity;
import com.example.cse441_project.model.Book;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> list;
    private Context context;

    public BookAdapter(Context context, List<Book> list) {
        this.list = list;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = list.get(position);
        String bookName = book.getName();
        String bookCategory = book.getCategoryId();
        String bookQuantity = book.getQuantity() + "";

        holder.txtName.setText(bookName);
        holder.txtCategory.setText("Danh mục:" + bookCategory);
        holder.txtQuantity.setText("Số lượng:" + bookQuantity);


        // Load hình ảnh từ URL
        Glide.with(context)
                .load(book.getImage())  // book.getImage() là URL của ảnh
                .placeholder(R.drawable.ic_launcher_background)  // Hình ảnh hiển thị khi đang tải
                .error(R.drawable.ic_delete)  // Hình ảnh hiển thị nếu tải thất bại
                .into(holder.imgBook);
        // Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.imgBook);

        // Sự kiện khi nhấn vào CardView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookDetailActivity.class);
                // Truyền dữ liệu sách qua Intent
                intent.putExtra("bookId", book.getId());
                intent.putExtra("bookName", book.getName());
                intent.putExtra("bookAuthor", book.getAuthor());
                intent.putExtra("bookDescription", book.getDescription());
                intent.putExtra("bookCategory", book.getCategoryId());
                intent.putExtra("bookQuantity", book.getQuantity());
                intent.putExtra("bookPublisher", book.getPublisherId());
                intent.putExtra("bookPublishYear", book.getPublishYear());
                intent.putExtra("bookImage", book.getImage()); // Chuyển ảnh
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCategory, txtQuantity;
        ImageView imgBook;
        ImageButton btnEditBook;
        View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            //itemClick
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
            });
            //item long click listener
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    mClickListener.onItemLongClick(view, getAdapterPosition());
                    return true;
                }
            });

            //initialize views
            imgBook = itemView.findViewById(R.id.imgBook);
            txtName = itemView.findViewById(R.id.txtName);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
        }

        private ViewHolder.ClickListener mClickListener;

        // interface for click listener
        public interface ClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }

        public void setOnClickListener(ViewHolder.ClickListener clickListener) {
            mClickListener = clickListener;
        }
    }


}