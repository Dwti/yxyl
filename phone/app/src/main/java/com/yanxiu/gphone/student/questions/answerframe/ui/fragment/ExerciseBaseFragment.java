package com.yanxiu.gphone.student.questions.answerframe.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.db.SaveAnswerDBHelper;
import com.yanxiu.gphone.student.questions.answerframe.bean.AnswerBean;
import com.yanxiu.gphone.student.questions.answerframe.ui.activity.AnswerQuestionActivity;
import com.yanxiu.gphone.student.questions.answerframe.bean.BaseQuestion;
import com.yanxiu.gphone.student.questions.answerframe.bean.Paper;
import com.yanxiu.gphone.student.questions.answerframe.listener.IExercise;
import com.yanxiu.gphone.student.questions.answerframe.util.FragmentUserVisibleController;
import com.yanxiu.gphone.student.util.StringUtil;
import com.yanxiu.gphone.student.util.TextTypefaceUtil;

import org.json.JSONArray;

import java.util.ArrayList;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * Created by 戴延枫 on 2017/5/5.
 * 习题Frament基类
 */

public abstract class ExerciseBaseFragment extends Fragment implements IExercise, FragmentUserVisibleController.UserVisibleCallback {
    public final String TAG = this.getClass().getSimpleName();

    public static final String KEY_NODE = "key_question";

    public BaseQuestion mBaseQuestion;

    public long mTotalTime;//总计时间
    public long mStartTime;//开始时间
    public long mEndTime;//结束时间

    private FragmentUserVisibleController userVisibleController;

    public TextView mQaNumber; //题号
    public TextView mQaName; //题目名称

    public ExerciseBaseFragment() {
        userVisibleController = new FragmentUserVisibleController(this, this);
    }

    @Override
    public void setData(BaseQuestion node) {
        mBaseQuestion = node;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userVisibleController.activityCreated();
    }

