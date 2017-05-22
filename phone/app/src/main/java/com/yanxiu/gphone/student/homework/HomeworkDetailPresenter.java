package com.yanxiu.gphone.student.homework;

import com.yanxiu.gphone.student.homework.classmanage.ClassStatus;
import com.yanxiu.gphone.student.homework.data.HomeworkDetailBean;

import java.util.List;

/**
 * Created by sp on 17-5-22.
 */

public class HomeworkDetailPresenter implements HomeworkDetailContract.Presenter {
    private final HomeworkDetailRepository mHomeworkResponsitory ;

    private final HomeworkDetailContract.View mHomeworkDetailView;

    private String mClassId;

    public HomeworkDetailPresenter(String classId, HomeworkDetailRepository mHomeworkResponsitory, HomeworkDetailContract.View mHomeworkDetailView) {
        this.mClassId = classId;
        this.mHomeworkResponsitory = mHomeworkResponsitory;
        this.mHomeworkDetailView = mHomeworkDetailView;
    }

    @Override
    public void start() {
        loadHomework();
    }

    @Override
    public void loadHomework() {
        mHomeworkDetailView.setLoadingIndicator(true);
        mHomeworkResponsitory.getHomeworkDetails(mClassId, new HomeworkDetailDataSource.LoadHomeworkDetailCallback() {
            @Override
            public void onHomeworkDetailLoaded(List<HomeworkDetailBean> homeworkDetails) {
                if(!mHomeworkDetailView.isActive()){
                    return;
                }
                mHomeworkDetailView.showHomework(homeworkDetails);
                mHomeworkDetailView.setLoadingIndicator(false);
            }

            @Override
            public void onDataEmpty() {
                if(!mHomeworkDetailView.isActive()){
                    return;
                }
                mHomeworkDetailView.setLoadingIndicator(false);
                mHomeworkDetailView.showDataEmpty();
            }

            @Override
            public void onDataError(int code, String msg) {
                if(!mHomeworkDetailView.isActive()){
                    return;
                }
                mHomeworkDetailView.setLoadingIndicator(false);
                if(code == ClassStatus.NO_CLASS.getCode()){
                    mHomeworkDetailView.openJoinClassUI();
                }else if(code == ClassStatus.APPLYING_CLASS.getCode()){
                    mHomeworkDetailView.showApplyingForClass();
                }else {
                    mHomeworkDetailView.showDataError();
                }
            }
        });
    }

    @Override
    public void loadMoreHomework() {
        if(!mHomeworkResponsitory.canLoadMore()){
            mHomeworkDetailView.showNoMoreData();
            return;
        }
        mHomeworkDetailView.setLoadingMoreIndicator(true);
        mHomeworkResponsitory.getMoreHomeworkDetails(mClassId, new HomeworkDetailDataSource.LoadHomeworkDetailCallback() {
            @Override
            public void onHomeworkDetailLoaded(List<HomeworkDetailBean> homeworkDetails) {
                if(!mHomeworkDetailView.isActive()){
                    return;
                }
                mHomeworkDetailView.setLoadingMoreIndicator(false);
                mHomeworkDetailView.showHomework(homeworkDetails);
            }

            @Override
            public void onDataEmpty() {
                if(!mHomeworkDetailView.isActive()){
                    return;
                }
                mHomeworkDetailView.setLoadingMoreIndicator(false);
                mHomeworkDetailView.showNoMoreData();
            }

            @Override
            public void onDataError(int code, String msg) {
                if(!mHomeworkDetailView.isActive()){
                    return;
                }
                mHomeworkDetailView.setLoadingMoreIndicator(false);
                if(code == ClassStatus.NO_CLASS.getCode()){
                    mHomeworkDetailView.openJoinClassUI();
                }else if(code == ClassStatus.APPLYING_CLASS.getCode()){
                    mHomeworkDetailView.showApplyingForClass();
                }else {
                    mHomeworkDetailView.showLoadMoreDataError(msg);
                }
            }
        });
    }

    @Override
    public void openAnswerQuestion(HomeworkDetailBean homeworkDetail) {
        mHomeworkDetailView.openAnswerQuestionUI();
    }

    @Override
    public void finishUI() {
        if(!mHomeworkDetailView.isActive()){
            mHomeworkDetailView.finishUI();
        }
    }
}
