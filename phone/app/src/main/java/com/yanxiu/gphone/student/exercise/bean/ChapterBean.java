package com.yanxiu.gphone.student.exercise.bean;

import com.yanxiu.gphone.student.base.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sp on 17-7-28.
 */

public class ChapterBean extends BaseBean{
    protected String id;
    protected String name;
    protected String question_num;
    protected List<ChapterBean> children = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestion_num() {
        return question_num;
    }

    public void setQuestion_num(String question_num) {
        this.question_num = question_num;
    }

    public List<ChapterBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChapterBean> children) {
        this.children = children;
    }
}
