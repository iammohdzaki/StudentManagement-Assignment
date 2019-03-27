package com.studentmanagement.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studentmanagement.backgroundTask.BackgroundTaskAsync;
import com.studentmanagement.backgroundTask.BackgroundTaskService;
import com.studentmanagement.backgroundTask.IntentServiceBackground;
import com.studentmanagement.R;
import com.studentmanagement.constant.Constant;
import com.studentmanagement.model.StudentInfo;
import com.studentmanagement.validator.CustomTextWatcher;
import com.studentmanagement.validator.Validator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.studentmanagement.constant.Constant.CHANNEL_ID;


public class EditorActivity extends AppCompatActivity {

    private Button btnSaveData;
    private EditText etName, etId;
    private TextInputLayout tiName, tiId;
    private BackgroundTaskAsync taskAsync;
    private String[] mBackgroundItems;
    private ServiceCompleteBroadcast receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Initialize all resources
        init();


        //Choose Mode in which activity is to be opened
        chooseMode();


    }

    //Init method will initialize all Views
    private void init(){
        tiName = findViewById(R.id.til_name);
        tiId = findViewById(R.id.til_roll);
        tiName.setHint(getString(R.string.til_hint_name));
        tiId.setHint(getString(R.string.til_hint_id));

        mBackgroundItems = getResources().getStringArray(R.array.background_Task);

        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.et_roll_number);

        //Add TextWatcher to EditText
        etName.addTextChangedListener(new CustomTextWatcher(etName));
        etId.addTextChangedListener(new CustomTextWatcher(etId));

        taskAsync = new BackgroundTaskAsync(EditorActivity.this);
        btnSaveData =findViewById(R.id.btn_save_data);
    }

    //Choose Mode
    private void chooseMode(){
        final Intent intent = getIntent();
        final ArrayList<StudentInfo> mStudentList=intent.getParcelableArrayListExtra(Constant.STUDENT_LIST);

        final String id=intent.getStringExtra(Constant.STUDENT_ID);
        setTitle(R.string.activity_title_add);

        switch (intent.getStringExtra(Constant.MODE_TYPE)){
            case Constant.MODE_NORMAL:
                setTitle(R.string.activity_title_add);
                btnSaveData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveDataMode(mStudentList);
                    }
                });
                break;
            case Constant.MODE_UPDATE:
                btnSaveData.setText(getString(R.string.btn_update));
                setTitle(R.string.activity_title_update);
                fillData(etName, etId, intent);
                btnSaveData.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateMode(id,mStudentList);
                    }
                });
                break;
            case Constant.MODE_VIEW:
                viewMode(etName, etId);
                fillData(etName, etId, intent);
                break;
            default:
                break;
        }
    }

    /**
     * When activity is opened in normal mode i.e Saves New Student Data
     * @param mStudentList as Current Student List
     */
    private void saveDataMode(ArrayList<StudentInfo> mStudentList) {
        Intent sendBack = new Intent();
        //Validate Name i.e No special Character and numbers
        if (!Validator.validateName(getNameEditText())) {
            tiName.setError(getString(R.string.err_msg_name));
            requestFocus(tiName);
            return;
        }
        //Validate ID or Roll Number
        if (!Validator.validateId(getIdEditText())) {
            tiId.setError(getString(R.string.err_msg_id));
            requestFocus(etId);
            return;
        }

        //Validate Unique ID
        if (!Validator.uniqueId(getIdEditText(),mStudentList)) {
            tiId.setError(getString(R.string.unique_id_err));
            requestFocus(etId);
            return;
        }

        tiName.setErrorEnabled(false);
        tiId.setErrorEnabled(false);

        setResult(Constant.SAVE_RESULT_CODE, sendBack);
        BackgroundTaskAlertDialog(Constant.MODE_NORMAL);
    }

    /**
     * Populate the Edit Text Field in Case of Edit and View Mode
     * @param mName as Name Edit Text reference
     * @param mID as Id Edit Text reference
     * @param intent as Intent Object reference
     */
    public void fillData(EditText mName, EditText mID, Intent intent) {
        setTitle(R.string.editor_title_details);
        Log.i("Pos", "ID:" + intent.getStringExtra(Constant.STUDENT_ID));
        mName.setText(intent.getStringExtra(Constant.STUDENT_NAME));
        mID.setText(intent.getStringExtra(Constant.STUDENT_ID));
    }

    /**
     * Show Data in View Mode
     * @param mName as Name Edit Text reference
     * @param mID as Id Edit Text reference
     */
    public void viewMode(EditText mName, EditText mID) {
        mName.setEnabled(false);
        mID.setEnabled(false);
        mID.setFocusable(false);
        mName.setFocusable(false);
        btnSaveData.setVisibility(View.INVISIBLE);
    }

    /**
     * @return Current Edit Text String
     */
    private String getNameEditText(){
        return etName.getText().toString().trim();
    }

    /**
     * @return Current Edit Text String
     */
    private String getIdEditText(){
        return etId.getText().toString().trim();
    }

    /**
     * When the activity opened in UPDATE or EDIT MODE
     * @param id as Student ID
     * @param mStudentList as Student Details List
     */
    public void updateMode(String id,ArrayList<StudentInfo> mStudentList) {

        //Validate Name i.e No special Character and numbers
        if (!Validator.validateName(getNameEditText())) {
            tiName.setError(getString(R.string.err_msg_name));
            requestFocus(tiName);
            return;
        }
        //Validate ID or Roll Number
        if (!Validator.validateId(getIdEditText())) {
            tiId.setError(getString(R.string.err_msg_id));
            requestFocus(etId);
            return;
        }

        if (id.equals(etId.getText().toString())) {
        } else {
            if (!Validator.uniqueId(getIdEditText(),mStudentList)) {
                tiId.setError(getString(R.string.unique_id_err));
                requestFocus(etId);
                return;
            }
        }

        tiName.setErrorEnabled(false);
        tiId.setErrorEnabled(false);

        Intent sendBack =new Intent();
        //Add Updated data to Intent
        setResult(2, sendBack);
        BackgroundTaskAlertDialog(Constant.MODE_UPDATE);
    }

    /**
     * Focus on the view where error occur
     * @param view as Current View Reference
     */
    private void requestFocus(@NotNull View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void BackgroundTaskAlertDialog(final String check) {
        android.app.AlertDialog.Builder mAlertBuilder = new android.app.AlertDialog.Builder(EditorActivity.this);
        mAlertBuilder.setTitle(getString(R.string.main_choose_option));
        mAlertBuilder.setIcon(R.drawable.vector_list_view);
        mAlertBuilder.setSingleChoiceItems(mBackgroundItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int modeType) {
                switch (modeType) {
                    case Constant.ASYNC:
                        if (check.equals(Constant.MODE_NORMAL)) {
                            Toast.makeText(EditorActivity.this, getString(R.string.data_save_async), Toast.LENGTH_LONG).show();
                            taskAsync.execute(Constant.MODE_NORMAL, getIdEditText(), getNameEditText());
                            finish();
                        } else if (check.equals(Constant.MODE_UPDATE)) {
                            Toast.makeText(EditorActivity.this, getString(R.string.data_update_async), Toast.LENGTH_LONG).show();
                            taskAsync.execute(Constant.MODE_UPDATE, getIdEditText(), getNameEditText(), getIntent().getStringExtra(Constant.STUDENT_ID));
                            finish();
                        }
                        dialog.dismiss();
                        break;
                    case Constant.SERVICE:
                        Intent service = new Intent(EditorActivity.this, BackgroundTaskService.class);
                        String current = getIntent().getStringExtra(Constant.STUDENT_ID);
                        service.putExtra(Constant.MODE_TYPE, Constant.MODE_NORMAL);
                        service.putExtra(Constant.STUDENT_NAME, getNameEditText());
                        service.putExtra(Constant.STUDENT_ID, getIdEditText());
                        service.putExtra(Constant.CURRENT_ID, current);
                        startService(service);
                        dialog.dismiss();
                        break;
                    case Constant.INTENT_SERVICE:
                        Intent intent = new Intent(EditorActivity.this, IntentServiceBackground.class);
                        String position = getIntent().getStringExtra(Constant.STUDENT_ID);
                        intent.putExtra(Constant.MODE_TYPE, Constant.MODE_NORMAL);
                        intent.putExtra(Constant.STUDENT_NAME, getNameEditText());
                        intent.putExtra(Constant.STUDENT_ID, getIdEditText());
                        intent.putExtra(Constant.CURRENT_ID, position);
                        startService(intent);
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog alert = mAlertBuilder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        setReceiver();
        super.onStart();

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    private void setReceiver(){
        receiver=new ServiceCompleteBroadcast();
        IntentFilter filter=new IntentFilter(Constant.FILTER_ACTION_KEY);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
    }


    /**
     * This class will receive the broadcast message from Services
     * and Show the notification and Toast
     */
    private class ServiceCompleteBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            //Show Notification
            Toast.makeText(EditorActivity.this,"BroadCast:"+intent.getStringExtra(Constant.BROADCAST_MESSAGE),Toast.LENGTH_LONG).show();
            showNotificationMessage(intent.getStringExtra(Constant.BROADCAST_MESSAGE));
        }

        /**
         * Shows the Notification
         * @param message as Message from Services
         */
        private void showNotificationMessage(String message){
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EditorActivity.this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(message);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, mBuilder.build());
        }
    }
}
