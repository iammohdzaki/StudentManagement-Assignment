package com.studentmanagement.validator;

import com.studentmanagement.constant.Constant;
import com.studentmanagement.model.StudentInfo;

import java.util.ArrayList;



public class Validator {

    /**
     * Validate Name
     * @param mName as Entered Student Name
     * @return true if Name is Valid else return false
     */
    public final static boolean validateName(String mName){
        if(mName.trim().isEmpty()){
            return false;
        }

        if(!mName.matches(Constant.NAME_REGEX)){
            return false;
        }
        return true;
    }

    /**
     * Validate Id
     * @param mID as Entered Student ID
     * @return true if ID is Unique else return false
     */
    public final static boolean validateId(String mID){
        if(mID.isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Validate Unique Id
     * @param mID as Entered Student ID
     * @param mStudentList as Current Student Details List
     * @return true if ID is Unique else return false
     */
    public final static boolean uniqueId(String mID, ArrayList<StudentInfo> mStudentList){
        for(StudentInfo s: mStudentList){
            if(mID.equals(s.getID())){
                return false;
            }
        }
        return true;
    }
}
