package mouse.com.cloudnote_01.notes;

import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

import mouse.com.cloudnote_01.beans.Note;

//((MyAdapter) listView.getAdapter()).deleteNote(i);
public interface INotePresenter {
    void loadNoteList();

    void insertOrUpdateNoteToDb(Note note);

    void deleteNote(int i);

    void sycnNotesBetweenCloud();

    void sycnNotesToLocal();

    LinkedList<Note> getNotes();
}
