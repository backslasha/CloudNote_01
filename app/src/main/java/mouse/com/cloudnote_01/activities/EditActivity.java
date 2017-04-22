package mouse.com.cloudnote_01.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.WindowManager;

import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.fragments.EditFragment;


public class EditActivity extends SingleFragmentActivity {
    private static final String EXTRA_SINGLE_NOTE = "EXTRA_SINGLE_NOTE";
    private Note note;

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

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

