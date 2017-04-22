package mouse.com.cloudnote_01.notes;


import android.content.Context;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.BmobHelper;
import mouse.com.cloudnote_01.utils.MyDatabaseHelper;

class NoteModel implements INoteModel {
    private LinkedList<Note> mNotes = new LinkedList<>();
    private Context mContext;

    MyDatabaseHelper getMyDatabaseHelper() {
        return myDatabaseHelper;
    }

    private MyDatabaseHelper myDatabaseHelper;

    NoteModel(Context context) {
        mContext = context;
        //打开数据库，把数据库中的数据查询出来，添加到mNotes
        myDatabaseHelper = new MyDatabaseHelper(mContext, "notes", null, 1);
        LinkedList<Note> datas = myDatabaseHelper.query();
        for (Note data : datas) {
            mNotes.add(data);
        }
    }


    /**
     * 传入note的id如果已经存在，则在mNotes中将已经存在的修改，此时属于修改笔记
     * 否则直接在顶部增加一个note，此时属于新增笔记
     *
     * @param note 需要增加或者修改的note
     */
    @Override
    public void insertOrUpdateNote(Note note) {
        if (mNotes.contains(note)) {
            //找到对应id的note时，1.对于mNotes，先删除原来的note，在把新的note添加到mNotes
            //2.对于数据库，使用update语句更新相应id字段的note
            mNotes.remove(note);
            mNotes.addFirst(note);
            myDatabaseHelper.update(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), note.getBmob_id(), note.getNeed_update_to_bmob());
        } else {
            mNotes.addFirst(note);
            myDatabaseHelper.insert(note);
        }
    }

    /**
     * 本地化一个note，将note的数据覆盖到 数据库 以及 mNotes
     *
     * @param note 带着最新信息的note
     */
    @Override
    public void hasSaved(Note note) {
        for (Note n : mNotes) {
            if (n.equals(note)) {
                //找到对应id的note时，1.对于mNotes，先删除原来的note，在把新的note添加到mNotes
                //2.对于数据库，使用update语句更新相应id字段的note
                mNotes.remove(n);
                mNotes.addFirst(note);
                myDatabaseHelper.update(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), note.getObjectId(), 0);
                return;
            }
        }
    }

    /**
     * 根据id字段删除数据库中id字段为i的索引
     *
     * @param i 索引的id字段
     */
    @Override
    public void deleteNote(int i) {
        myDatabaseHelper.delete(mNotes.get(i).getNote_id());
        mNotes.remove(i);
        Toast.makeText(mContext, "已删除.", Toast.LENGTH_SHORT).show();
        myDatabaseHelper.query();
    }


    @Override
    public LinkedList<Note> getNotes() {
        return mNotes;
    }

    @Override
    public void addNotes(List<Note> newNotes) {
        mNotes.addAll(newNotes);
        myDatabaseHelper.insert(newNotes);
    }
}
