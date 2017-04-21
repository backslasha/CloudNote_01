package mouse.com.cloudnote_01.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.beans.Note;

public class NotesAdapter extends RecyclerView.Adapter<NoteHolder> {
    private List<Note> mNotes;
    private Context mContext;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public NotesAdapter(Context context, LinkedList<Note> notes) {
        mContext = context;
        mNotes = notes;
    }

    @Override
    public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_note, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteHolder holder, int position) {
        final int p = position;
        Note note = mNotes.get(position);
        holder.bindView(note);
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(p);
                }
            });
        }
        if (mOnItemLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(p);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}

class NoteHolder extends ViewHolder {
    private Note mNote;


    public NoteHolder(View itemView) {
        super(itemView);
    }

    public void bindView(Note note) {
        mNote = note;
        TextView tv_title = (TextView) itemView.findViewById(R.id.id_tv_note_title);
        TextView tv_summary = (TextView) itemView.findViewById(R.id.id_tv_note_summary);
        TextView tv_time = (TextView) itemView.findViewById(R.id.id_tv_note_time);
        tv_time.setText(note.getNote_time());
        tv_summary.setText(note.getNote_content());
        tv_title.setText(note.getNote_title());
    }
}