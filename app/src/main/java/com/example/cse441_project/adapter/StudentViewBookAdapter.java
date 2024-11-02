package com.example.cse441_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.cse441_project.R;
import com.example.cse441_project.StudentViewBookDetail;
import com.example.cse441_project.model.Book;

import java.io.Serializable;
import java.util.List;

public class StudentViewBookAdapter extends RecyclerView.Adapter<StudentViewBookAdapter.ViewHolder> {
    private Context context;
    private List<Book> itemList;

    public StudentViewBookAdapter(Context context, List<Book> listBook) {
        this.context = context;
        this.itemList = listBook;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_view_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book currentItem = itemList.get(position);
        holder.textViewBookName.setText(currentItem.getName());
        holder.textViewBookAuthor.setText(currentItem.getAuthor());

        String imageUrl = currentItem.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imgLogo);
        } else {
            // Load ảnh placeholder nếu imageUrl null hoặc trống
            Glide.with(context)
                    .load(R.drawable.img_default) // Placeholder image resource
                    .into(holder.imgLogo);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StudentViewBookDetail.class);
            intent.putExtra("book", currentItem); // Truyền đối tượng Book qua Intent
            context.startActivity(intent);


        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookName, textViewBookAuthor;
        ImageView imgLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewBookName = itemView.findViewById(R.id.textViewBookName);
            textViewBookAuthor = itemView.findViewById(R.id.textViewAuthorName);
            imgLogo = itemView.findViewById(R.id.imageViewBooks);
        }
    }

    public void updateBookList(List<Book> filteredBooks) {
        this.itemList.clear();
        this.itemList.addAll(filteredBooks);
        notifyDataSetChanged();
    }

}
