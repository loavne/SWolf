package com.wolf.swolf.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
    @Bind(R.id.content_frame_layout)
    FrameLayout mFrameLayout;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentTransaction mFragmentTransaction;

    private List<Fragment> fragments = new ArrayList<>();
    //临时fragment
    private Fragment mFragment;
    private FragmentNews mFragmentNews;
    private FragmentGirls mFragmentGirls;
    private FragmentPerson mFragmentPerson;
    private FragmentSettings mFragmentSettings;

    /**
     * Fragment的TAG 用于解决app内存被回收之后导致的fragment重叠问题
     */
    private static final String[] FRAGEMENT_TAG = {"news", "girls", "person", "settings"};

    /**
     * 上一次界面 onSaveInstanceState 之前的tab被选中的状态 key 和 value
     */
    private static final String SELECTED_INDEX = "selected_index";
    private int currentIndex = 0;
    private int mMenuItem;
    private FragmentManager mFragmentManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防止activity被销毁，fragment重叠
        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(SELECTED_INDEX, currentIndex);
            mFragmentNews = (FragmentNews) mFragmentManager.findFragmentByTag(FRAGEMENT_TAG[0]);
            mFragmentGirls = (FragmentGirls) mFragmentManager.findFragmentByTag(FRAGEMENT_TAG[1]);
            mFragmentPerson = (FragmentPerson) mFragmentManager.findFragmentByTag(FRAGEMENT_TAG[2]);
            mFragmentSettings = (FragmentSettings) mFragmentManager.findFragmentByTag(FRAGEMENT_TAG[3]);
        }
    }

    @Override
    protected void initView() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        //设置toolbar
        setSupportActionBar(mToolbar);
        //设置返回键可用
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mDrawerLayout.setScrimColor(Color.TRANSPARENT); //去除侧边阴影
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

        //设置第一个显示的fragment
        mFragmentTransaction.add(R.id.content_frame_layout,mFragment, FRAGEMENT_TAG[0]).commit();
        mTlTitle.setText(getResources().getString(R.string.sliding_menu_news));
    }

    private void initFragment() {
        mFragmentNews = new FragmentNews();
        mFragmentGirls = new FragmentGirls();
        mFragmentPerson = new FragmentPerson();
        mFragmentSettings = new FragmentSettings();
        fragments.add(mFragmentNews);
        fragments.add(mFragmentGirls);
        fragments.add(mFragmentPerson);
        fragments.add(mFragmentSettings);

        mFragment = mFragmentNews;
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
        mMenuItem = item.getItemId();

        //选择选中的item
        selectMenuItem(mMenuItem);

        item.setChecked(true);
        mDrawerLayout.closeDrawers();

        return true;
    }

    private void selectMenuItem(int menuItem) {
        switch (menuItem) {
            case R.id.nav_news:
                if (mFragmentNews == null)
                    mFragmentNews = new FragmentNews();
                switchFragment(mFragment, mFragmentNews, 0);
                mTlTitle.setText(getResources().getString(R.string.sliding_menu_news));
                break;
            case R.id.nav_girls:
                if (mFragmentGirls == null)
                    mFragmentGirls = new FragmentGirls();
                switchFragment(mFragment, mFragmentGirls, 1);
                mTlTitle.setText(getResources().getString(R.string.sliding_menu_girls));
                break;
            case R.id.nav_person:
                if (mFragmentPerson == null)
                    mFragmentPerson = new FragmentPerson();
                switchFragment(mFragment, mFragmentPerson, 2);
                mTlTitle.setText(getResources().getString(R.string.sliding_menu_person));
                break;
            case R.id.nav_settings:
                if (mFragmentSettings == null)
                    mFragmentSettings = new FragmentSettings();
                switchFragment(mFragment, mFragmentSettings, 3);
                mTlTitle.setText(getResources().getString(R.string.sliding_menu_settings));
                break;
        }
    }

    public void switchFragment(Fragment from, Fragment to, int index) {
        currentIndex = index;
        if (mFragment != to) {
            mFragment = to;
            // 先判断是否被add过
            if (!to.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                mFragmentTransaction.hide(from).add(R.id.content_frame_layout, to, FRAGEMENT_TAG[index]);
            } else {
                // 隐藏当前的fragment，显示下一个
                mFragmentTransaction.hide(from).show(to);
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_INDEX, currentIndex);
    }
}
