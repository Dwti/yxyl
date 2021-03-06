package com.yanxiu.gphone.student.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.yanxiu.network.RequestBase;
import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.base.EXueELianBaseCallback;
import com.yanxiu.gphone.student.base.YanxiuBaseActivity;
import com.yanxiu.gphone.student.customviews.PublicLoadLayout;
import com.yanxiu.gphone.student.customviews.WavesLayout;
import com.yanxiu.gphone.student.homepage.MainActivity;
import com.yanxiu.gphone.student.login.request.JoinClassSubmitRequest;
import com.yanxiu.gphone.student.login.request.JoinClassSubmitThridRequest;
import com.yanxiu.gphone.student.login.response.JoinClassResponse;
import com.yanxiu.gphone.student.login.response.LoginResponse;
import com.yanxiu.gphone.student.login.response.ThridMessageBean;
import com.yanxiu.gphone.student.login.response.UserMessageBean;
import com.yanxiu.gphone.student.userevent.UserEventManager;
import com.yanxiu.gphone.student.util.EditTextManger;
import com.yanxiu.gphone.student.util.LoginInfo;
import com.yanxiu.gphone.student.util.SysEncryptUtil;
import com.yanxiu.gphone.student.util.ToastManager;
@SuppressWarnings("all")
/**
 * Created by Canghaixiao.
 * Time : 2017/5/23 11:27.
 * Function :
 */
public class JoinClassSubmitActivity extends YanxiuBaseActivity implements View.OnClickListener, EditTextManger.onTextLengthChangedListener {

    private Context mContext;
    private PublicLoadLayout rootView;
    private TextView mClassIdView;
    private TextView mTeacherNameView;
    private TextView mStudentNumberView;
    private TextView mSchoolNameView;
    private EditText mInputNameView;
    private ImageView mClearNameView;
    private WavesLayout mWavesView;
    private TextView mAddClassView;
    private JoinClassResponse.Data mData;
    private ThridMessageBean thridMessageBean;
    private JoinClassSubmitRequest mJoinClassSubmitRequest;
    private JoinClassSubmitThridRequest mJoinClassSubmitThridRequest;
    private ImageView mBackView;
    private TextView mTitleView;
    private TextView mClassNameView;

    public static void LaunchActivity(Context context, JoinClassResponse.Data response){
        Intent intent=new Intent(context,JoinClassSubmitActivity.class);
        intent.putExtra(LoginActivity.TYPE,LoginActivity.TYPE_DEFAULT);
        intent.putExtra(JoinClassActivity.KEY,response);
        context.startActivity(intent);
    }

