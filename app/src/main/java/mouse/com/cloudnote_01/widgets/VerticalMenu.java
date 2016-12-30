package mouse.com.cloudnote_01.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import mouse.com.cloudnote_01.R;

public class VerticalMenu extends ViewGroup {
    private Status mCurrentStatus = Status.CLOSE;
    private int mIntervals;//菜单项之间的间隔
    private int mPositionHorizontal = POSITION_HORIZONTAL_CENTER;//菜单出现的位置
    private View mCButton;//中心菜单按钮
    private OnMenuItemClickListener mMenuItemClickListener;
    private OnMainButtonClickListener mOnClickListener;
    private boolean rotateToggle = false;

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

    public interface OnMainButtonClickListener {
        void onClick(View view);
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
        rotateToggle = a.getBoolean(R.styleable.VerticalMenu_rotate_toggle, false);
        a.recycle();

    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        if (b) {
            //layout中心菜单
            layoutCButton();

            //依次layout每个菜单项
            int count = getChildCount();
            for (int i = 1; i < count; i++) {

                View child = getChildAt(i);
                child.setVisibility(GONE);//菜单项默认gone

                int cWidth = child.getMeasuredWidth();
                int cHeight = child.getMeasuredHeight();

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


    // ------------------------------- interface method-------------------------------------------


    /**
     * 若当前菜单打开，则执行关闭动画
     * 若当前菜单关闭，则执行打开动画
     *
     * @param duration 动画执行时长
     */
    public void toggleMenu(int duration) {
        int count = getChildCount();
        for (int i = 1; i < count; i++) {
            final View child = getChildAt(i);
            child.setVisibility(VISIBLE);//无论是关闭还是打开菜单,先设置可见性,动画才能运行

            int d = mCButton.getTop() - child.getTop();

            AnimationSet set = new AnimationSet(true);

            Animation tranAnim;

            //打开菜单时,设置child可点击可聚焦,加监听事件(被点击时播放放大缩小动画)
            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(0, 0, d, 0);
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
            } else {//关闭菜单时,取消child的可点击可聚焦性
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
            set.setInterpolator(new DecelerateInterpolator());
            set.addAnimation(tranAnim);
            set.setDuration(duration);
            set.setStartOffset(100 * i / count);//稍微延迟

            //播放动画
            child.startAnimation(set);

        }
        changeState();
    }

    /**
     * @return 返回菜单是否打开的boolean
     */
    public boolean isMenuOpen() {
        return mCurrentStatus == Status.OPEN;
    }

    /**
     * 为菜单的每一个菜单项设置监听事件
     *
     * @param mMenuItemClickListener mMenuItemClickListener
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    public void setOnMainClickListener(OnMainButtonClickListener mOnClickListner) {
        this.mOnClickListener = mOnClickListner;
    }
    // ----------------------------------private methods----------------------------------------

    /**
     * 选中某个菜单项时执行此方法，遍历所有菜单项，选中项执行放大动画，其他项执行缩小动画
     *
     * @param pos 选中的菜单项的序号
     */
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

    /**
     * @param duration 动画进行时长
     * @return 返回设计好的缩小动画 animationSet
     */
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

    /**
     * @param duration 动画进行时长
     * @return 返回设计好的放大动画 animationSet
     */
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

    /**
     * 装换菜单的开关状态
     */
    private void changeState() {
        mCurrentStatus = mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE;
    }

    /**
     * 以mCButton为基准，layout所有菜单项
     */
    private void layoutCButton() {
        mCButton = getChildAt(0);
        mCButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (rotateToggle)
                    rotateButton(view, 0, 360f, 300);
                if(mOnClickListener!=null){
                    mOnClickListener.onClick(view);
                }else{
                    toggleMenu(300);
                }

            }
        });

        int l = 0, t = getMeasuredHeight() - mCButton.getMeasuredHeight();
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();

        if (mPositionHorizontal == POSITION_HORIZONTAL_CENTER) {
            l = (getMeasuredWidth() - mCButton.getMeasuredWidth()) / 2;
        } else if (mPositionHorizontal == POSITION_HORIZONTAL_RIGHT) {
            l = getMeasuredWidth() - mCButton.getMeasuredWidth() - getPaddingRight();
            t = t - getPaddingBottom();
        } else {
            l = getMeasuredWidth() - mCButton.getMeasuredWidth();
        }
        mCButton.layout(l, t, l + width, t + height);
    }

    /**
     * 对某个view执行旋转动画
     *
     * @param button   传进来的view
     * @param start    起始角度
     * @param end      终止较低
     * @param duration 执行时间
     */
    private void rotateButton(View button, float start, float end, int duration) {
        RotateAnimation animation = new RotateAnimation(start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setFillAfter(true);
        animation.setDuration(duration);
        button.startAnimation(animation);
    }


}
