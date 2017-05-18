package com.yanxiu.gphone.student.homepage.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.homework.classmanage.InputClassNumberFragment;

/**
 * 首页 作业列表Fragment
 */
public class HomeworkFragment extends Fragment {
    private final static String TAG = HomeworkFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homework, container, false);
        ImageView mJoinClass = (ImageView) view.findViewById(R.id.iv_join_class);
        mJoinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(getId(), InputClassNumberFragment.getInstance()).commit();
            }
        });
        return view;
    }
}
