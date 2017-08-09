package com.yanxiu.gphone.student.exercise;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.test.yanxiu.network.HttpCallback;
import com.test.yanxiu.network.RequestBase;
import com.yanxiu.gphone.student.R;
import com.yanxiu.gphone.student.base.EXueELianBaseCallback;
import com.yanxiu.gphone.student.customviews.ChapterSwitchBar;
import com.yanxiu.gphone.student.exercise.adapter.PopListAdapter;
import com.yanxiu.gphone.student.exercise.bean.EditionBeanEx;
import com.yanxiu.gphone.student.exercise.bean.EditionChildBean;
import com.yanxiu.gphone.student.exercise.bean.ExerciseBean;
import com.yanxiu.gphone.student.exercise.request.EditionRequest;
import com.yanxiu.gphone.student.exercise.request.ExerciseHistoryByChapterRequest;
import com.yanxiu.gphone.student.exercise.request.ExerciseHistoryByKnowRequest;
import com.yanxiu.gphone.student.exercise.response.EditionResponse;
import com.yanxiu.gphone.student.exercise.response.ExerciseHistoryResponse;
import com.yanxiu.gphone.student.homework.request.HomeworkReportRequest;
import com.yanxiu.gphone.student.homework.request.PaperRequest;
import com.yanxiu.gphone.student.homework.response.PaperResponse;
import com.yanxiu.gphone.student.questions.answerframe.bean.Paper;
import com.yanxiu.gphone.student.questions.answerframe.ui.activity.AnswerQuestionActivity;
import com.yanxiu.gphone.student.questions.answerframe.ui.activity.AnswerReportActicity;
import com.yanxiu.gphone.student.questions.answerframe.util.QuestionShowType;
import com.yanxiu.gphone.student.util.DESBodyDealer;
import com.yanxiu.gphone.student.util.DataFetcher;
import com.yanxiu.gphone.student.util.ScreenUtils;
import com.yanxiu.gphone.student.util.ToastManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sp on 17-8-2.
 */

public class ExerciseHistoryActivity extends Activity {

    public static final String EXTRA_SUBJECT_ID = "SUBJECT_ID";
    public static final String EXTRA_SUBJECT_NAME = "SUBJECT_NAME";
    public static final String EXTRA_EDITION_ID = "EDITION_ID";

    private List<ExerciseBean> mExerciseListChapter = new ArrayList<>();
    private List<ExerciseBean> mExerciseListKnow = new ArrayList<>();
    private ExerciseAdapter mChapterAdapter,mKnowAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mRootView,mTipsView, mBack,mLayoutStage,mToolBar;
    private String mSubjectId,mEditionId,mVolume;
    private TextView mTitle, mTips,mStage;
    private Button mRefreshBtn;
    private List<EditionChildBean> mEditionChildBeanList;
    private ChapterSwitchBar mSwitchBar;
    private PopupWindow popupWindow;
    private int mLastSelectedPos = 0;
    private int mChapterCurrentPage =1;
    private int mKnowCurrentPage = 1;
    private int mChapterTotalPage = 0;
    private int mKnowTotalPage = 0;
    private boolean mIsChapterMode = true;  //当前选中的是否是章节
    private boolean mNoEditions = true; //第一次进来是否没有请求Editions数据失败


