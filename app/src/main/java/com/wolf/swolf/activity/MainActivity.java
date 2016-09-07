package com.wolf.swolf.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.wolf.swolf.R;
import com.wolf.swolf.adapter.MyViewPagerAdapter;
import com.wolf.swolf.fragment.FragmentOne;
import com.wolf.wlibrary.activity.SwActivity;

public class MainActivity extends SwActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //设置toolbar
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.icon_back);

        TextView tl_title = (TextView) findViewById(R.id.tl_title);
        tl_title.setText("标题");

        mToolbar.setTitle("");
        mToolbar.setSubtitle("");

        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentOne(), "first");
        viewPagerAdapter.addFragment(new FragmentOne(), "second");
        viewPagerAdapter.addFragment(new FragmentOne(), "third");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout.addTab(mTabLayout.newTab().setText("first"));
        mTabLayout.addTab(mTabLayout.newTab().setText("second"));
        mTabLayout.addTab(mTabLayout.newTab().setText("third"));
        mTabLayout.setupWithViewPager(mViewPager);

    }
}
