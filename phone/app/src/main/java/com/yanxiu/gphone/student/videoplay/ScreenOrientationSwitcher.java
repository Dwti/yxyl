package com.yanxiu.gphone.student.videoplay;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.OrientationEventListener;

import java.lang.ref.WeakReference;

/**
 * Created by cailei on 12/06/2017.
 */

public class ScreenOrientationSwitcher extends OrientationEventListener {
    private static final long MAX_CHECK_INTERVAL = 3000;


    public static interface OnChangeListener {
        void onChanged(int requestedOrientation);
    }


    private WeakReference<Context> mContextRef;
    private boolean mIsSupportGravity = false;
    private int mCurrOrientation = ORIENTATION_UNKNOWN;
    private long mLastCheckTimestamp = 0;
    private OnChangeListener mChangeListener;


    public ScreenOrientationSwitcher(Context context) {
        super(context);
        mContextRef = new WeakReference<Context>(context);
    }

    public ScreenOrientationSwitcher(Context context, int rate) {
        super(context, rate);
        mContextRef = new WeakReference<Context>(context);
    }

    public void setChangeListener(OnChangeListener listener) {
        mChangeListener = listener;
    }

    public int getCurrOrientation() {
        return mCurrOrientation;
    }

    public static boolean isScreenAutoRotate(Context context) {
        int gravity = 0;
        try {
            gravity = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gravity == 1;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        Context context = mContextRef.get();
        if (context == null || !(context instanceof Activity)) {
            return;
        }

        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp - mLastCheckTimestamp > MAX_CHECK_INTERVAL) {
            mIsSupportGravity = isScreenAutoRotate(context);
            mLastCheckTimestamp = currTimestamp;
        }

        if (!mIsSupportGravity) {
            return;
        }

        if (orientation == ORIENTATION_UNKNOWN) {
            return;
        }

        int requestOrientation = ORIENTATION_UNKNOWN;
        if (orientation > 350 || orientation < 10) {
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if (orientation > 80 && orientation < 100) {
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else if (orientation > 260 && orientation < 280) {
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            return;
        }

        if (requestOrientation == mCurrOrientation) {
            return;
        }

        boolean needNotify = mCurrOrientation != ORIENTATION_UNKNOWN;
        mCurrOrientation = requestOrientation;

        if (needNotify) {
            if (mChangeListener != null) {
                mChangeListener.onChanged(requestOrientation);
            } else {
                Activity activity = (Activity) context;
                activity.setRequestedOrientation(requestOrientation);
            }
        }
    }
}