    public static void LaunchActivity(Context context, JoinClassResponse.Data response, ThridMessageBean message){
        Intent intent=new Intent(context,JoinClassSubmitActivity.class);
        intent.putExtra(JoinClassActivity.KEY,response);
        intent.putExtra(LoginActivity.TYPE,LoginActivity.TYPE_THRID);
        intent.putExtra(LoginActivity.THRID_LOGIN,message);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=JoinClassSubmitActivity.this;
        rootView=new PublicLoadLayout(mContext);
        rootView.setContentView(R.layout.activity_join_class_submit);
        String type=getIntent().getStringExtra(LoginActivity.TYPE);
        if (type.equals(LoginActivity.TYPE_THRID)) {
            thridMessageBean = (ThridMessageBean) getIntent().getSerializableExtra(LoginActivity.THRID_LOGIN);
        }
        mData= (JoinClassResponse.Data) getIntent().getSerializableExtra(JoinClassActivity.KEY);
        setContentView(rootView);
        initView();
        listener();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mJoinClassSubmitRequest!=null){
            mJoinClassSubmitRequest.cancelRequest();
            mJoinClassSubmitRequest=null;
        }
        if (mJoinClassSubmitThridRequest!=null){
            mJoinClassSubmitThridRequest.cancelRequest();
            mJoinClassSubmitThridRequest=null;
        }
    }

    private void initView() {
        mBackView= (ImageView) findViewById(R.id.iv_left);
        mTitleView= (TextView) findViewById(R.id.tv_title);
        mClassNameView= (TextView) findViewById(R.id.tv_class_name);
        mClassIdView= (TextView) findViewById(R.id.tv_class_id);
        mTeacherNameView= (TextView) findViewById(R.id.tv_teacher_name);
        mStudentNumberView= (TextView) findViewById(R.id.tv_student_number);
        mSchoolNameView= (TextView) findViewById(R.id.tv_school_name);
        mInputNameView= (EditText) findViewById(R.id.et_name);
        mClearNameView= (ImageView) findViewById(R.id.iv_clear);
        mWavesView= (WavesLayout) findViewById(R.id.wavesLayout);
        mAddClassView= (TextView) findViewById(R.id.tv_add_class);
    }

    private void listener() {
        mBackView.setOnClickListener(JoinClassSubmitActivity.this);
        EditTextManger.getManager(mInputNameView).setTextChangedListener(JoinClassSubmitActivity.this);
        mClearNameView.setOnClickListener(JoinClassSubmitActivity.this);
        mAddClassView.setOnClickListener(JoinClassSubmitActivity.this);
    }

    private void initData() {
        mBackView.setVisibility(View.VISIBLE);
        mTitleView.setText(getText(R.string.submitname));
//        mInputNameView.setEnabled(false);
        mWavesView.setCanShowWave(false);
        mAddClassView.setEnabled(false);
        if (mData!=null){
            mClassNameView.setText(mData.gradename+mData.name);
            mClassIdView.setText(mData.id);
            mTeacherNameView.setText(mData.adminName);
            mStudentNumberView.setText(mData.stdnum+getText(R.string.person));
            mSchoolNameView.setText(mData.schoolname);
        }
        mBackView.setBackgroundResource(R.drawable.selector_white_back);
        mInputNameView.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                JoinClassSubmitActivity.this.finish();
                EditTextManger.getManager(mTitleView).hideSoftInput();
                break;
            case R.id.iv_clear:
                mInputNameView.setText("");
                break;
            case R.id.tv_add_class:
                String userName=mInputNameView.getText().toString().trim();
                if (thridMessageBean !=null){
                    addClassThrid(userName);
                }else {
                    addClass(userName);
                }
                break;
        }
    }

    @Override
    public void onChanged(View view, String value, boolean isEmpty) {
        if (isEmpty){
            mWavesView.setCanShowWave(false);
            mAddClassView.setEnabled(false);
            mClearNameView.setEnabled(false);
            mClearNameView.setVisibility(View.INVISIBLE);
        }else {
            mWavesView.setCanShowWave(true);
            mAddClassView.setEnabled(true);
            mClearNameView.setEnabled(true);
            mClearNameView.setVisibility(View.VISIBLE);
        }
    }

    private void addClass(String userName){
        rootView.showLoadingView();
        mJoinClassSubmitRequest=new JoinClassSubmitRequest();
        mJoinClassSubmitRequest.realname=userName;
        mJoinClassSubmitRequest.areaid=mData.areaid;
        mJoinClassSubmitRequest.cityid=mData.cityid;
        mJoinClassSubmitRequest.classId=mData.id;
        mJoinClassSubmitRequest.stageid=mData.stageid;
        mJoinClassSubmitRequest.mobile= LoginInfo.getMobile();
        mJoinClassSubmitRequest.schoolid=mData.schoolid;
        mJoinClassSubmitRequest.schoolName=mData.schoolname;
        mJoinClassSubmitRequest.provinceid=mData.provinceid;
        mJoinClassSubmitRequest.validKey=SysEncryptUtil.getMd5_32(LoginInfo.getMobile() + "&" + "yxylmobile");
        mJoinClassSubmitRequest.startRequest(LoginResponse.class, new EXueELianBaseCallback<LoginResponse>() {

            @Override
            protected void onResponse(RequestBase request, LoginResponse response) {
                rootView.hiddenLoadingView();
                if (response.getStatus().getCode()==0){
                    LoginInfo.saveCacheData(response.data.get(0));
                    LoginInfo.saveLoginType(UserMessageBean.LOGIN_ACCOUNT);
                    MainActivity.invoke(JoinClassSubmitActivity.this,true);
                    UserEventManager.getInstense().whenEnterClass();
                    JoinClassSubmitActivity.this.finish();
                }else {
                    ToastManager.showMsg(response.getStatus().getDesc());
                }
            }

            @Override
            public void onFail(RequestBase request, Error error) {
                rootView.hiddenLoadingView();
                ToastManager.showMsg(error.getMessage().trim());
            }
        });
    }

    private void addClassThrid(String userName){
        rootView.showLoadingView();
        mJoinClassSubmitThridRequest=new JoinClassSubmitThridRequest();
        mJoinClassSubmitThridRequest.headimg= thridMessageBean.head;
        mJoinClassSubmitThridRequest.openid= thridMessageBean.openid;
        mJoinClassSubmitThridRequest.pltform= thridMessageBean.platform;
        mJoinClassSubmitThridRequest.sex= thridMessageBean.sex;
        mJoinClassSubmitThridRequest.uniqid= thridMessageBean.uniqid;
        mJoinClassSubmitThridRequest.realname=userName;
        mJoinClassSubmitThridRequest.areaid=mData.areaid;
        mJoinClassSubmitThridRequest.cityid=mData.cityid;
        mJoinClassSubmitThridRequest.classId=mData.id;
        mJoinClassSubmitThridRequest.stageid=mData.stageid;
        mJoinClassSubmitThridRequest.schoolid=mData.schoolid;
        mJoinClassSubmitThridRequest.schoolname=mData.schoolname;
        mJoinClassSubmitThridRequest.provinceid=mData.provinceid;
        mJoinClassSubmitThridRequest.startRequest(LoginResponse.class, new EXueELianBaseCallback<LoginResponse>() {

            @Override
            protected void onResponse(RequestBase request, LoginResponse response) {
                rootView.hiddenLoadingView();
                if (response.getStatus().getCode()==0){
                    LoginInfo.saveCacheData(response.data.get(0));
                    if (ThridMessageBean.TYPE_QQ.equals(thridMessageBean.getType())) {
                        LoginInfo.saveLoginType(UserMessageBean.LOGIN_QQ);
                    }else {
                        LoginInfo.saveLoginType(UserMessageBean.LOGIN_WX);
                    }
                    MainActivity.invoke(JoinClassSubmitActivity.this,true);
                    JoinClassSubmitActivity.this.finish();
                }else {
                    ToastManager.showMsg(response.getStatus().getDesc());
                }
            }

            @Override
            public void onFail(RequestBase request, Error error) {
                rootView.hiddenLoadingView();
                ToastManager.showMsg(error.getMessage().trim());
            }
        });
    }
}
