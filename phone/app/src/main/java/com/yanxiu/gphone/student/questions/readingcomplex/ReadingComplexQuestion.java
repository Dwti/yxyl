package com.yanxiu.gphone.student.questions.readingcomplex;


import com.yanxiu.gphone.student.questions.answerframe.bean.BaseQuestion;
import com.yanxiu.gphone.student.questions.answerframe.util.QuestionShowType;
import com.yanxiu.gphone.student.questions.bean.PaperTestBean;
import com.yanxiu.gphone.student.questions.answerframe.ui.fragment.base.ExerciseBaseFragment;
import com.yanxiu.gphone.student.questions.listencomplex.ListenRedoFragment;

/**
 * Created by sunpeng on 2017/5/11.
 */

public class ReadingComplexQuestion extends BaseQuestion {
    public ReadingComplexQuestion(PaperTestBean bean, QuestionShowType showType, String paperStatus) {
        super(bean, showType,paperStatus);
    }

    @Override
    public ExerciseBaseFragment answerFragment() {
        return new ReadingAnswerComplexFragment();
    }

    @Override
    public ExerciseBaseFragment analysisFragment() {
        //解析
        return new ReadingAnalysisComplexFragment();
    }

    @Override
    public ExerciseBaseFragment wrongFragment() {
        return new ReadingWrongComplexFragment();
    }

    @Override
    public ExerciseBaseFragment redoFragment() {
        return new ReadingRedoFragment();
    }

    @Override
    public Object getAnswer() {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }
}
