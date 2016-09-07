package com.wolf.wlibrary.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.wolf.wlibrary.base.SwActivityManager;
import com.wolf.wlibrary.utils.IntentUtil;

import butterknife.ButterKnife;

/**
 * 描述：Activity基类
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-02 10:53
 */
public abstract class SwActivity extends AppCompatActivity{

    /** 是否透明状态栏 */
    private boolean isStatusBar = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(getLayoutId());
        initView();
        SwActivityManager.getInstance().addActivity(this);
    }

//    /**
//     * 初始化toolbar
//     */
//    protected void initToolbar(){
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
//        mToolbarSubTitle = (TextView) findViewById(R.id.toolbar_subtitle);
//        if (mToolbar != null) {
//            //将Toolbar添加至界面
//            setSupportActionBar(mToolbar);
//        }
//        //...标题主标题设置
//    }

    /**
     * 获取布局资源Id
     */
    public abstract int getLayoutId();

    /**
     * 初始化
     */
    protected abstract void initView();

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

}