    public static void invoke(Context context,String subjectId, String subjectName,String editionId){
        Intent intent = new Intent(context,ExerciseHistoryActivity.class);
        intent.putExtra(EXTRA_SUBJECT_ID,subjectId);
        intent.putExtra(EXTRA_SUBJECT_NAME,subjectName);
        intent.putExtra(EXTRA_EDITION_ID,editionId);
        context.startActivity(intent);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_history);
        initView();
        initListener();
        initData();
    }

    private void initView(){
        mRootView = findViewById(R.id.root);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mBack = findViewById(R.id.iv_back);
        mToolBar = findViewById(R.id.rl_tool_bar);
        mSwitchBar = (ChapterSwitchBar) findViewById(R.id.switchBar);
        mLayoutStage = findViewById(R.id.ll_stage);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mTipsView = findViewById(R.id.tips_layout);
        mTips = (TextView) findViewById(R.id.tv_tips);
        mStage = (TextView) findViewById(R.id.tv_stage);
        mRefreshBtn = (Button) findViewById(R.id.btn_refresh);

        mChapterAdapter = new ExerciseAdapter(mExerciseListChapter,mItemClickListener);
        mKnowAdapter = new ExerciseAdapter(mExerciseListKnow,mItemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mChapterAdapter);

    }


    private void initData() {
        mSubjectId = getIntent().getStringExtra(EXTRA_SUBJECT_ID);
        String subjectName = getIntent().getStringExtra(EXTRA_SUBJECT_NAME);
        mEditionId = getIntent().getStringExtra(EXTRA_EDITION_ID);
        mTitle.setText(subjectName);

        setSwitchBarVisibility(mSubjectId);

        getEditionList(mSubjectId);
    }

    private void initListener(){
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishUI();
            }
        });

        mSwitchBar.setOnCheckedChangedListener(new ChapterSwitchBar.OnCheckedChangedListener() {
            @Override
            public void onCheckedChanged(boolean isOff) {
                if(isOff){
                    mIsChapterMode = true;
                    showContentView();
                    mLayoutStage.setVisibility(View.VISIBLE);
                    mRecyclerView.setAdapter(mChapterAdapter);
                    if(mChapterAdapter.getItemCount() == 0){
                        if(mEditionChildBeanList == null || mEditionChildBeanList.size() == 0){
                            getEditionList(mSubjectId);
                        }else {
                            getExercisesByChapter(1);
                        }
                    }
                }else {
                    mIsChapterMode = false;
                    showContentView();
                    mLayoutStage.setVisibility(View.GONE);
                    mRecyclerView.setAdapter(mKnowAdapter);
                    if(mKnowAdapter.getItemCount() == 0){
                        getExercisesByKnow(1);
                    }
                }
            }
        });

        mLayoutStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow ==null || (popupWindow != null && !popupWindow.isShowing())){
                    showPop(mEditionChildBeanList);
                }else {
                    dismissPop();
                }
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int totalItemCount ;
            int lastVisibleItemPosition;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = layoutManager.getItemCount();
                lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                boolean isLoadingMore = ((ExerciseAdapter)recyclerView.getAdapter()).isLoadingMore();
                if(dy > 0 && !isLoadingMore && lastVisibleItemPosition + 1 == totalItemCount){
                    loadMoreData();
                }
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsChapterMode){
                    mChapterAdapter.clearData();
                }else {
                    mKnowAdapter.clearData();
                }
                showContentView();
                if(mNoEditions){
                    getEditionList(mSubjectId);
                }else {
                    loadData();
                }
            }
        });
    }

    private void getEditionList(String subjectId){
        EditionRequest request = new EditionRequest();
        request.setSubjectId(subjectId);
        request.startRequest(EditionResponse.class,mEditionCallback);
    }

    private void getExercisesByChapter(int nextPage){
        mChapterCurrentPage = nextPage;
        ExerciseHistoryByChapterRequest request = new ExerciseHistoryByChapterRequest();
        request.setSubjectId(mSubjectId);
        request.setBeditionId(mEditionId);
        request.setVolume(mVolume);
        request.setNextPage(String.valueOf(nextPage));
        request.startRequest(ExerciseHistoryResponse.class,mExerciseByChapter);
    }

    private void getExercisesByKnow(int nextPage){
        mKnowCurrentPage = nextPage;
        ExerciseHistoryByKnowRequest request = new ExerciseHistoryByKnowRequest();
        request.setSubjectId(mSubjectId);
        request.setNextPage(String.valueOf(nextPage));
        request.startRequest(ExerciseHistoryResponse.class,mExerciseByKnow);
    }

    private void getReport(String paperId){
        HomeworkReportRequest request = new HomeworkReportRequest();
        request.setPpid(paperId);
        request.bodyDealer = new DESBodyDealer();
        request.startRequest(PaperResponse.class,mReportCallback);
    }

    private void getPaper(String paperId){
        PaperRequest request = new PaperRequest();
        request.setPaperId(paperId);
        request.bodyDealer = new DESBodyDealer();
        request.startRequest(PaperResponse.class,mPaperCallback);
    }

    private String getVolume(List<EditionBeanEx> list, String editionId){
        String volume = "";
        for(EditionBeanEx bean : list){
            if(editionId.equals(bean.getId())){
                if(bean.getChildren() != null && bean.getChildren().size() > 0){
                    volume = bean.getChildren().get(0).getId();
                }
                break;
            }
        }
        return volume;
    }

    private void loadData(){
        setLoadingIndicator(true);
        if(mIsChapterMode){
            getExercisesByChapter(1);
        }else {
            getExercisesByChapter(1);
        }
    }

    private void loadMoreData(){
        if(!canLoadMore(mIsChapterMode))
            return;
        setLoadingMoreIndicator(true);
        if(mIsChapterMode){
            mChapterCurrentPage++;
            getExercisesByChapter(mChapterCurrentPage);
        }else {
            mKnowCurrentPage++;
            getExercisesByKnow(mKnowCurrentPage);
        }
    }

    public void setLoadingIndicator(boolean active) {
        mSwipeRefreshLayout.setRefreshing(active);
    }

    public void setLoadingMoreIndicator(boolean active) {
        ExerciseAdapter adapter = (ExerciseAdapter) mRecyclerView.getAdapter();
        if(active){
            adapter.addFooterView();
        }else {
            adapter.removeFooterView();
        }
    }

    private boolean canLoadMore(boolean isChapterMode){
        if(isChapterMode){
            return mChapterCurrentPage < mChapterTotalPage;
        }else {
            return  mKnowCurrentPage < mKnowTotalPage;
        }
    }

    public void updateExercises(ExerciseAdapter adapter) {
        showContentView();
        adapter.notifyDataSetChanged();
    }

    private void showPop(final List<EditionChildBean> data){
        if(data == null || data.size() == 0)
            return;
        if(popupWindow == null){
            View view = LayoutInflater.from(this).inflate(R.layout.popwindow_stage,null);
            popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);

            View stage = view.findViewById(R.id.ll_stage_pop);
            final TextView tvPop = (TextView) view.findViewById(R.id.tv_pop);
            data.get(0).setSelected(true);
            tvPop.setText(data.get(0).getName());

            ListView listView = (ListView) view.findViewById(R.id.list_view);
            final PopListAdapter adapter = new PopListAdapter(data);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(position != mLastSelectedPos){
                        tvPop.setText(data.get(position).getName());
                        mStage.setText(data.get(position).getName());
                        mVolume = data.get(position).getId();
                        data.get(position).setSelected(true);
                        data.get(mLastSelectedPos).setSelected(false);
                        mLastSelectedPos = position;
                        adapter.notifyDataSetChanged();

                        dismissPop();
                        getExercisesByChapter(1);
                    }

                }
            });

            stage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPop();
                }
            });

            if(data.size() > 6){
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) listView.getLayoutParams();
                layoutParams.height = ScreenUtils.dpToPxInt(this,51 * 6);
                listView.setLayoutParams(layoutParams);
            }

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mLayoutStage.setVisibility(View.VISIBLE);
                }
            });
            popupWindow.setFocusable(true);
        }
        mLayoutStage.setVisibility(View.INVISIBLE);
        popupWindow.showAsDropDown(mRootView);

    }

    private void dismissPop(){
        if(popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }


    public void openAnswerQuestionUI(String key) {
        AnswerQuestionActivity.invoke(this,key);
    }

    public void openAnswerReportUI(String key) {
        AnswerReportActicity.invoke(this,key);
    }

    public void showNoMoreData() {
        showMsg(getString(R.string.no_more_data));
    }

    public void showMsg(String msg) {
        ToastManager.showMsg(msg);
    }

    public void showLoadMoreDataError(String msg) {
        showMsg(msg);
    }

    public boolean isActive() {
        return !(isDestroyed() || isFinishing());
    }

    public void finishUI() {
        finish();
    }

    private void showContentView(){
        mToolBar.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mTipsView.setVisibility(View.GONE);
    }

    private void showDataEmptyView(boolean hideToolBar){
        if(hideToolBar){
            mToolBar.setVisibility(View.GONE);
        }
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mTipsView.setVisibility(View.VISIBLE);
        mTips.setText(R.string.no_exercise);
        mRefreshBtn.setText(R.string.click_to_refresh);
    }

    private void showDataErrorView(boolean hideToolBar){
        if(hideToolBar){
            mToolBar.setVisibility(View.GONE);
        }
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mTipsView.setVisibility(View.VISIBLE);
        mTips.setText(R.string.load_failed);
        mRefreshBtn.setText(R.string.click_to_retry);
    }

    private void setSwitchBarVisibility(String subjectId) {
        switch (subjectId){
            //非数理化生科目，不展示章节知识点切换（只有章节）
            case "1102":
            case "1104":
            case "1108":
            case "1109":
            case "1110":
                mSwitchBar.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    ExerciseAdapter.ExerciseItemClickListener mItemClickListener = new ExerciseAdapter.ExerciseItemClickListener() {
        @Override
        public void onHomeworkClick(ExerciseBean bean) {
            if(2 == bean.getStatus()){
                getReport(bean.getPaperId());
            }else {
                getPaper(bean.getPaperId());
            }
        }

        @Override
        public void onLoadMoreClick() {
            if(!((ExerciseAdapter)mRecyclerView.getAdapter()).isLoadingMore()){
                loadMoreData();
            }
        }
    };

    HttpCallback<ExerciseHistoryResponse> mExerciseByChapter = new EXueELianBaseCallback<ExerciseHistoryResponse>() {
        @Override
        protected void onResponse(RequestBase request, ExerciseHistoryResponse response) {
            if(response.getStatus().getCode() == 0){
                if(mChapterCurrentPage == 1){
                    setLoadingIndicator(false);
                    mChapterTotalPage = response.getPage().getTotalPage();
                    if(response.getData() != null && response.getData().size() > 0){
                        mExerciseListChapter.clear();
                        mExerciseListChapter.addAll(response.getData());
                        updateExercises(mChapterAdapter);
                    }else {
                        mChapterAdapter.clearData();
                        showDataEmptyView(false);
                    }
                }else if(mChapterCurrentPage > 1){
                    setLoadingMoreIndicator(false);
                    mChapterTotalPage = response.getPage().getTotalPage();
                    if(response.getData() != null && response.getData().size() > 0){
                        mExerciseListChapter.addAll(response.getData());
                        updateExercises(mChapterAdapter);
                    }else {
                        showNoMoreData();
                    }
                }

            }else {
                if(mChapterCurrentPage == 1){
                    setLoadingIndicator(false);
                    mChapterAdapter.clearData();
                    showDataEmptyView(false);
                }else if(mChapterCurrentPage > 1){
                    setLoadingMoreIndicator(false);
                    showLoadMoreDataError(response.getStatus().getDesc());
                }
            }
        }

        @Override
        public void onFail(RequestBase request, Error error) {
            if(mChapterCurrentPage == 1){
                setLoadingIndicator(false);
                mChapterAdapter.clearData();
                showDataErrorView(false);
            }else if(mChapterCurrentPage > 1){
                setLoadingMoreIndicator(false);
                showLoadMoreDataError(error.getLocalizedMessage());
            }
        }
    };

    HttpCallback<ExerciseHistoryResponse> mExerciseByKnow = new EXueELianBaseCallback<ExerciseHistoryResponse>() {
        @Override
        protected void onResponse(RequestBase request, ExerciseHistoryResponse response) {
            if(response.getStatus().getCode() == 0){
                if(mKnowCurrentPage == 1){
                    setLoadingIndicator(false);
                    mKnowTotalPage = response.getPage().getTotalPage();
                    if(response.getData() != null && response.getData().size() > 0){
                        mExerciseListKnow.clear();
                        mExerciseListKnow.addAll(response.getData());
                        updateExercises(mKnowAdapter);
                    }else {
                        mKnowAdapter.clearData();
                        showDataEmptyView(false);
                    }
                }else if(mKnowCurrentPage > 1){
                    setLoadingMoreIndicator(false);
                    mKnowTotalPage = response.getPage().getTotalPage();
                    if(response.getData() != null && response.getData().size() > 0){
                        mExerciseListKnow.addAll(response.getData());
                        updateExercises(mKnowAdapter);
                    }else {
                        showNoMoreData();
                    }
                }

            }else {
                if(mKnowCurrentPage == 1){
                    setLoadingIndicator(false);
                    mKnowAdapter.clearData();
                    showDataEmptyView(false);
                }else if(mKnowCurrentPage > 1){
                    setLoadingMoreIndicator(false);
                    showLoadMoreDataError(response.getStatus().getDesc());
                }
            }
        }

        @Override
        public void onFail(RequestBase request, Error error) {
            if(mKnowCurrentPage == 1){
                setLoadingIndicator(false);
                mKnowAdapter.clearData();
                showDataErrorView(false);
            }else if(mKnowCurrentPage > 1){
                setLoadingMoreIndicator(false);
                showLoadMoreDataError(error.getLocalizedMessage());
            }
        }
    };

    HttpCallback<PaperResponse> mReportCallback = new HttpCallback<PaperResponse>() {
        @Override
        public void onSuccess(RequestBase request, PaperResponse ret) {
            if(ret.getStatus().getCode() == 0){
                if(ret.getData().size() > 0){
                    Paper paper = new Paper(ret.getData().get(0), QuestionShowType.ANALYSIS);
                    DataFetcher.getInstance().save(paper.getId(),paper);
                    openAnswerReportUI(paper.getId());
                }else {
                    showMsg(ret.getStatus().getDesc());
                }
            }else {
                showMsg(ret.getStatus().getDesc());
            }
        }

        @Override
        public void onFail(RequestBase request, Error error) {
            showMsg(error.getLocalizedMessage());
        }
    };

    HttpCallback<PaperResponse> mPaperCallback = new HttpCallback<PaperResponse>() {
        @Override
        public void onSuccess(RequestBase request, PaperResponse ret) {
            if(ret.getStatus().getCode() == 0){
                if(ret.getData().size() > 0){
                    QuestionShowType type = QuestionShowType.ANSWER;
                    Paper paper = new Paper(ret.getData().get(0), type);
                    DataFetcher.getInstance().save(paper.getId(),paper);
                    openAnswerQuestionUI(paper.getId());
                }else {
                    showMsg(ret.getStatus().getDesc());
                }
            }else {
                showMsg(ret.getStatus().getDesc());
            }
        }

        @Override
        public void onFail(RequestBase request, Error error) {
            showMsg(error.getLocalizedMessage());
        }
    };

    HttpCallback<EditionResponse> mEditionCallback = new EXueELianBaseCallback<EditionResponse>() {
        @Override
        protected void onResponse(RequestBase request, EditionResponse response) {
            if(response.getStatus().getCode() == 0){
                mNoEditions = false;
                mEditionChildBeanList = response.getData().get(0).getChildren();
                mVolume = getVolume(response.getData(),mEditionId);
                mStage.setText(response.getData().get(0).getChildren().get(0).getName());
                getExercisesByChapter(1);
            }else {
                showDataErrorView(true);
            }
        }

        @Override
        public void onFail(RequestBase request, Error error) {
            showDataErrorView(true);
        }
    };
}