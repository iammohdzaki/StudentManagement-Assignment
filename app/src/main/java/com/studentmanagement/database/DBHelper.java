package com.studentmanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.studentmanagement.database.StudentContract.StudentEntry;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "student.db";
    public static final int DATABASE_VERSION = 1;
    private int position;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STUDENT_TABLE = "CREATE TABLE " + StudentEntry.TABLE_NAME + "("
                + StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StudentEntry.COLUMN_ID + " INTEGER NOT NULL,"
                + StudentEntry.COLUMN_NAME + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Saves Data to DB
     * @param db as DataBase Object
     * @param id as Student ID
     * @param name as Student Name
     */
    public void insertStudentData(SQLiteDatabase db, int id, String name){
        ContentValues values=new ContentValues();
        values.put(StudentEntry.COLUMN_NAME,name);
        values.put(StudentEntry.COLUMN_ID,id);

        long pos=db.insert(StudentEntry.TABLE_NAME,null,values);
        Log.i("DATA","DATA ADDED at :"+pos);
        db.close();
    }

    /**
     * Get Student Data
     * @param db as Database Object
     * @return cursor of DB
     */
    public Cursor getStudentData(SQLiteDatabase db){
        String[] projection={
                StudentEntry.COLUMN_ID,
                StudentEntry.COLUMN_NAME
        };

        Cursor cursor= db.query(
                StudentEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }

    /**
     * Update Student Data
     * @param db as DB Object
     * @param id as Student Id
     * @param name as Student Name
     * @param currentPosition as Current Row to be Updated
     */
    public void updateStudentData(SQLiteDatabase db,int id,String name,int currentPosition){
        ContentValues values=new ContentValues();
        values.put(StudentEntry.COLUMN_ID,id);
        values.put(StudentEntry.COLUMN_NAME,name);

        String selection=StudentEntry.COLUMN_ID +" LIKE ?";
        String[] selectionArgs={String.valueOf(currentPosition)};

        int count=db.update(StudentEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        Log.i("UPDATE","ROW UPDATED:"+count);
        db.close();
    }

    public void deleteStudentData(SQLiteDatabase db,int position){
        String selection= StudentEntry.COLUMN_ID + " LIKE ? ";
        String[] selectionArgs={String.valueOf(position)};

        int deletedRows=db.delete(StudentEntry.TABLE_NAME,selection,selectionArgs);
        Log.i("DELETE","ROW DELETED:"+deletedRows);
        db.close();
    }


    public void deleteAllStudentData(SQLiteDatabase db){
        db.delete(StudentEntry.TABLE_NAME,null,null);
        db.close();
    }
}
