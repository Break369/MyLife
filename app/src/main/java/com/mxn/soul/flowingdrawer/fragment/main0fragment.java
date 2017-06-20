package com.mxn.soul.flowingdrawer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer.R;
import com.mxn.soul.flowingdrawer.activity.PublishActivity;
import com.mxn.soul.flowingdrawer.adapter.MyItemAdapter;
import com.mxn.soul.flowingdrawer.enity.Order;
import com.mxn.soul.flowingdrawer.enity.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * 这是首页的fragment
 * Created by mk on 2017/6/20.
 */

public class Main0Fragment extends BaseFragment {

    //(在MainActivity中通过反射机制获得单例对象)
    public static volatile Main0Fragment instance = null;
    @BindView(R.id.rvFeed)
    RecyclerView rvFeed;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.imgAdd)
    ImageView imgAdd;
    Unbinder unbinder;
    private List<Order> mLists = new ArrayList<Order>();
    private MyItemAdapter myItemAdapter = null;
    private User user = null;

    //单例模式
    public static Main0Fragment newInstance() {
        if (instance == null) {
            synchronized (Main0Fragment.class) {
                if (instance == null) {
                    instance = new Main0Fragment();
                }
            }
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main0, null);
        unbinder = ButterKnife.bind(this, view);
        query();
        return view;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initTitle() {

    }

    @Override
    public void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };

        rvFeed.setLayoutManager(linearLayoutManager);
        myItemAdapter = new MyItemAdapter(mLists,getActivity());
        rvFeed.setAdapter(myItemAdapter);
    }

    @Override
    public void setListener() {
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    PublishActivity.startAction(getActivity());
                } else {
                    Toast.makeText(getActivity(), "请登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        rvFeed.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        myItemAdapter.setOnMyClickItemListener(new MyItemAdapter.onMyClickItemListener() {
            @Override
            public void clickItem(View view, int position) {
                Toast.makeText(getActivity(), "点击了" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    public void query() {
        BmobQuery<Order> queryData = new BmobQuery<Order>();
        queryData.setLimit(15);
        queryData.include("user");
        queryData.findObjects(getActivity(), new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if (list != null) {
                    mLists = list;
                    myItemAdapter.updateItems(mLists);
                }
            }
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "查询失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refresh() {
        BmobQuery<Order> queryData = new BmobQuery<Order>();
        queryData.setLimit(15);
        queryData.include("user");
        queryData.findObjects(getActivity(), new FindListener<Order>() {
            @Override
            public void onSuccess(List<Order> list) {
                if (list != null) {
                    mLists.clear();
                    mLists.addAll(list);
                    Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
                    myItemAdapter.updateItems(mLists);
                    refreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onError(int i, String s) {
                Toast.makeText(getActivity(), "网络出错", Toast.LENGTH_SHORT).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }
}
