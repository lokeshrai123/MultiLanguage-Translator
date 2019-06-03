package com.ultra.translator.float_widget;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * Created by ASUS on 16-Oct-17.
 */

public class FloatView extends android.support.v7.widget.AppCompatImageView {
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private AutoMoveAnimation moveAnimation;
    private AutoHideAnimation hideAnimation;
    private OnBubbleChange onBubbleChange;
    private int windowWidth;
    private float xPos, yPos, downX, downY;
    private long startTime;

    public FloatView(Context context) {
        super(context);
        initView(context);
    }

    public FloatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FloatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        moveAnimation = new AutoMoveAnimation();
        hideAnimation = new AutoHideAnimation();
    }

    public void addView() {
        windowManager.addView(this, params);
    }

    public WindowManager.LayoutParams getParams() {
        return params;
    }

    public void setOnBubbleChange(OnBubbleChange onBubbleChange) {
        this.onBubbleChange = onBubbleChange;
    }

    public void setParams(WindowManager.LayoutParams params) {
        this.params = params;
    }

    private void move(float deltaX, float deltaY) {
        params.x += deltaX;
        params.y += deltaY;
        windowManager.updateViewLayout(this, params);
    }

    private void hide() {
        this.setAlpha(0.5f);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getSize();
        gotoWall();
    }

    private void getSize() {
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        windowWidth = point.x;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (System.currentTimeMillis() - startTime < 150
                        && isNotTranslate(downX, downY, event.getRawX(), event.getRawY())) {
                    if (onBubbleChange != null) onBubbleChange.onClick();
                }
                if (onBubbleChange != null) onBubbleChange.onIdle();
                hideAnimation.start();
                gotoWall();
                showAnimUp();
                return false;
            }
            case MotionEvent.ACTION_DOWN: {
                startTime = System.currentTimeMillis();
                downX = event.getRawX();
                downY = event.getRawY();
                xPos = params.x;
                yPos = params.y;
                showAnimDown();
                hideAnimation.stop();
                moveAnimation.stop();
                return false;
            }
            case MotionEvent.ACTION_MOVE: {
                if (onBubbleChange != null) onBubbleChange.onMove();
                params.x = (int) (xPos + event.getRawX() - downX);
                params.y = (int) (yPos + event.getRawY() - downY);
                updateViewPositon();
                return false;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean isNotTranslate(float downX, float downY, float rawX, float rawY) {
        return (Math.abs(downX - rawX) < 5) && (Math.abs(downY - rawY) < 5);
    }

    private void showAnimDown() {
        this.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.8f).setDuration(100).start();
    }

    private void showAnimUp() {
        this.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(100).start();
    }

    private void updateViewPositon() {
        windowManager.updateViewLayout(this, params);
    }

    private void gotoWall() {
        float wallX = getParams().x >= 0 ? windowWidth / 2 : (-windowWidth / 2);
        moveAnimation.start(wallX, getParams().y);
    }

    public interface OnBubbleChange {
        void onClick();

        void onMove();

        void onIdle();
    }

    private class AutoMoveAnimation implements Runnable {
        Handler handler = new Handler(Looper.getMainLooper());
        long startTime;
        float desX, desY;

        private void start(float desX, float desY) {
            startTime = System.currentTimeMillis();
            this.desX = desX;
            this.desY = desY;
            handler.post(this);
        }

        @Override
        public void run() {
            float time = Math.min(1, (System.currentTimeMillis() - startTime) / 1000f);
            float deltaX = (desX - getParams().x) * time;
            float deltaY = (desY - getParams().y) * time;
            move(deltaX, deltaY);
            if (time < 1) handler.post(this);
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }


    private class AutoHideAnimation implements Runnable {
        Handler handler = new Handler(Looper.getMainLooper());

        private void start() {
            handler.postDelayed(this, 3000);
        }

        @Override
        public void run() {
            hide();
            handler.removeCallbacks(this);
        }


        private void stop() {
            handler.removeCallbacks(this);
        }
    }

}
