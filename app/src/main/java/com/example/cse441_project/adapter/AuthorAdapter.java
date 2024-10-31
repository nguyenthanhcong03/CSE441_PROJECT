package com.example.cse441_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.model.Author;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {
    private List<Author> authorList;

    public AuthorAdapter(List<Author> authorList) {
        this.authorList = authorList;
    }

    @NonNull
    @Override
    public AuthorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_author, parent, false);
        return new AuthorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthorViewHolder holder, int position) {
        Author author = authorList.get(position);
        holder.nameTextView.setText(author.getName());
        holder.genderTextView.setText("Giới tính: " + author.getGender());

        holder.editButton.setOnClickListener(v -> {
            // Handle edit action
        });

        holder.deleteButton.setOnClickListener(v -> {
            // Handle delete action
        });
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    public static class AuthorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, genderTextView;
        ImageView avatarImageView;
        ImageButton editButton, deleteButton;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.img_ql_tacgia_ten);
            genderTextView = itemView.findViewById(R.id.img_ql_tacgia_gioitinh);
            avatarImageView = itemView.findViewById(R.id.img_ql_tacgia_avt);
            editButton = itemView.findViewById(R.id.button_ql_tacgia_sua);
            deleteButton = itemView.findViewById(R.id.button_ql_tacgia_xoa);
        }
    }
}
