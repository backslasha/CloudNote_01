package mouse.com.cloudnote_01.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import mouse.com.cloudnote_01.R;
import mouse.com.cloudnote_01.activities.SingleFragmentActivity;
import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.BmobHelper;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class EditFragment extends Fragment {
    private SingleFragmentActivity mHostActivity;
    private static final String ARG_SINGLE_NOTE = "ARG_SINGLE_NOTE";
    private EditText edt_content;
    private Toolbar mToolbar;
    private String preNoteTitle = "",
            preNoteContent = "";//上次关闭时的本条笔记的标题和内容，若是新开的笔记，则都为“”
    private Note note;

    public static EditFragment newInstance(Note note) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_SINGLE_NOTE, note);
        EditFragment fragment = new EditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit, container, false);
        edt_content = (EditText) view.findViewById(R.id.id_edt_content);

        mToolbar = (Toolbar) view.findViewById(R.id.id_toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEdit();
            }
        });
//        mHostActivity.setSupportActionBar(mToolbar);
//        if (mHostActivity.getSupportActionBar() != null){
//            mHostActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            mHostActivity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
//        }

        Button button = (Button) view.findViewById(R.id.id_btn_store);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEdit();
            }
        });


        //本编辑Activity开启时，若intent中包含有Note信息，则本Activity属于修改页面，此时
        // 先还原数据(即读取笔记内容），再往下修改内容
        if (getArguments().getSerializable(ARG_SINGLE_NOTE) instanceof Note) {
            note = (Note) getArguments().getSerializable(ARG_SINGLE_NOTE);

            preNoteContent = note.getNote_content();
            if (note.getNote_title() != null)
                preNoteTitle = note.getNote_title();

            //SpannableString spannableString = new SpannableString(preNoteContent);
            //spannableString.setSpan(new ForegroundColorSpan(Color.BLUE),0,preNoteContent.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            // spannableString.setSpan(new BackgroundColorSpan(Color.CYAN),0,preNoteContent.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            //spannableString.setSpan(new URLSpan("http://www.baidu.com"),0,preNoteContent.length(),  Spanned.SPAN_INCLUSIVE_INCLUSIVE);
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
            mToolbar.setTitle(preNoteTitle);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHostActivity = (SingleFragmentActivity) getActivity();

    }

    //编辑完按保存按钮触发
    public void finishEdit() {
        String time;
        long newId;
        String title, content, bmob_id;

        //计算当前时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        time = formatter.format(curDate);

        //以创建笔记的时间作为笔记的id
        newId = curDate.getTime();

        //获取editText中的文本
        content = edt_content.getText().toString();


        int index = 0;
        int index0 = content.indexOf("\n");
        int index1 = content.indexOf(" ");
        if (index0 >= 0 && index1 >= 0)
            index = Math.min(index0, index1);
        else if (index0 < 0 && index1 < 0)
            index = 0;
        else index = Math.max(index0, index1);
        title = content.substring(0, index);

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
            note.setBmob_id(note.getBmob_id());
            note.setNeed_update_to_bmob(BmobHelper.NEED_UPDATE_TO_BMOB);
        }

        //将修改后或者新增的note包装进intent
        Intent intent = new Intent();
        intent.putExtra("note", note);

        //如果本次提交的note没有修改过，则结果码设置为RESULT_CANCELED
        if (mToolbar.getTitle().toString().equals(preNoteTitle)
                && edt_content.getText().toString().equals(preNoteContent)) {
            mHostActivity.setResult(RESULT_CANCELED, intent);
        } else {
            mHostActivity.setResult(RESULT_OK, intent);
        }

        //关闭本页面，将note信息回执给MainActivity
        mHostActivity.finish();
    }
}
