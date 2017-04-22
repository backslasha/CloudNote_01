package mouse.com.cloudnote_01.notes;


import android.content.Context;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import mouse.com.cloudnote_01.beans.Note;
import mouse.com.cloudnote_01.utils.BmobHelper;

public class NotePresenter implements INotePresenter {
    private INoteView mView;
    private NoteModel mNoteModel;
    private Context mContext;
    private BmobHelper mBmobHelper;

    public NotePresenter(INoteView view, Context context) {
        mView = view;
        mNoteModel = new NoteModel(context);
        mContext = (Context) mView;
        mBmobHelper = BmobHelper.getInstance();
    }

    @Override
    public void loadNoteList() {
        mView.notifyChanged();
    }

    @Override
    public void insertOrUpdateNoteToDb(Note note) {
        mNoteModel.insertOrUpdateNote(note);
        mView.notifyChanged();
    }


    @Override
    public void deleteNote(int i, boolean needDeleteCloud) {
        String bmob_id = mNoteModel.getNotes().get(i).getBmob_id();
        mNoteModel.deleteNote(i);
        if (needDeleteCloud) {
            mBmobHelper.deleteFromBmob(bmob_id, mContext);
        }
    }

    @Override
    public void sycnNotesBetweenCloud() {
        mView.toggleSycnButtonRotate();
        //从数据库中找出所有 bmob_id 为MyAdapter.EMPTY_BMOB_ID的note数据，添加到BmobHelper准备上传
        List<Note> notes = mNoteModel.getMyDatabaseHelper().query(BmobHelper.EMPTY_BMOB_ID);
        //从数据库中找出所有需要update的notes，添加到BmobHelper准备更新
        notes.addAll(mNoteModel.getMyDatabaseHelper().query(BmobHelper.NEED_UPDATE_TO_BMOB));

//        mNoteModel.getMyDatabaseHelper().query();

        mBmobHelper.sycnToBmob((Context) mView, notes,
                new BmobHelper.OnSycnFinishListener() {
                    @Override
                    public void onSuccess(int suc, int fal, List<Note> successSycnNotes) {
                        Toast.makeText((Context) mView, "成功上传到云端" + suc + ",失败" + fal, Toast.LENGTH_SHORT).show();

                        //更新成功后的notes的 bmob_id和need_update_to_bomb两个信息变动，需要更新到本地
                        for (Note note : successSycnNotes) {
                            mNoteModel.hasSaved(note);
                        }

                        sycnNotesToLocal();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText((Context) mView, "出错了 " + i + ":" + s, Toast.LENGTH_SHORT).show();
                        mView.toggleSycnButtonRotate();
                    }
                });

    }

    @Override
    public void sycnNotesToLocal() {
        mBmobHelper.fetchCloudNotes(mContext, new BmobHelper.OnSycnFinishListener() {
            @Override
            public void onSuccess(int suc, int fal, List<Note> cloudNotes) {
                int count = 0;
                for (Note note : cloudNotes) {
                    note.setBmob_id(note.getObjectId());
                    if (!mNoteModel.getNotes().contains(note)) {
                        insertOrUpdateNoteToDb(note);
                        count++;
                    }
                }
                mView.notifyChanged();
                Toast.makeText(mContext, "成功同步 " + count + "条数据到本地", Toast.LENGTH_SHORT).show();
                mView.toggleSycnButtonRotate();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(mContext, "fetchCloudNotes Fail: " + s, Toast.LENGTH_SHORT).show();
                mView.toggleSycnButtonRotate();
            }
        });
    }

    public LinkedList<Note> getNotes() {
        return mNoteModel.getNotes();
    }
}
