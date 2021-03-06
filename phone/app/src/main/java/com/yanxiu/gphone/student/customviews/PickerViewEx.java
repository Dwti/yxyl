package com.yanxiu.gphone.student.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.yanxiu.gphone.student.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class PickerViewEx extends View {

    public static final int DEFAULT_LEFT = 0x000;
    public static final int DEFAULT_CENTER = 0x001;
    public static final int DEFAULT_RIGHT = 0x002;

    /**
     * 新增字段 控制是否首尾相接循环显示 默认为循环显示
     */
    private boolean loop = false;

    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 3.5f;
    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 50;

    private Context mContext;

    private List<String> mDataList;
    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelected;
    private Paint mPaint, nPaint;

    private float mMaxTextSize = 80;
    private float mMinTextSize = 40;

    private float mMaxTextAlpha = 255;
    private float mMinTextAlpha = 120;

    private int mViewHeight;
    private int mViewWidth;

    private int locationType = DEFAULT_CENTER;

    private float mLastDownY;
    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;
    private boolean isInit = false;
    private onSelectListener mSelectListener;
    private Timer timer;
    private MyTimerTask mTask;

    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            invalidate();
        }

    };
    private boolean canScroll = true;

    public PickerViewEx(Context context) {
        super(context);
        init(context);
    }

    public PickerViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PickerView);
            mMaxTextSize = array.getDimensionPixelSize(R.styleable.PickerView_choose_max_textsize, 40);
            mMinTextSize = array.getDimensionPixelSize(R.styleable.PickerView_choose_min_textsize, 20);
            array.recycle();
        }
        init(context);
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    private void performSelect() {
        if (mSelectListener != null)
            mSelectListener.onSelect(PickerViewEx.this, mDataList.get(mCurrentSelected), mCurrentSelected);
    }

    public void setData(List<String> datas) {
        mDataList = datas;
        mCurrentSelected = datas.size() / 3;
        invalidate();
    }

    /**
     * 选择选中的item的index
     *
     * @param selected
     */
    public void setSelected(int selected) {
        mCurrentSelected = selected;
        if (loop) {
            int distance = mDataList.size() / 2 - mCurrentSelected;
            if (distance < 0)
                for (int i = 0; i < -distance; i++) {
                    moveHeadToTail();
                    mCurrentSelected--;
                }
            else if (distance > 0)
                for (int i = 0; i < distance; i++) {
                    moveTailToHead();
                    mCurrentSelected++;
                }
        }
        invalidate();
    }

    /**
     * 选择选中的内容
     *
     * @param mSelectItem
     */
    public void setSelected(String mSelectItem) {
        for (int i = 0; i < mDataList.size(); i++)
            if (mDataList.get(i).equals(mSelectItem)) {
                setSelected(i);
                break;
            }
    }

    public int getSelectedIndex() {
        return mCurrentSelected;
    }

    public void setTextLocation(int location_type) {
        this.locationType = location_type;
        if (locationType == DEFAULT_LEFT) {
            mPaint.setTextAlign(Align.LEFT);
            nPaint.setTextAlign(Align.LEFT);
        } else if (locationType == DEFAULT_CENTER) {
            mPaint.setTextAlign(Align.CENTER);
            nPaint.setTextAlign(Align.CENTER);
        } else if (locationType == DEFAULT_RIGHT) {
            mPaint.setTextAlign(Align.RIGHT);
            nPaint.setTextAlign(Align.RIGHT);
        }
    }

    private void moveHeadToTail() {
        if (loop) {
            String head = mDataList.get(0);
            mDataList.remove(0);
            mDataList.add(head);
        }
    }

    private void moveTailToHead() {
        if (loop) {
            String tail = mDataList.get(mDataList.size() - 1);
            mDataList.remove(mDataList.size() - 1);
            mDataList.add(0, tail);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewHeight = h;
        mViewWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 按照View的高度计算字体大小
//        mMaxTextSize = mViewHeight / 7f;
//        mMinTextSize = mMaxTextSize / 2.2f;
        isInit = true;
        invalidate();
    }

    private void init(Context context) {
        this.mContext = context;
        timer = new Timer();
        mDataList = new ArrayList<String>();
        //第一个paint
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.FILL);
        mPaint.setTextAlign(Align.RIGHT);
        mPaint.setFakeBoldText(true);
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.color_ffffff));

        //第二个paint
        nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Style.FILL);
        nPaint.setFakeBoldText(true);
        nPaint.setTextAlign(Align.RIGHT);
        nPaint.setColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
    }

    private float getDraw_X() {
        if (locationType == DEFAULT_LEFT) {
            return 0;
        } else if (locationType == DEFAULT_CENTER) {
            return (float) (mViewWidth / 2.0);
        } else if (locationType == DEFAULT_RIGHT) {
            return mViewWidth;
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit)
            drawLines(canvas);
        if (mDataList.size() > 0) {
            drawData(canvas);
        }
    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize;
        mPaint.setTextSize(size);
        mPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = getDraw_X();
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        FontMetricsInt fmi = mPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        String text = shearString(mDataList.get(mCurrentSelected), mViewWidth, mPaint);
        canvas.drawText(text, x, baseline, mPaint);
        // 绘制上方data
        for (int i = 1; i < 3; i++) {
            if ((mCurrentSelected - i) >= 0) {
                drawOtherText(canvas, i, -1);
            }
        }
        // 绘制下方data
        for (int i = 1; i < 3; i++) {
            if ((mCurrentSelected + i) < mDataList.size()) {
                drawOtherText(canvas, i, 1);
            }
        }
    }

    private String shearString(String text, int max_width, Paint paint) {
        int width = (int) paint.measureText(text + "...");
        if (width > max_width) {
            while (width >= max_width) {
                text = text.substring(0, text.length() - 1);
                width = (int) paint.measureText(text + "...");
            }
            return text + "...";
        } else {
            return text;
        }
    }

    private void drawLines(Canvas canvas) {
        Paint nPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        nPaint.setStyle(Style.STROKE);
        nPaint.setTextAlign(Align.CENTER);
        nPaint.setColor(ContextCompat.getColor(mContext, R.color.color_ffffff));
        nPaint.setStrokeWidth(4);
        nPaint.setTextSize(mMinTextSize);
        Paint.FontMetrics metrics = nPaint.getFontMetrics();
        float text_height = metrics.descent - metrics.ascent;
        float extra = mMinTextSize / 2;

        RectF rect = new RectF(0 + 2 + extra, mViewHeight / 2 - (int) text_height - 5 - extra, mViewWidth - 2 - extra, mViewHeight / 2 + (int) text_height + 5 + extra);
        canvas.drawRoundRect(rect, 6, 6, nPaint);
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = (float) (MARGIN_ALPHA * mMinTextSize * position + type
                * mMoveLen);
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mMaxTextSize - mMinTextSize) * scale + mMinTextSize + (3 - position) * 5;
        nPaint.setTextSize(size);
        nPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d - type * (position - 1) * 30);
        FontMetricsInt fmi = nPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        String text = shearString(mDataList.get(mCurrentSelected + type * position), mViewWidth, mPaint);
        canvas.drawText(text, getDraw_X(), baseline, nPaint);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveLen += (event.getY() - mLastDownY);

                if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
                    if (!loop && mCurrentSelected == 0) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!loop) mCurrentSelected--;
                    // 往下滑超过离开距离
                    moveTailToHead();
                    mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
                } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
                    if (mCurrentSelected == mDataList.size() - 1) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!loop) mCurrentSelected++;
                    // 往上滑超过离开距离
                    moveHeadToTail();
                    mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
                }

                mLastDownY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

//    private void doMove(MotionEvent event) {
//
//        mMoveLen += (event.getY() - mLastDownY);
//
//        if (mMoveLen > MARGIN_ALPHA * mMinTextSize / 2) {
//            // 往下滑超过离开距离
//            moveTailToHead();
//            mMoveLen = mMoveLen - MARGIN_ALPHA * mMinTextSize;
//        } else if (mMoveLen < -MARGIN_ALPHA * mMinTextSize / 2) {
//            // 往上滑超过离开距离
//            moveHeadToTail();
//            mMoveLen = mMoveLen + MARGIN_ALPHA * mMinTextSize;
//        }
//
//        mLastDownY = event.getY();
//        invalidate();
//    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(updateHandler);
        timer.schedule(mTask, 0, 10);
    }

    class MyTimerTask extends TimerTask {
        Handler handler;

        public MyTimerTask(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage());
        }

    }

    public interface onSelectListener {
        void onSelect(View view, String text, int selectId);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (canScroll)
            return super.dispatchTouchEvent(event);
        else
            return false;
    }

    /**
     * 新增字段 控制内容是否首尾相连
     * by liuli
     *
     * @param isLoop
     */
    public void setIsLoop(boolean isLoop) {
        loop = isLoop;
    }
}