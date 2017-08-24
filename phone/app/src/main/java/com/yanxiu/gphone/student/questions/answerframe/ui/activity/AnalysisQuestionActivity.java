package com.yanxiu.gphone.student.questions.answerframe.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.base.YanxiuBaseActivity;
import com.yanxiu.gphone.student.constant.Constants;
import com.yanxiu.gphone.student.questions.answerframe.adapter.QAViewPagerAdapter;
import com.yanxiu.gphone.student.questions.answerframe.bean.BaseQuestion;
import com.yanxiu.gphone.student.questions.answerframe.bean.HomeEventMessage;
import com.yanxiu.gphone.student.questions.answerframe.bean.Paper;
import com.yanxiu.gphone.student.questions.answerframe.ui.fragment.analysisbase.AnalysisComplexExerciseBaseFragment;
import com.yanxiu.gphone.student.questions.answerframe.ui.fragment.analysisbase.AnalysisSimpleExerciseBaseFragment;
import com.yanxiu.gphone.student.questions.answerframe.ui.fragment.base.ExerciseBaseFragment;
import com.yanxiu.gphone.student.questions.answerframe.view.QAViewPager;
import com.yanxiu.gphone.student.util.DataFetcher;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 解析页面
 */
public class AnalysisQuestionActivity extends YanxiuBaseActivity implements View.OnClickListener {
    private FragmentManager mFragmentManager;
    private QAViewPager mViewPager;
    private QAViewPagerAdapter mAdapter;
    private String mComeFrom;//获取数据的来源--从哪个页面跳转过来的
    private String mKey;//获取数据的key
    private Paper mPaper;//试卷数据
    public ArrayList<BaseQuestion> mQuestions;//题目数据

    private View mBottomLayout,mBottom_line;
    private LinearLayout mPrevious_question, mNext_question;//上一题，下一题
    private TextView mNext_text;//下一题textview
    private ImageView mBackView;//返回按钮
    private TextView mErrorview;//报错

