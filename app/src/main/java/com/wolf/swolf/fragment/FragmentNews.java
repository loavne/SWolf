package com.wolf.swolf.fragment;

import android.support.v7.widget.RecyclerView;

import com.wolf.swolf.R;
import com.wolf.wlibrary.fragment.SwFragment;

import butterknife.Bind;

/**
 * <p>Description: hot news</p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-08 16:45
 */
public class FragmentNews extends SwFragment {

    @Bind(R.id.recyclerView_news)
    RecyclerView mRecyclerViewNews;

    @Override
    public int getLayoutId() {
        return R.layout.layout_news_fragment;
    }


    @Override
    protected void lazyData() {

    }

}
