package com.yanxiu.gphone.student.homework.homeworkdetail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.homework.data.HomeworkDetailBean;

import java.util.List;

/**
 * Created by sp on 17-5-22.
 */

public class HomeworkDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static int TYPE_NORMAL = 0;
    private static int TYPE_FOOTER = 1;
    private List<HomeworkDetailBean> mData;
    private Context mContext;
    private boolean mIsLoadingMore = false;

    private HomeworkItemClickListener mItemListener;

    public HomeworkDetailAdapter(List<HomeworkDetailBean> data, HomeworkItemClickListener mItemListener) {
        this.mData = data;
        this.mItemListener = mItemListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_NORMAL){
            return new HomeworkDetailViewHolder(inflater.inflate(R.layout.item_homework_detail,parent,false));
        }else if(viewType == TYPE_FOOTER){
            return new FooterViewHolder(inflater.inflate(R.layout.footer_loadmore,parent,false));
        }else {
            return new HomeworkDetailViewHolder(inflater.inflate(R.layout.item_homework_detail,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeworkDetailBean bean = null;
        if(position < mData.size()){
            bean = mData.get(position);
        }
        if(holder instanceof HomeworkDetailViewHolder){
            HomeworkDetailViewHolder viewHolder = (HomeworkDetailViewHolder)holder;
            viewHolder.mName.setText(bean.getName());
            //TODO 根据状态设置icon
            if(bean.getPaperStatus().getStatus() == 0){   //待完成
                viewHolder.mState1.setText(mContext.getString(R.string.homework_done_num) + bean.getAnswernum()  + "/" + bean.getQuesnum());
                if(bean.getIsEnd() == 0){  //未截止
                    viewHolder.mState2.setText(mContext.getString(R.string.homework_remain_time)+ bean.getRemaindertimeStr());
                }else {   //已截止
                    viewHolder.mState2.setText(R.string.can_redo);
                }
                viewHolder.mCommentLayout.setVisibility(View.GONE);
            }else if(bean.getPaperStatus().getStatus() ==1){ // 未完成
                viewHolder.mState1.setText(R.string.over_deadline);
                viewHolder.mState2.setText("");
                viewHolder.mCommentLayout.setVisibility(View.GONE);
            }else if(bean.getPaperStatus().getStatus() == 2){//已完成
                if(TextUtils.isEmpty(bean.getPaperStatus().getTeachercomments()) || TextUtils.isEmpty(bean.getPaperStatus().getTeacherName())){
                    viewHolder.mCommentLayout.setVisibility(View.GONE);
                    viewHolder.mState1.setText(R.string.homework_done_uncheck);
                }else {
                    viewHolder.mCommentLayout.setVisibility(View.VISIBLE);
                    viewHolder.mState1.setText(R.string.homework_checked);
                    viewHolder.mState2.setText(mContext.getString(R.string.score_rate) + bean.getPaperStatus().getScoreRate());
                    //TODO 前面几个字儿需要加粗
                    String text = bean.getPaperStatus().getTeacherName() + mContext.getString(R.string.comment) + bean.getPaperStatus().getTeachercomments();
                    viewHolder.mComment.setText(text);
                }
            }
        }else if(holder instanceof FooterViewHolder){
            FooterViewHolder viewHolder = (FooterViewHolder) holder;
            if(mIsLoadingMore){
                viewHolder.mItemVIew.setVisibility(View.VISIBLE);
                viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                viewHolder.mLoadingText.setText(((FooterViewHolder) holder).mLoadingText.getContext().getResources().getString(R.string.text_loading));
            }else {
                viewHolder.mItemVIew.setVisibility(View.GONE);
                viewHolder.mProgressBar.setVisibility(View.GONE);
                viewHolder.mLoadingText.setText(((FooterViewHolder) holder).mLoadingText.getContext().getResources().getString(R.string.text_click_to_load_more));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0? mData.size() +1 :0;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() -1){
            return TYPE_FOOTER;
        }else {
            return TYPE_NORMAL;
        }
    }

    public HomeworkDetailBean getItem(int position){
        if(position < mData.size()){
            return  mData.get(position);
        }else {
            return null;
        }
    }
    public void replaceData(List<HomeworkDetailBean> data){
        this.mData = data;
        notifyDataSetChanged();
    }

    public void addFooterView(){
        mIsLoadingMore = true;
        notifyDataSetChanged();
    }

    public void removeFooterView(){
        mIsLoadingMore = false;
        notifyDataSetChanged();
    }

    class HomeworkDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView mIcon;
        TextView mName,mState1,mState2,mComment;
        View mCommentLayout;
        public HomeworkDetailViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mState1 = (TextView) itemView.findViewById(R.id.tv_state1);
            mState2 = (TextView) itemView.findViewById(R.id.tv_state2);
            mComment = (TextView) itemView.findViewById(R.id.tv_comment);
            mCommentLayout = itemView.findViewById(R.id.ll_comment);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemListener != null){
                        mItemListener.onHomeworkClick(mData.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder{

        ProgressBar mProgressBar;
        TextView mLoadingText;
        View mItemVIew;
        public FooterViewHolder(View itemView) {
            super(itemView);
            mItemVIew = itemView;
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            mLoadingText = (TextView) itemView.findViewById(R.id.loadingText);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemListener != null){
                        mItemListener.onLoadMoreClick();
                    }
                }
            });
        }
    }

    public interface HomeworkItemClickListener{
        void onHomeworkClick(HomeworkDetailBean homeworkDetailBean);

        void onLoadMoreClick();
    }
}