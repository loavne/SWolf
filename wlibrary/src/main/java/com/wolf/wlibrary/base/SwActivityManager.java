package com.wolf.wlibrary.base;

import android.app.Activity;
import android.content.Context;

import java.util.LinkedList;
import java.util.List;

/**
 * 描述：用于处理退出程序时可以退出所有Activity, 而编写的通用类
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-02 11:07
 */
public class SwActivityManager {

    private List<Activity> mActivityList = new LinkedList<>();

    private volatile static SwActivityManager instance = null;

    public SwActivityManager() {
    }

    public static SwActivityManager getInstance() {
        if (instance == null) {
            synchronized (SwActivityManager.class) {
                if (instance == null) {
                    instance = new SwActivityManager();
                }
            }
        }
        return instance;
    }

    /**
     * 添加Activity到容器中.
     * @param activity
     */
    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    /**
     * 移除Activity从容器中.
     * @param activity
     */
    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    /**
     * 遍历所有Activity并finish.
     */
    public void clearAllActivity() {
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            clearAllActivity();
            android.app.ActivityManager activityMgr= (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) { }
    }
}
