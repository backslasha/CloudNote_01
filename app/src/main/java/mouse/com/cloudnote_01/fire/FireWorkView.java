package mouse.com.cloudnote_01.fire;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class FireWorkView extends View {
    private ArrayList<FireWork> fireWorks = new ArrayList<>();

    public FireWorkView(Context context) {
        super(context);
    }

    public FireWorkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (FireWork fireWork : fireWorks)
            fireWork.draw(canvas);
        if (!fireWorks.isEmpty())
            invalidate();
    }

    /**
     * @param editText 要绑定的EditText
     */
    public void bindEditText(final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            int windDirction;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (count > before) {
                    windDirction = -1;
                } else {
                    windDirction = 1;
                }

                Point location = getCursorCoordinate(editText);
                final FireWork fireWork = new FireWork(location, windDirction);
                fireWorks.add(fireWork);//加入一个烟花对象
                fireWork.setAnimationEndListener(new FireWork.AnimationEndListener() {
                    @Override
                    public void onAnimationEnd() {
                        fireWorks.remove(fireWork);//烟花动画结束时，移除此烟花对象
                    }
                });
                fireWork.fire();//开始点燃动画
                invalidate();//重绘

                editText.clearAnimation();
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(editText, "rotation", 0.8f, -0.8f, 0);
                ObjectAnimator translateAnim = ObjectAnimator.ofFloat(editText, "translationX", 5, -5, 0);
                rotateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                rotateAnim.setDuration(70);
                rotateAnim.setRepeatCount(3);
                translateAnim.setInterpolator(new AccelerateDecelerateInterpolator());
                translateAnim.setDuration(70);
                translateAnim.setRepeatCount(3);
                set.playTogether(rotateAnim, translateAnim);
                set.start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //获取cursor坐标，返回一个Point
    private Point getCursorCoordinate(EditText editText) {
         /*-getSelectionStart返回当前editText中文本的offset：cursor往后偏移的次数-*/
        int offset = editText.getSelectionStart();

        //Layout：A base class that manages text layout in visual elements on the screen.
        Layout layout = editText.getLayout();

        //Get the line number on which the specified text offset appears. If you ask for a position before 0, you get 0; if you ask for a position beyond the end of the text, you get the last line
        //意思是根据当前text中文本offset值，返回当前的行数
        int lineNumber = layout.getLineForOffset(offset);

        //返回特定行的ascent值，ascent：本行baseline到ascent的距离，注意也是negative负数
        //int ascent = layout.getLineAscent(lineNumber);

        //返回特定行的竖直方向上的BaseLine在本View内的坐标
        int baseline = layout.getLineBaseline(lineNumber);

        //Get the primary horizontal position for the specified text offset. This is the location where a new character would be inserted in the paragraph's primary direction
        //把经过offset次向后偏移的cursor的横坐标返回
        int x = (int) layout.getPrimaryHorizontal(offset);

        //在本行的baseline高度处点燃烟花
        //int y = (int) (baseline + ascent + 0.5 * (p.descent() - p.ascent()));
        int y = baseline;

        return new Point(x, y);
    }
}
