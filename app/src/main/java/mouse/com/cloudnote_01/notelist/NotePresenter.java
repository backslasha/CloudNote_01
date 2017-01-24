package mouse.com.cloudnote_01.notelist;


import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import mouse.com.cloudnote_01.adapters.MyAdapter;
import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.BmobHelper;

public class NotePresenter implements INotePresenter {
    private INoteView mView;
    private NoteModel mNoteModel;
    private Context mContext;

    public NotePresenter(INoteView view, Context context) {
        mView = view;
        mNoteModel = new NoteModel(context);
        mContext = (Context) mView;
    }

    @Override
    public void loadNoteList() {
        mView.notifyChanged();
    }

    @Override
    public void insertOrUpdateNote(Note note) {
        mNoteModel.insertOrUpdateNote(note);
        mView.notifyChanged();
    }

    @Override
    public void locatedNote(Note note) {
        mNoteModel.locatedNote(note);
        mView.notifyChanged();
    }

    @Override
    public void deleteNote(int i) {
        mNoteModel.deleteNote(i);
        mView.notifyChanged();
    }

    @Override
    public void sycnNotesToBmob() {
        mView.toggleSycnButtonRotate();
        //从数据库中找出所有bmob_id为MyAdapter.EMPTY_BMOB_ID的note数据，添加到BmobHelper准备上传
        List<Note> notes = mNoteModel.getMyDatabaseHelper().query(BmobHelper.EMPTY_BMOB_ID);
        //从数据库中找出所有需要update的notes，添加到BmobHelper准备更新
        notes.addAll(mNoteModel.getMyDatabaseHelper().query(BmobHelper.NEED_UPDATE_TO_BMOB));

        BmobHelper.getInstance().sycnToBmob((Context) mView, notes, new BmobHelper.OnSycnFinishListener() {
            @Override
            public void onSuccess(int suc, int fal, List<Note> successSycnNotes) {
                Toast.makeText((Context) mView, "同步完成" + suc + ",失败" + fal, Toast.LENGTH_SHORT).show();
                mView.toggleSycnButtonRotate();
                //更新成功后的notes的 bmob_id和need_update_to_bomb两个信息变动，需要更新到本地
                for (Note note : successSycnNotes) {
                    mNoteModel.locatedNote(note);
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText((Context) mView, "出错了，肯定是Bmob的问题/害怕 " + i + ":" + s, Toast.LENGTH_SHORT).show();
                mView.toggleSycnButtonRotate();
            }
        });
    }

    @Override
    public void initListView(ListView listView) {

        listView.setAdapter(new MyAdapter(mContext, mNoteModel.getNotes()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mView.startEditAt(i);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                mView.showDeleteDialog(i);
                return true;
            }
        });
    }

}
