package com.example.cse441_project.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.example.cse441_project.fragment.EditAuthorActivity;
import com.example.cse441_project.model.Author;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.AuthorViewHolder> {
    private List<Author> authorList;
    private ActivityResultLauncher<Intent> authorActivityLauncher;

    public AuthorAdapter(List<Author> authorList, ActivityResultLauncher<Intent> authorActivityLauncher) {
        this.authorList = authorList;
        this.authorActivityLauncher = authorActivityLauncher;
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
        holder.brithdayTextView.setText("Ngày sinh: " + author.getBirthday());
        holder.nationalityTextView.setText("Quốc tịch: " + author.getNationality());

        Glide.with(holder.avatarImageView.getContext())
                .load(author.getAvatarUrl())
                .into(holder.avatarImageView);

        holder.editButton.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EditAuthorActivity.class);
            intent.putExtra("authorId", author.getId());
            intent.putExtra("authorName", author.getName());
            intent.putExtra("authorGender", author.getGender());
            intent.putExtra("authorBirthday", author.getBirthday());
            intent.putExtra("authorNationality", author.getNationality());
            intent.putExtra("authorAvatarUrl", author.getAvatarUrl());

            authorActivityLauncher.launch(intent);
        });

        holder.deleteButton.setOnClickListener(v -> showConfirmationDialog(v, author, position));
    }

    @Override
    public int getItemCount() {
        return authorList.size();
    }

    private void deleteAuthor(View view, Author author, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Authors").document(author.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showNotificationDialog(view, "Xóa tác giả thành công");
                    authorList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    showNotificationDialog(view, "Xóa tác giả thất bại");
                    Toast.makeText(view.getContext(), "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showConfirmationDialog(View view, Author author, int position) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.quanly_dialog_confirmation);
        dialog.setCancelable(true);

        TextView confirmTitle = dialog.findViewById(R.id.confirmTitle);
        TextView confirmMessage = dialog.findViewById(R.id.confirmMessage);
        Button btnConfirmYes = dialog.findViewById(R.id.btnConfirmYes);
        Button btnConfirmNo = dialog.findViewById(R.id.btnConfirmNo);

        confirmTitle.setText("Xác nhận xóa");
        confirmMessage.setText("Bạn có chắc chắn muốn xóa tác giả " + author.getName() + " không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            deleteAuthor(view, author, position);
        });

        btnConfirmNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showNotificationDialog(View view, String message) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.quanly_dialog_notification);
        dialog.setCancelable(false);

        TextView contentDialog = dialog.findViewById(R.id.contentDialog);
        Button btnBack = dialog.findViewById(R.id.btnBack);

        contentDialog.setText(message);

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static class AuthorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, genderTextView, brithdayTextView, nationalityTextView;
        ImageView avatarImageView;
        ImageButton editButton, deleteButton;

        public AuthorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.img_ql_tacgia_ten);
            genderTextView = itemView.findViewById(R.id.img_ql_tacgia_gioitinh);
            brithdayTextView = itemView.findViewById(R.id.img_ql_tacgia_ngaysinh);
            nationalityTextView = itemView.findViewById(R.id.img_ql_tacgia_quoctich);
            avatarImageView = itemView.findViewById(R.id.img_ql_tacgia_avt);
            editButton = itemView.findViewById(R.id.button_ql_tacgia_sua);
            deleteButton = itemView.findViewById(R.id.button_ql_tacgia_xoa);
        }
    }

    public void updateList(List<Author> newList) {
        authorList = newList;
        notifyDataSetChanged();
    }
}
