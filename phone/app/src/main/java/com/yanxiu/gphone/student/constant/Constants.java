package com.yanxiu.gphone.student.constant;

import com.yanxiu.gphone.student.util.LoginInfo;

/**
 * Created by dyf
 * 存储常量
 */

public class Constants {
    public static final int  NET_ERROR = -1;
    public static final String OS = "android";
    public static final String osType = "0";
    public static final String pcode = "010110000";
    public static String token = LoginInfo.getToken();
    public static String trace_uid = LoginInfo.getUID();
//    public static String deviceId= DeviceUtil.getAppDeviceId();
    public static String version = "2.4.1";

    public static final String EXTRA_PAPER = "extra_Paper";//传递给答题页的paper数据的key

}
