package mouse.com.cloudnote_01.notelist;

import android.widget.ListView;

import java.util.LinkedList;

import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.MyDatabaseHelper;

//((MyAdapter) listView.getAdapter()).deleteNote(i);
public interface INotePresenter {
    void loadNoteList();

    void insertOrUpdateNote(Note note);

    void locatedNote(Note note);

    void deleteNote(int i);

    void sycnNotesToBmob();void initListView(ListView listView);
}
