package com.yanxiu.gphone.student.login.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.yanxiu.network.RequestBase;
import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.base.EXueELianBaseCallback;
import com.yanxiu.gphone.student.base.EXueELianBaseResponse;
import com.yanxiu.gphone.student.base.YanxiuBaseActivity;
import com.yanxiu.gphone.student.customviews.PublicLoadLayout;
import com.yanxiu.gphone.student.login.adapter.ChooseSchoolAdapter;
import com.yanxiu.gphone.student.login.request.ChooseSchoolRequest;
import com.yanxiu.gphone.student.login.request.ChooseSearchSchoolRequest;
import com.yanxiu.gphone.student.login.response.ChooseSchoolResponse;
import com.yanxiu.gphone.student.util.EditTextManger;
import com.yanxiu.gphone.student.util.LoginInfo;
import com.yanxiu.gphone.student.util.ToastManager;

import de.greenrobot.event.EventBus;

@SuppressWarnings("all")
/**
 * Created by Canghaixiao.
 * Time : 2017/5/22 17:54.
 * Function :
 */
public class ChooseSchoolActivity extends YanxiuBaseActivity implements View.OnClickListener, EditTextManger.onTextLengthChangedListener, ChooseSchoolAdapter.OnItemClickListener, View.OnFocusChangeListener {

    private Context mContext;
    private EditText mSchoolNameView;
    private ImageView mSearchView;
    private TextView mProvinceView;
    private TextView mCityView;
    private TextView mAreaView;
    private RecyclerView mSchoolListView;
    private ImageView mBackView;
    private View mTopView;

    private ChooseLocationActivity.SchoolMessage message;
    private PublicLoadLayout rootView;
    private ChooseSearchSchoolRequest mChooseSearchSchoolRequest;
    private ChooseSchoolRequest mChooseSchoolRequest;
    private ChooseSchoolAdapter adapter;
    private TextView mTitleView;
    private LinearLayout mFocusView;

    public static void LuanchActivity(Context context, ChooseLocationActivity.SchoolMessage message){
        Intent intent=new Intent(context,ChooseSchoolActivity.class);
        intent.putExtra(ChooseLocationActivity.KEY,message);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=ChooseSchoolActivity.this;
        rootView=new PublicLoadLayout(mContext);
        rootView.setContentView(R.layout.activity_chooseschool);
        setContentView(rootView);
        message= (ChooseLocationActivity.SchoolMessage) getIntent().getSerializableExtra(ChooseLocationActivity.KEY);
        initView();
        listener();
        initData();
        searchSchool("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelRequest();
    }

    private void initView() {
        mTopView=findViewById(R.id.include_top);
        mTitleView= (TextView) findViewById(R.id.tv_title);
        mBackView= (ImageView) findViewById(R.id.iv_left);
        mSchoolNameView= (EditText) findViewById(R.id.et_school_name);
        mSearchView= (ImageView) findViewById(R.id.iv_search);
        mProvinceView= (TextView) findViewById(R.id.tv_province);
        mCityView= (TextView) findViewById(R.id.tv_city);
        mAreaView= (TextView) findViewById(R.id.tv_area);
        mFocusView= (LinearLayout) findViewById(R.id.ll_focus);
        mSchoolListView= (RecyclerView) findViewById(R.id.recy_school_list);
        mSchoolListView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter=new ChooseSchoolAdapter(mContext);
    }

    private void listener() {
        mBackView.setOnClickListener(ChooseSchoolActivity.this);
        mSearchView.setOnClickListener(ChooseSchoolActivity.this);
        adapter.setOnItemClickListener(ChooseSchoolActivity.this);
        mSchoolNameView.setOnFocusChangeListener(ChooseSchoolActivity.this);
        EditTextManger.getManager(mSchoolNameView).setTextChangedListener(ChooseSchoolActivity.this);
    }

    private void initData() {
        mTitleView.setText(getText(R.string.chooseschool));
        mTitleView.setTextColor(ContextCompat.getColor(mContext,R.color.color_666666));
        mTopView.setBackgroundColor(Color.WHITE);
        mBackView.setVisibility(View.VISIBLE);
        mSearchView.setEnabled(false);
        mProvinceView.setText(message.provinceName);
        mCityView.setText(message.cityName);
        mAreaView.setText(message.areaName);
        mSchoolListView.setAdapter(adapter);

        mBackView.setBackgroundResource(R.drawable.selector_back);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_search:
                String schoolName=mSchoolNameView.getText().toString().trim();
                searchSchool(schoolName);
                break;
            case R.id.iv_left:
                ChooseSchoolActivity.this.finish();
                EditTextManger.getManager(mTitleView).hideSoftInput();
                break;
        }
    }

