package com.yanxiu.gphone.student.questions.answerframe.ui.fragment;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.questions.answerframe.bean.BaseQuestion;
import com.yanxiu.gphone.student.util.HtmlImageGetter;

/**
 * Created by 戴延枫 on 2017/5/5.
 * 单题型Frament基类
 */

public abstract class SimpleExerciseBaseFragment extends ExerciseBaseFragment {

    /**
     * 如果是只有一个子题的复合题，显示大题的题干
     *(所有单题型都需要支持该方法)
     * @param view
     */
    public void initComplexStem(View view, BaseQuestion data) {
        LinearLayout complex_stem_layout = (LinearLayout) view.findViewById(R.id.complex_stem_layout);
        TextView complex_stem = (TextView) view.findViewById(R.id.complex_stem);
        String complexStem = data.getStem_complexToSimple();
        if (!TextUtils.isEmpty(complexStem)) {
            Spanned spanned = Html.fromHtml(complexStem, new HtmlImageGetter(complex_stem), null);
            complex_stem.setText(spanned);
            complex_stem_layout.setVisibility(View.VISIBLE);
        }
    }
}
