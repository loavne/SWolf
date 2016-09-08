package com.wolf.swolf.activity;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.wolf.swolf.R;
import com.wolf.swolf.adapter.MyViewPagerAdapter;
import com.wolf.swolf.fragment.FragmentNews;
import com.wolf.swolf.fragment.FragmentOne;
import com.wolf.wlibrary.activity.SwActivity;

import butterknife.Bind;

public class MainActivity extends SwActivity {

    @Bind(R.id.tl_title)
    TextView mTlTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        //设置toolbar
        setSupportActionBar(mToolbar);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

//        mDrawerLayout.setScrimColor(Color.TRANSPARENT); //去除侧边阴影

        mTlTitle.setText("标题");
        mToolbar.setTitle("");

        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FragmentOne(), "first");
        viewPagerAdapter.addFragment(new FragmentNews(), "second");
        viewPagerAdapter.addFragment(new FragmentOne(), "third");
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout.addTab(mTabLayout.newTab().setText("first"));
        mTabLayout.addTab(mTabLayout.newTab().setText("second"));
        mTabLayout.addTab(mTabLayout.newTab().setText("third"));
        mTabLayout.setupWithViewPager(mViewPager);

    }

}
