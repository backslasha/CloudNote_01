package mouse.com.cloudnote_01.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import mouse.com.cloudnote_01.beans.Note;


public class BmobHelper {
    public static final String EMPTY_BMOB_ID = "null";
    public static final int NEED_UPDATE_TO_BMOB = 1;
    public static final int NEED_NOT_UPDATE_TO_BMOB = 0;
    private static BmobHelper instance = null;
    private List<Note> waitToInsertNotes = null;
    private List<Note> waitToUpdateNotes = null;//将待更新的note都保存在此数组中
    private List<Note> successSycnNotes = null;
    private int suc;
    private int fal;

    private BmobHelper() {
        waitToInsertNotes = new ArrayList<>();
        waitToUpdateNotes = new ArrayList<>();
        successSycnNotes = new ArrayList<>();
    }

    public static synchronized BmobHelper getInstance() {
        if (instance == null) {
            instance = new BmobHelper();
        }
        return instance;
    }

    public interface OnSycnFinishListener {
        void onSuccess(int suc, int fal, List<Note> successSycnNotes);

        void onFailure(int i, String s);
    }

    public interface OnUpdateFinishListener {
        void onSuccess();

        void onFailure(int i, String s);
    }


    /**
     * 本地数据同步至Bmob后台
     *
     * @param context              context
     * @param waitToSycnNotes      把需要 sycn 的 notes 封装在list中
     * @param onSycnFinishListener sycn 结束时回调的接口
     */
    public void sycnToBmob(final Context context,
                           final List<Note> waitToSycnNotes,
                           final OnSycnFinishListener onSycnFinishListener) {
        suc = fal = 0;
        BmobQuery<Note> query = new BmobQuery<>();
        successSycnNotes.clear();

        //若没有需要同步的notes，则同步完成
        if (waitToSycnNotes.size() == 0) {
            if (onSycnFinishListener != null)
                onSycnFinishListener.onSuccess(0, 0, successSycnNotes);
            return;
        }

        //查询所有待更新的notes
        query.findObjects(context, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> existedInBmobNotes) {
                //根据查询结果把waitToSycnNotes分成waitToUpdateNotes和waitToInsertNotes两个代操作列表
                sortOutNotes(existedInBmobNotes, waitToSycnNotes, waitToInsertNotes, waitToUpdateNotes);

                //把 waitToUpdateNotes 中的 notes 全部更新到 Bmob，
                //更新完毕后在回调 OnUpdateFinishListener 中继续将 waitToInsertNotes 的 notes插入到Bmob
                //全部插入完成后才调用 onSycnFinishListener 的回调接口
                updateToBmob(context, waitToUpdateNotes, new OnUpdateFinishListener() {
                    @Override
                    public void onSuccess() {
                        insertToBmob(context, waitToInsertNotes, onSycnFinishListener);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        if (onSycnFinishListener != null)
                            onSycnFinishListener.onFailure(i, s);
                    }
                });

            }

            @Override
            public void onError(int i, String s) {
                if (onSycnFinishListener != null)
                    onSycnFinishListener.onFailure(i, s);
            }
        });


    }

    public void fetchCloudNotes(final Context context,
                                final OnSycnFinishListener onSycnFinishListener) {

        BmobQuery<Note> query = new BmobQuery<>();
        query.setLimit(100);
        query.findObjects(context, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> list) {
                if (onSycnFinishListener != null) {
                    onSycnFinishListener.onSuccess(+1, 0, list);
                }
            }

            @Override
            public void onError(int i, String s) {
                if (onSycnFinishListener != null) {
                    onSycnFinishListener.onFailure(1, s);
                }
            }
        });
    }

    /**
     * 根据传进来的existedBmobNotes，即从Bmob后端查询到的notes数据，
     * 根据是否属于existedBmobNotes，把待同步的notes分入待插入和待更新两个列表
     * 把waitToSycnNotes中已经在Bmob后端存在id字段的分到waitToUpdateNotes，
     * 其他的分到waitToInsertNotes
     *
     * @param existedBmobNotes 在Bmob后台已经有id的notes集合
     */
    private void sortOutNotes(List<Note> existedBmobNotes, List<Note> waitToSycnNotes,
                              List<Note> waitToInsertNotes, List<Note> waitToUpdateNotes) {

        for (Note note : waitToSycnNotes) {
            if (existedBmobNotes.contains(note)) {
                waitToUpdateNotes.add(note);
            } else {
                waitToInsertNotes.add(note);
            }
        }
    }

    /**
     * 逐个插入待插入的note
     *
     * @param context              context
     * @param waitToInsertNotes    waitToInsertNotes
     * @param onSycnFinishListener onSycnFinishListener
     */
    private void insertToBmob(final Context context,
                              final List<Note> waitToInsertNotes,
                              final OnSycnFinishListener onSycnFinishListener) {
        if (waitToInsertNotes.size() == 0) {
            if (onSycnFinishListener != null)
                onSycnFinishListener.onSuccess(suc, fal, successSycnNotes);
            return;
        }
        for (final Note note : waitToInsertNotes) {
            note.save(context, new SaveListener() {
                @Override
                public void onSuccess() {
                    note.setBmob_id(note.getObjectId());
                    successSycnNotes.add(note);
                    waitToInsertNotes.remove(note);//同步成功，从待插入List中移除
                    suc++;
                    if (waitToInsertNotes.size() == 0) {
                        if (onSycnFinishListener != null)
                            onSycnFinishListener.onSuccess(suc, fal, successSycnNotes);
                    }
                }

                @Override
                public void onFailure(int i, String s) {
                    fal++;
                    if (waitToInsertNotes.size() == fal - waitToUpdateNotes.size())
                        if (onSycnFinishListener != null)
                            onSycnFinishListener.onFailure(i, s);
                }
            });
        }
    }

    /**
     * 逐个更新待插入的note,全部更新完毕时回调方法 onUpdateFinishListener.onSuccess/onFailure
     *
     * @param context           context
     * @param waitToUpdateNotes waitToUpdateNotes
     */
    private void updateToBmob(final Context context,
                              final List<Note> waitToUpdateNotes,
                              final OnUpdateFinishListener onUpdateFinishListener) {
        if (waitToUpdateNotes.size() == 0) {
            if (onUpdateFinishListener != null)
                onUpdateFinishListener.onSuccess();
            return;
        }
        //逐个更新查询到的note
        for (final Note note : waitToUpdateNotes) {
            note.setObjectId(note.getBmob_id());
            note.update(context, new UpdateListener() {
                @Override
                public void onSuccess() {
                    successSycnNotes.add(note);
                    waitToUpdateNotes.remove(note);//同步成功，从待更新List中移除
                    suc++;
                    if (waitToUpdateNotes.size() == 0) {
                        if (null != onUpdateFinishListener) onUpdateFinishListener.onSuccess();
                    }

                }

                @Override
                public void onFailure(int i, String s) {
                    fal++;
                    if (waitToUpdateNotes.size() == fal) {
                        if (null != onUpdateFinishListener) onUpdateFinishListener.onFailure(i, s);
                    }
                }
            });

        }
    }

}
