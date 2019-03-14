package com.studentmanagement.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.studentmanagement.adapter.StudentAdapter;
import com.studentmanagement.comparator.ComparatorID;
import com.studentmanagement.comparator.ComparatorName;
import com.studentmanagement.R;
import com.studentmanagement.constant.Constant;
import com.studentmanagement.touchListener.RecyclerTouchListener;
import com.studentmanagement.model.StudentInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {


    private ArrayList<StudentInfo> mStudentList=new ArrayList<>();
    private Button btnAddData;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private RelativeLayout rlEmptyView;
    private String[] mDialogItems;
    private Switch mSwitch;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mItemPosition;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init method will setup Recycler View and Layout Manager
        init();

        //button Click Handle will perform action onClick event on Add Student Button
        buttonClickHandle();

        //recycler Click Handler will handle onClick events on recycler View
        recyclerClickHandler();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //When Result code is Save Data
        if(resultCode == Constant.SAVE_RESULT_CODE){
            String name=data.getStringExtra("NAME");
            String id=data.getStringExtra("ID");
            mStudentList.add(new StudentInfo(name,id));
            hideLayout();
            studentAdapter.notifyDataSetChanged();
        }

        //When Result code is Update Data
        if(resultCode== Constant.UPDATE_RESULT_CODE){
            String name=data.getStringExtra("NAME");
            String id=data.getStringExtra("ID");
            //Get location of current clicked element
            StudentInfo info=mStudentList.get(getPosition());
            info.setID(id);
            info.setName(name);
            studentAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //Set Switch to the Action Bar for Toggling Views
        MenuItem item=menu.findItem(R.id.menu_switch);
        item.setActionView(R.layout.switch_layout);
        handleLayoutView(menu);
        return true;
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

    //Set up Views
    public void init(){
        mDialogItems=getResources().getStringArray(R.array.Dialog_Operations);

        recyclerView=findViewById(R.id.recycler_view);
        studentAdapter=new StudentAdapter(mStudentList);

        mLayoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(studentAdapter);
    }

    //Handle Button Click Event
    public void buttonClickHandle(){
        btnAddData =findViewById(R.id.btn_add_student);
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=createIntent(EditorActivity.class);
                intent.putExtra(Constant.MODE_TYPE,Constant.MODE_NORMAL);
                intent.putParcelableArrayListExtra(Constant.STUDENT_LIST,mStudentList);
                startActivityForResult(intent,Constant.SAVE_RESULT_CODE);
            }
        });
    }

    //Handle Recycler Click Events
    public void recyclerClickHandler(){
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final StudentInfo mInfo=mStudentList.get(position);
                AlertDialog.Builder mAlertBuilder=new AlertDialog.Builder(MainActivity.this);
                mAlertBuilder.setTitle(getString(R.string.main_choose_option));
                mAlertBuilder.setIcon(R.drawable.vector_list_view);
                mAlertBuilder.setSingleChoiceItems(mDialogItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int modeType) {
                        intent=createIntent(EditorActivity.class);
                        switch (modeType){
                            //View Mode
                            case Constant.VIEW:
                                intent.putExtra(Constant.MODE_TYPE,Constant.MODE_VIEW);
                                packData(intent,mInfo);
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            //Update Mode
                            case Constant.UPDATE:
                                intent.putExtra(Constant.MODE_TYPE,Constant.MODE_UPDATE);
                                packData(intent,mInfo);
                                intent.putParcelableArrayListExtra(Constant.STUDENT_LIST,mStudentList);
                                setPosition(position);
                                startActivityForResult(intent,Constant.UPDATE_RESULT_CODE);
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
     * Create Delete Dialog
     * @param position as View Position
     */
    private void createDeleteAlertDialog(final int position){
        AlertDialog.Builder deleteDialog=new AlertDialog.Builder(MainActivity.this);
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
     * Packs the details of Student in Intent
     * @param intent as Intent Object
     * @param mInfo as Student Object Reference
     */
    private void packData(Intent intent, StudentInfo mInfo) {
        intent.putExtra(Constant.STUDENT_NAME,mInfo.getName());
        intent.putExtra(Constant.STUDENT_ID,mInfo.getID());
    }

    /**
     * Set the element position of current recycler view element
     * @param position as current recycler view element
     */
    private void setPosition(int position){
             mItemPosition=position;
    }

    /**
     * Return current Position of element
     * @return Current position of the recycler view element
     */
    private int getPosition(){
        return mItemPosition;
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
                    mLayoutManager=new GridLayoutManager(MainActivity.this,3);
                    recyclerView.setLayoutManager(mLayoutManager);
                   Toast.makeText(MainActivity.this,"Grid View",Toast.LENGTH_SHORT).show();
                }else{
                    mLayoutManager=new LinearLayoutManager(MainActivity.this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    Toast.makeText(MainActivity.this,"List View",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Show dialog before deleting all records
    private void deleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    //Hide Layout when data is available
    private void hideLayout(){
        rlEmptyView =findViewById(R.id.empty_view);

        if(studentAdapter.getItemCount()==-1){
            rlEmptyView.setVisibility(RelativeLayout.VISIBLE);
        }else{
            rlEmptyView.setVisibility(RelativeLayout.INVISIBLE);
        }
    }

    /**
     * Delete One Student Record
     * @param position as current position of Student
     */
    private void deleteStudentRecord(int position){
        mStudentList.remove(mStudentList.get(position));
        studentAdapter.notifyDataSetChanged();
        if(mStudentList.size()==0){
            rlEmptyView.setVisibility(RelativeLayout.VISIBLE);
        }
    }

    //Delete All Student Record
    private void deleteAllStudentRecord(){
        mStudentList.removeAll(mStudentList);
        studentAdapter.notifyDataSetChanged();
        rlEmptyView.setVisibility(RelativeLayout.VISIBLE);
    }

    /**
     * Creates Intent for a Class
     * @param editorActivityClass as the class name
     * @return intent
     */
    public Intent createIntent(Class<?> editorActivityClass){
        Intent intent=new Intent(this,editorActivityClass);
        return intent;
    }
}