    @Override
    public void onChanged(View view, String value, boolean isEmpty) {
        if (isEmpty){
            mSearchView.setEnabled(false);
        } else {
            mSearchView.setEnabled(true);
        }
        searchSchool(value);
    }

    private void cancelRequest(){
        if (mChooseSearchSchoolRequest !=null){
            mChooseSearchSchoolRequest.cancelRequest();
            mChooseSearchSchoolRequest =null;
        }
        if (mChooseSchoolRequest!=null){
            mChooseSchoolRequest.cancelRequest();
            mChooseSchoolRequest=null;
        }
    }

    private void searchSchool(String schoolName){
        cancelRequest();
        rootView.showLoadingView();
        mChooseSearchSchoolRequest =new ChooseSearchSchoolRequest();
        mChooseSearchSchoolRequest.school=schoolName;
        mChooseSearchSchoolRequest.regionId=message.areaId;
        mChooseSearchSchoolRequest.startRequest(ChooseSchoolResponse.class, new EXueELianBaseCallback<ChooseSchoolResponse>() {

            @Override
            protected void onResponse(RequestBase request, ChooseSchoolResponse response) {
                rootView.hiddenLoadingView();
                if (response.getStatus().getCode()==0&&response.data!=null){
                    adapter.setDatas(response.data);
                }else {
                    adapter.setDatas(null);
                    ToastManager.showMsg(getText(R.string.search_school_no));
                }
            }

            @Override
            public void onFail(RequestBase request, Error error) {
                rootView.hiddenLoadingView();
                ToastManager.showMsg(error.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(View view, ChooseSchoolResponse.School school, int position) {
        message.schoolId=school.id;
        message.schoolName=school.name;
        if (LoginInfo.isLogIn()){
            saveSchoolMessage();
        }else {
            result();
        }
    }

    private void result(){
        EventBus.getDefault().post(message);
        ChooseSchoolActivity.this.finish();
        EditTextManger.getManager(mTitleView).hideSoftInput();
    }

    private void saveSchoolMessage(){
        rootView.showLoadingView();
        mChooseSchoolRequest=new ChooseSchoolRequest();
        mChooseSchoolRequest.areaid=message.areaId;
        mChooseSchoolRequest.areaName=message.areaName;
        mChooseSchoolRequest.cityid=message.cityId;
        mChooseSchoolRequest.cityName=message.cityName;
        mChooseSchoolRequest.provinceid=message.provinceId;
        mChooseSchoolRequest.provinceName=message.provinceName;
        mChooseSchoolRequest.schoolid=message.schoolId;
        mChooseSchoolRequest.schoolName=message.schoolName;
        mChooseSchoolRequest.startRequest(EXueELianBaseResponse.class, new EXueELianBaseCallback<EXueELianBaseResponse>() {
            @Override
            protected void onResponse(RequestBase request, EXueELianBaseResponse response) {
                rootView.hiddenLoadingView();
                if (response.getStatus().getCode()==0){
                    LoginInfo.saveSchoolMessage(message.areaId,message.areaName,message.cityId,message.cityName,message.provinceId,message.provinceName,message.schoolId,message.schoolName);
                    result();
                }else {
                    ToastManager.showMsg(response.getStatus().getDesc());
                }
            }

            @Override
            public void onFail(RequestBase request, Error error) {
                rootView.hiddenLoadingView();
                ToastManager.showMsg(error.getMessage());
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            mFocusView.setBackgroundResource(R.drawable.shape_input_school_layout_focus);
        }else {
            mFocusView.setBackgroundResource(R.drawable.shape_input_school_layout_unfocus);
        }
    }
}
