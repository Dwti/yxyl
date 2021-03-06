package com.yanxiu.gphone.student.exercise.bean;

import com.yanxiu.gphone.student.base.BaseBean;

/**
 * Created by sp on 17-7-26.
 */

public class SubjectBean extends BaseBean {
    private String id;
    private String name;
    private EditionBean data;
    private String type=""; //给BC资源用

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

    public EditionBean getData() {
        return data;
    }

    public void setData(EditionBean data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
