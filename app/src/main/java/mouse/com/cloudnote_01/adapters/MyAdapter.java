package mouse.com.cloudnote_01.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;

import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.beans.Note;


public class MyAdapter extends BaseAdapter {
    private LinkedList<Note> mNotes = new LinkedList<>();
    private Context mContext;

    public MyAdapter(Context context) {
        mContext = context;
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
        tv_time.setText(note.getNote_tiem());
        tv_summary.setText(note.getNote_content());
        tv_title.setText(note.getNote_title());
        return itemView;
    }

    /**
     * 传入note的id如果已经存在，则在mNotes中将已经存在的删除，再在顶部增加一个note，此时属于修改笔记
     * 否则直接在顶部增加一个note，此时属于新增笔记
     * @param note 需要增加或者修改的note
     */
    public void addNote(Note note) {
        for (Note n :mNotes) {
            if (n.equals(note)) {
                mNotes.remove(n);
                break;
            }
        }
        mNotes.addFirst(note);
    }
}
