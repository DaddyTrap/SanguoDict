package cn.org.sanguodict.sanguodict.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DaddyTrapC on 2017/11/22.
 */

public class Moment implements Serializable {
    public int momentId;
    public int fromUser;
    public String date;
    public String location;
    public String contentText;
    public String contentImgBase64;

    public Moment(int momentId, int fromUser, String date, String location, String contentText, String contentImgBase64) {
        this.momentId = momentId;
        this.fromUser = fromUser;
        this.date = date;
        this.location = location;
        this.contentText = contentText;
        this.contentImgBase64 = contentImgBase64;
    }

}
