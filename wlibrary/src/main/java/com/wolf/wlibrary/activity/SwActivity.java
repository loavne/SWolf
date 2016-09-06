package com.wolf.wlibrary.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * 描述：Activity基类
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-02 10:53
 */
public abstract class SwActivity extends Activity {

    /** 是否透明状态栏 */
    private boolean isStatusBar = true;
    /** 是否允许全屏 */
    private boolean isAllowFullScreen = true;
    /** 是否禁止旋转屏幕 */
    private boolean isAllowRotateScreen = true;
    /** 是否输出日志 */
    private boolean isShowDebug = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initData();
    }

    protected abstract void initData();

    public abstract int getLayoutId();


}
