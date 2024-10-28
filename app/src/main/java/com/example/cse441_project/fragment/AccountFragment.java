package com.example.cse441_project.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cse441_project.R;

public class AccountFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        ImageButton btnEditAccount = view.findViewById(R.id.btnEditAccount);
        TableRow tableRowChangePassword = view.findViewById(R.id.tableRowChangePassword);

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(), EditAdminProfileActivity.class);
                startActivity(intent1);
            }
        });

        tableRowChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getActivity(), ChangePassWordActivity.class);
                startActivity(intent1);
            }
        });

        return view;
    }
}
