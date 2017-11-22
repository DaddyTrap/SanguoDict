package cn.org.sanguodict.sanguodict.model;

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
    public char gender; // 0 for unknown, 1 for male, 2 for female
    public String birthDate;
    public String deathDate;
    public String nativePlace;
    public String force;

    public User(int userId, String name, String avatarBase64, char gender, String birthDate, String deathDate, String nativePlace, String force) {
        this.userId = userId;
        this.name = name;
        this.avatarBase64 = avatarBase64;
        this.gender = gender;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.nativePlace = nativePlace;
        this.force = force;
    }
}
