package cn.org.sanguodict.sanguodict.model;

import android.content.ContentValues;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DaddyTrapC on 2017/11/22.
 */

public class User implements Serializable {
    public int userId;
    public String name;
    public String avatarBase64;
    public int gender; // 0 for unknown, 1 for male, 2 for female
    public String birthDate;
    public String deathDate;
    public String nativePlace;
    public String force;

    public User(int userId, String name, String avatarBase64, int gender, String birthDate, String deathDate, String nativePlace, String force) {
        this.userId = userId;
        this.name = name;
        this.avatarBase64 = avatarBase64;
        this.gender = gender;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.nativePlace = nativePlace;
        this.force = force;
    }

    public User() {
        this(-1, null, null, 0, null, null, null, null);
    }

    public ContentValues toContentValues() {
        ContentValues ret = new ContentValues();
        ret.put("userId", userId);
        ret.put("name", name);
        ret.put("avatarBase64", avatarBase64);
        ret.put("gender", gender);
        ret.put("birthDate", birthDate);
        ret.put("deathDate", deathDate);
        ret.put("nativePlace", nativePlace);
        ret.put("force", force);
        return ret;
    }
}
