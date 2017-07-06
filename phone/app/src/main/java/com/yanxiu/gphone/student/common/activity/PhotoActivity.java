package com.yanxiu.gphone.student.common.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.base.YanxiuBaseActivity;
import com.yanxiu.gphone.student.common.Bean.PhotoDeleteBean;
import com.yanxiu.gphone.student.common.adapter.PhotoPagerAdapter;
import com.yanxiu.gphone.student.customviews.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Canghaixiao.
 * Time : 2017/6/23 14:11.
 * Function :
 */
public class PhotoActivity extends YanxiuBaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private static final String PATH_LIST="paths";
    private static final String SELECTID="select";
    private static final String FROM="from";

    private static final String ISCANDELETE="delete";
    public static final int DELETE_CAN=0;
    public static final int DELETE_CANNOT=1;

    private Context mContext;
    private ViewPager mImagePhotoView;
    private ImageView mBackView;
    private TextView mTitleView;
    private ImageView mDeleteView;
    private PhotoPagerAdapter mAdapter;

    private int mFromId;
    private int mSelectPosition=-1;
    private int mTotalNum;
    private int mIsCanDelete=DELETE_CAN;

    public static void LaunchActivity(Context context, ArrayList<String> list, int selectPosition,int fromId,int isCanDelete){
        Intent intent=new Intent(context,PhotoActivity.class);
        intent.putStringArrayListExtra(PATH_LIST,list);
        intent.putExtra(SELECTID,selectPosition);
        intent.putExtra(FROM,fromId);
        intent.putExtra(ISCANDELETE,isCanDelete);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        mContext=PhotoActivity.this;
        mSelectPosition=getIntent().getIntExtra(SELECTID,-1);
        mFromId=getIntent().getIntExtra(FROM,-1);
        mIsCanDelete=getIntent().getIntExtra(ISCANDELETE,DELETE_CAN);
        initView();
        listener();
        initData(getIntent().getStringArrayListExtra(PATH_LIST));
    }

    private void initView() {
        mBackView= (ImageView) findViewById(R.id.iv_left);
        mTitleView= (TextView) findViewById(R.id.tv_title);
        mDeleteView= (ImageView) findViewById(R.id.iv_right);

        mImagePhotoView= (ViewPager) findViewById(R.id.vp_image_review);
        mAdapter=new PhotoPagerAdapter(mContext);
        mImagePhotoView.setAdapter(mAdapter);
    }

    private void listener() {
        mBackView.setOnClickListener(PhotoActivity.this);
        mDeleteView.setOnClickListener(PhotoActivity.this);
        mImagePhotoView.addOnPageChangeListener(PhotoActivity.this);
    }

    private void initData(ArrayList<String> list) {
        mBackView.setVisibility(View.VISIBLE);
        if (mIsCanDelete==DELETE_CAN) {
            mDeleteView.setVisibility(View.VISIBLE);
        }else {
            mDeleteView.setVisibility(View.GONE);
        }
        mTitleView.setTextColor(Color.WHITE);
        if (list==null){
            return;
        }
        mTotalNum=list.size();
        List<ZoomImageView> zoomImageViews=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            ZoomImageView imageView=new ZoomImageView(mContext);
            zoomImageViews.add(imageView);
        }
        mAdapter.setData(list,zoomImageViews);
        mImagePhotoView.setCurrentItem(mSelectPosition);
        mTitleView.setText((mSelectPosition+1)+"/"+mTotalNum);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.mSelectPosition=position;
        mTitleView.setText((mSelectPosition+1)+"/"+mTotalNum);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_left:
                PhotoActivity.this.finish();
                break;
            case R.id.iv_right:
                mAdapter.deleteItem(mSelectPosition);
                mTotalNum=mAdapter.getCount();
                if (mTotalNum==0){
                    PhotoActivity.this.finish();
                }else {
                    mTitleView.setText((mSelectPosition + 1) + "/" + mTotalNum);
                    PhotoDeleteBean deleteBean = new PhotoDeleteBean();
                    deleteBean.formId = mFromId;
                    deleteBean.deleteId = mSelectPosition;
                    EventBus.getDefault().post(deleteBean);
                }
                break;
        }
    }
}
