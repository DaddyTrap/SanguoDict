package cn.org.sanguodict.sanguodict.model;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.util.SparseArray;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by DaddyTrapC on 2017/11/22.
 */

public class DataManager {
    private static DataManager _dataManager;

    private LinkedList<Moment> momentList;
    private LinkedList<User> userList;

    private DataManager() {
        momentList = new LinkedList<>();
        userList = new LinkedList<>();

        // TODO: Get data from SQLite here
    }

    public static DataManager getInstance() {
        if (_dataManager == null) _dataManager = new DataManager();
        return _dataManager;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.i("Info", "DataManager finalize");

        // TODO: Save data to SQLite here
    }

    public List<Moment> getMoments() {
        return momentList;
    }

    public List<User> getUsers() {
        return userList;
    }
}
