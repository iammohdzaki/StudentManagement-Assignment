package com.studentmanagement.Validator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.studentmanagement.R;

//Custom Text Watcher for Error Handling
public class CustomTextWatcher implements TextWatcher {
    private View view;

    public CustomTextWatcher(View view){
        this.view=view;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        switch (view.getId()){
            case R.id.et_name:
                break;
            case R.id.et_roll_number:
                break;
        }
    }
}