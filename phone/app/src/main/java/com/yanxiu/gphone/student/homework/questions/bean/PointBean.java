package com.yanxiu.gphone.student.homework.questions.bean;

import com.yanxiu.gphone.student.base.BaseBean;

import java.io.Serializable;

/**
 * Created by sunpeng on 2017/5/10.
 */

public class PointBean extends BaseBean {
    private String id;
    private String name;

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
}
