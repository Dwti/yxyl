package com.yanxiu.gphone.student.login.request;


import com.yanxiu.gphone.student.base.EXueELianBaseRequest;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/18 15:02.
 * Function :
 */
public class ForgetPassWordRequest extends EXueELianBaseRequest {
    public String mobile;
    public String code;
    public String type;

    @Override
    protected String urlPath() {
        return "/user/firstStepCommit.do";
    }
}
