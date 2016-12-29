package mouse.com.cloudnote_01.beans;

import java.io.Serializable;

public class Note implements Serializable {
    private String note_title;
    private String note_content;
    private String note_time;


    private long note_id;
    private Object obj;

    public Note(String note_title, String note_content, String note_time, long note_id) {
        this.note_title = note_title;
        this.note_content = note_content;
        this.note_time = note_time;
        this.note_id = note_id;
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Note&&((Note) obj).getNote_id() == note_id;
    }
}
