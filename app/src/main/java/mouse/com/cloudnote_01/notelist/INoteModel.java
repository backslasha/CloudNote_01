package mouse.com.cloudnote_01.notelist;


import java.util.LinkedList;

import mouse.com.cloudnote_01.beans.Note;

interface INoteModel {

    void insertOrUpdateNote(Note note);

    void locatedNote(Note note);

    void deleteNote(int i);

    LinkedList<Note> getNotes();
}
