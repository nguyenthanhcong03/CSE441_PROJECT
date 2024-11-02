package com.example.cse441_project.fragment.student;

import static com.example.cse441_project.StudentActivity.FRAGMENT_RETURN_BOOK;
import static com.example.cse441_project.StudentActivity.FRAGMENT_VIEW_BOOK;
import static com.example.cse441_project.StudentActivity.FRAGMENT_VIEW_CATEGORY;
import static com.example.cse441_project.StudentActivity.FRAGMENT_VIEW_RULE;
import static com.example.cse441_project.StudentActivity.FRAGMENT_STUDENT_ACCOUNT;
import static com.example.cse441_project.StudentActivity.FRAGMENT_BORROW_BOOK;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cse441_project.StudentActivity;
import com.example.cse441_project.R;


public class FragmentStudentHome extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        // Khởi tạo các button
        Button btnViewBook = view.findViewById(R.id.btnViewBook);
        Button btnViewCategory = view.findViewById(R.id.btnViewCategory);
        Button btnBorrowBook = view.findViewById(R.id.btnBorrowBook);
        Button btnReturnBook = view.findViewById(R.id.btnReturnBook);
        Button btnViewRule = view.findViewById(R.id.btnViewRule);
        Button btnStudentAccount = view.findViewById(R.id.btnStudentAccount);


        btnViewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof StudentActivity) {
                    if (((StudentActivity) getActivity()).currentFragment != FRAGMENT_VIEW_BOOK) {
                        replaceFragment(new FragmentViewBook());
                        ((StudentActivity) getActivity()).currentFragment = FRAGMENT_VIEW_BOOK;
                        ((StudentActivity) getActivity()).itemId = R.id.nav_view_book; // Lưu ID item được chọn
                        ((StudentActivity) getActivity()).navigationView.getMenu().findItem(((StudentActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnViewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof StudentActivity) {
                    if (((StudentActivity) getActivity()).currentFragment != FRAGMENT_VIEW_CATEGORY) {
                        replaceFragment(new FragmentViewCategory());
                        ((StudentActivity) getActivity()).currentFragment = FRAGMENT_VIEW_CATEGORY;
                        ((StudentActivity) getActivity()).itemId = R.id.nav_view_category; // Lưu ID item được chọn
                        ((StudentActivity) getActivity()).navigationView.getMenu().findItem(((StudentActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnBorrowBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof StudentActivity) {
                    if (((StudentActivity) getActivity()).currentFragment != FRAGMENT_BORROW_BOOK) {
                        replaceFragment(new FragmentBorrowBook());
                        ((StudentActivity) getActivity()).currentFragment = FRAGMENT_BORROW_BOOK;
                        ((StudentActivity) getActivity()).itemId = R.id.nav_borrow_book; // Lưu ID item được chọn
                        ((StudentActivity) getActivity()).navigationView.getMenu().findItem(((StudentActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnReturnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof StudentActivity) {
                    if (((StudentActivity) getActivity()).currentFragment != FRAGMENT_RETURN_BOOK) {
                        replaceFragment(new FragmentReturnBook());
                        ((StudentActivity) getActivity()).currentFragment = FRAGMENT_RETURN_BOOK;
                        ((StudentActivity) getActivity()).itemId = R.id.nav_return_book; // Lưu ID item được chọn
                        ((StudentActivity) getActivity()).navigationView.getMenu().findItem(((StudentActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnViewRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof StudentActivity) {
                    if (((StudentActivity) getActivity()).currentFragment != FRAGMENT_VIEW_RULE) {
                        replaceFragment(new FragmentViewRule());
                        ((StudentActivity) getActivity()).currentFragment = FRAGMENT_VIEW_RULE;
                        ((StudentActivity) getActivity()).itemId = R.id.nav_view_rule; // Lưu ID item được chọn
                        ((StudentActivity) getActivity()).navigationView.getMenu().findItem(((StudentActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnStudentAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof StudentActivity) {
                    if (((StudentActivity) getActivity()).currentFragment != FRAGMENT_STUDENT_ACCOUNT) {
                        ((StudentActivity) getActivity()).toolbarTitle.setText("Thông tin cá nhân");
                        replaceFragment(new FragmentStudentAccount());
                        ((StudentActivity) getActivity()).currentFragment = FRAGMENT_STUDENT_ACCOUNT;
                        ((StudentActivity) getActivity()).itemId = R.id.nav_student_account; // Lưu ID item được chọn
                        ((StudentActivity) getActivity()).navigationView.getMenu().findItem(((StudentActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });
        

        return view;
    }

    // Hàm thay thế fragment
    private void replaceFragment(Fragment fragment) {
        if (getActivity() instanceof StudentActivity) {
            ((StudentActivity) getActivity()).replaceFragment(fragment);
        }
    }

}