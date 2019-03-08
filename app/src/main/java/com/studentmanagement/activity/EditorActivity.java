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

import com.studentmanagement.model.StudentInfo;
import com.studentmanagement.R;
import com.studentmanagement.validator.CustomTextWatcher;
import com.studentmanagement.validator.Validator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class EditorActivity extends AppCompatActivity {

    private final static String MODETYPE = "MODE_TYPE";
    private Button mSaveButton;
    private EditText mNameInfo, mIdInfo;
    private TextInputLayout nameTextInput, rollTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        nameTextInput = findViewById(R.id.til_name);
        rollTextInput = findViewById(R.id.til_roll);
        nameTextInput.setHint("Name");
        rollTextInput.setHint("Roll Number");

        mNameInfo = findViewById(R.id.et_name);
        mIdInfo = findViewById(R.id.et_roll_number);

        //Add TextWatcher to EditText
        mNameInfo.addTextChangedListener(new CustomTextWatcher(mNameInfo));
        mIdInfo.addTextChangedListener(new CustomTextWatcher(mIdInfo));

        mSaveButton = (Button) findViewById(R.id.btn_save_data);

        final Intent intent = getIntent();
        final ArrayList<StudentInfo> mStudentList=intent.getParcelableArrayListExtra("STUDENT_LIST");

        final String id=intent.getStringExtra("STUDENT_ID");
        setTitle(R.string.activity_title_add);

        if (intent.getStringExtra(MODETYPE).equals("NORMAL")) {
            setTitle(R.string.activity_title_add);
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDataMode(mStudentList);
                }
            });
        } else if (intent.getStringExtra(MODETYPE).equals("UPDATE")) {
            mSaveButton.setText(getString(R.string.btn_update));
            setTitle(R.string.activity_title_update);
            fillData(mNameInfo, mIdInfo, intent);
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateMode(id,mStudentList);
                }
            });
        } else if (intent.getStringExtra(MODETYPE).equals("VIEW")) {
            viewMode(mNameInfo, mIdInfo);
            fillData(mNameInfo, mIdInfo, intent);
        }

    }

    // When activity is opened in normal mode i.e Saves New Student Data
    public void saveDataMode(ArrayList<StudentInfo> mStudentList) {
        Intent sendBack = new Intent();
        //Validate Name i.e No special Character and numbers
        if (!Validator.validateName(getNameEditText())) {
            nameTextInput.setError(getString(R.string.err_msg_name));
            requestFocus(nameTextInput);
            return;
        }
        //Validate ID or Roll Number
        if (!Validator.validateId(getIdEditText())) {
            rollTextInput.setError(getString(R.string.err_msg_id));
            requestFocus(mIdInfo);
            return;
        }

        //Validate Unique ID
        if (!Validator.uniqueId(getIdEditText(),mStudentList)) {
            rollTextInput.setError(getString(R.string.unique_id_err));
            requestFocus(mIdInfo);
            return;
        }

        nameTextInput.setErrorEnabled(false);
        rollTextInput.setErrorEnabled(false);

        Toast.makeText(EditorActivity.this, "Data Saved", Toast.LENGTH_LONG).show();
        sendBack.putExtra("ID", mIdInfo.getText().toString());
        sendBack.putExtra("NAME", mNameInfo.getText().toString());
        setResult(1, sendBack);
        finish();
    }

    // Populate the Edit Text Field in Case of Edit and View Mode
    public void fillData(EditText mName, EditText mID, Intent intent) {
        setTitle("Student Details");

        mName.setText(intent.getStringExtra("STUDENT_NAME"));
        mID.setText(intent.getStringExtra("STUDENT_ID"));
    }

    // Show Data in View Mode
    public void viewMode(EditText mName, EditText mID) {
        mName.setEnabled(false);
        mID.setEnabled(false);
        mID.setFocusable(false);
        mName.setFocusable(false);
        mSaveButton.setVisibility(View.INVISIBLE);
    }


    @NotNull
    private String getNameEditText(){
        return mNameInfo.getText().toString().trim();
    }

    @NotNull
    private String getIdEditText(){
        return mIdInfo.getText().toString().trim();
    }
    //When the activity opened in UPDATE or EDIT MODE
    public void updateMode(String id,ArrayList<StudentInfo> mStudentList) {

        //Validate Name i.e No special Character and numbers
        if (!Validator.validateName(getNameEditText())) {
            nameTextInput.setError(getString(R.string.err_msg_name));
            requestFocus(nameTextInput);
            return;
        }
        //Validate ID or Roll Number
        if (!Validator.validateId(getIdEditText())) {
            rollTextInput.setError(getString(R.string.err_msg_id));
            requestFocus(mIdInfo);
            return;
        }

        /*
        if (id.equals(mIdInfo.getText().toString())) {

        } else {
            if (!Validator.uniqueId(getIdEditText(),mStudentList)) {
                rollTextInput.setError(getString(R.string.unique_id_err));
                requestFocus(mIdInfo);
                return;
            }
        }*/

        nameTextInput.setErrorEnabled(false);
        rollTextInput.setErrorEnabled(false);

        Intent sendBack =new Intent();
        Toast.makeText(EditorActivity.this, "Data Updated", Toast.LENGTH_LONG).show();

        //Add Updated data to Intent
        sendBack.putExtra("ID",getIdEditText());
        sendBack.putExtra("NAME",getNameEditText());
        setResult(2, sendBack);
        finish();
    }

    //Focus on the view where error occur
    private void requestFocus(@NotNull View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}
