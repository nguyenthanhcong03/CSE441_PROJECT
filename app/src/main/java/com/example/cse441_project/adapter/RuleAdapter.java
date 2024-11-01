package com.example.cse441_project.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.fragment.RuleFragment;
import com.example.cse441_project.fragment.EditRuleActivity;
import com.example.cse441_project.model.Rule;


import java.util.List;

public class RuleAdapter extends RecyclerView.Adapter<RuleAdapter.ViewHolder> {
    FragmentActivity listActivity;
    List<Rule> list;
    Context context;

    public RuleAdapter(FragmentActivity listActivity, List<Rule> list) {
        this.listActivity = listActivity;
        this.list = list;
    }

    public interface OnItemClickListener {
        void onItemClick(Rule rule);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rule_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        //Handle item click
        viewHolder.setOnClickListener(new ViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // this will be called when user click item

                // show data in toast on clicking
                String ruleName = list.get(position).getName();

                //Toast.makeText(listActivity, ruleName + "\n" + ruleRule + "\n" + ruleQuantity, Toast.LENGTH_SHORT).show();
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
                            String content = list.get(position).getContent();

                            Intent intent = new Intent(listActivity, EditRuleActivity.class);
                            intent.putExtra("pId", id);
                            intent.putExtra("pName", name);
                            intent.putExtra("pContent", content);

                            listActivity.startActivity(intent);
                        }
                        if (i == 1) {
                            RuleFragment.getInstance().deleteData(position);
                        }
                    }
                }).create().show();
            }
        });

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull RuleAdapter.ViewHolder holder, int position) {
        Rule rule = list.get(position);
        String ruleName = rule.getName();
        String ruleContent = rule.getContent();

        holder.txtName.setText(ruleName);
        holder.txtContent.setText(ruleContent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtContent;
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

            //Ánh xạ id
            txtName = itemView.findViewById(R.id.txtName);
            txtContent = itemView.findViewById(R.id.txtContent);

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