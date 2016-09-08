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
        //添加Activity到堆栈
        SwActivityManager.getAppManager().addActivity(this);
        setContentView(getLayoutId());
        //初始化黄油刀
        ButterKnife.bind(this);
        initView();
    }

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
    public void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
//        //系统大于5.0
//        if (Build.VERSION.SDK_INT >= 21) {
//            //获取根视图
//            View decorView = getWindow().getDecorView();
//            //全屏,会将状态栏隐藏
//            int option =View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        }
    }

    /**
     * 透明导航栏
     */
    public void setNavigation() {
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

    @Override
    protected void onDestroy() {
        // 结束Activity&从堆栈中移除
        SwActivityManager.getAppManager().finishActivity(this);
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
