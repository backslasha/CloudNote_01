package mouse.com.cloudnote_01.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import cn.bmob.v3.Bmob;
import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.adapters.MyAdapter;
import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.BmobHelper;
import mouse.com.cloudnote_01.utils.MyDatabaseHelper;
import mouse.com.cloudnote_01.widgets.VerticalMenu;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ListView listView;
    private Button btn_flush;
    private VerticalMenu verticalMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_flush = (Button) findViewById(R.id.id_btn_flush);

        verticalMenu = (VerticalMenu) findViewById(R.id.id_vertical_menu);
        listView = (ListView) findViewById(R.id.id_lv);

        verticalMenu.setOnMainClickListener(new VerticalMenu.OnMainButtonClickListener() {
            @Override
            public void onClick(View view) {
                startNewEdit();
            }
        });
        btn_flush.setOnClickListener(this);
        listView.setAdapter(new MyAdapter(this));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startEditAt(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("删除此记录？").setPositiveButton("是哒", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        ((MyAdapter) listView.getAdapter()).deleteNote(i);
                    }
                }).setNegativeButton("不了", null).setCancelable(true).show();
                return true;
            }
        });

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
                ((MyAdapter) listView.getAdapter()).addNote(note);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_btn_flush:
                RotateAnimation rotateAnim = new RotateAnimation(0, 360f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(700);
                rotateAnim.setRepeatCount(-1);
                rotateAnim.setRepeatMode(RotateAnimation.START_ON_FIRST_FRAME);
                rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                btn_flush.startAnimation(rotateAnim);
                List<Note> notes = ((MyAdapter) (listView.getAdapter())).getMyDatabaseHelper().query(MyAdapter.EMPTY_BMOB_ID);
                BmobHelper.getInstance().addToSycnNotes(notes);
                BmobHelper.getInstance().sycnToBmob(this, new BmobHelper.OnSycnFinishListener() {
                    @Override
                    public void onSuccess(int suc, int fal, List<Note> successSycnNotes) {
                        Toast.makeText(MainActivity.this, "同步完成" + suc + ",失败" + fal, Toast.LENGTH_SHORT).show();
                        btn_flush.clearAnimation();
                        MyDatabaseHelper myDatabaseHelper = ((MyAdapter) (listView.getAdapter())).getMyDatabaseHelper();
                        for (Note note : successSycnNotes) {
                            myDatabaseHelper.update(note.getNote_title(), note.getNote_content(), note.getNote_time(), note.getNote_id(), note.getBmob_id());
                        }
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(MainActivity.this, "出错了，肯定是Bmob的问题/害怕 " + i + ":" + s, Toast.LENGTH_SHORT).show();
                        btn_flush.clearAnimation();
                    }
                });
                break;

        }
    }

    //打开一个新的编辑页面
    private void startNewEdit() {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        startActivityForResult(intent, 0);
    }

    //根据点击的note打开相应的编辑页面
    private void startEditAt(int i) {
        Intent intent = new Intent(MainActivity.this, EditActivity.class);
        intent.putExtra("note", (Note) listView.getAdapter().getItem(i));
        startActivityForResult(intent, 0);
    }
}
