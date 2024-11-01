package com.example.cse441_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.fragment.BookDetailActivity;
import com.example.cse441_project.fragment.BookDetailActivity;
import com.example.cse441_project.fragment.BookFragment;
import com.example.cse441_project.fragment.EditBookActivity;
import com.example.cse441_project.model.Book;
import com.bumptech.glide.Glide;
import com.example.cse441_project.model.Book;
import com.example.cse441_project.model.Category;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;


public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    FragmentActivity listActivity;
    List<Book> list;
    Context context;

    public BookAdapter(FragmentActivity listActivity, List<Book> list) {
        this.listActivity = listActivity;
        this.list = list;
    }

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        BookAdapter.ViewHolder viewHolder = new BookAdapter.ViewHolder(itemView);
        //Handle item click
        viewHolder.setOnClickListener(new BookAdapter.ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // this will be called when user click item

                // show data in toast on clicking
                String id = list.get(position).getId();
                String name = list.get(position).getName();
                String author = list.get(position).getAuthor();
                String category = list.get(position).getCategoryId();
                String description = list.get(position).getDescription();
                String publisher = list.get(position).getPublisherId();
//                String publishYear = list.get(position).getPublishYear();
//                String quantity = list.get(position).getQuantity();
                String publishYear = String.valueOf(list.get(position).getPublishYear());
                String quantity = String.valueOf(list.get(position).getQuantity());
                String image = list.get(position).getImage();

                Intent intent = new Intent(listActivity, BookDetailActivity.class);
                intent.putExtra("pId", id);
                intent.putExtra("pName", name);
                intent.putExtra("pAuthor", author);
                intent.putExtra("pCategory", category);
                intent.putExtra("pDescription", description);
                intent.putExtra("pPublisher", publisher);
                intent.putExtra("pPublishYear", publishYear);
                intent.putExtra("pQuantity", quantity);
                intent.putExtra("pImage", image);

                listActivity.startActivity(intent);

                //Toast.makeText(listActivity, bookName + "\n" + bookBook + "\n" + bookQuantity, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(listActivity);
                String[] options = {"Sửa", "Xóa"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            String id = list.get(position).getId();
                            String name = list.get(position).getName();
                            String author = list.get(position).getAuthor();
                            String category = list.get(position).getCategoryId();
                            String description = list.get(position).getDescription();
                            String publisher = list.get(position).getPublisherId();
//                String publishYear = list.get(position).getPublishYear();
//                String quantity = list.get(position).getQuantity();
                            String publishYear = String.valueOf(list.get(position).getPublishYear());
                            String quantity = String.valueOf(list.get(position).getQuantity());
                            String image = list.get(position).getImage();

                            Intent intent = new Intent(listActivity, EditBookActivity.class);
                            intent.putExtra("pId", id);
                            intent.putExtra("pName", name);
                            intent.putExtra("pAuthor", author);
                            intent.putExtra("pCategory", category);
                            intent.putExtra("pDescription", description);
                            intent.putExtra("pPublisher", publisher);
                            intent.putExtra("pPublishYear", publishYear);
                            intent.putExtra("pQuantity", quantity);
                            intent.putExtra("pImage", image);

                            listActivity.startActivity(intent);
                        }
                        if (i == 1) {
                            BookFragment.getInstance().deleteData(position);
                        }
                    }
                }).create().show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = list.get(position);
        String bookName = book.getName();
        String bookAuthor = book.getAuthor();
        String bookCategory = book.getCategoryId();
        String bookDescription = book.getDescription();
        String bookPublisher = book.getPublisherId();
        String bookPublishYear = String.valueOf(book.getPublishYear());
        String bookQuantity = String.valueOf(book.getQuantity());
        String bookImage = book.getImage();

        holder.txtName.setText(bookName);
        holder.txtQuantity.setText("Số lượng: " + bookQuantity);
        // Lấy categoryName từ categoryId
        FirebaseFirestore.getInstance().collection("Categories")
                .document(book.getCategoryId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String categoryName = documentSnapshot.getString("name");
                        holder.txtCategory.setText("Danh mục: " + categoryName); // Đặt categoryName vào txtCategory
                    }
                })
                .addOnFailureListener(e -> {
                    holder.txtCategory.setText("Danh mục không xác định");
                });

        // Load hình ảnh từ URL
//        Glide.with(context)
//                .load(book.getImage())  // book.getImage() là URL của ảnh
//                .placeholder(R.drawable.ic_launcher_background)  // Hình ảnh hiển thị khi đang tải
//                .error(R.drawable.ic_delete)  // Hình ảnh hiển thị nếu tải thất bại
//                .into(holder.imgBook);
        Glide.with(holder.itemView.getContext()).load(bookImage).into(holder.imgBook);

//        // Sự kiện khi nhấn vào CardView
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, BookDetailActivity.class);
//                // Truyền dữ liệu sách qua Intent
//                intent.putExtra("bookId", book.getId());
//                intent.putExtra("bookName", book.getName());
//                intent.putExtra("bookAuthor", book.getAuthor());
//                intent.putExtra("bookDescription", book.getDescription());
//                intent.putExtra("bookBook", book.getBookId());
//                intent.putExtra("bookQuantity", book.getQuantity());
//                intent.putExtra("bookPublisher", book.getPublisherId());
//                intent.putExtra("bookPublishYear", book.getPublishYear());
//                intent.putExtra("bookImage", book.getImage()); // Chuyển ảnh
//                context.startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtCategory, txtQuantity;
        ImageView imgBook;
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