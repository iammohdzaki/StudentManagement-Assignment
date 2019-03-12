package com.studentmanagement.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentInfo implements Parcelable {

    private String mName,mID;

    /**
     * Constructor for storing Student Data
     * @param mName as Student Name
     * @param mID as Student Id
     */
    public StudentInfo(String mName,String mID){
        this.mID=mID;
        this.mName=mName;
    }

    /**
     * Constructor for Packaging data
     * @param in as Details for Packaging
     */
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

    /**
     * @return Student's Name
     */
    public String getName(){
        return mName;
    }

    /**
     * @return Student's Id
     */
    public String getID(){
        return mID;
    }

    /**
     * Set Student Name
     * @param mName as Student Name
     */
    public void setName(String mName){
        this.mName=mName;
    }

    /**
     * Set Student Id
     * @param mID as Student Id
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
