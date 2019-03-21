package com.studentmanagement.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;


import com.studentmanagement.activity.EditorActivity;
import com.studentmanagement.communicator.Communicator;
import com.studentmanagement.R;
import com.studentmanagement.activity.MainActivity;
import com.studentmanagement.adapter.StudentAdapter;
import com.studentmanagement.services.BackgroundTaskAsync;
import com.studentmanagement.comparator.ComparatorID;
import com.studentmanagement.comparator.ComparatorName;
import com.studentmanagement.constant.Constant;
import com.studentmanagement.database.DBHelper;
import com.studentmanagement.database.StudentContract;
import com.studentmanagement.model.StudentInfo;
import com.studentmanagement.touchListener.RecyclerTouchListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class MainFragment extends Fragment {

    private Button btnAddData;
    private ArrayList<StudentInfo> mStudentList=new ArrayList<>();
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private RelativeLayout rlEmptyView;
    private String[] mDialogItems;
    private Switch mSwitch;
    private RecyclerView.LayoutManager mLayoutManager;
    private DBHelper mDbHelper;
    private BackgroundTaskAsync taskAsync;
    private Context context;
    private Communicator mCommunicator;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView= inflater.inflate(R.layout.fragment_student_list, container, false);
        init(rootView);
        recyclerClickHandler();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        this.context=context;
        try {
            mCommunicator=(Communicator) context;
        }catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu,menu);
        //Set Switch to the Action Bar for Toggling Views
        MenuItem item=menu.findItem(R.id.menu_switch);
        item.setActionView(R.layout.switch_layout);
        handleLayoutView(menu);
        super.onCreateOptionsMenu(menu,menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_all_entries:
                deleteConfirmationDialog();
                return true;
            case R.id.menu_switch:
                return true;
            case R.id.action_sort_name:
                Collections.sort(mStudentList,new ComparatorName());
                studentAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_sort_id:
                Collections.sort(mStudentList,new ComparatorID());
                studentAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize Resources
     * @param rootView as View Reference
     */
    public void init(View rootView){
        mDialogItems=getResources().getStringArray(R.array.Dialog_Operations);
        recyclerView=rootView.findViewById(R.id.recycler_view);
        studentAdapter=new StudentAdapter(mStudentList);
        rlEmptyView = rootView.findViewById(R.id.empty_view);
        mLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(studentAdapter);
        btnAddData=rootView.findViewById(R.id.btn_add_student);
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).changeTab();
                ((MainActivity)context).changeToolbarTitle();
            }
        });

        mDbHelper =new DBHelper(context);
        //mStudentList.addAll(showStudentData());
        displayStudentList();
        studentAdapter.notifyDataSetChanged();
        ((MainActivity)context).changeToolbarTitle();
        hideLayout();

    }

    //Handle Recycler Click Events
    public void recyclerClickHandler(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(context, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final StudentInfo mInfo=mStudentList.get(position);
                AlertDialog.Builder mAlertBuilder=new AlertDialog.Builder(context);
                mAlertBuilder.setTitle(getString(R.string.main_choose_option));
                mAlertBuilder.setIcon(R.drawable.vector_list_view);
                mAlertBuilder.setSingleChoiceItems(mDialogItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int modeType) {
                        switch (modeType){
                            //View Mode
                            case Constant.VIEW:
                                Intent intent =new Intent(context, EditorActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            //Update Mode
                            case Constant.UPDATE:
                                packUpdateData(mInfo,position);
                                ((MainActivity) context).changeTab();
                                dialog.dismiss();
                                break;
                            //Delete Mode
                            case Constant.DELETE:
                                createDeleteAlertDialog(position);
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                });
                AlertDialog alert=mAlertBuilder.create();
                alert.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    /**
     * Packs The Data to be Send to Other Fragment
     * @param mInfo as Student Instance
     * @param position as Current Position
     */
    private void packUpdateData(StudentInfo mInfo,int position){
        Bundle bundle=new Bundle();
        bundle.putString(Constant.MODE_TYPE,Constant.MODE_UPDATE);
        bundle.putString(Constant.CURRENT_POSITION,String.valueOf(position));
        bundle.putString(Constant.STUDENT_NAME,mInfo.getName());
        bundle.putString(Constant.STUDENT_ID,mInfo.getID());
        mCommunicator.updateStudent(bundle);
    }
    //Hide Layout when data is available
    private void hideLayout(){
        if (mStudentList.size() == 0) {
            rlEmptyView.setVisibility(RelativeLayout.VISIBLE);
        }else{
            rlEmptyView.setVisibility(RelativeLayout.INVISIBLE);
        }
    }

    /**
     * Add Student Data to List
     * @param bundle as Bundle
     */
    public void addStudentData(Bundle bundle) {
            StudentInfo studentInfo=new StudentInfo(bundle.getString(Constant.STUDENT_NAME),bundle.getString(Constant.STUDENT_ID));
            mStudentList.add(studentInfo);
            hideLayout();
            studentAdapter.notifyDataSetChanged();
    }


    public void updateStudentList(Bundle bundle){
        if(bundle.getString(Constant.CURRENT_POSITION)==null){
            Log.i("BUNDLE", "updateStudentList: NULL");
        }else{
            StudentInfo mInfo=mStudentList.get(Integer.parseInt(bundle.getString(Constant.CURRENT_POSITION)));
            mInfo.setName(bundle.getString(Constant.STUDENT_NAME));
            mInfo.setID(bundle.getString(Constant.STUDENT_ID));
            studentAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Create Delete Dialog
     * @param position as View Position
     */
    private void createDeleteAlertDialog(final int position){
        AlertDialog.Builder deleteDialog=new AlertDialog.Builder(context);
        deleteDialog.setTitle(getString(R.string.delete_title));
        deleteDialog.setMessage(getString(R.string.delete_message));
        deleteDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteStudentRecord(position);
            }
        });
        deleteDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.show();
    }

    /**
     * Delete One Student Record
     * @param position as current position of Student
     */
    private void deleteStudentRecord(int position){
        taskAsync = new BackgroundTaskAsync(context);
        taskAsync.execute(Constant.MODE_DELETE, mStudentList.get(position).getID());
        mStudentList.remove(mStudentList.get(position));
        studentAdapter.notifyDataSetChanged();
        if(mStudentList.size()==0){
            rlEmptyView.setVisibility(RelativeLayout.VISIBLE);
        }
    }

    //Delete All Student Record
    private void deleteAllStudentRecord(){
        taskAsync = new BackgroundTaskAsync(context);
        taskAsync.execute(Constant.MODE_DELETE_ALL);
        mStudentList.removeAll(mStudentList);
        studentAdapter.notifyDataSetChanged();
        rlEmptyView.setVisibility(RelativeLayout.VISIBLE);
    }

    //Show Student in Recycler View
    private ArrayList<StudentInfo> showStudentData(){
        ArrayList<StudentInfo> studentList=new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = mDbHelper.getStudentData(db);
        try {
            Log.i("ROWS", "Rows Count:" + cursor.getCount());
            int idColumnIndex = cursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_NAME);

            while (cursor.moveToNext()) {
                StudentInfo student=new StudentInfo();
                student.setID(cursor.getString(idColumnIndex));
                student.setName(cursor.getString(nameColumnIndex));
                studentList.add(student);
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
            db.close();
        }

        return studentList;
    }

    /**
     * Change View from List to Grid and vice versa
     * @param menu as Menu Reference
     */
    private void handleLayoutView(@NotNull Menu menu) {
        mSwitch=menu.findItem(R.id.menu_switch).getActionView().findViewById(R.id.switch_toggle_view);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mLayoutManager=new GridLayoutManager(context,3);
                    recyclerView.setLayoutManager(mLayoutManager);
                    Toast.makeText(context,"Grid View",Toast.LENGTH_SHORT).show();
                }else{
                    mLayoutManager=new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(mLayoutManager);
                    Toast.makeText(context,"List View",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Show dialog before deleting all records
    private void deleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(getString(R.string.delete_all_dialog));
        builder.setPositiveButton(getString(R.string.delete_all_positive_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete all accounts.
                if(mStudentList.size()==0){
                    return;
                }
                deleteAllStudentRecord();
            }
        });
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the account.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void displayStudentList(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = mDbHelper.getStudentData(db);
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

}
