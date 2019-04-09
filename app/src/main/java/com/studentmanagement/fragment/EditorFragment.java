package com.studentmanagement.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.studentmanagement.communicator.Communicator;
import com.studentmanagement.R;
import com.studentmanagement.activity.MainActivity;
import com.studentmanagement.services.BackgroundTaskAsync;
import com.studentmanagement.services.BackgroundTaskService;
import com.studentmanagement.services.IntentServiceBackground;
import com.studentmanagement.constant.Constant;
import com.studentmanagement.database.DBHelper;
import com.studentmanagement.database.StudentContract;
import com.studentmanagement.model.StudentInfo;
import com.studentmanagement.validator.CustomTextWatcher;
import com.studentmanagement.validator.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EditorFragment extends Fragment {


    private Button btnSaveData;
    private EditText etName, etId;
    private TextInputLayout tiName, tiId;
    private BackgroundTaskAsync taskAsync;
    private String[] mBackgroundItems;
    private Communicator mCommunicator;
    private String modeType;
    private String current;
    private String currentIndex;
    private Context mContext;
    private ArrayList<StudentInfo> studentArrayList;

    @Override
    public void onAttach(Context context) {
        this.mContext=context;
        try {
            mCommunicator=(Communicator) mContext;
        }catch (ClassCastException e) {
            throw new ClassCastException(getString(R.string.error_string_editor_fragment));
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_editor, container, false);
        //Initialize Resources
        init(rootView);
        return rootView;
    }

    /**
     * Initialize Resources
     * @param rootView as View Object
     */
    private void init(View rootView){
        tiName = rootView.findViewById(R.id.til_name);
        tiId = rootView.findViewById(R.id.til_roll);
        tiName.setHint(getString(R.string.til_hint_name));
        tiId.setHint(getString(R.string.til_hint_id));

        mBackgroundItems = getResources().getStringArray(R.array.background_task_array);

        etName = rootView.findViewById(R.id.et_name);
        etId = rootView.findViewById(R.id.et_roll_number);

        //Add TextWatcher to EditText
        etName.addTextChangedListener(new CustomTextWatcher(etName));
        etId.addTextChangedListener(new CustomTextWatcher(etId));

        btnSaveData = rootView.findViewById(R.id.btn_save_data);

        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                if(Constant.MODE_UPDATE.equals(modeType)){
                    updateMode();
                }else{
                    saveMode(bundle);
                }
            }
        });
    }

    /**
     * Save Student Data
     * @param bundle as Bundle from MainFragment
     */
    private void saveMode(Bundle bundle){

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
        if (!Validator.uniqueId(getIdEditText(),getStudentList())) {
            tiId.setError(getString(R.string.unique_id_err));
            requestFocus(etId);
            return;
        }

        tiName.setErrorEnabled(false);
        tiId.setErrorEnabled(false);
        bundle.putString(Constant.STUDENT_ID,getIdEditText());
        bundle.putString(Constant.STUDENT_NAME,getNameEditText());
        mCommunicator.addStudent(bundle);
        BackgroundTaskAlertDialog(Constant.MODE_SAVE);
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

    private void updateMode(){
        Bundle bundle=new Bundle();
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
        if(getCurrent().equals(getIdEditText())){
        }else{
            if (!Validator.uniqueId(getIdEditText(),getStudentList())) {
                tiId.setError(getString(R.string.unique_id_err));
                requestFocus(etId);
                return;
            }
        }

        tiName.setErrorEnabled(false);
        tiId.setErrorEnabled(false);
        bundle.putString(Constant.CURRENT_POSITION,getCurrentIndex());
        bundle.putString(Constant.STUDENT_ID,getIdEditText());
        bundle.putString(Constant.STUDENT_NAME,getNameEditText());
        mCommunicator.updateStudentList(bundle);
        BackgroundTaskAlertDialog(Constant.MODE_UPDATE);
    }

    /**
     * Sets Student Data to Edit Text
     * @param bundleData as Bundle
     */
    public void updateStudentData(Bundle bundleData) {
        modeType=bundleData.getString(Constant.MODE_TYPE);
        currentIndex=bundleData.getString(Constant.CURRENT_POSITION);
        etId.setText(bundleData.getString(Constant.STUDENT_ID));
        etName.setText(bundleData.getString(Constant.STUDENT_NAME));
        current=bundleData.getString(Constant.STUDENT_ID);
        setCurrent(current);
        setCurrentIndex(currentIndex);
    }

    public String getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(String currentIndex) {
        this.currentIndex = currentIndex;
    }

    /**
     * Student ID List
     * @return ArrayList of Student ID
     */
    public ArrayList<StudentInfo> getStudentList(){
       studentArrayList=new ArrayList<>();
       DBHelper dbhelper=new DBHelper(mContext);
       SQLiteDatabase db=dbhelper.getReadableDatabase();
       Cursor cursor=dbhelper.getStudentData(db);
        try {
            int idColumnIndex = cursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_ID);
            int nameColumnIndex = cursor.getColumnIndex(StudentContract.StudentEntry.COLUMN_NAME);

            while (cursor.moveToNext()) {
                String currentID = cursor.getString(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                studentArrayList.add(new StudentInfo(currentName, currentID));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
            db.close();
        }
       return studentArrayList;
    }

    /**
     * Set Current ID of Student for Update
     * @param current as Current Id
     */
    public void setCurrent(String current){
        this.current=current;
    }

    /**
     * @return Current Student Id
     */
    public String getCurrent(){
        return current;
    }

    /**
     * Focus on the view where error occur
     * @param view as Current View Reference
     */
    private void requestFocus(@NotNull View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    /**
     * @param check as Current Service To be Used
     */
    private void BackgroundTaskAlertDialog(final String check) {
        android.app.AlertDialog.Builder mAlertBuilder = new android.app.AlertDialog.Builder(mContext);
        mAlertBuilder.setTitle(getString(R.string.main_choose_option));
        mAlertBuilder.setIcon(R.drawable.vector_list_view);
        mAlertBuilder.setSingleChoiceItems(mBackgroundItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int modeType) {
                switch (modeType) {

                    case Constant.ASYNC:
                        asyncMode(check);
                        dialog.dismiss();
                        ((MainActivity)mContext).changeTab();
                        break;

                    case Constant.SERVICE:
                        serviceMode();
                        dialog.dismiss();
                        ((MainActivity)mContext).changeTab();
                        break;
                    case Constant.INTENT_SERVICE:
                        intentServiceMode();
                        dialog.dismiss();
                        ((MainActivity)mContext).changeTab();
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog alert = mAlertBuilder.create();
        alert.show();
    }

    /**
     * When User picks Async to Perform Operations
     * @param check as Current Mode
     */
    private void asyncMode(String check){
        taskAsync = new BackgroundTaskAsync(getActivity());
        if (check.equals(Constant.MODE_SAVE)) {
            Toast.makeText(mContext, getString(R.string.data_save_async), Toast.LENGTH_LONG).show();
            taskAsync.execute(Constant.MODE_SAVE, getIdEditText(), getNameEditText());
        } else if (check.equals(Constant.MODE_UPDATE)) {
            Toast.makeText(mContext, getString(R.string.data_update_async), Toast.LENGTH_LONG).show();
            taskAsync.execute(Constant.MODE_UPDATE, getIdEditText(), getNameEditText(),getCurrent());
        }
        clearFields();
    }

    //When User picks Service to perform Operations
    private void serviceMode(){
        Intent service = new Intent(mContext, BackgroundTaskService.class);
        Toast.makeText(mContext, "Service Started..", Toast.LENGTH_LONG).show();
        service.putExtra(Constant.MODE_TYPE, Constant.MODE_SAVE);
        service.putExtra(Constant.STUDENT_NAME, getNameEditText());
        service.putExtra(Constant.STUDENT_ID, getIdEditText());
        service.putExtra(Constant.CURRENT_ID, getCurrent());
        mContext.startService(service);
        clearFields();
    }

    //When User picks Intent Service to perform Operations
    private void intentServiceMode(){
        Intent intent = new Intent(mContext, IntentServiceBackground.class);
        Toast.makeText(mContext, getString(R.string.data_save_intent_service), Toast.LENGTH_LONG).show();
        intent.putExtra(Constant.MODE_TYPE, Constant.MODE_SAVE);
        intent.putExtra(Constant.STUDENT_NAME, getNameEditText());
        intent.putExtra(Constant.STUDENT_ID, getIdEditText());
        intent.putExtra(Constant.CURRENT_ID, getCurrent());
        mContext.startService(intent);
        clearFields();
    }

    //Clear fields after Update
    private void clearFields(){
        etId.setText("");
        etName.setText("");
    }

    /**
     * When fragment is reused for View Mode
     * @param bundleData as Bundle
     */
    public void viewMode(Bundle bundleData){
        Log.i("TESTING","NAME:"+bundleData);
        etId.setText(bundleData.getString(Constant.STUDENT_ID));
        etName.setText(bundleData.getString(Constant.STUDENT_NAME));
        btnSaveData.setVisibility(View.GONE);
        etId.setEnabled(false);
        etName.setEnabled(false);
    }
}
