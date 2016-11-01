package mouse.com.cloudnote_01.frags;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import mouse.com.cloudnote_01.R;


public class EmptyFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_edit, container, false);
        return v;
    }

    public View getAddNoteView(View view) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_note, null);
        TextView tv_title = (TextView) v.findViewById(R.id.id_tv_note_title);
        TextView tv_summary = (TextView) v.findViewById(R.id.id_tv_note_summary);
        TextView tv_time = (TextView) v.findViewById(R.id.id_tv_note_time);

        tv_time.setText(new Date().toString());
        tv_summary.setText("new Text Summary");
        tv_title.setText("new Title");
        return v;
    }
}
