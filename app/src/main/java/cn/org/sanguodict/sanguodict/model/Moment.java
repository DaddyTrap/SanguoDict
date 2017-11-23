package cn.org.sanguodict.sanguodict.model;

import java.io.Serializable;

/**
 * Created by DaddyTrapC on 2017/11/22.
 */

public class Moment implements Serializable {
    public int momentId;
    public int fromUser;
    public String time;
    public String location;
    public String contentText;
    public String contentImgBase64;

    public Moment(int momentId, int fromUser, String time, String location, String contentText, String contentImgBase64) {
        this.momentId = momentId;
        this.fromUser = fromUser;
        this.time = time;
        this.location = location;
        this.contentText = contentText;
        this.contentImgBase64 = contentImgBase64;
    }

    public Moment() {
        this(-1, -1, null, null, null, null);
    }
}
