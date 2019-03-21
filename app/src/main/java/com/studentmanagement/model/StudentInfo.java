package com.studentmanagement.model;

public class StudentInfo {

    private String mName,mID;

    public StudentInfo() {
    }

    public StudentInfo(String mName,String mID){
        this.mID=mID;
        this.mName=mName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }
}
