package com.studentmanagement.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.studentmanagement.constant.Constant;
import com.studentmanagement.database.DBHelper;
public class BackgroundTaskAsync extends AsyncTask<String,Void,String> {

    Context context;
    SQLiteDatabase db;
    public BackgroundTaskAsync(Context context){
        this.context=context;
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onProgressUpdate(Void... values) {

    }

    @Override
    protected String doInBackground(String... strings) {
        String method=strings[0];
        DBHelper dbHelper=new DBHelper(context);
        switch (method){
            case Constant.MODE_SAVE:
                int id=Integer.parseInt(strings[1]);
                String name=strings[2];
                db=dbHelper.getWritableDatabase();
                dbHelper.insertStudentData(db,id,name);
                db.close();
                break;
            case Constant.MODE_UPDATE:
                int sId=Integer.parseInt(strings[1]);
                String sName=strings[2];
                db=dbHelper.getWritableDatabase();
                int current=Integer.parseInt(strings[3]);
                dbHelper.updateStudentData(db,sId,sName,current);
                db.close();
                break;
            case Constant.MODE_DELETE:
                db=dbHelper.getWritableDatabase();
                int position=Integer.parseInt(strings[1]);
                Log.i("STRING","Row ID->"+strings[1]);
                dbHelper.deleteStudentData(db,position);
                db.close();
                break;
            case Constant.MODE_DELETE_ALL:
                db=dbHelper.getWritableDatabase();
                dbHelper.deleteAllStudentData(db);
                db.close();
                break;
            default:
                break;

        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}
