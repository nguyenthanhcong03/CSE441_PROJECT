package com.example.cse441_project.auth.forgotpassword;


import com.example.cse441_project.auth.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class getPassword {
    public interface FirestoreCallback{
        void onCallback(String password);
    }

    public void getPass(String email, FirestoreCallback callback) {
        FirebaseFirestore firebase = FirebaseFirestore.getInstance();
        firebase.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String pass = documentSnapshot.getString(Constants.KEY_PASSWORD);
                        callback.onCallback(pass);
                    } else {
                        callback.onCallback(null);  // Email không tồn tại hoặc có lỗi
                    }
                });
    }
}
