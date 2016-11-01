package mouse.com.cloudnote_01.beans;

import java.io.Serializable;

public class Note implements Serializable {
    private String note_title;
    private String note_content;
    private String note_tiem;
    private int index = -1;

    public Note(String note_title, String note_content, String note_tiem, long note_id) {
        this.note_title = note_title;
        this.note_content = note_content;
        this.note_tiem = note_tiem;
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

    private long note_id;

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

    public String getNote_tiem() {
        return note_tiem;
    }

    public void setNote_time(String note_tiem) {
        this.note_tiem = note_tiem;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Note) obj).getNote_id() == note_id || super.equals(obj);
    }
}
