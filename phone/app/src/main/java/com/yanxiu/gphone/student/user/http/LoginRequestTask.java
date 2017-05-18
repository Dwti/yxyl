package com.yanxiu.gphone.student.user.http;

import com.test.yanxiu.network.RequestBase;
import com.yanxiu.gphone.student.constant.Constants;

/**
 * Created by Canghaixiao.
 * Time : 2017/5/17 10:17.
 * Function :
 */

public class LoginRequestTask extends RequestBase {
    public String mobile;
    public String password;
    public String deviceId= "-";
    public String pcode=Constants.pcode;
    public String version=Constants.version;
    public String osType=Constants.osType;

    @Override
    protected boolean shouldLog() {
        return false;
    }

    @Override
    protected String urlServer() {
        return "http://mobile.hwk.yanxiu.com/app/user/";
    }

    @Override
    protected String urlPath() {
        return "login.do";
    }

    @Override
    protected HttpType httpType() {
        return HttpType.POST;
    }
}
