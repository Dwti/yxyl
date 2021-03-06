package com.yanxiu.gphone.student.util.time;

import android.os.CountDownTimer;

import java.lang.ref.WeakReference;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/11 17:02.
 * Function :
 */

@SuppressWarnings("unused")
public class CountDownManager {

    private static final long DEFAULT_TOTALTIME = 45000;
    private static final long DEFAULT_INTERVATIME = 1000;
    private countDownTimer timer;

    public interface ScheduleListener {
        void onProgress(long progress);

        void onFinish();
    }

    /**
     * the countdown total time
     */
    private long totalTime = DEFAULT_TOTALTIME;
    /**
     * the countdown interval time
     */
    private long intervalTime = DEFAULT_INTERVATIME;
    private ScheduleListener listener;
    private static CountDownManager manager;

    public static CountDownManager getManager() {
        if (manager == null) {
            manager = new CountDownManager();
        }
        return manager;
    }

    /**
     * finished callback
     */
    public void setFinished() {
        this.listener = null;
        if (timer != null) {
            this.timer.cancel();
        }
    }

    /**
     * the totalTime in milliseconds
     */
    public CountDownManager setTotalTime(long totalTime) {
        this.totalTime = totalTime;
        return this;
    }

    /**
     * the intervalTime in milliseconds
     */
    public CountDownManager setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
        return this;
    }

    public CountDownManager setScheduleListener(ScheduleListener listener) {
        this.listener = listener;
        return this;
    }

    public void start() {
        timer = new countDownTimer(totalTime, intervalTime, listener);
        timer.start();
    }

    public void cancel() {
        try {
            timer.clear();
            timer.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class countDownTimer extends CountDownTimer {

        private WeakReference<ScheduleListener> reference;

        countDownTimer(long millisInFuture, long countDownInterval, ScheduleListener listener) {
            super(millisInFuture, countDownInterval);
            if (listener != null) {
                reference = new WeakReference<>(listener);
            }
        }

        private void clear() {
            reference = null;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (reference != null) {
                ScheduleListener listener = reference.get();
                if (listener != null) {
                    listener.onProgress(millisUntilFinished);
                }
            }
        }

        @Override
        public void onFinish() {
            if (reference != null) {
                ScheduleListener listener = reference.get();
                if (listener != null) {
                    listener.onFinish();
                }
            }
        }
    }
}

