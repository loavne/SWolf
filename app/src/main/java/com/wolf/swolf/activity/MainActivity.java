package com.wolf.swolf.activity;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wolf.swolf.R;
import com.wolf.swolf.fragment.FragmentGirls;
import com.wolf.swolf.fragment.FragmentNews;
import com.wolf.swolf.fragment.FragmentPerson;
import com.wolf.swolf.fragment.FragmentSettings;
import com.wolf.wlibrary.activity.SwActivity;
import com.wolf.wlibrary.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MainActivity extends SwActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.tl_title)
    TextView mTlTitle;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.content_frame_layout)
    FrameLayout mFrameLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;

    private List<Fragment> fragments;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentTransaction mTransaction;

    private int index;
    private int currentIndex;

    private FragmentNews mFragmentNews;
    private FragmentGirls mFragmentGirls;
    private FragmentPerson mFragmentPerson;
    private FragmentSettings mFragmentSettings;

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
//        MyViewPagerAdapter viewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
//        viewPagerAdapter.addFragment(new FragmentOne(), "first");
//        viewPagerAdapter.addFragment(new FragmentNews(), "second");
//        viewPagerAdapter.addFragment(new FragmentOne(), "third");
//        mViewPager.setAdapter(viewPagerAdapter);
//
//        mTabLayout.addTab(mTabLayout.newTab().setText("first"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("second"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("third"));
//        mTabLayout.setupWithViewPager(mViewPager);
        mNavView.setNavigationItemSelectedListener(this);
        initFragment();

        initSlidingHeader();
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new FragmentNews());
        fragments.add(new FragmentGirls());
        fragments.add(new FragmentPerson());
        fragments.add(new FragmentSettings());
    }

    private void initSlidingHeader() {
        //NavigationView头部调用
        View header = mNavView.getHeaderView(0);
        CircleImageView userHeader = (CircleImageView) header.findViewById(R.id.iv_user_header);
        TextView userName = (TextView) header.findViewById(R.id.tv_user_name);
        userHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(LoginActivity.class);
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        mTransaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_news:
                if (mFragmentNews == null) {
                    mFragmentNews = new FragmentNews();
                    mTransaction.add(R.id.content_frame_layout, mFragmentNews);
                } else {
                    mTransaction.show(mFragmentNews);
                }
                selectTab(getResources().getString(R.string.sliding_menu_news), item);
                break;
            case R.id.nav_girls:
                if (mFragmentGirls == null) {
                    mFragmentGirls = new FragmentGirls();
                    mTransaction.add(R.id.content_frame_layout, mFragmentGirls);
                } else {
                    mTransaction.show(mFragmentGirls);
                }
                selectTab(getResources().getString(R.string.sliding_menu_girls), item);
                break;
            case R.id.nav_person:
                if (mFragmentPerson == null) {
                    mFragmentPerson = new FragmentPerson();
                    mTransaction.add(R.id.content_frame_layout, mFragmentPerson);
                } else {
                    mTransaction.show(mFragmentPerson);
                }
                selectTab(getResources().getString(R.string.sliding_menu_person), item);
                break;
            case R.id.nav_settings:
                if (mFragmentSettings == null) {
                    mFragmentSettings = new FragmentSettings();
                    mTransaction.add(R.id.content_frame_layout, mFragmentSettings);
                } else {
                    mTransaction.show(mFragmentSettings);
                }

                selectTab(getResources().getString(R.string.sliding_menu_settings), item);
                break;
        }

        mTransaction.commitAllowingStateLoss();
        return false;
    }

    private void selectTab(String title, MenuItem item) {
        item.setChecked(true);
        item.setTitle(title);
        mTlTitle.setText(title);
        mDrawerLayout.closeDrawers();
    }
}
