package mouse.com.cloudnote_01.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.MaskFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.MaskFilterSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.adapters.MyAdapter;
import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.fire.FireWorkView;
import mouse.com.cloudnote_01.utils.BmobHelper;


public class EditActivity extends BaseActivity {
    private EditText edt_content, edt_title;
    private String preNoteTitle = "", preNoteContent = "";//上次关闭时的本条笔记的标题和内容，若是新开的笔记，则都为“”
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edt_content = (EditText) findViewById(R.id.id_edt_content);
        edt_title = (EditText) findViewById(R.id.id_edt_title);


        //本编辑Activity开启时，若intent中包含有Note信息，则本Activity属于修改页面，此时
        // 先还原数据(即读取笔记内容），再往下修改内容
        if (getIntent().getSerializableExtra("note") instanceof Note) {
            note = (Note) getIntent().getSerializableExtra("note");

            preNoteContent = note.getNote_content();
            preNoteTitle = note.getNote_title();

            //SpannableString spannableString = new SpannableString(preNoteContent);
            //spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),0,preNoteContent.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
           // spannableString.setSpan(new BackgroundColorSpan(Color.CYAN),0,preNoteContent.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            //spannableString.setSpan(new URLSpan("http://www.baidu.com"),0,preNoteContent.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            //ImageSpan imageSpan = new ImageSpan(this,R.mipmap.ic_launcher);
            // //  spannableString.setSpan(imageSpan,6,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //   ClickableSpan clickableSpan = new ClickableSpan() {
                //    @Override
                //      public void onClick(View widget) {
                    //         Toast.makeText(EditActivity.this, "cao", Toast.LENGTH_SHORT).show();
                    //     }
                // };
            // spannableString.setSpan(clickableSpan,6,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            // edt_content.setText(spannableString);

           edt_content.setText(preNoteContent);
            edt_title.setText(preNoteTitle);
        }

        //setFire();
    }

    //编辑完按保存按钮触发
    public void finishEdit(View view) {
        String time;
        long newId;
        String title, content,bmob_id;

        //计算当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        time = formatter.format(curDate);

        //以创建笔记的时间作为笔记的id
        newId = curDate.getTime();

        //获取editText中的文本
        title = edt_title.getText().toString();
        content = edt_content.getText().toString();

        //保存时，缺省的标题的和文本内容
        if (title.equals("") && !content.equals("")) {
            title = "未命名笔记";
        } else if (!title.equals("") && content.equals("")) {
            content = "无内容";
        }

        //新增的笔记，则设置全新属性（包括id），
        //若是修改的笔记，则只设置部分属性，不修改id
        if (note == null) {
            note = new Note(title, content, time, newId, BmobHelper.EMPTY_BMOB_ID, BmobHelper.NEED_NOT_UPDATE_TO_BMOB);
        } else {
            note.setNote_content(content);
            note.setNote_title(title);
            note.setNote_time(time);
        }

        //将修改后或者新增的note包装进intent
        Intent intent = new Intent();
        intent.putExtra("note", note);

        //如果本次提交的note没有修改过，则结果码设置为RESULT_CANCELED
        if (edt_title.getText().toString().equals(preNoteTitle) && edt_content.getText().toString().equals(preNoteContent)) {
            setResult(RESULT_CANCELED, intent);
        } else {
            setResult(RESULT_OK, intent);
        }

        //关闭本页面，将note信息回执给MainActivity
        finish();
    }

    //设置编辑时的烟花效果
    private void setFire() {
        FireWorkView fireWorkView;
        EditText editText;
        fireWorkView = (FireWorkView) findViewById(R.id.id_fireworkview);
        editText = (EditText) findViewById(R.id.id_edt_content);
        fireWorkView.bindEditText(editText);
    }
}

