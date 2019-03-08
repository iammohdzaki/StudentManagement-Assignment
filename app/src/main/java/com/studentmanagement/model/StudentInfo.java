package com.studentmanagement.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentInfo implements Parcelable {

    private String mName,mID;

    /*
     * @param mName for Student Name
     * @param mID for Student ID or Roll Number
     */
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

    /*
     * @return Student Name
     */
    public String getName(){
        return mName;
    }

    /*
     * @return Student ID
     */
    public String getID(){
        return mID;
    }

    /*
     * @param mName as Student Name
     */
    public void setName(String mName){
        this.mName=mName;
    }

    /*
     * @param mID as Student Name
     */
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
