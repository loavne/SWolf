package com.wolf.wlibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 描述：Fragment基类
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-02 14:27
 */
public abstract class SwFragment extends Fragment{

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        }
        ButterKnife.bind(getActivity());
        initView();
        return rootView;
    }




    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            lazy();
        }
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    /**
     * 懒加载数据 and 懒初始化views
     */
    protected abstract void lazy();

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ViewGroup)rootView).removeView(rootView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
