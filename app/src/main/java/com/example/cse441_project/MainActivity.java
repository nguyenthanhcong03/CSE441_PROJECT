package com.example.cse441_project;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.cse441_project.fragment.AccountFragment;
import com.example.cse441_project.fragment.HomeFragment;
import com.example.cse441_project.fragment.BookFragment;
import com.example.cse441_project.fragment.CategoryFragment;
import com.example.cse441_project.fragment.StudentFragment;
import com.example.cse441_project.fragment.AuthorFragment;
import com.example.cse441_project.fragment.PublisherFragment;
import com.example.cse441_project.fragment.StatisticFragment;
import com.example.cse441_project.fragment.RuleFragment;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_BOOK = 1;
    public static final int FRAGMENT_CATEGORY = 2;
    public static final int FRAGMENT_STUDENT = 3;
    public static final int FRAGMENT_AUTHOR = 4;
    public static final int FRAGMENT_PUBLISHER = 5;
    public static final int FRAGMENT_STATISTIC = 6;
    public static final int FRAGMENT_RULE = 7;
    public static final int FRAGMENT_ACCOUNT = 8;

    public int currentFragment = FRAGMENT_HOME;

    public int itemId = R.id.nav_home; // ID mặc định, có thể đặt lại sau

    DrawerLayout drawerLayout;
    ImageButton btnDrawerToggle, btnManageAccount;
    public NavigationView navigationView;
    public TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        btnDrawerToggle = findViewById(R.id.btnDrawerToggle);
        navigationView = findViewById(R.id.navigation_view);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnManageAccount = findViewById(R.id.btnManageAccount);

        // Đặt trạng thái checked cho item mặc định
        navigationView.setCheckedItem(R.id.nav_home);

        // Hiển thị HomeFragment khi ứng dụng được khởi động
        replaceFragment(new HomeFragment());
        currentFragment = FRAGMENT_HOME; // Cập nhật fragment hiện tại

        // Mở thanh menu trượt
        btnDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.open();
            }
        });

        // Mở fragment thông tin cá nhân
        btnManageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentFragment != FRAGMENT_ACCOUNT) {
                    // Bỏ chọn trạng thái checked cho item trong Navigation Drawer
                    navigationView.getMenu().findItem(itemId).setChecked(false); // Bỏ chọn item hiện tại
                    toolbarTitle.setText("Thông tin cá nhân");
                    replaceFragment(new AccountFragment());
                    currentFragment = FRAGMENT_ACCOUNT;
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                itemId = item.getItemId(); // Lưu ID item được chọn
                item.setChecked(true); // Đặt trạng thái checked

                // Khi chọn các nav_item thì sẽ được thay thế bằng các fragment tương ứng
                if (itemId == R.id.nav_home) {
                    if (currentFragment != FRAGMENT_HOME) {
                        toolbarTitle.setText("AZ-Library");
                        replaceFragment(new HomeFragment());
                        currentFragment = FRAGMENT_HOME;
                    }
                } else if (itemId == R.id.nav_manage_book) {
                    if (currentFragment != FRAGMENT_BOOK) {
                        toolbarTitle.setText("Quản lý sách");
                        replaceFragment(new BookFragment());
                        currentFragment = FRAGMENT_BOOK;
                    }
                } else if (itemId == R.id.nav_manage_category) {
                    if (currentFragment != FRAGMENT_CATEGORY) {
                        toolbarTitle.setText("Quản lý danh mục");
                        replaceFragment(new CategoryFragment());
                        currentFragment = FRAGMENT_CATEGORY;
                    }
                } else if (itemId == R.id.nav_manage_student) {
                    if (currentFragment != FRAGMENT_STUDENT) {
                        toolbarTitle.setText("Quản lý sinh viên");
                        replaceFragment(new StudentFragment());
                        currentFragment = FRAGMENT_STUDENT;
                    }
                } else if (itemId == R.id.nav_manage_author) {
                    if (currentFragment != FRAGMENT_AUTHOR) {
                        toolbarTitle.setText("Quản lý tác giả");
                        replaceFragment(new AuthorFragment());
                        currentFragment = FRAGMENT_AUTHOR;
                    }
                } else if (itemId == R.id.nav_manage_publisher) {
                    if (currentFragment != FRAGMENT_PUBLISHER) {
                        toolbarTitle.setText("Quản lý nhà xuất bản");
                        replaceFragment(new PublisherFragment());
                        currentFragment = FRAGMENT_PUBLISHER;
                    }
                } else if (itemId == R.id.nav_statistic) {
                    if (currentFragment != FRAGMENT_STATISTIC) {
                        toolbarTitle.setText("Thống kê");
                        replaceFragment(new StatisticFragment());
                        currentFragment = FRAGMENT_STATISTIC;
                    }
                } else if (itemId == R.id.nav_manage_rule) {
                    if (currentFragment != FRAGMENT_RULE) {
                        toolbarTitle.setText("Quy định");
                        replaceFragment(new RuleFragment());
                        currentFragment = FRAGMENT_RULE;
                    }
                }

                navigationView.setCheckedItem(itemId); // Đặt lại trạng thái checked cho item đã chọn
                drawerLayout.close();
                return false;
            }


        });
    }

    // Sự kiện khi nhấn nút back
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            // Kiểm tra xem fragment hiện tại có phải là HomeFragment không
            if (currentFragment != FRAGMENT_HOME) {
                // Trở về HomeFragment
                toolbarTitle.setText("AZ-Library");
                replaceFragment(new HomeFragment());
                currentFragment = FRAGMENT_HOME; // Cập nhật fragment hiện tại
                // Đặt trạng thái checked cho item mặc định
                navigationView.setCheckedItem(R.id.nav_home);
            } else {
                // Nếu đang ở HomeFragment, thực hiện hành động mặc định
                super.onBackPressed();
            }
        }
    }

    // Hàm thay thế fragment
    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }


}