    public void setQaNumber(View v) {
        mQaNumber = (TextView) v.findViewById(R.id.qa_number);
        String str = mBaseQuestion.numberStringForShow();
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof ComplexExerciseBaseFragment) {
            mQaNumber.setTextColor(getResources().getColor(R.color.color_999999));
            TextTypefaceUtil.setViewTypeface(TextTypefaceUtil.TypefaceType.METRO_PLAY,mQaNumber);
        }
        mQaNumber.setText(str);
    }

    /**
     * 设置题目标题
     * 单题型和复合题的题干部分，默认显示template；
     * 复合题的子题，题目标题如果不显示template,传入name；
     *
     * @param v
     */
    public void setQaName(View v) {
        mQaName = (TextView) v.findViewById(R.id.qa_name);
        String templateName;
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof ComplexExerciseBaseFragment) {
            templateName = getString(R.string.question);
            TextPaint tp = mQaName.getPaint();
            tp.setTypeface(DEFAULT_BOLD);
            mQaName.setTextColor(getResources().getColor(R.color.color_333333));
        } else {
            templateName = StringUtil.getTemplateName(mBaseQuestion.getTemplate());
        }
        mQaName.setText(templateName);
    }

    @Override
    public void onResume() {
        super.onResume();
        userVisibleController.resume(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        userVisibleController.pause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        userVisibleController.setUserVisibleHint(isVisibleToUser);
    }


    public void setUserVisibleHin2(boolean is) {
        userVisibleController.resume(is);
    }

    @Override
    public void setWaitingShowToUser(boolean waitingShowToUser) {
        userVisibleController.setWaitingShowToUser(waitingShowToUser);
    }

    @Override
    public boolean isWaitingShowToUser() {
        return userVisibleController.isWaitingShowToUser();
    }

    @Override
    public boolean isVisibleToUser() {
        return userVisibleController.isVisibleToUser();
    }

    @Override
    public void callSuperSetUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onVisibleToUserChanged(boolean isVisibleToUser, boolean invokeInResumeOrPause) {
        Log.w(TAG, "onVisibilityChangedToUser: " + "_______" + this.getClass().getSimpleName() + " == " + isVisibleToUser);
        if (isVisibleToUser) {
            //用户可见，计时开始
            mStartTime = System.currentTimeMillis();
            hiddenSwitchQuestionView();
            if (!invokeInResumeOrPause) {
                //是左右滑动
                updateProgress();//进入屏幕，更新进度条
            }
        } else {
            //不可见，计时结束
            mEndTime = System.currentTimeMillis();
            calculateExerciseTime();
        }
        onVisibilityChangedToUser(isVisibleToUser, invokeInResumeOrPause);
    }

    /**
     * 当Fragment对用户的可见性发生了改变的时候就会回调此方法
     *
     * @param isVisibleToUser       true：用户能看见当前Fragment；false：用户看不见当前Fragment
     * @param invokeInResumeOrPause true：发生在onResume或onPause方法里；false：本次回调发生在setUserVisibleHintMethod方法里
     */
    public abstract void onVisibilityChangedToUser(boolean isVisibleToUser, boolean invokeInResumeOrPause);

    /**
     * 计算题目时间
     */
    public void calculateExerciseTime() {
        long exerciseTime = mEndTime - mStartTime;
        //Todo 计算时间及保存
        mTotalTime += exerciseTime;
    }

    /**
     * 切换下一题目
     */
    public void nextQuestion() {
        if (getActivity() instanceof AnswerQuestionActivity) {
            AnswerQuestionActivity acticity = (AnswerQuestionActivity) getActivity();
            acticity.nextQuestion();
        }

    }

    /**
     * 切换上一题目
     */
    public void previousQuestion() {
        if (getActivity() instanceof AnswerQuestionActivity) {
            AnswerQuestionActivity acticity = (AnswerQuestionActivity) getActivity();
            acticity.previousQuestion();
        }
    }

    /**
     * 当处在第一题和最后一题时，隐藏相应切换题目按钮
     */
    public void hiddenSwitchQuestionView() {
        if (getActivity() instanceof AnswerQuestionActivity) {
            AnswerQuestionActivity acticity = (AnswerQuestionActivity) getActivity();
            acticity.hiddenSwitchQuestionView();
        }

    }

    /**
     * 保存答案
     */
    public void saveAnswer(BaseQuestion question) {
        ArrayList<Integer> LevelPositions = question.getLevelPositions();//获取当前节点（题号）数据，通过节点可以判断出处在ArrayList的位置
        if (LevelPositions == null || LevelPositions.size() < 1)
            return;
        int outIndex = -1;//大题index
        int innerIndex = -1;//小题index
        if (getActivity() instanceof AnswerQuestionActivity) {
            AnswerQuestionActivity acticity = (AnswerQuestionActivity) getActivity();
            Paper paper = acticity.getPaper();//获取paper数据
            ArrayList<BaseQuestion> quesitonList = paper.getQuestions();//获取试题数据
            if (LevelPositions.size() == 1) { //单题型
                outIndex = LevelPositions.get(0);
            } else if (LevelPositions.size() == 2) { //复合题
                outIndex = LevelPositions.get(0);
                innerIndex = LevelPositions.get(1);
            } else {
                return;
            }
            BaseQuestion outQuestion = quesitonList.get(outIndex);//大题数据
            BaseQuestion innerQuestion;//小题数据
            ArrayList<BaseQuestion> childrenQusetion;//小题集合
            if (innerIndex == -1) { //单题型
                outQuestion = question;
            } else { //复合题,需要取到当前小题
                childrenQusetion = outQuestion.getChildren();
                if (childrenQusetion == null || childrenQusetion.size() < 1) {
                    //出错，既然是复合题，必须有小题
                    return;
                }
                innerQuestion = childrenQusetion.get(innerIndex);
                innerQuestion = question;
            }
            //TODO 后续保存逻辑
            Gson gson = new Gson();
            Object ansewr = question.getAnswer();
            if(null != ansewr){
                String json = gson.toJson(ansewr);//转化成json，保存该json
                AnswerBean bean = new AnswerBean();
                bean.setAid(SaveAnswerDBHelper.makeId(question));
                bean.setAnswerJson(json);
                Log.e("dyf", "json = " +json);
                boolean is = SaveAnswerDBHelper.save(bean);
                Log.e("dyf", "is = " +is);
                try{
                    JSONArray js = new JSONArray(json);
                    Log.e("dyf", "json = " +js.length());
                }catch (Exception e){

                }

//                String result = SaveAnswerDBHelper.getAnswerJson(SaveAnswerDBHelper.makeId(question));
//                Log.e("dyf", "result = " +result);
            }

        }
    }

    /**
     * 更新答题进度条
     */
    public void updateProgress() {
        int answeredCount = 0;
        if (getActivity() instanceof AnswerQuestionActivity) {
            AnswerQuestionActivity acticity = (AnswerQuestionActivity) getActivity();
            Paper paper = acticity.getPaper();//获取paper数据
            ArrayList<BaseQuestion> quesitonList = paper.getQuestions();//获取试题数据
            for (int i = 0; i < quesitonList.size(); i++) { //遍历大题
                ArrayList<BaseQuestion> childrenList = quesitonList.get(i).getChildren();//小题集合
                if (childrenList == null || childrenList.size() < 1) { //单题型
                    if (quesitonList.get(i).getIsAnswer())
                        answeredCount++;
                }else{ //复合题
                    if(childrenList == null || childrenList.size() < 1){
                        //出错，既然是复合题，必须有小题
                        return;
                    }else{ //遍历小题
                        for(int j = 0;j < childrenList.size(); j++){
                            if(childrenList.get(j).getIsAnswer())
                                answeredCount++;
                        }
                    }
                }
            }
            acticity.getProgressView().updateProgress(answeredCount);
        }
    }
}
