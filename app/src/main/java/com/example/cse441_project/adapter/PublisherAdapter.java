package com.example.cse441_project.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse441_project.R;
import com.example.cse441_project.fragment.EditPublisherActivity;
import com.example.cse441_project.model.Publisher;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PublisherAdapter extends RecyclerView.Adapter<PublisherAdapter.PublisherViewHolder> {

    private List<Publisher> publishers;
    private List<Publisher> publishersFull;
    private ActivityResultLauncher<Intent> editLauncher;

    public PublisherAdapter(List<Publisher> publishers, ActivityResultLauncher<Intent> editLauncher) {
        this.publishers = publishers;
        this.publishersFull = new ArrayList<>(publishers);
        this.editLauncher = editLauncher;
    }

    @NonNull
    @Override
    public PublisherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_publisher, parent, false);
        return new PublisherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublisherViewHolder holder, int position) {
        Publisher publisher = publishers.get(position);
        holder.tvId.setText(publisher.getId());
        holder.tvName.setText(publisher.getName());
        holder.tvAddress.setText(publisher.getAddress());
        holder.tvCountry.setText(publisher.getCountry());

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), EditPublisherActivity.class);
            intent.putExtra("PUBLISHER_ID", publisher.getId());
            intent.putExtra("PUBLISHER_NAME", publisher.getName());
            intent.putExtra("PUBLISHER_ADDRESS", publisher.getAddress());
            intent.putExtra("PUBLISHER_COUNTRY", publisher.getCountry());

            editLauncher.launch(intent);
        });

        holder.btnDelete.setOnClickListener(v -> showConfirmationDialog(v, publisher, position));
    }

    @Override
    public int getItemCount() {
        return publishers.size();
    }

    public void filterList(List<Publisher> filteredList) {
        publishers = filteredList;
        notifyDataSetChanged();
    }

    public static class PublisherViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvAddress, tvCountry;
        ImageButton btnEdit, btnDelete;

        public PublisherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvCountry = itemView.findViewById(R.id.tvCountry);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void deletePublisher(View view, Publisher publisher, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Publishers").document(publisher.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showNotificationDialog(view, "Xóa thành công");
                    publishers.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    showNotificationDialog(view, "Xóa thất bại");
                    Toast.makeText(view.getContext(), "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showConfirmationDialog(View view, Publisher publisher, int position) {
        Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.quanly_dialog_confirmation);
        dialog.setCancelable(true);

        TextView confirmTitle = dialog.findViewById(R.id.confirmTitle);
        TextView confirmMessage = dialog.findViewById(R.id.confirmMessage);
        Button btnConfirmYes = dialog.findViewById(R.id.btnConfirmYes);
        Button btnConfirmNo = dialog.findViewById(R.id.btnConfirmNo);

        confirmTitle.setText("Xác nhận xóa");
        confirmMessage.setText("Bạn có chắc chắn muốn xóa nhà xuất bản " + publisher.getName() + " không?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            deletePublisher(view, publisher, position);
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
}
