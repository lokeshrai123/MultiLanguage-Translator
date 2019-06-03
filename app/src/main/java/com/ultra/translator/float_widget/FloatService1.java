package com.ultra.translator.float_widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ultra.translator.R;

public class FloatService1 extends Service implements FloatView.OnBubbleChange {

    public static final String START = "start";
    public static final String STOP = "stop";
    private FloatView mFloatView;
    private FloatTranslatePanel mFloatPanel;
    private WindowManager manager;

    public FloatService1() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mFloatView == null) addFloat(this);
        Log.e("service", "onCreate");
    }

    private void addFloat(Context context) {

        int LAYOUT_FLAG;

        if (Build.VERSION.SDK_INT >= 26) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        if (mFloatView == null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0, 20,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            mFloatView = new FloatView(this);
            mFloatView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            mFloatView.setParams(params);
            mFloatView.addView();
            mFloatView.setOnBubbleChange(this);
        }

        if (mFloatPanel == null) {
            WindowManager.LayoutParams panelParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    LAYOUT_FLAG,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                            WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                    PixelFormat.TRANSLUCENT
            );
            panelParams.dimAmount = 0.5f;
            mFloatPanel = new FloatTranslatePanel(this);
            mFloatPanel.setVisibility(View.GONE);
            panelParams.gravity = Gravity.CENTER;
            if (manager == null) manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            manager.addView(mFloatPanel, panelParams);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = null;
        if (intent != null) action = intent.getAction();
        if (!TextUtils.isEmpty(action))
            if (action.equals(START)) {
                Log.e("service", "onstart");
                if (mFloatView == null) addFloat(this);
            } else if (action.equals(STOP)) {
                Log.e("service", "onstop");
                stopSelf();
            }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            manager.removeView(mFloatView);
            manager.removeView(mFloatPanel);
        } catch (Exception e) {
            Log.e("service error", e.getMessage() + "");
        }
        Log.e("service", "Destroy");
        super.onDestroy();
    }

    @Override
    public void onClick() {
        mFloatPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMove() {

    }

    @Override
    public void onIdle() {

    }
}
