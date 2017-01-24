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

    public MyAdapter(Context context, LinkedList<Note> notes) {
        mContext = context;
        mNotes = notes;
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


}
