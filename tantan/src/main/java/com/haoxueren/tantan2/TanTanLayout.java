package com.haoxueren.tantan2;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class TanTanLayout extends FrameLayout {

    private View child;

    private boolean isRemoved = false;

    private AnimatorSet animatorSet;


    public TanTanLayout(Context context) {
        super(context);
    }

    public TanTanLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TanTanLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private int left;
    private int right;
    private int top;
    private int bottom;

    float downX = 0;
    float downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.getChildCount() == 0) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isRemoved && animatorSet != null) {
                    animatorSet.end();
                }
                int lastIndex = this.getChildCount() - 1;
                child = this.getChildAt(lastIndex);

                left = child.getLeft();
                right = child.getRight();
                top = child.getTop();
                bottom = child.getBottom();

                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                float dx = moveX - downX;
                float dy = moveY - downY;

                int newLeft = (int) (left + dx);
                int newRight = (int) (right + dx);
                int newTop = (int) (top + dy);
                int newBottom = (int) (bottom + dy);

                if (dx > 0) {
                    child.setRotation(10);
                } else {
                    child.setRotation(-10);
                }
                child.layout(newLeft, newTop, newRight, newBottom);
                break;
            case MotionEvent.ACTION_UP:
                int layoutWidth = this.getMeasuredWidth();
                float distance = event.getX() - downX;
                if (distance > layoutWidth / 3) {
                    isRemoved = false;
                    // float distanceX = layoutWidth - child.getLeft();
                    float distanceX = layoutWidth;
                    float factor = child.getTop() * 1.0f / child.getLeft();
                    float distanceY = distanceX * factor;
                    translate(child, distanceX, distanceY);
                } else if (-distance > layoutWidth / 3) {
                    isRemoved = false;
                    float distanceX = layoutWidth;
                    float factor = child.getTop() * 1.0f / child.getLeft();
                    float distanceY = distanceX * factor;
                    translate(child, -distanceX, -distanceY);
                } else {
                    child.setRotation(0);
                    child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }


    /**
     * 执行View的属性动画
     */
    public void translate(final View child, float distanceX, float distanceY) {
        ObjectAnimator translationX = ObjectAnimator.ofFloat(child, "TranslationX", 0, distanceX);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(child, "TranslationY", 0, distanceY);
        animatorSet = new AnimatorSet();
        animatorSet.setDuration(1000);
        animatorSet.playTogether(translationX, translationY);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int lastIndex = TanTanLayout.this.getChildCount() - 1;
                if (!isRemoved) {
                    // 移除掉滑出屏幕的View
                    View lastChild = TanTanLayout.this.getChildAt(lastIndex);
                    TanTanLayout.this.removeView(lastChild);
                    isRemoved = true;

                    // 将滑出屏幕的View重新添加进来
                    TanTanLayout.this.addView(lastChild, 0);
                    lastChild.setRotation(0);
                    lastChild.setTranslationX(0);
                    lastChild.setTranslationY(0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }
}
