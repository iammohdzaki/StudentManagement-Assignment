package com.studentmanagement.backgroundTask;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.studentmanagement.R;
import com.studentmanagement.constant.Constant;
import com.studentmanagement.database.DBHelper;

public class BackgroundTaskService extends Service {

    public BackgroundTaskService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String method=intent.getStringExtra(Constant.MODE_TYPE);
        DBHelper dbHelper=new DBHelper(this);
        int id=Integer.parseInt(intent.getStringExtra(Constant.STUDENT_ID));
        String name=intent.getStringExtra(Constant.STUDENT_NAME);
        SQLiteDatabase db=dbHelper.getWritableDatabase();

        switch (method){
            case Constant.MODE_NORMAL:
                dbHelper.insertStudentData(db,id,name);
                db.close();
                break;
            case Constant.MODE_UPDATE:
                int current=Integer.parseInt(intent.getStringExtra(Constant.CURRENT_ID));
                dbHelper.updateStudentData(db,id,name,current);
                db.close();
                break;
            default:
                break;
        }

        intent.setAction(Constant.FILTER_ACTION_KEY);
        String echoMessage="Service Task Completed";
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent.putExtra(Constant.BROADCAST_MESSAGE, echoMessage));
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
