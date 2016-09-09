package com.wolf.swolf.activity;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wolf.swolf.R;
import com.wolf.wlibrary.activity.SwActivity;
import com.wolf.wlibrary.base.SwActivityManager;

import butterknife.Bind;

/**
 * <p>Description: </p>
 *
 * @author: swolf (https://github.com/loavne)
 * @date : 2016-09-08 17:35
 */
public class LoginActivity extends SwActivity {

    @Bind(R.id.toolbar_login)
    Toolbar mToolbarLogin;
    @Bind(R.id.et_user_name)
    EditText mEtUserName;
    @Bind(R.id.et_password)
    EditText mEtPassword;
    @Bind(R.id.login)
    Button mLogin;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        setSupportActionBar(mToolbarLogin);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbarLogin.setNavigationIcon(R.drawable.navigationview_back);
        mToolbarLogin.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwActivityManager.getAppManager().finishActivity(LoginActivity.class);
            }
        });
    }

}
