package mouse.com.cloudnote_01.fire;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireWork {
    private static final int[] baseColors = {0xff000000, 0xffff0000, 0xffffffff, 0xff00ff00, 0xff0000ff, 0xffffff00, 0xffff00ff};
    private final static int DEFAULT_SPARK_COUNT = 16;
    private final static float DEFAULT_SPARK_SIZE = 8;
    private final static int DEFAULT_DURATION = 400;
    private final static float DEFAULT_COMPLETE_SPEED = 18;
    private final static float DEFAULT_WIND_SPEED = 6;
    private final static float DEFAULT_GRAVITY = 6;
    private final Point location;

    private List<Spark> sparks = new ArrayList<>();
    private int duration, windDirection, sparkCount, color;
    private float windSpeed, completeSpeed, gravity, sparkSize, animatorValue;
    private Paint mPaint;
    private AnimationEndListener animationEndListener;

    interface AnimationEndListener {
        void onAnimationEnd();
    }

    public void setAnimationEndListener(AnimationEndListener animationEndListener) {
        this.animationEndListener = animationEndListener;
    }

    public FireWork(Point location, int windDirection) {
        this.location = location;
        this.windDirection = windDirection;

        gravity = DEFAULT_GRAVITY;
        completeSpeed = DEFAULT_COMPLETE_SPEED;
        windSpeed = DEFAULT_WIND_SPEED;
        duration = DEFAULT_DURATION;
        sparkCount = DEFAULT_SPARK_COUNT;
        sparkSize = DEFAULT_SPARK_SIZE;
        addSparks();

        mPaint = new Paint();
        mPaint.setColor(color);

    }

    //调用fire后，本FireWork对象中的Sparks的x和y根据ValueAnimator的值不断变化
    public void fire() {

        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0);
        animator.setDuration(duration).setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float) animation.getAnimatedValue();

                //这里的速度speed理解为，dlata Animatorvalue内，spark自己移动的距离
                for (Spark spark : sparks) {
                    spark.x = (float) (spark.x + Math.cos(spark.direction) * spark.speed * animatorValue + windSpeed * windDirection);
                    spark.y = (float) (spark.y - Math.sin(spark.direction) * spark.speed * animatorValue + gravity * (1 - animatorValue));
                }

            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationEndListener != null)
                    animationEndListener.onAnimationEnd();
            }
        });
        animator.start();

    }

    //在绑定的View中的canvas上，绘制本FireWork的所有Sparks
    public void draw(Canvas canvas) {
        mPaint.setAlpha((int) (225 * animatorValue));
        for (Spark spark : sparks) {
            mPaint.setColor(spark.color);
            canvas.drawCircle(location.x + spark.x, location.y + spark.y, sparkSize, mPaint);
        }

    }

    //初始化时，将本FireWork中添加进一些属性随机的Sparks，保存在ArrayList中
    private void addSparks() {
        Random random = new Random();
        color = baseColors[random.nextInt(baseColors.length)];
        for (int i = 0; i < sparkCount; i++) {
            sparks.add(new Spark(random.nextFloat() * completeSpeed,
                    color, Math.toRadians(random.nextInt(180))));
        }
    }
}
