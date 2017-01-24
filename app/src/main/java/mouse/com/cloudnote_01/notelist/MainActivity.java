package mouse.com.cloudnote_01.notelist;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ListView;

import cn.bmob.v3.Bmob;
import mouse.com.cloudnote_01.activities.BaseActivity;
import mouse.com.cloudnote_01.activities.EditActivity;
import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.adapters.MyAdapter;
import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.widgets.VerticalMenu;


public class MainActivity extends BaseActivity implements View.OnClickListener, INoteView {
    private ListView listView;
    private Button btn_flush;
    private VerticalMenu verticalMenu;

    private INotePresenter mNotePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.id_lv);
        btn_flush = (Button) findViewById(R.id.id_btn_flush);

        mNotePresenter = new NotePresenter(this, this);
        mNotePresenter.initListView(listView);

        verticalMenu = (VerticalMenu) findViewById(R.id.id_vertical_menu);
        verticalMenu.setOnMainClickListener(new VerticalMenu.OnMainButtonClickListener() {
            @Override
            public void onClick(View view) {
                startNewEdit();
            }
        });
        btn_flush.setOnClickListener(this);

        //①.默认初始化
        Bmob.initialize(this, "a4f2c00bbb157465b1b7d5ba19851421");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN： decorView充满屏幕（不包括NavigationBar区域）,但是会被Winodow的statusBar遮盖
        //View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 防止status隐藏时contentView大小发生改变
        //如果5.0以上，则设置状态按透明，并全屏显示
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);//21以上
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorAppTheme));
            View decorView = getWindow().getDecorView();
            int option =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Note note = (Note) data.getSerializableExtra("note");
                mNotePresenter.insertOrUpdateNote(note);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_flush:
                mNotePresenter.sycnNotesToBmob();
                break;

        }
    }

    @Override
    public void startNewEdit() {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void startEditAt(int i) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("note", (Note) listView.getAdapter().getItem(i));
        startActivityForResult(intent, 0);
    }

    @Override
    public void notifyChanged() {
        ((MyAdapter) listView.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void showDeleteDialog(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("删除此记录？").setPositiveButton("是哒", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                mNotePresenter.deleteNote(index);
            }
        }).setNegativeButton("不了", null).setCancelable(true).show();
    }

    @Override
    public void toggleSycnButtonRotate() {
        if (btn_flush.getAnimation() == null) {
            RotateAnimation rotateAnim = new RotateAnimation(0, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnim.setDuration(700);
            rotateAnim.setRepeatCount(-1);
            rotateAnim.setRepeatMode(RotateAnimation.START_ON_FIRST_FRAME);
            rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            btn_flush.startAnimation(rotateAnim);
        } else {
            btn_flush.clearAnimation();
        }
    }

}
