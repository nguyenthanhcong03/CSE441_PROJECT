package com.example.cse441_project.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.cse441_project.LoginActivity;
import com.example.cse441_project.R;
import com.example.cse441_project.auth.Constants;
import com.example.cse441_project.auth.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnSuccessListener;

public class AccountFragment extends Fragment {
    private TextView textViewUsername, textViewFullname, textViewDate, textViewGender, textViewAddress, textViewPhone, textViewEmail;
    ImageView imgAvatarAdmin;
    ImageButton btnEditAccount;
    Button btnLogout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        // Khởi tạo các thành phần UI
        textViewUsername = view.findViewById(R.id.textViewUsername);
        textViewFullname = view.findViewById(R.id.textViewFulllname);
        textViewDate = view.findViewById(R.id.textViewDate);
        textViewGender = view.findViewById(R.id.textViewGender);
        textViewAddress = view.findViewById(R.id.textViewAddress);
        textViewPhone = view.findViewById(R.id.textViewPhone);
        textViewEmail = view.findViewById(R.id.textViewEmail);

        imgAvatarAdmin = view.findViewById(R.id.imgAvatarAdmin);
        btnEditAccount = view.findViewById(R.id.btnEditAccount);
        btnLogout = view.findViewById(R.id.btnLogout);


        ImageView iconChangePassword = view.findViewById(R.id.iconChangePassword);
        TextView txChangPassword = view.findViewById(R.id.textViewChangePassword);
        ImageView iconRedirectChangPassword = view.findViewById(R.id.iconRedirectChangPassword);


        // Setup sự kiện
        iconChangePassword.setOnClickListener(v -> changePasswordListener());
        txChangPassword.setOnClickListener(v -> changePasswordListener());
        iconRedirectChangPassword.setOnClickListener(v -> changePasswordListener());
        btnLogout.setOnClickListener(v -> showConfirmationDialog());

        getUserData();

        return view;
    }

    // Hàm chuyển sang activity đổi mật khẩu
    private void changePasswordListener() {
        Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    // Hàm hiển thị dialog xác nhận đăng xuất
    private void showConfirmationDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.quanly_dialog_confirmation);
        dialog.setCancelable(true);

        TextView confirmTitle = dialog.findViewById(R.id.confirmTitle);
        TextView confirmMessage = dialog.findViewById(R.id.confirmMessage);
        Button btnConfirmYes = dialog.findViewById(R.id.btnConfirmYes);
        Button btnConfirmNo = dialog.findViewById(R.id.btnConfirmNo);

        confirmTitle.setText("Thông báo");
        confirmMessage.setText("Bạn có muốn đăng xuất không ?");

        btnConfirmYes.setOnClickListener(v -> {
            dialog.dismiss();
            logoutUser();
        });

        btnConfirmNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Hàm thực hiện đăng xuất
    private void logoutUser() {
        PreferenceManager preferenceManager = new PreferenceManager(getActivity());
        preferenceManager.remove(Constants.KEY_USER_ID);

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    // Lấy dữ liệu admin
    private void getUserData() {
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
                            startActivityForResult(intent, 1); // Gọi với REQUEST_CODE
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

    // Cập nhật dữ liệu admin khi quay lại fragment
    @Override
    public void onResume() {
        super.onResume();
        getUserData();
    }

    //   Nhận kết quả từ activity khác và cập nhật dữ liệu admin
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            getUserData();
        }
    }

}
