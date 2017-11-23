package cn.org.sanguodict.sanguodict;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.org.sanguodict.sanguodict.activity.MomentsActivity;
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
    private int currentUserId;

    // Used to transport large data -- Intent cannot save too large data
    private Object tempObj;

    private Map<String, Bitmap> bitmapCache;

    public SQLiteOpenHelper dbHelper;

    public boolean hasReadStoragePermission = false;

    public boolean _debugDontSave = false;

    @Override
    public void onCreate() {
        super.onCreate();
        momentList = new LinkedList<>();
        userList = new LinkedList<>();
        bitmapCache = new HashMap<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = preferences.getInt("currentUserId", 1);

        // Singleton
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
        Log.i("Info", "SGApplication terminate");
        saveEverything();
    }

    public List<Moment> getMoments() {
        return momentList;
    }

    public List<User> getUsers() {
        return userList;
    }

    public User findUserWithId(int userId) {
        User ret = null;
        for (User user : userList) {
            if (user.userId == userId) {
                ret = user;
                break;
            }
        }
        return ret;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int userId) {
        currentUserId = userId;
    }

    public User getCurrentUser() {
        return findUserWithId(currentUserId);
    }

    public Bitmap getBitmap(String strBase64) {
        if (bitmapCache.containsKey(strBase64)) return bitmapCache.get(strBase64);
        byte[] bytes = Base64.decode(strBase64, Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        bitmapCache.put(strBase64, bm);
        return bm;
    }

    public boolean requestPermission(Activity activity, String permissionString, int requestCode) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity, permissionString);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // No permission, request it
                ActivityCompat.requestPermissions(activity, new String[] {permissionString}, requestCode);
            } else {
                // Permission granted
                if (permissionString == MomentsActivity.READ_PERMISSION)
                    hasReadStoragePermission = true;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "Unknown Error");
            System.exit(0);
        }
        return false;
    }

    public void setTempObj(Object obj) {
        tempObj = obj;
    }

    public Object getTempObj() {
        return  tempObj;
    }

    public List<User> searchUserWithPartOfName(String partOfName) {
        List<User> ret = new LinkedList<>();
        for (User user : userList) {
            if (user.name.contains(partOfName))
                ret.add(user);
        }
        return ret;
    }

    public void addUser(User user) {
        user.userId = userList.getLast().userId + 1;
        userList.add(user);
    }

    public void addMoment(Moment moment) {
        moment.momentId = momentList.getLast().momentId + 1;
        momentList.add(moment);
    }

    public void saveEverything() {
        if (_debugDontSave) return;
        // Brutal save -- clear and then insert
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // Clear tables
        db.delete(DbHelper.USER_TABLE_NAME, null, null);
        db.delete(DbHelper.MOMENT_TABLE_NAME, null, null);

        // Insert users
        for (User user : userList) {
            db.insert(DbHelper.USER_TABLE_NAME, null, user.toContentValues());
        }
        // Insert moments
        for (Moment moment : momentList) {
            db.insert(DbHelper.MOMENT_TABLE_NAME, null, moment.toContentValues());
        }
    }
}
