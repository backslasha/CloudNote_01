package mouse.com.cloudnote_01.beans;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Note extends BmobObject implements Serializable {
    private String note_title;
    private String note_content;
    private String note_time;

    public String getBmob_id() {
        return bmob_id;
    }

    public void setBmob_id(String bmob_id) {
        this.bmob_id = bmob_id;
    }

    private String bmob_id;


    private long note_id;

    public Note(String note_title, String note_content, String note_time, long note_id, String bmob_id) {
        this.note_title = note_title;
        this.note_content = note_content;
        this.note_time = note_time;
        this.note_id = note_id;
        this.bmob_id = bmob_id;

    }

    public Note() {

    }

    public long getNote_id() {
        return note_id;
    }

    public void setNote_id(long note_id) {
        this.note_id = note_id;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_content() {
        return note_content;
    }

    public void setNote_content(String note_content) {
        this.note_content = note_content;
    }

    public String getNote_time() {
        return note_time;
    }

    public void setNote_time(String note_item) {
        this.note_time = note_item;
    }

    /**
     * 判断两个Note是否相等的唯一依据就是，它们的id是否相等
     *
     * @param obj obj
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Note && ((Note) obj).getNote_id() == note_id;
    }
}
