package com.studentmanagement.activity;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studentmanagement.constant.Constant;
import com.studentmanagement.model.StudentInfo;
import com.studentmanagement.R;
import com.studentmanagement.validator.CustomTextWatcher;
import com.studentmanagement.validator.Validator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class EditorActivity extends AppCompatActivity {

    private Button btnSaveData;
    private EditText etName, etId;
    private TextInputLayout tiName, tiId;

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
    public void init(){
        tiName = findViewById(R.id.til_name);
        tiId = findViewById(R.id.til_roll);
        tiName.setHint(getString(R.string.til_hint_name));
        tiId.setHint(getString(R.string.til_hint_id));

        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.et_roll_number);

        //Add TextWatcher to EditText
        etName.addTextChangedListener(new CustomTextWatcher(etName));
        etId.addTextChangedListener(new CustomTextWatcher(etId));

        btnSaveData =findViewById(R.id.btn_save_data);
    }

    //Choose Mode
    public void chooseMode(){
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
        }
    }

    /**
     * When activity is opened in normal mode i.e Saves New Student Data
     * @param mStudentList as Current Student List
     */
    public void saveDataMode(ArrayList<StudentInfo> mStudentList) {
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

        Toast.makeText(EditorActivity.this, getString(R.string.toast_data_saved), Toast.LENGTH_LONG).show();
        sendBack.putExtra(Constant.UPDATED_ID, etId.getText().toString());
        sendBack.putExtra(Constant.UPDATED_NAME, etName.getText().toString());
        setResult(Constant.SAVE_RESULT_CODE, sendBack);
        finish();
    }

    /**
     * Populate the Edit Text Field in Case of Edit and View Mode
     * @param mName as Name Edit Text reference
     * @param mID as Id Edit Text reference
     * @param intent as Intent Object reference
     */
    public void fillData(EditText mName, EditText mID, Intent intent) {
        setTitle(R.string.editor_title_details);

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
        Toast.makeText(EditorActivity.this, getString(R.string.update_data_editor), Toast.LENGTH_LONG).show();

        //Add Updated data to Intent
        sendBack.putExtra(Constant.UPDATED_ID,getIdEditText());
        sendBack.putExtra(Constant.UPDATED_NAME,getNameEditText());
        setResult(2, sendBack);
        finish();
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

}
