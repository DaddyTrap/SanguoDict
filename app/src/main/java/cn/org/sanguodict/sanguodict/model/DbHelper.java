package cn.org.sanguodict.sanguodict.model;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cn.org.sanguodict.sanguodict.R;

/**
 * Created by DaddyTrapC on 2017/11/22.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Data0.db";

    public static final String MOMENT_TABLE_NAME = "Moment";
    public static final String USER_TABLE_NAME = "User";

    private Context mContext;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("Info", "In DbHelper Constructor");
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Info", "DbHelper onCreate");
        // Read init sql
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.init_sqlite);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        StringBuilder builder = new StringBuilder();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Execute the sql
        try {
            Log.i("Info", "Create table");
            String[] sqls = builder.toString().split(";");
            for (String sql : sqls) {
                Log.i("Info", sql);
                db.execSQL(sql);
            }
        } catch (SQLException e) {
            Log.i("Info", "Maybe data existed.");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Info", "In onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + MOMENT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }
}
