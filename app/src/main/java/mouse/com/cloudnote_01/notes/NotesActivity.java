package mouse.com.cloudnote_01.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;

import cn.bmob.v3.Bmob;
import mouse.com.cloudnote_01.activities.BaseActivity;
import mouse.com.cloudnote_01.activities.EditActivity;
import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.adapters.NotesAdapter;
import mouse.com.cloudnote_01.beans.Note;


public class NotesActivity extends BaseActivity implements View.OnClickListener, INoteView {
    private RecyclerView mRecyclerView;
    private NotesAdapter mNotesAdapter;
    private Button mRefreshButton;

    private NotePresenter mNotePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recycler_view);
        mRefreshButton = (Button) findViewById(R.id.id_btn_flush);

        mNotePresenter = new NotePresenter(this, this);

        initRecyclerView(mRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.id_fab_start_new_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewEdit();
            }
        });

        mRefreshButton.setOnClickListener(this);

        //①.默认初始化
        Bmob.initialize(this, "a4f2c00bbb157465b1b7d5ba19851421");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra("note");
                mNotePresenter.insertOrUpdateNoteToDb(note);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_flush:
                mNotePresenter.sycnNotesBetweenCloud();
                break;

        }
    }

    @Override
    public void startNewEdit() {
        Intent intent = new Intent(NotesActivity.this, EditActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void startEditAt(int i) {
        Intent intent = EditActivity.newIntent(this, mNotePresenter.getNotes().get(i));
        startActivityForResult(intent, 0);
    }

    @Override
    public void notifyChanged() {
        mNotesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeleteDialog(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotesActivity.this);
        builder.setTitle("删除此记录？").setPositiveButton("是哒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                mNotePresenter.deleteNote(index);
            }
        }).setNegativeButton("不了", null).setCancelable(true).show();
    }

    @Override
    public void toggleSycnButtonRotate() {
        if (mRefreshButton.getAnimation() == null) {
            RotateAnimation rotateAnim = new RotateAnimation(0, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(700);
            rotateAnim.setRepeatCount(-1);
            rotateAnim.setRepeatMode(RotateAnimation.START_ON_FIRST_FRAME);
            rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            mRefreshButton.startAnimation(rotateAnim);
        } else {
            mRefreshButton.clearAnimation();
        }
    }


    public void initRecyclerView(RecyclerView recyclerView) {
        mNotesAdapter = new NotesAdapter(this, mNotePresenter.getNotes());
        mNotesAdapter.setOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startEditAt(position);
            }
        });
        mNotesAdapter.setOnItemLongClickListener(new NotesAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showDeleteDialog(position);
            }
        });
        recyclerView.setAdapter(mNotesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
