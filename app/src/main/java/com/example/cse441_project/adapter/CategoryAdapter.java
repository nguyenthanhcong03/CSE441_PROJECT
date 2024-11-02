package com.example.cse441_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.fragment.CategoryFragment;
import com.example.cse441_project.fragment.EditCategoryActivity;
import com.example.cse441_project.model.Category;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    FragmentActivity listActivity;
    List<Category> list;
    Context context;

    public CategoryAdapter(FragmentActivity listActivity, List<Category> list) {
        this.listActivity = listActivity;
        this.list = list;
    }

    public interface OnItemClickListener {
        void onItemClick(Category category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        //Handle item click
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // this will be called when user click item

                // show data in toast on clicking
//                String id = list.get(position).getId();
//                String name = list.get(position).getName();
//
//                Intent intent = new Intent(listActivity, CategoryDetailActivity.class);
//                intent.putExtra("pId", id);
//                intent.putExtra("pName", name);
//
//                listActivity.startActivity(intent);

                //Toast.makeText(listActivity, categoryName + "\n" + categoryCategory + "\n" + categoryQuantity, Toast.LENGTH_SHORT).show();
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

                            Intent intent = new Intent(listActivity, EditCategoryActivity.class);
                            intent.putExtra("pId", id);
                            intent.putExtra("pName", name);

                            listActivity.startActivity(intent);
                        }
                        if (i == 1) {
                            CategoryFragment.getInstance().deleteData(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category category = list.get(position);
        String categoryName = category.getName();

        holder.txtName.setText(categoryName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
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
            txtName = itemView.findViewById(R.id.txtName);

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