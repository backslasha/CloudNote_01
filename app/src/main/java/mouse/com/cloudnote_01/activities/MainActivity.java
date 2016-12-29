package mouse.com.cloudnote_01.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.adapters.MyAdapter;
import mouse.com.cloudnote_01.beans.Note;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ListView listView;
    private Button btn_addNewNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_addNewNote = (Button) findViewById(R.id.id_btn_add_new_note);
        listView = (ListView) findViewById(R.id.id_lv);

        btn_addNewNote.setOnClickListener(this);
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
                        ((MyAdapter)listView.getAdapter()).deleteNote(i);
                    }
                }).setNegativeButton("不了",null).setCancelable(true).show();
                return true;
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN： decorView充满屏幕（不包括NavigationBar区域）,但是会被Winodow的statusBar遮盖
        //View.SYSTEM_UI_FLAG_LAYOUT_STABLE: 防止status隐藏时contentView大小发生改变
        //如果5.0以上，则设置状态按透明，并全屏显示
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            //int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            int option =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    ;
            decorView.setSystemUiVisibility(option);
            //getWindow().setStatusBarColor(Color.TRANSPARENT);
            //getWindow().setNavigationBarColor(Color.TRANSPARENT);
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
        switch (view.getId()){
            case R.id.id_btn_add_new_note:
                startNewEdit();
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
