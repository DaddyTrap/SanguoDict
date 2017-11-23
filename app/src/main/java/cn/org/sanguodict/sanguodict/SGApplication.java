package cn.org.sanguodict.sanguodict;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import cn.org.sanguodict.sanguodict.model.DbHelper;
import cn.org.sanguodict.sanguodict.model.Moment;
import cn.org.sanguodict.sanguodict.model.User;

/**
 * Created by DaddyTrapC on 2017/11/22.
 */

public class SGApplication extends Application {
    private static SGApplication _SGApplication;

    private LinkedList<Moment> momentList;
    private LinkedList<User> userList;

    public SQLiteOpenHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        momentList = new LinkedList<>();
        userList = new LinkedList<>();

        _SGApplication = this;

        Log.i("Info", "Getting helper");
        dbHelper = new DbHelper(this);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // User Table
        Cursor cursor = db.query(
                DbHelper.USER_TABLE_NAME,
                new String[] {"userId", "name", "avatarBase64", "gender", "birthDate", "deathDate", "nativePlace", "force"},
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                User user = new User(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        (char) cursor.getInt(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );
                userList.addLast(user);
            } while (cursor.moveToNext());
        }

        // Moment Table
        cursor = db.query(
                DbHelper.MOMENT_TABLE_NAME,
                new String[] {"momentId", "fromUser", "time", "location", "contentText", "contentImgBase64"},
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                Moment moment = new Moment(
                        cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5)
                );
                Log.i("Info", "momentId: " + moment.momentId);
                momentList.addLast(moment);
            } while (cursor.moveToNext());
        }
    }

    public static SGApplication getInstance() {
        return _SGApplication;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.i("Info", "SGApplication finalize");

        // TODO: Save data to SQLite here
    }

    public List<Moment> getMoments() {
        return momentList;
    }

    public List<User> getUsers() {
        return userList;
    }
}
