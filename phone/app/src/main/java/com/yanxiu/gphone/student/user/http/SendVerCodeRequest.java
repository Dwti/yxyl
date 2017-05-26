package com.yanxiu.gphone.student.user.http;

import com.test.yanxiu.network.RequestBase;
import com.yanxiu.gphone.student.constant.Constants;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/18 12:36.
 * Function :
 */
public class SendVerCodeRequest extends RequestBase {
    public String mobile;
    public String type;
    public String pcode= Constants.pcode;
    public String version=Constants.version;
    public String osType=Constants.osType;

    @Override
    protected boolean shouldLog() {
        return false;
    }

    @Override
    protected String urlServer() {
        return "http://mobile.hwk.yanxiu.com/app";
    }

    @Override
    protected String urlPath() {
        return "/user/produceCode.do";
    }
}