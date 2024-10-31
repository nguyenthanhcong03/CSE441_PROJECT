package com.example.cse441_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cse441_project.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnSuccessListener;

public class AccountFragment extends Fragment {
    private TextView textViewUsername, textViewFullname, textViewDate, textViewGender, textViewAddress, textViewPhone, textViewEmail;
    ImageView imgAvatarAdmin;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        textViewUsername = view.findViewById(R.id.textViewUsername);
        textViewFullname = view.findViewById(R.id.textViewFulllname);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewGender = view.findViewById(R.id.textViewGender);
        textViewAddress = view.findViewById(R.id.textViewAddress);
        textViewPhone = view.findViewById(R.id.textViewPhone);
        textViewEmail = view.findViewById(R.id.textViewEmail);

        imgAvatarAdmin = view.findViewById(R.id.imgAvatarAdmin);

        ImageButton btnEditAccount = view.findViewById(R.id.btnEditAccount);
        ImageView iconChangPassword = view.findViewById(R.id.iconChangPassword);
        TextView txChangPassword = view.findViewById(R.id.textViewChangePassword);
        ImageView iconRedirectChangPassword = view.findViewById(R.id.iconRedirectChangPassword);

        View.OnClickListener changePasswordListener = v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        };

        iconChangPassword.setOnClickListener(changePasswordListener);
        txChangPassword.setOnClickListener(changePasswordListener);
        iconRedirectChangPassword.setOnClickListener(changePasswordListener);

        getUserData(btnEditAccount);

        return view;
    }

    private void getUserData(ImageButton btnEditAccount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("Users").whereEqualTo("role", "Admin");

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        String userId = snapshot.getId();
                        String username = snapshot.getString("username");
                        String fullname = snapshot.getString("fullname");
                        String date = snapshot.getString("birthday");
                        String gender = snapshot.getString("gender");
                        String address = snapshot.getString("address");
                        String phone = snapshot.getString("phone");
                        String email = snapshot.getString("email");
                        String avatarUrl = snapshot.getString("avatarUrl");

                        textViewUsername.setText(username);
                        textViewFullname.setText(fullname);
                        textViewDate.setText(date);
                        textViewGender.setText(gender);
                        textViewAddress.setText(address);
                        textViewPhone.setText(phone);
                        textViewEmail.setText(email);

                        Glide.with(AccountFragment.this)
                                .load(avatarUrl)
                                .into(imgAvatarAdmin);

                        btnEditAccount.setOnClickListener(v -> {
                            Intent intent = new Intent(getActivity(), EditAdminProfileActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("username", username);
                            intent.putExtra("fullname", fullname);
                            intent.putExtra("date", date);
                            intent.putExtra("gender", gender);
                            intent.putExtra("address", address);
                            intent.putExtra("phone", phone);
                            intent.putExtra("email", email);
                            intent.putExtra("avatarUrl", avatarUrl);
                            startActivity(intent);
                        });
                    }
                } else {
                    Log.d("UserData", "No data found for role Admin");
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("FirebaseError", e.getMessage());
        });
    }
}
