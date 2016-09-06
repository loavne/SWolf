package com.wolf.wlibrary.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.wolf.wlibrary.R;
import com.wolf.wlibrary.base.SwActivityManager;
import com.wolf.wlibrary.utils.IntentUtil;

/**
 * 描述：Activity基类
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-02 10:53
 */
public abstract class SwActivity extends AppCompatActivity{

    /** 是否透明状态栏 */
    private boolean isStatusBar = true;

    protected Toolbar mToolbar;
    protected TextView mToolbarTitle;
    protected TextView mToolbarSubTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initToolbar();
        initData();
        SwActivityManager.getInstance().addActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getToolbar() != null && isShowBack()) {
            showBack();
        }
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     * @return
     */
    protected boolean isShowBack() {
        return true;
    }


    /**
     * 版本号小于21的后退按钮图片
     */
    private void showBack(){
        //setNavigationIcon必须在setSupportActionBar(toolbar);方法后面加入
        getToolbar().setNavigationIcon(R.drawable.icon_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 初始化toolbar
     */
    protected void initToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbarSubTitle = (TextView) findViewById(R.id.toolbar_subtitle);
        if (mToolbar != null) {
            //将Toolbar添加至界面
            setSupportActionBar(mToolbar);
        }
        //...标题主标题设置
    }

    /**
     * 初始化
     */
    protected abstract void initData();

    /**
     * 获取布局资源Id
     */
    public abstract int getLayoutId();

    /**
     * 透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 透明导航栏
     */
    private void setNavigation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 启动activity
     * @param clz
     */
    public void openActivity(Class<?> clz) {
        openActivity(clz, null, 0);
    }

    public void openActivity(Class<?> clz, int requestCode) {
        openActivity(clz, null, requestCode);
    }

    public void openActivity(Class<?> clz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (requestCode == 0) {
            IntentUtil.startPreviewActivity(this, intent, 0);
        } else {
            IntentUtil.startPreviewActivity(this, intent, requestCode);
        }
    }

    public TextView getToolbarTitle() {
        return mToolbarTitle;
    }

    /**
     * 设置Toolbar标题
     * @param title
     */
    public void setToolbarTitle(CharSequence title) {
        if (mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        } else {
            getToolbar().setTitle(title);
            setSupportActionBar(getToolbar());
        }
    }

    /**
     * 获取头部Toolbar
     * @return
     */
    private Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

}
