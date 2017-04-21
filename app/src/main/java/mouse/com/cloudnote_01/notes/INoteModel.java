package mouse.com.cloudnote_01.notes;


import java.util.LinkedList;
import java.util.List;

import mouse.com.cloudnote_01.beans.Note;

interface INoteModel {

    void insertOrUpdateNote(Note note);

    void hasSaved(Note note);

    void deleteNote(int i);

    LinkedList<Note> getNotes();

    void addNotes(List<Note> notes);
}
