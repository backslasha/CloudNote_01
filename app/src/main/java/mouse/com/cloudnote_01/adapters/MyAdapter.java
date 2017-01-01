package mouse.com.cloudnote_01.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.BmobHelper;
import mouse.com.cloudnote_01.utils.MyDatabaseHelper;


public class MyAdapter extends BaseAdapter {
    private LinkedList<Note> mNotes = new LinkedList<>();
    private Context mContext;

    public MyDatabaseHelper getMyDatabaseHelper() {
        return myDatabaseHelper;
    }

    private MyDatabaseHelper myDatabaseHelper;

    public MyAdapter(Context context) {
        mContext = context;
        //打开数据库，把数据库中的数据查询出来，添加到mNotes
        myDatabaseHelper = new MyDatabaseHelper(mContext, "notes", null, 1);
        LinkedList<Note> datas = myDatabaseHelper.query();
        for (Note data : datas) {
            mNotes.add(data);
        }
    }

    @Override
    public int getCount() {
        return mNotes.size();
    }

    @Override
    public Object getItem(int i) {
        return mNotes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mNotes.get(i).getNote_id();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_note, null);
        TextView tv_title = (TextView) itemView.findViewById(R.id.id_tv_note_title);
        TextView tv_summary = (TextView) itemView.findViewById(R.id.id_tv_note_summary);
        TextView tv_time = (TextView) itemView.findViewById(R.id.id_tv_note_time);

        Note note = mNotes.get(i);
        tv_time.setText(note.getNote_time());
        tv_summary.setText(note.getNote_content());
        tv_title.setText(note.getNote_title());
        return itemView;
    }

    /**
     * 传入note的id如果已经存在，则在mNotes中将已经存在的修改，此时属于修改笔记
     * 否则直接在顶部增加一个note，此时属于新增笔记
     * @param note 需要增加或者修改的note
     */
    public void insertOrUpdateNote(Note note) {
        for (Note n : mNotes) {
            if (n.equals(note)) {
                //找到对应id的note时，1.对于mNotes，先删除原来的note，在把新的note添加到mNotes
                //2.对于数据库，使用update语句更新相应id字段的note
                mNotes.remove(n);
                mNotes.addFirst(note);
                myDatabaseHelper.update(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), note.getBmob_id(),BmobHelper.NEED_UPDATE_TO_BMOB);
                notifyDataSetChanged();
                myDatabaseHelper.query();
                return;
            }
        }
        //新增节点note到mNotes中并且insert到数据库中
        mNotes.addFirst(note);
        notifyDataSetChanged();
        myDatabaseHelper.insert(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), BmobHelper.EMPTY_BMOB_ID,BmobHelper.NEED_NOT_UPDATE_TO_BMOB);
        myDatabaseHelper.query();
    }

    /**
     * 本地化一个note，将note的数据覆盖到 数据库 以及 mNotes
     * @param note 带着最新信息的note
     */
    public void locatedNote(Note note) {
        for (Note n : mNotes) {
            if (n.equals(note)) {
                //找到对应id的note时，1.对于mNotes，先删除原来的note，在把新的note添加到mNotes
                //2.对于数据库，使用update语句更新相应id字段的note
                mNotes.remove(n);
                mNotes.addFirst(note);
                myDatabaseHelper.update(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), note.getBmob_id(),note.getNeed_update_to_bmob());
                notifyDataSetChanged();
                myDatabaseHelper.query();
                return;
            }
        }
        //新增节点note到mNotes中并且insert到数据库中
        mNotes.addFirst(note);
        notifyDataSetChanged();
        myDatabaseHelper.insert(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), BmobHelper.EMPTY_BMOB_ID,BmobHelper.NEED_NOT_UPDATE_TO_BMOB);
        myDatabaseHelper.query();
    }

    /**
     * 根据id字段删除数据库中id字段为i的索引
     * @param i 索引的id字段
     */
    public void deleteNote(int i) {
        myDatabaseHelper.delete(mNotes.get(i).getNote_id());
        mNotes.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mContext, "已删除.", Toast.LENGTH_SHORT).show();
    }


}
