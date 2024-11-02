package com.example.cse441_project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.cse441_project.fragment.HomeFragment;
import com.example.cse441_project.fragment.student.FragmentBorrowBook;
import com.example.cse441_project.fragment.student.FragmentReturnBook;
import com.example.cse441_project.fragment.student.FragmentStudentAccount;
import com.example.cse441_project.fragment.student.FragmentStudentHome;
import com.example.cse441_project.fragment.student.FragmentViewBook;
import com.example.cse441_project.fragment.student.FragmentViewCategory;
import com.example.cse441_project.fragment.student.FragmentViewRule;
import com.google.android.material.navigation.NavigationView;

public class StudentActivity extends AppCompatActivity {

    public static final int FRAGMENT_STUDENT_HOME = 0;
    public static final int FRAGMENT_VIEW_BOOK = 1;
    public static final int FRAGMENT_VIEW_CATEGORY = 2;
    public static final int FRAGMENT_BORROW_BOOK = 3;
    public static final int FRAGMENT_RETURN_BOOK = 4;
    public static final int FRAGMENT_VIEW_RULE = 5;
    public static final int FRAGMENT_STUDENT_ACCOUNT = 6;


    public int currentFragment = FRAGMENT_STUDENT_HOME;

    public int itemId = R.id.nav_student_home; // ID mặc định, có thể đặt lại sau

    DrawerLayout drawerLayout;
    ImageButton btnDrawerToggle, btnManageAccount;
    public NavigationView navigationView;
    public TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);

        drawerLayout = findViewById(R.id.drawer_layout);
        btnDrawerToggle = findViewById(R.id.btnDrawerToggle);
        navigationView = findViewById(R.id.navigation_view);
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnManageAccount = findViewById(R.id.btnManageAccount);

        // Đặt trạng thái checked cho item mặc định
        navigationView.setCheckedItem(R.id.nav_student_home);

        // Hiển thị FragmentStudentHome khi ứng dụng được khởi động
        replaceFragment(new FragmentStudentHome());
        currentFragment = FRAGMENT_STUDENT_HOME; // Cập nhật fragment hiện tại

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

                if (currentFragment != FRAGMENT_STUDENT_ACCOUNT) {
                    // Bỏ chọn trạng thái checked cho item trong Navigation Drawer
                    navigationView.getMenu().findItem(itemId).setChecked(false); // Bỏ chọn item hiện tại
                    toolbarTitle.setText("Thông tin cá nhân");
                    replaceFragment(new FragmentStudentAccount());
                    currentFragment = FRAGMENT_STUDENT_ACCOUNT;
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                itemId = item.getItemId(); // Lưu ID item được chọn
                item.setChecked(true); // Đặt trạng thái checked

                // Khi chọn các nav_item thì sẽ được thay thế bằng các fragment tương ứng
                if (itemId == R.id.nav_student_home) {
                    if (currentFragment != FRAGMENT_STUDENT_HOME) {
                        toolbarTitle.setText("AZ-Library");
                        replaceFragment(new FragmentStudentHome());
                        currentFragment = FRAGMENT_STUDENT_HOME;
                    }
                } else if (itemId == R.id.nav_view_book) {
                    if (currentFragment != FRAGMENT_VIEW_BOOK) {
                        replaceFragment(new FragmentViewBook());
                        currentFragment = FRAGMENT_VIEW_BOOK;
                    }
                } else if (itemId == R.id.nav_view_category) {
                    if (currentFragment != FRAGMENT_VIEW_CATEGORY) {
                        replaceFragment(new FragmentViewCategory());
                        currentFragment = FRAGMENT_VIEW_CATEGORY;
                    }
                } else if (itemId == R.id.nav_borrow_book) {
                    if (currentFragment != FRAGMENT_BORROW_BOOK) {
                        replaceFragment(new FragmentBorrowBook());
                        currentFragment = FRAGMENT_BORROW_BOOK;
                    }
                }  else if (itemId == R.id.nav_view_rule) {
                    if (currentFragment != FRAGMENT_VIEW_RULE) {
                        replaceFragment(new FragmentViewRule());
                        currentFragment = FRAGMENT_VIEW_RULE;
                    }
                } else if (itemId == R.id.nav_student_account) {
                    if (currentFragment != FRAGMENT_STUDENT_ACCOUNT) {
                        replaceFragment(new FragmentStudentAccount());
                        currentFragment = FRAGMENT_STUDENT_ACCOUNT;
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
            if (currentFragment != FRAGMENT_STUDENT_HOME) {
                // Trở về HomeFragment
                toolbarTitle.setText("AZ-Library");
                replaceFragment(new FragmentStudentHome());
                currentFragment = FRAGMENT_STUDENT_HOME; // Cập nhật fragment hiện tại
                // Đặt trạng thái checked cho item mặc định
                navigationView.setCheckedItem(R.id.nav_student_home);
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