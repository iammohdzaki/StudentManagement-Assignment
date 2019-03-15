package com.studentmanagement.database;

import android.provider.BaseColumns;

public class StudentContract {

    private StudentContract(){}

    public static final class StudentEntry implements BaseColumns{

        public static final String TABLE_NAME="studentInfo";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME="name";
        public final static String COLUMN_ID="id";
    }
}