    private HomeEventMessage mHomeEventMessage=new HomeEventMessage();
    private HomeKeyEventBroadCastReceiver mHomeKeyEventBroadCastReceiver=new HomeKeyEventBroadCastReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysisquestion);
        initData();
        initView();
        initReportData();
        registerReceiver(mHomeKeyEventBroadCastReceiver, new IntentFilter(Intent. ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHomeKeyEventBroadCastReceiver);
    }

    private class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post(mHomeEventMessage);
        }
    }

    private void initData() {
        mKey = getIntent().getStringExtra(Constants.EXTRA_PAPER);
        if (TextUtils.isEmpty(mKey))
            finish();
        mComeFrom = getIntent().getStringExtra(Constants.EXTRA_COME);
        mPaper = DataFetcher.getInstance().getPaper(mKey);
        mQuestions = mPaper.getQuestions();
    }

    /**
     * 从答题报告页过来
     */
    private void initReportData() {
        if (Constants.COME_REPORT.equals(mComeFrom)) {
            mViewPager.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //答题报告过来的
                    ArrayList<Integer> levelPositions = getIntent().getIntegerArrayListExtra(Constants.EXTRA_ANALYSIS_LEVELPOSITION);
                    if (null != levelPositions && !levelPositions.isEmpty()) {
                        // 2, 跳转
                        int index = levelPositions.get(0);
                        FragmentStatePagerAdapter a1 = (FragmentStatePagerAdapter) mViewPager.getAdapter();
                        mViewPager.setCurrentItem(index,false);
                        ExerciseBaseFragment currentFragment = (ExerciseBaseFragment) a1.instantiateItem(mViewPager, index);
                        currentFragment.setUserVisibleHin2(true);


                        ArrayList<Integer> remainPositions = new ArrayList<>(levelPositions);
                        remainPositions.remove(0);
                        if (remainPositions.size() > 0) { // 表明这层依然是 复合题
                            FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) mViewPager.getAdapter();
                            AnalysisComplexExerciseBaseFragment f = (AnalysisComplexExerciseBaseFragment) a.instantiateItem(mViewPager, index);
                            f.setChildrenPositionRecursively(remainPositions);
                        }
                    }
                    mViewPager.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    private void initView() {
        mPrevious_question = (LinearLayout) findViewById(R.id.previous_question);
        mNext_question = (LinearLayout) findViewById(R.id.next_question);
        mNext_text = (TextView) findViewById(R.id.next_text);
        mBackView = (ImageView) findViewById(R.id.backview);
        mErrorview = (TextView) findViewById(R.id.errorview);
        mBottomLayout = findViewById(R.id.bottom);
        mBottom_line = findViewById(R.id.analysis_bottom_line);

        setListener();
        initViewPager();
        hiddenBottomLayout();
    }

    private void setListener() {
        mPrevious_question.setOnClickListener(this);
        mNext_question.setOnClickListener(this);
        mBackView.setOnClickListener(this);
        mErrorview.setOnClickListener(this);
    }

    private void initViewPager() {
        mFragmentManager = getSupportFragmentManager();
        mViewPager = (QAViewPager) findViewById(R.id.vp_viewPager);
        mViewPager.setOffscreenPageLimit(1);
        mAdapter = new QAViewPagerAdapter(mFragmentManager);
//        mViewPager.setFragmentManager(fm);
        Paper.generateUsedNumbersForNodes(mQuestions);
        mAdapter.setData(mQuestions);
        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnSwipeOutListener(new QAViewPager.OnSwipeOutListener() {
            @Override
            public void onSwipeOutAtEnd() {
                Log.e("TAG", "Outter swipe out at end");
            }
        });
    }

    /**
     * 当只有一个题时，隐藏bottom，并且把解析单题型fragment的bottom去掉
     */
    private void hiddenBottomLayout(){
        if(mQuestions.size() == 1 && (null == mQuestions.get(0).getChildren() || mQuestions.get(0).getChildren().size() <= 1)){
            mBottomLayout.setVisibility(View.GONE);
            mBottom_line.setVisibility(View.GONE);
        }
    }

    /**
     * 切换下一题目
     */
    public void nextQuestion() {
        ExerciseBaseFragment currentFramgent = null;//当前的Fragment
        FragmentStatePagerAdapter adapter;
        int index;//当前Fragment在外层viewPager中的index
        int size;//viewPager的总共的size
        try {
            adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();
            index = mViewPager.getCurrentItem();
            currentFramgent = (ExerciseBaseFragment) adapter.instantiateItem(mViewPager, index);
            size = adapter.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (adapter == null || index < 0 || size < 1 || mViewPager == null || currentFramgent == null)
            return;

        if (currentFramgent instanceof AnalysisComplexExerciseBaseFragment) {
            AnalysisComplexExerciseBaseFragment complexExerciseFragment = (AnalysisComplexExerciseBaseFragment) currentFramgent;
            ViewPager innerViewPager = complexExerciseFragment.getmViewPager();
            FragmentStatePagerAdapter innerAdapter = (FragmentStatePagerAdapter) innerViewPager.getAdapter();
            int innerIndex = innerViewPager.getCurrentItem();
            int innerSize = innerViewPager.getAdapter().getCount();

            if (complexExerciseFragment == null || innerViewPager == null || innerAdapter == null || innerIndex < 0 || innerSize < 1)
                return;
            /**
             * 复合题型，切换下一题，共有三种状态：
             * 1.正常切换下一小题
             * 2.处在最后一个小题时，且外部大题不是最后一道题，那么外部大题进入下一题
             * 3.处在最后一个小题，且外部大题也是最后一题，那么判断为是最后一道题，展现答题卡
             */
            if (innerIndex == (innerSize - 1) && index == (size - 1)) {
                //状态3
            } else if (innerIndex == (innerSize - 1)) {
                //状态2
                mViewPager.setCurrentItem(index + 1);
            } else {
                //状态1
                innerViewPager.setCurrentItem(innerIndex + 1);
            }

        } else if (currentFramgent instanceof AnalysisSimpleExerciseBaseFragment) {
            if (index == (size - 1)) {
                //最后一题,展示答题卡
            } else {
                //下一题
                mViewPager.setCurrentItem(index + 1);
            }
        }

    }

    /**
     * 切换上一题目
     */
    public void previousQuestion() {
        ExerciseBaseFragment currentFramgent = null;//当前的Fragment
        FragmentStatePagerAdapter adapter;
        int index;//当前Fragment在外层viewPager中的index
        int size;//viewPager的总共的size
        try {
            adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();
            index = mViewPager.getCurrentItem();
            currentFramgent = (ExerciseBaseFragment) adapter.instantiateItem(mViewPager, index);
            size = adapter.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (adapter == null || index < 0 || size < 1 || mViewPager == null || currentFramgent == null)
            return;

        if (currentFramgent instanceof AnalysisComplexExerciseBaseFragment) {
            AnalysisComplexExerciseBaseFragment complexExerciseFragment = (AnalysisComplexExerciseBaseFragment) currentFramgent;
            ViewPager innerViewPager = complexExerciseFragment.getmViewPager();
            FragmentStatePagerAdapter innerAdapter = (FragmentStatePagerAdapter) innerViewPager.getAdapter();
            int innerIndex = innerViewPager.getCurrentItem();
            int innerSize = innerViewPager.getAdapter().getCount();

            if (complexExerciseFragment == null || innerViewPager == null || innerAdapter == null || innerIndex < 0 || innerSize < 1)
                return;

            if (innerIndex >= 1) {
                //小题不是第一题时,有上一小题
                innerViewPager.setCurrentItem(innerIndex - 1);
            } else if (index >= 1) {
                //外部大题不是第一题时,有上一大题
                mViewPager.setCurrentItem(index - 1);
            }

        } else if (currentFramgent instanceof AnalysisSimpleExerciseBaseFragment) {
            if (index >= 1) {
                //不是第一大题时,有上一题
                mViewPager.setCurrentItem(index - 1);
            }
        }
    }

    /**
     * 当处在第一题和最后一题时，隐藏相应切换题目按钮
     */
    public void hiddenSwitchQuestionView() {
        ExerciseBaseFragment currentFramgent = null;//当前的Fragment
        FragmentStatePagerAdapter adapter;
        int index;//当前Fragment在外层viewPager中的index
        int size;//viewPager的总共的size
        try {
            adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();
            index = mViewPager.getCurrentItem();
            currentFramgent = (ExerciseBaseFragment) adapter.instantiateItem(mViewPager, index);
            size = adapter.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (adapter == null || index < 0 || size < 1 || mViewPager == null || currentFramgent == null)
            return;

        if (currentFramgent instanceof AnalysisComplexExerciseBaseFragment) {
            AnalysisComplexExerciseBaseFragment complexExerciseFragment = (AnalysisComplexExerciseBaseFragment) currentFramgent;
            ViewPager innerViewPager = complexExerciseFragment.getmViewPager();
            FragmentStatePagerAdapter innerAdapter = (FragmentStatePagerAdapter) innerViewPager.getAdapter();
            int innerIndex = innerViewPager.getCurrentItem();
            int innerSize = innerViewPager.getAdapter().getCount();

            if (complexExerciseFragment == null || innerViewPager == null || innerAdapter == null || innerIndex < 0 || innerSize < 1)
                return;
            /**
             * 复合题型，切换下一题，共有三种状态：
             * 1.正常切换下一小题
             * 2.处在最后一个小题时，且外部大题不是最后一道题，那么外部大题进入下一题
             * 3.处在最后一个小题，且外部大题也是最后一题，那么判断为是最后一道题，展现答题卡
             */
            if (innerIndex == (innerSize - 1) && index == (size - 1)) { //状态3
                mNext_question.setVisibility(View.GONE);
            } else {
                mNext_question.setVisibility(View.VISIBLE);
                mNext_text.setText(R.string.next_question);
            }

            if (innerIndex == 0 && index == 0) { //第一题
                mPrevious_question.setVisibility(View.GONE);
            } else {
                mPrevious_question.setVisibility(View.VISIBLE);
            }

        } else if (currentFramgent instanceof AnalysisSimpleExerciseBaseFragment) {
            if (index == (size - 1)) { //最后一题
                mNext_question.setVisibility(View.GONE);
            } else {
                mNext_question.setVisibility(View.VISIBLE);
                mNext_text.setText(R.string.next_question);
            }

            if (index == 0) { //第一题
                mPrevious_question.setVisibility(View.GONE);
            } else {
                mPrevious_question.setVisibility(View.VISIBLE);
            }
        }
    }

    public Paper getPaper() {
        return mPaper;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.previous_question:
                previousQuestion();
                break;
            case R.id.next_question:
                nextQuestion();
                break;
            case R.id.backview:
                finish();
                break;
            case R.id.errorview:
                BaseQuestion currentQuestion = getCurrentQuestion();
                String qid = currentQuestion.getQid();
                if(currentQuestion != null && !TextUtils.isEmpty(qid)){
                    AnswerErrorActicity.invoke(AnalysisQuestionActivity.this,qid);
                }
                break;
        }
    }

    /**
     * 获取当前题目
     */
    public BaseQuestion getCurrentQuestion() {
        BaseQuestion currentQuestion = null;
        ExerciseBaseFragment currentFramgent = null;//当前的Fragment
        FragmentStatePagerAdapter adapter;
        int index;//当前Fragment在外层viewPager中的index
        int size;//viewPager的总共的size
        try {
            adapter = (FragmentStatePagerAdapter) mViewPager.getAdapter();
            index = mViewPager.getCurrentItem();
            currentFramgent = (ExerciseBaseFragment) adapter.instantiateItem(mViewPager, index);
            size = adapter.getCount();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        if (adapter == null || index < 0 || size < 1 || mViewPager == null || currentFramgent == null)
            return null;

        if (currentFramgent instanceof AnalysisComplexExerciseBaseFragment) {
            AnalysisComplexExerciseBaseFragment complexExerciseFragment = (AnalysisComplexExerciseBaseFragment) currentFramgent;
            ViewPager innerViewPager = complexExerciseFragment.getmViewPager();
            FragmentStatePagerAdapter innerAdapter = (FragmentStatePagerAdapter) innerViewPager.getAdapter();
            int innerIndex = innerViewPager.getCurrentItem();
            int innerSize = innerViewPager.getAdapter().getCount();

            if (complexExerciseFragment == null || innerViewPager == null || innerAdapter == null || innerIndex < 0 || innerSize < 1)
                return null;
            currentQuestion = mQuestions.get(index).getChildren().get(innerIndex);

        } else if (currentFramgent instanceof AnalysisSimpleExerciseBaseFragment) { // 单题
            currentQuestion = mQuestions.get(index);

        }
        return currentQuestion;
    }


    /**
     * 跳转AnalysisQuestionActivity
     *
     * @param activity
     */
    public static void invoke(Activity activity, String key) {
        Intent intent = new Intent(activity, AnalysisQuestionActivity.class);
        intent.putExtra(Constants.EXTRA_PAPER, key);
        activity.startActivity(intent);
    }

    /**
     * 答题报告跳转AnalysisQuestionActivity
     *
     * @param activity
     */
    public static void invoke(Activity activity, String key, ArrayList<Integer> levelPositions) {
        Intent intent = new Intent(activity, AnalysisQuestionActivity.class);
        intent.putExtra(Constants.EXTRA_COME, Constants.COME_REPORT);
        intent.putExtra(Constants.EXTRA_PAPER, key);
        intent.putIntegerArrayListExtra(Constants.EXTRA_ANALYSIS_LEVELPOSITION, levelPositions);
        activity.startActivity(intent);
    }

}
