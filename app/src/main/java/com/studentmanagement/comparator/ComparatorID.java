package com.studentmanagement.comparator;

import com.studentmanagement.model.StudentInfo;

import java.util.Comparator;

public class ComparatorID implements Comparator<StudentInfo> {
    //Validate Student ID
    @Override
    public int compare(StudentInfo s1, StudentInfo s2) {
        return Integer.parseInt(s1.getID()) - Integer.parseInt(s2.getID());
    }
}
