package mouse.com.cloudnote_01.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import mouse.com.cloudnote_01.beans.Note;


public class BmobHelper {

    private static BmobHelper instance = null;
    private List<Note> waitToSycnNotes = null;
    private List<Note> waitToUpdateNotes = null;//将待更新的note都保存在此数组中
    private List<Note> waitToInsertNotes = null;
    private List<Note> successInsertNotes = null;
    private int suc;
    private int fal;

    private BmobHelper() {
        waitToSycnNotes = new ArrayList<>();
        waitToUpdateNotes = new ArrayList<>();
        waitToInsertNotes = new ArrayList<>();
        successInsertNotes = new ArrayList<>();
    }

    public interface OnSycnFinishListener {
        void onSuccess(int suc, int fal, List<Note> successSycnNotes);
        void onFailure(int i, String s);
    }

    public static synchronized BmobHelper getInstance() {
        if (instance == null) {
            instance = new BmobHelper();
        }
        return instance;
    }

    /**
     * 调用这个方法把需要更新或者插入的note添加到BmobHelper中来
     *
     * @param note note
     */
    public void addToSycnNotes(Note note) {
        waitToSycnNotes.add(note);
    }

    /**
     * 调用这个方法把需要更新或者插入的notes添加到BmobHelper中来
     *
     * @param notes notes
     */
    public void addToSycnNotes(List<Note> notes) {
        waitToSycnNotes.addAll(notes);
    }

    /**
     * 本地数据同步至Bmob后台
     * @param context 上下文
     */
    public void sycnToBmob(final Context context, final OnSycnFinishListener onSycnFinishListener) {
        suc = fal = 0;
        BmobQuery<Note> query = new BmobQuery<>();

        //若没有需要同步的notes，则同步完成
        if (waitToSycnNotes.size() == 0) {
            if (onSycnFinishListener != null)
                onSycnFinishListener.onSuccess(0, 0, successInsertNotes);
            return;
        }
        //查询所有待更新的notes
        query.findObjects(context, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> list) {
                //根据查询结果把waitToSycnNotes分成waitToUpdateNotes和waitToInsertNotes两个代操作列表
                sortOutNotes(list);

                //当无须插入和更新时，同步完成
                if (waitToUpdateNotes.size() == 0 && waitToInsertNotes.size() == 0) {
                    if (onSycnFinishListener != null)
                        onSycnFinishListener.onSuccess(0, 0, successInsertNotes);
                    //否则若无须更新只需添加数据，则直接添加数据到Bmob
                } else if (waitToUpdateNotes.size() == 0) {
                    successInsertNotes.clear();
                    saveToBmob(context, waitToInsertNotes, onSycnFinishListener);
                    //否则先更新需要更新的每条数据，之后再调用saveToBmob方法添加新数据到Bmob，即先update完，再insert
                } else {
                    //逐个更新查询到的note
                    for (final Note note : waitToUpdateNotes) {
                        note.update(context, new UpdateListener() {

                            @Override
                            public void onSuccess() {
                                waitToUpdateNotes.remove(note);//同步成功，从待更新List中移除
                                suc++;
                                if (waitToUpdateNotes.size() == 0 && waitToInsertNotes.size() == 0) {
                                    if (onSycnFinishListener != null)
                                        onSycnFinishListener.onSuccess(suc, fal, successInsertNotes);
                                } else if (waitToUpdateNotes.size() == 0 && waitToInsertNotes.size() != 0) {
                                    saveToBmob(context, waitToInsertNotes, onSycnFinishListener);
                                }

                            }

                            @Override
                            public void onFailure(int i, String s) {
                                fal++;
                                if (onSycnFinishListener != null)
                                    onSycnFinishListener.onFailure(i, s);
                            }
                        });

                    }
                }

            }

            @Override
            public void onError(int i, String s) {
                if (onSycnFinishListener != null)
                    onSycnFinishListener.onFailure(i, s);
            }
        });


    }

    /**
     * 根据传进来的hasIdInBmobNotes，即从Bmob后端查询到的notes数据，
     * 把waitToSycnNotes中已经在Bmob后端存在id字段的分到waitToUpdateNotes，
     * 其他的分到waitToInsertNotes
     * @param hasIdInBmobNotes 在Bmob后台已经有id的notes集合
     */
    private void sortOutNotes(List<Note> hasIdInBmobNotes) {
        for (Note note : waitToSycnNotes) {
            if (hasIdInBmobNotes.contains(note)) {
                waitToUpdateNotes.add(note);
            } else {
                waitToInsertNotes.add(note);
            }
        }
        waitToSycnNotes.clear();
    }

    /**
     * 逐个插入待插入的note
     * @param context context
     * @param waitToInsertNotes waitToInsertNotes
     * @param onSycnFinishListener onSycnFinishListener
     */
    private void saveToBmob(final Context context, final List<Note> waitToInsertNotes, final OnSycnFinishListener onSycnFinishListener) {
        for (final Note note : waitToInsertNotes) {
            note.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    note.setBmob_id(note.getObjectId());
                    successInsertNotes.add(note);

                    waitToInsertNotes.remove(note);//同步成功，从待插入List中移除
                    suc++;
                    if (waitToInsertNotes.size() == 0) {
                        if (onSycnFinishListener != null)
                            onSycnFinishListener.onSuccess(suc, fal, successInsertNotes);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    fal++;
                    if (onSycnFinishListener != null)
                        onSycnFinishListener.onFailure(i, s);
                }
            });
        }
    }

}
