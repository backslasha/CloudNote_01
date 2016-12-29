package mouse.com.cloudnote_01.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import mouse.com.cloudnote_01.R;

public class VerticalMenu extends ViewGroup {
    private Status mCurrentStatus = Status.CLOSE;
    private int mIntervals;
    private int mPositionHorizontal = POSITION_HORIZONTAL_CENTER;
    private View mCButton;
    private OnMenuItemClickListener mMenuItemClickListener;

    public final static int POSITION_HORIZONTAL_LEFT = 1;
    public final static int POSITION_HORIZONTAL_CENTER = 2;
    public final static int POSITION_HORIZONTAL_RIGHT = 3;
    //表示菜单开关状态的枚举值
    private enum Status {
        OPEN, CLOSE
    }
    public interface OnMenuItemClickListener {
        void onClick(View view, int pos);
    }

    public VerticalMenu(Context context) {
        this(context, null);
    }

    public VerticalMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置默认半径
        mPositionHorizontal = POSITION_HORIZONTAL_CENTER;
        mIntervals = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        //获取xml文件信息
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VerticalMenu, defStyleAttr, 0);
        mIntervals = (int) a.getDimension(R.styleable.VerticalMenu_interval, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        mPositionHorizontal = a.getInteger(R.styleable.VerticalMenu_position_horizontal, POSITION_HORIZONTAL_CENTER);
        a.recycle();

    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        if (b) {
            layoutButton();

            int count = getChildCount();
            for (int i = 1; i < count; i++) {

                View child = getChildAt(i);
                child.setVisibility(GONE);

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

                //int cl = mCButton.getLeft() + (mCButton.getMeasuredWidth() - child.getMeasuredWidth()) / 2;
                int cl = mCButton.getLeft();
                int ct = getChildAt(i - 1).getTop() - mIntervals - cHeight;

                child.layout(cl, ct, cl + cWidth, ct + cHeight);

            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

   /**-------------------------------intefaces methosd-------------------------------------------**/
   //根据情况展开或关闭按钮
   public void toggleMenu(int duration) {
        int count = getChildCount();
        for (int i = 1; i < count; i++) {
            final View child = getChildAt(i);
            child.setVisibility(VISIBLE);

            int d = mCButton.getTop() - child.getTop();

            AnimationSet set = new AnimationSet(true);
            set.setStartOffset(100 * i / count);
            Animation tranAnim;
            RotateAnimation rotaAnim;

            //to open
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(0, 0, d, 0);
                rotaAnim = new RotateAnimation(0, 540f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                child.setFocusable(true);
                child.setClickable(true);

                final int pos = i;
                child.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mMenuItemClickListener != null) {
                            mMenuItemClickListener.onClick(child, pos);
                        }
                        menuItemAnim(pos);
                        changeState();
                    }
                });
            } else {//to close
                rotaAnim = new RotateAnimation(540f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                tranAnim = new TranslateAnimation(0, 0, 0, d);
                child.setFocusable(false);
                child.setClickable(false);
            }

            tranAnim.setFillAfter(true);
            set.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        child.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
           // set.addAnimation(rotaAnim);
            set.setInterpolator(new DecelerateInterpolator());
            set.addAnimation(tranAnim);
            set.setDuration(duration);

            child.startAnimation(set);

        }
        changeState();
    }

    //是否菜单打开
    public boolean isMenuOpen() {
        return mCurrentStatus == Status.OPEN;
    }

    //设置菜单项监听器
    public void setOnMenuItemClickListener(OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    /**----------------------------------private methods----------------------------------------****/
    private void menuItemAnim(int pos) {
        for (int i = 1; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (i == pos) {
                child.startAnimation(scaleBigAnim(300));
            } else {
                child.startAnimation(scaleSmallAnim(300));
            }
            child.setClickable(false);
            child.setFocusable(false);

        }
    }

    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private void changeState() {
        mCurrentStatus = mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE;
    }

    private void layoutButton() {
        mCButton = getChildAt(0);
        mCButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                rotateButton(view, 0, 360f, 300);
                toggleMenu(300);
            }
        });

        int l = 0, t = getMeasuredHeight() - mCButton.getMeasuredHeight();
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();

        if (mPositionHorizontal == POSITION_HORIZONTAL_CENTER) {
            l = (getMeasuredWidth() - mCButton.getMeasuredWidth()) / 2;
        } else if (mPositionHorizontal == POSITION_HORIZONTAL_RIGHT) {
            l = getMeasuredWidth() - mCButton.getMeasuredWidth();
        }
        mCButton.layout(l, t, l + width, t + height);
    }

    private void rotateButton(View button, float start, float end, int duration) {
        RotateAnimation animation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(duration);
        button.startAnimation(animation);
    }


}
