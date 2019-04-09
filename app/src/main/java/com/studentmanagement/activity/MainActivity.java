package com.studentmanagement.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.studentmanagement.R;
import com.studentmanagement.adapter.StudentPagerAdapter;
import com.studentmanagement.communicator.Communicator;
import com.studentmanagement.database.DBHelper;
import com.studentmanagement.fragment.EditorFragment;
import com.studentmanagement.fragment.MainFragment;
import com.studentmanagement.model.StudentInfo;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Communicator {


    private ArrayList<StudentInfo> mStudentList = new ArrayList<>();
    private DBHelper mDbHelper;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private StudentPagerAdapter mFragmentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        mFragmentAdapter = new StudentPagerAdapter(getSupportFragmentManager());

        mFragmentAdapter.addFragment(new MainFragment(), "");
        mFragmentAdapter.addFragment(new EditorFragment(), "");

        viewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_student_list);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_student_add);

        mDbHelper = new DBHelper(this);

    }

    @Override
    public void addStudent(Bundle bundleData) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 0;
        MainFragment f = (MainFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.addStudentData(bundleData);
    }

    @Override
    public void updateStudent(Bundle bundleData) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 1;
        EditorFragment f = (EditorFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.updateStudentData(bundleData);
    }

    @Override
    public void updateStudentList(Bundle bundleData) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 0;
        MainFragment f = (MainFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.updateStudentList(bundleData);
    }

    //Changes The View Pager Tab
    public void changeTab() {
        if (viewPager.getCurrentItem() == 0) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    //Changes the View Pager Title
    public void changeToolbarTitle() {
        if (viewPager.getCurrentItem() == 0) {
            setTitle(R.string.add_student);
        } else {
            setTitle(R.string.add_student);
        }
    }

}
