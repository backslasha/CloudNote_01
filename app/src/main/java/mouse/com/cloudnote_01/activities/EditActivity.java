package mouse.com.cloudnote_01.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.fragments.EditFragment;


public class EditActivity extends SingleFragmentActivity {
    private static final String EXTRA_SINGLE_NOTE = "EXTRA_SINGLE_NOTE";
    private Note note;


    public static Intent newIntent(Context context, Note note) {
        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra(EXTRA_SINGLE_NOTE, note);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        note = (Note) getIntent().getSerializableExtra(EXTRA_SINGLE_NOTE);
        return EditFragment.newInstance(note);
    }

}

