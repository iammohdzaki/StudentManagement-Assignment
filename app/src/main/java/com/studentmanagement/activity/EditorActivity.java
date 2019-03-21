package com.studentmanagement.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.studentmanagement.R;
import com.studentmanagement.communicator.Communicator;
import com.studentmanagement.fragment.EditorFragment;

public class EditorActivity extends AppCompatActivity implements Communicator {

    private Bundle bundle;
    private EditorFragment editorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //Initialize all resources
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        editorFragment.viewMode(bundle);
    }

    //Init method will initialize all Views
    private void init() {

        bundle = getIntent().getExtras();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        editorFragment = new EditorFragment();
        fragmentTransaction.add(R.id.fragment_editor, editorFragment, "");
        fragmentTransaction.commit();

    }

    @Override
    public void addStudent(Bundle bundleData) {

    }

    @Override
    public void updateStudent(Bundle bundleData) {

    }

    @Override
    public void updateStudentList(Bundle bundleData) {

    }
}
