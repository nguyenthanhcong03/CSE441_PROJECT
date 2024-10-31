package com.example.cse441_project.fragment;

import static com.example.cse441_project.MainActivity.FRAGMENT_AUTHOR;
import static com.example.cse441_project.MainActivity.FRAGMENT_BOOK;
import static com.example.cse441_project.MainActivity.FRAGMENT_CATEGORY;
import static com.example.cse441_project.MainActivity.FRAGMENT_PUBLISHER;
import static com.example.cse441_project.MainActivity.FRAGMENT_RULE;
import static com.example.cse441_project.MainActivity.FRAGMENT_STATISTIC;
import static com.example.cse441_project.MainActivity.FRAGMENT_STUDENT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.cse441_project.MainActivity;
import com.example.cse441_project.R;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo các button
        Button btnManageBook = view.findViewById(R.id.btnManageBook);
        Button btnManageCategory = view.findViewById(R.id.btnManageCategory);
        Button btnManageStudent = view.findViewById(R.id.btnManageStudent);
        Button btnManageAuthor = view.findViewById(R.id.btnManageAuthor);
        Button btnManagePublisher = view.findViewById(R.id.btnManagePublisher);
        Button btnStatistic = view.findViewById(R.id.btnStatistic);
        Button btnManageRule = view.findViewById(R.id.btnManageRule);


        btnManageBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_BOOK) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Quản lý sách");
                        replaceFragment(new BookFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_BOOK;
                        ((MainActivity) getActivity()).itemId = R.id.nav_manage_book; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnManageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_CATEGORY) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Quản lý danh mục");
                        replaceFragment(new CategoryFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_CATEGORY;
                        ((MainActivity) getActivity()).itemId = R.id.nav_manage_category; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnManageStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_STUDENT) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Quản lý sinh viên");
                        replaceFragment(new StudentFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_STUDENT;
                        ((MainActivity) getActivity()).itemId = R.id.nav_manage_student; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnManageAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_AUTHOR) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Quản lý tác giả");
                        replaceFragment(new AuthorFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_AUTHOR;
                        ((MainActivity) getActivity()).itemId = R.id.nav_manage_author; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnManagePublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_PUBLISHER) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Quản lý nhà xuất bản");
                        replaceFragment(new PublisherFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_PUBLISHER;
                        ((MainActivity) getActivity()).itemId = R.id.nav_manage_publisher; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_STATISTIC) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Thống kê");
                        replaceFragment(new StatisticFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_STATISTIC;
                        ((MainActivity) getActivity()).itemId = R.id.nav_statistic; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        btnManageRule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof MainActivity) {
                    if (((MainActivity) getActivity()).currentFragment != FRAGMENT_RULE) {
                        ((MainActivity) getActivity()).toolbarTitle.setText("Quản lý quy định");
                        replaceFragment(new RuleFragment());
                        ((MainActivity) getActivity()).currentFragment = FRAGMENT_RULE;
                        ((MainActivity) getActivity()).itemId = R.id.nav_manage_rule; // Lưu ID item được chọn
                        ((MainActivity) getActivity()).navigationView.getMenu().findItem(((MainActivity) getActivity()).itemId).setChecked(true);

                    }
                }
            }
        });

        return view;
    }

    // Hàm thay thế fragment
    private void replaceFragment(Fragment fragment) {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).replaceFragment(fragment);
        }
    }


}
