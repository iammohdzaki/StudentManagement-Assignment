package com.studentmanagement.Validator;

import com.studentmanagement.Activity.MainActivity;
import com.studentmanagement.Model.StudentInfo;


public class Validator {


    //RegEx For Name
    private final static String NAME_REGEX="^[a-zA-Z\\s]+$";

    /*
     *@param mName as Entered Student Name
     *@return true if Name is Valid else return false
     */
    public final static boolean validateName(String mName){
        if(mName.trim().isEmpty()){
            return false;
        }

        if(!mName.matches(NAME_REGEX)){
            return false;
        }
        return true;
    }

    /*
     *@param mID as Entered Student ID
     *@return true if ID is Unique else return false
     */
    public final static boolean validateID(String mID){
        if(mID.isEmpty()){
            return false;
        }
        return true;
    }

    /*
     *@param mID as Entered Student ID
     *@return true if ID is Unique else return false
     */
    public final static boolean uniqueID(String mID){
        for(StudentInfo s: MainActivity.mStudentList){
            if(mID.equals(s.getID())){
                return false;
            }
        }
        return true;
    }
}
