package com.studentmanagement.comparator;

import com.studentmanagement.model.StudentInfo;

import java.util.Comparator;

public class ComparatorName implements Comparator<StudentInfo> {
    //Validate Student Name
    @Override
    public int compare(StudentInfo s1, StudentInfo s2) {
         return s1.getName().compareTo(s2.getName());
    }
}
