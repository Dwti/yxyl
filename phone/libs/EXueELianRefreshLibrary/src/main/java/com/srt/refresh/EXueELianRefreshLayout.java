package com.srt.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;


/**
 * Created by 戴延枫 on 2017/8/2.
 */
public class EXueELianRefreshLayout extends BaseRefreshLayout {
    private float waveHeight;
    private float headHeight;
    private float footerHeight;
    private final int SHAKE_TIME = 1000;//wave震动时间

    //刷新头部相关view
    private View mHeadView;
    private WaveView mWaveView;
    private GifView mFloatView;
    private GifView mBubblesView;//火箭气泡
    private ImageView mLoadingView;
    private TextView mTv_tip;
    //加载更多相关view
    private View mFooterView;
    private ImageView mFooterLoadingView;

    public EXueELianRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public EXueELianRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EXueELianRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     */
    private void init(Context context, AttributeSet attrs) {
        waveHeight = context.getResources().getDimensionPixelSize(R.dimen.waveHeight);
        headHeight = context.getResources().getDimensionPixelSize(R.dimen.headHeight);
        footerHeight = context.getResources().getDimensionPixelSize(R.dimen.footerview_height);
        /**
         * attrs  需要在xml设置什么属性  自己自定义吧  啊哈哈
         */

        /**
         * 初始化headView
         */
        mHeadView = LayoutInflater.from(getContext()).inflate(R.layout.view_head, null);
        mWaveView = (WaveView) mHeadView.findViewById(R.id.draweeView);
        mFloatView = (GifView) mHeadView.findViewById(R.id.floatView);
        mBubblesView = (GifView) mHeadView.findViewById(R.id.bubblesView);
        mLoadingView = (ImageView) mHeadView.findViewById(R.id.loadingView);
        mFloatView.setMovieResource(R.drawable.pencil_gif);
        mFloatView.pause();
        mBubblesView.setMovieResource(R.drawable.bubble_gif);
        mTv_tip = (TextView) mHeadView.findViewById(R.id.tv_tip);
        /**
         * 初始化footerview
         */
        mFooterView = LayoutInflater.from(getContext()).inflate(R.layout.view_footer, null);
        mFooterLoadingView = (ImageView) mFooterView.findViewById(R.id.footerloadingView);
        /**
         * 设置波浪的高度
         */
        setWaveHeight(waveHeight);
        /**
         * 设置headView的高度
         */
        setHeaderHeight(headHeight);
        /**
         * 设置footerView的高度
         */
        setFooterHeight(footerHeight);
        /**
         * 设置headView
         */
        setHeaderView(mHeadView);
        /**
         * 设置floatView
         */
        setFloatView(mFloatView);
        /**
         * 设置loadingview
         */
        setLoadingView(mLoadingView);
        /**
         * 设置footerView
         */
        setFooterView(mFooterView);
        /**
         * 设置footerloadingview
         */
        setFooterLoadingView(mFooterLoadingView);
        /**
         * 监听波浪变化监听
         */
        setPullStateListener(new PullStateListener() {
            /**
             * 下拉中
             */
            @Override
            public void onPulling(BaseRefreshLayout baseRefreshLayout, float offsetY) {
                if (pullReleasingOA != null) {
                    pullReleasingOA.cancel();
                }
                onPullingLogic(offsetY);
            }

            /**
             * 下拉松开
             */
            @Override
            public void onPullReleasing(BaseRefreshLayout baseRefreshLayout, float offsetY) {
                if (!baseRefreshLayout.isRefreshing) {
                    onPullingLogic(offsetY);
                }
            }

            /**
             * 加载更多的上拉中回调
             */
            @Override
            public void onLoadMorePulling(BaseRefreshLayout baseRefreshLayout, float offsetY) {
                if (pullReleasingOA != null) {
                    pullReleasingOA.cancel();
                }
                if (null != mFooterLayout) {
                    mFooterLayout.setTranslationY(-offsetY);
                }
            }

            /**
             * 加载更多的pull回调
             */
            @Override
            public void onLoadMorePullReleasing(BaseRefreshLayout baseRefreshLayout, float offsetY) {
                if (null != mFooterLayout) {
                    mFooterLayout.setTranslationY(-offsetY);
                }
            }
        });

        /**
         * 松开后的监听
         */
        setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onRefresh(BaseRefreshLayout baseRefreshLayout) {
                mTv_tip.setText("刷新中...");
                mBubblesView.start();
                mWaveView.setHeadHeight((int) (headHeight));
                ValueAnimator animator = ValueAnimator.ofInt(mWaveView.getWaveHeight(), 0, -100, 0, -50, 0);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Log.i("anim", "value--->" + (int) animation.getAnimatedValue());
                        mWaveView.setWaveHeight((int) animation.getAnimatedValue());
                        mWaveView.invalidate();

                    }
                });
                animator.setInterpolator(new BounceInterpolator());
                animator.setDuration(SHAKE_TIME);
                animator.start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mBubblesView.stop();
                        mFloatView.setMovieResource(R.drawable.pencil_gif);
                        mFloatView.stop();
                        showLoadingView();
                        if (listener != null) {
                            listener.onRefresh(EXueELianRefreshLayout.this);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            /**
             * 加载更多中
             *
             * @param baseRefreshLayout
             */
            @Override
            public void onLoadMore(BaseRefreshLayout baseRefreshLayout) {
                showFooterLoadingView();
                if (listener != null) {
                    listener.onLoadMore(EXueELianRefreshLayout.this);
                }
            }
        });
    }

    /**
     * 下拉的逻辑
     *
     * @下拉了，但是还没有达到刷新的高度，然后松开手指后，已经显示的head部分，需要自动收回，这个逻辑和下拉的逻辑完全一样，所以，提取出一个方法
     */
    private void onPullingLogic(float offsetY) {
        mFloatView.updateframes(offsetY / (waveHeight + headHeight));
        float waveFraction = offsetY / waveHeight;
        float headFraction = (offsetY - waveHeight) / headHeight;
        float headW = waveHeight;
        float waveHeight = headW * Math.max(0, waveFraction);
        if (waveHeight >= headW) {
            mWaveView.setHeadHeight((int) (headHeight * Math.min(1, headFraction)));
            mWaveView.setWaveHeight((int) headW);
        } else {
            mWaveView.setHeadHeight(0);
            mWaveView.setWaveHeight((int) (headW * Math.max(0, waveFraction)));
        }
//        Log.d("pull", "onPulling: getHeadHeight ==  " + mWaveView.getHeadHeight());
//        Log.d("pull", "onPulling: getWaveHeight ==  " + mWaveView.getWaveHeight());
//        Log.e("pull", "onPulling: offsetY ==  " + offsetY);

        mWaveView.invalidate();
        if (null != mFloatView) {
            mFloatView.setTranslationY(mWaveView.getHeadHeight() + mWaveView.getWaveHeight() - 50);
        }
        if (headHeight > (int) headHeight * limitValue(1, headFraction)) {
            mTv_tip.setText("下拉刷新");
        } else {
            mTv_tip.setText("松开刷新");
        }
    }


//    public void shakeAnim(View view) {
//        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, 2, 0, -2, 0);
//        animator.setDuration(100);
//        animator.setRepeatCount(-1);
//        animator.setRepeatMode(ValueAnimator.RESTART);
//        animator.start();
//    }


    /**
     * 限定值
     *
     * @param a
     * @param b
     * @return
     */
    public float limitValue(float a, float b) {
        float valve = 0;
        final float min = Math.min(a, b);
        final float max = Math.max(a, b);
        valve = valve > min ? valve : min;
        valve = valve < max ? valve : max;
        if (b <= 1) {

        }
        return valve;
    }

    public interface RefreshListener {
        /**
         * 刷新中
         */
        void onRefresh(EXueELianRefreshLayout refreshLayout);

        /**
         * 加载更多中
         */
        void onLoadMore(EXueELianRefreshLayout refreshLayout);
    }

    private RefreshListener listener;

    public void setRefreshListener(RefreshListener listener) {
        this.listener = listener;
    }
}
