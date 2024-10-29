package com.example.cse441_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    public BookAdapter(Context context,List<Book> list) {
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
                intent.putExtra("bookCategory", book.getCategoryId());
                intent.putExtra("bookQuantity", book.getQuantity());
                intent.putExtra("bookPublishYear", book.getPublishYear());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtName,txtCategory,txtQuantity;
        ImageView imgBook;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBook = itemView.findViewById(R.id.imgBook);
            txtName = itemView.findViewById(R.id.txtName);
            txtCategory=itemView.findViewById(R.id.txtCategory);
            txtQuantity =itemView.findViewById(R.id.txtQuantity);
        }
    }
}



