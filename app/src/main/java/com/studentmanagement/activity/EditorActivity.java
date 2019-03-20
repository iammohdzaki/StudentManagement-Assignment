package com.studentmanagement.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.studentmanagement.R;
import com.studentmanagement.constant.Constant;

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
    private void init() {
        tiName = findViewById(R.id.til_name);
        tiId = findViewById(R.id.til_roll);
        tiName.setHint(getString(R.string.til_hint_name));
        tiId.setHint(getString(R.string.til_hint_id));

        etName = findViewById(R.id.et_name);
        etId = findViewById(R.id.et_roll_number);

        btnSaveData = findViewById(R.id.btn_save_data);
    }

    //Choose Mode
    private void chooseMode() {
        final Intent intent = getIntent();
        switch (intent.getStringExtra(Constant.MODE_TYPE)) {
            case Constant.MODE_VIEW:
                viewMode(etName, etId);
                fillData(etName, etId, intent);
                break;
            default:
                break;
        }
    }

    /**
     * Populate the Edit Text Field in Case of Edit and View Mode
     *
     * @param mName  as Name Edit Text reference
     * @param mID    as Id Edit Text reference
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
     *
     * @param mName as Name Edit Text reference
     * @param mID   as Id Edit Text reference
     */
    public void viewMode(EditText mName, EditText mID) {
        mName.setEnabled(false);
        mID.setEnabled(false);
        mID.setFocusable(false);
        mName.setFocusable(false);
        btnSaveData.setVisibility(View.INVISIBLE);
    }

}
