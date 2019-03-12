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
import com.studentmanagement.touchListener.RecyclerTouchListener;
import com.studentmanagement.model.StudentInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    public final static int VIEW=0;
    public final static int UPDATE=1;
    public final static int DELETE=2;
    public final static int SAVE_RESULT_CODE =1;
    public final static int UPDATE_RESULT_CODE =2;
    private ArrayList<StudentInfo> mStudentList=new ArrayList<>();
    private Button btnAddData;
    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private RelativeLayout mLayout;
    private String[] mDialogItems;
    private Switch mSwitch;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mItemPosition;

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
        if(resultCode == SAVE_RESULT_CODE){
            String name=data.getStringExtra("NAME");
            String id=data.getStringExtra("ID");
            mStudentList.add(new StudentInfo(name,id));
            hideLayout();
            studentAdapter.notifyDataSetChanged();
        }

        //When Result code is Update Data
        if(resultCode== UPDATE_RESULT_CODE){
            String name=data.getStringExtra("NAME");
            String id=data.getStringExtra("ID");
            //Get location of current clicked element
            StudentInfo info=mStudentList.get(getPosition());
            info.setID(id);
            info.setName(name);
            studentAdapter.notifyDataSetChanged();
        }

    }

    //Show menu on the Screen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        //Set Switch to the Action Bar for Toggling Views
        MenuItem item=menu.findItem(R.id.menu_switch);
        item.setActionView(R.layout.switch_layout);
        handleLayoutView(menu);
        return true;
    }

    //Handle Click Events on Menu Item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_all_entries:
                //Show Dialog for Confirmation
                deleteConfirmationDialog();
                return true;
            case R.id.menu_switch:
                return true;
            case R.id.action_sort_name:
                //Sort Student List by Name i.e A-Z
                Collections.sort(mStudentList,new ComparatorName());
                studentAdapter.notifyDataSetChanged();
                return true;
            case R.id.action_sort_id:
                //Sort Student List by ID or Roll Number i.e 0-9
                Collections.sort(mStudentList,new ComparatorID());
                studentAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Set up Views
    public void init(){
        //Setup Dialog Items to String Array
        mDialogItems=getResources().getStringArray(R.array.Dialog_Operations);

        //Get Reference for recycler and setup Adapter
        recyclerView=(RecyclerView) findViewById(R.id.recycler_view);
        studentAdapter=new StudentAdapter(mStudentList);

        //Set layout default layout view for Recycler View
        mLayoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(studentAdapter);
    }

    //Handle Button Click Event
    public void buttonClickHandle(){
        btnAddData =(Button) findViewById(R.id.btn_add_student);
        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("MODE_TYPE","NORMAL");
                intent.putParcelableArrayListExtra("STUDENT_LIST",mStudentList);
                startActivityForResult(intent,1);
            }
        });
    }

    //Handle Recycler Click Events
    public void recyclerClickHandler(){
        //Responds to the Clicks on Student Info
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Dialog For Choosing Operation
                final StudentInfo mInfo=mStudentList.get(position);
                AlertDialog.Builder mAlertBuilder=new AlertDialog.Builder(MainActivity.this);
                mAlertBuilder.setTitle("Choose Option");
                mAlertBuilder.setIcon(R.drawable.list);
                mAlertBuilder.setSingleChoiceItems(mDialogItems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int modeType) {
                        Intent intent=new Intent(MainActivity.this,EditorActivity.class);
                        //Choose item from the list and handle click events
                        switch (modeType){
                            //View Mode
                            case VIEW:
                                intent.putExtra("MODE_TYPE","VIEW");
                                intent.putExtra("STUDENT_NAME",mInfo.getName());
                                intent.putExtra("STUDENT_ID",mInfo.getID());
                                startActivity(intent);
                                dialog.dismiss();
                                break;
                            //Update Mode
                            case UPDATE:
                                intent.putExtra("MODE_TYPE","UPDATE");
                                intent.putExtra("STUDENT_NAME",mInfo.getName());
                                intent.putExtra("STUDENT_ID",mInfo.getID());
                                intent.putParcelableArrayListExtra("STUDENT_LIST",mStudentList);
                                setPosition(position);
                                startActivityForResult(intent,2);
                                dialog.dismiss();
                                break;
                            //Delete Mode
                            case DELETE:
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
                                dialog.dismiss();
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

    /*Set the element position of current recycler view element
     *@param position as current recycler view element
     */
    private void setPosition(int position){
             mItemPosition=position;
    }

    /*Return current Position of element
     *@return Current position of the recycler view element
     */
    private int getPosition(){
        return mItemPosition;
    }

    //Change View from List to Grid and vice versa
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
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
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
        mLayout=(RelativeLayout) findViewById(R.id.empty_view);

        if(studentAdapter.getItemCount()==-1){
            mLayout.setVisibility(RelativeLayout.VISIBLE);
        }else{
            mLayout.setVisibility(RelativeLayout.INVISIBLE);
        }
    }

    //Delete One Student Record
    private void deleteStudentRecord(int position){
        mStudentList.remove(mStudentList.get(position));
        studentAdapter.notifyDataSetChanged();
        if(mStudentList.size()==0){
            mLayout.setVisibility(RelativeLayout.VISIBLE);
        }
    }

    //Delete All Student Record
    private void deleteAllStudentRecord(){
        mStudentList.removeAll(mStudentList);
        studentAdapter.notifyDataSetChanged();
        mLayout.setVisibility(RelativeLayout.VISIBLE);
    }

}
