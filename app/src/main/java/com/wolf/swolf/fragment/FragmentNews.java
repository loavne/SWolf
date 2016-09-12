package com.wolf.swolf.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.wolf.swolf.R;
import com.wolf.swolf.adapter.MyViewPagerAdapter;
import com.wolf.wlibrary.fragment.SwFragment;

import butterknife.Bind;

/**
 * <p>Description: hot news</p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-08 16:45
 */
public class FragmentNews extends SwFragment {

    @Bind(R.id.tabLayout_news)
    TabLayout mTabLayoutNews;
    @Bind(R.id.viewPager_news)
    ViewPager mViewPagerNews;

    @Override
    public int getLayoutId() {
        return R.layout.layout_news_fragment;
    }

    @Override
    protected void initView() {
        MyViewPagerAdapter pagerAdapter = new MyViewPagerAdapter(getChildFragmentManager());
        pagerAdapter.addFragment(new FragmentOne(), "first");
        pagerAdapter.addFragment(new FragmentOne(), "second");
        pagerAdapter.addFragment(new FragmentOne(), "third");
        mViewPagerNews.setAdapter(pagerAdapter);

        mTabLayoutNews.addTab(mTabLayoutNews.newTab().setText("first"));
        mTabLayoutNews.addTab(mTabLayoutNews.newTab().setText("second"));
        mTabLayoutNews.addTab(mTabLayoutNews.newTab().setText("third"));
        mTabLayoutNews.setupWithViewPager(mViewPagerNews);
    }

    @Override
    protected void lazy() {
    }

}
