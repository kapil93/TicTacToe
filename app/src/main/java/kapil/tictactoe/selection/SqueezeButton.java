package kapil.tictactoe.selection;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * This is a custom button which gives visual feedback on touch or click.
 */

class SqueezeButton extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private static final int MAX_CLICK_DURATION = 600;

    private ValueAnimator scaleIn;
    private ValueAnimator scaleOut;

    private OnClickListener onClickListener;

    private RectF touchToleranceBounds;

    private boolean touching;
    private boolean clickCancelled;

    private long touchDownTime;

    public SqueezeButton(Context context) {
        super(context);
        init();
    }

    public SqueezeButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SqueezeButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        scaleIn = new ValueAnimator();
        scaleIn.setFloatValues(1, 0.85f);
        scaleIn.setDuration(50);
        scaleIn.addUpdateListener(this);
        scaleIn.addListener(this);

        scaleOut = new ValueAnimator();
        scaleOut.setFloatValues(0.85f, 1);
        scaleOut.setDuration(150);
        scaleOut.addUpdateListener(this);
        scaleOut.addListener(this);

        touchToleranceBounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        touchToleranceBounds.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (onClickListener == null) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                touchDownTime = Calendar.getInstance().getTimeInMillis();
                touching = true;
                clickCancelled = false;
                scaleIn.start();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((!touchToleranceBounds.contains(x, y)) && (!clickCancelled)) {
                    clickCancelled = true;
                    if (scaleIn.isRunning()) {
                        touching = false;
                    } else {
                        scaleOut.start();
                    }
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touching = false;
                if (!scaleIn.isRunning() && (!clickCancelled)) {
                    scaleOut.start();
                }
                break;
        }

        return true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onClickListener = l;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float scale = (float) animation.getAnimatedValue();
        setScaleX(scale);
        setScaleY(scale);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == scaleIn) {
            if (!touching) {
                scaleOut.start();
            }
        } else if (animation == scaleOut) {
            long durationOfClick = Calendar.getInstance().getTimeInMillis() - touchDownTime;
            if ((!clickCancelled) && (durationOfClick <= MAX_CLICK_DURATION)) {
                onClickListener.onClick(SqueezeButton.this);
            }
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
