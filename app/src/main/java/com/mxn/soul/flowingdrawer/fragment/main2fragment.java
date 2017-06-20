package com.mxn.soul.flowingdrawer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mxn.soul.flowingdrawer.R;

/**
 * 这是首页的fragment
 * Created by mk on 2017/6/20.
 */

public class Main2Fragment extends BaseFragment{

    //(在MainActivity中通过反射机制获得单例对象)
    public static volatile Main2Fragment instance = null;

    //单例模式
    public static Main2Fragment newInstance(){
        if(instance == null){
            synchronized (Main2Fragment.class){
                if(instance == null){
                    instance = new Main2Fragment();
                }
            }
        }
        return instance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main2,null);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initTitle() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }
}
