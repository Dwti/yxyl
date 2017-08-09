package com.yanxiu.gphone.student.questions.subjective;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.common.activity.PhotoActivity;
import com.yanxiu.gphone.student.constant.Constants;
import com.yanxiu.gphone.student.customviews.AlbumGridView;
import com.yanxiu.gphone.student.customviews.spantextview.SubjectClozeTextView;
import com.yanxiu.gphone.student.questions.answerframe.bean.BaseQuestion;
import com.yanxiu.gphone.student.questions.answerframe.ui.fragment.analysisbase.AnalysisSimpleExerciseBaseFragment;
import com.yanxiu.gphone.student.questions.answerframe.util.QuestionUtil;
import com.yanxiu.gphone.student.util.StemUtil;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/14 11:02.
 * Function :
 */
public class SubjectiveWrongFragment extends AnalysisSimpleExerciseBaseFragment implements AlbumGridView.onClickListener {

    private SubjectiveQuestion mData;
    private SubjectClozeTextView mQuestionView;
    private View mAnswerView;
    private AlbumGridView mSubjectView;
    private TextView mNoPictureView;

    @Override
    public void setData(BaseQuestion node) {
        super.setData(node);
        mData = (SubjectiveQuestion) node;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && mData == null) {
            setData((SubjectiveQuestion) savedInstanceState.getSerializable(KEY_NODE));
        }
    }

    @Override
    public View addAnswerView(LayoutInflater inflater, @Nullable ViewGroup container) {
        mAnswerView = inflater.inflate(R.layout.fragment_analysis_subjective, container, false);
        return mAnswerView;
    }

    @Override
    public void initAnswerView(LayoutInflater inflater, @Nullable ViewGroup container) {
        initView();
        initData();
        listener();
    }

    @Override
    public void initAnalysisView() {
        String answer = "";
        if (mData.getSubjectAnswer() != null) {
            for (String s : mData.getSubjectAnswer()) {
                answer += s;
            }
        }
        if (mData.getTypeId() == QuestionUtil.QUESTION_TYP.QUESTION_FILL_BLANKS.type || mData.getTypeId() == QuestionUtil.QUESTION_TYP.QUESTION_TRANSLATION.type || mData.getTypeId() == QuestionUtil.QUESTION_TYP.QUESTION_SUBJECTSWERE.type) {
            String result;
            if (mData.getScore() == 5) {
                result = getString(R.string.correct);
                showAnswerResultView(true, null, result);
            } else {
                result = getString(R.string.wrong);
                showAnswerResultView(false, null, result);
            }
        } else {
            showScoreView(String.valueOf(mData.getScore()));
        }
        showVoiceScoldedView(mData.getAudioList());
        showDifficultyview(mData.getStarCount());
        showAnswerView(answer);
        showAnalysisview(mData.getQuestionAnalysis());
        showPointView(mData.getPointList());
    }

    private void initView() {
        mQuestionView = (SubjectClozeTextView) mAnswerView.findViewById(R.id.tv_question);
        mSubjectView = (AlbumGridView) mAnswerView.findViewById(R.id.ag_image);
        mNoPictureView = (TextView) mAnswerView.findViewById(R.id.no_picture);
    }

    private void initData() {
        String string = StemUtil.initClozeStem(mData.getStem());
        mQuestionView.setText(string);
        if (mData.answerList.size() > 0) {
            mNoPictureView.setVisibility(View.GONE);
            mSubjectView.setData(mData.answerList);
            mSubjectView.setCanAddItem(false);
        } else {
            mNoPictureView.setVisibility(View.VISIBLE);
            mSubjectView.setVisibility(View.GONE);
        }
    }

    private void listener() {
        mSubjectView.addClickListener(this);
    }

    @Override
    public void onClick(int Type, int position) {
        switch (Type) {
            case AlbumGridView.TYPE_IMAGE:
                mToVoiceIsIntent = true;
                PhotoActivity.LaunchActivity(getContext(), mData.answerList, position, SubjectiveWrongFragment.this.hashCode(), PhotoActivity.DELETE_CANNOT);
                break;
        }
    }

}