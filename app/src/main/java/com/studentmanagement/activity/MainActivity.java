package com.studentmanagement.activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.studentmanagement.R;
import com.studentmanagement.adapter.ViewPagerAdapter;
import com.studentmanagement.database.DBHelper;
import com.studentmanagement.database.StudentContract;
import com.studentmanagement.fragment.EditorFragment;
import com.studentmanagement.fragment.StudentListFragment;
import com.studentmanagement.fragmentCommunication.Communicator;
import com.studentmanagement.model.StudentInfo;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Communicator {


    private ArrayList<StudentInfo> mStudentList=new ArrayList<>();
    private DBHelper mDBhelper;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter mFragmentAdapter;
    private FragmentRefreshListener fragmentRefreshListener;

    public FragmentRefreshListener getFragmentRefreshListener() {
        return fragmentRefreshListener;
    }

    public void setFragmentRefreshListener(FragmentRefreshListener fragmentRefreshListener) {
        this.fragmentRefreshListener = fragmentRefreshListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        mFragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        mFragmentAdapter.addFragment(new StudentListFragment(), "");
        mFragmentAdapter.addFragment(new EditorFragment(), "");


        viewPager.setAdapter(mFragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (getFragmentRefreshListener() != null) {
                    getFragmentRefreshListener().refreshData();
                }
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_student_list);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_student_add);

        mDBhelper = new DBHelper(this);

    }

    @Override
    public void addStudent(Bundle bundleData) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 0;
        StudentListFragment f = (StudentListFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.addStudentData(bundleData);
    }

    @Override
    public void updateStudent(Bundle bundleData) {
        String tag = "android:switcher:" + R.id.view_pager + ":" + 1;
        EditorFragment f = (EditorFragment) getSupportFragmentManager().findFragmentByTag(tag);
        f.updateStudentData(bundleData);
    }

    public void changeTab() {
        if (viewPager.getCurrentItem() == 0) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
    }

    public void showStudentData() {
        SQLiteDatabase db = mDBhelper.getReadableDatabase();
        Cursor cursor = mDBhelper.getStudentData(db);
        try {
            Log.i("ROWS", "Rows Count:" + cursor.getCount());
            int idColumnIndex = cursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_NAME);

            mStudentList.removeAll(mStudentList);
            while (cursor.moveToNext()) {
                String currentID = cursor.getString(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                mStudentList.add(new StudentInfo(currentName, currentID));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
            db.close();
        }

    }

    public interface FragmentRefreshListener {
        void refreshData();
    }
}
