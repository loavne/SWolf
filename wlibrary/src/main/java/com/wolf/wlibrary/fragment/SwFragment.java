package com.wolf.wlibrary.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wolf.wlibrary.R;

import butterknife.ButterKnife;

/**
 * 描述：Fragment基类
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-02 14:27
 */
public abstract class SwFragment extends Fragment{

    private View rootView, contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_base_fragment, container, false);
            if (contentView != null) {
                ((ViewGroup)rootView).addView(contentView);
            }
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (contentView == null) {
                contentView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null);
                if (rootView != null) {
                    ((ViewGroup)rootView).addView(contentView);
                }
            }
            lazyData();
        }
    }

    public abstract int getLayoutId();

    protected abstract void lazyData();

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
