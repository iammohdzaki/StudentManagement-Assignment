package com.studentmanagement.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentInfo implements Parcelable {

    private String mName,mID;

    public StudentInfo() {
    }
    public StudentInfo(String mName,String mID){
        this.mID=mID;
        this.mName=mName;
    }

    protected StudentInfo(Parcel in) {
        mName = in.readString();
        mID = in.readString();
    }

    public static final Creator<StudentInfo> CREATOR = new Creator<StudentInfo>() {
        @Override
        public StudentInfo createFromParcel(Parcel in) {
            return new StudentInfo(in);
        }

        @Override
        public StudentInfo[] newArray(int size) {
            return new StudentInfo[size];
        }
    };

    public String getName(){
        return mName;
    }

    public String getID(){
        return mID;
    }

    public void setName(String mName){
        this.mName=mName;
    }

    public void setID(String mID){
        this.mID=mID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mID);
    }
}
