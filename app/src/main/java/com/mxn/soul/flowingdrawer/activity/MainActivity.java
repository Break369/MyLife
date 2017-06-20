package com.mxn.soul.flowingdrawer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mxn.soul.flowingdrawer.R;
import com.mxn.soul.flowingdrawer.enity.User;
import com.mxn.soul.flowingdrawer.fragment.Main0Fragment;
import com.mxn.soul.flowingdrawer.fragment.Main1Fragment;
import com.mxn.soul.flowingdrawer.fragment.Main2Fragment;
import com.mxn.soul.flowingdrawer.util.MenuListFragment;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.BmobDialogButtonListener;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.layout_home)
    LinearLayout layoutHome;
    @BindView(R.id.layout_categorize)
    LinearLayout layoutCategorize;
    @BindView(R.id.layout_more)
    LinearLayout layoutMore;
    private FlowingDrawer mDrawer;
    private User user = null;
    private TextView tvUserName;
    private MenuListFragment mMenuFragment;
    private static final String KEY_FRAGMENT_TAG = "fragment_tag";
    private static final String FRAGMENT_TAG_HOME = "fragment_home";
    private static final String FRAGMENT_TAG_Categorize = "fragment_categorize";
    private static final String FRAGMENT_TAG_PERSON = "fragment_person";
    private Main0Fragment mHomeFragment;
    private Main1Fragment mCategorizeFragment;
    private Main2Fragment mPersonFragment;
    private String[] mFragmentTags = new String[]{FRAGMENT_TAG_HOME, FRAGMENT_TAG_Categorize, FRAGMENT_TAG_PERSON};
    private String mFragmentCurrentTag = FRAGMENT_TAG_HOME;
    private LinearLayout[] mLayouts = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            Log.e("fragment","恢复fragment"+KEY_FRAGMENT_TAG);
            restoreFragments();//恢复fragment
            mFragmentCurrentTag = savedInstanceState.getString(KEY_FRAGMENT_TAG);
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bmob.initialize(getApplication(), "8641a3984f7c4a4e3d19558a69423ecf");
        user = BmobUser.getCurrentUser(this, User.class);
        mDrawer = (FlowingDrawer) findViewById(R.id.drawerlayout);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);
        mLayouts = new LinearLayout[]{
                layoutHome, layoutCategorize, layoutMore
        };
        setupToolbar();
        setupFeed();
        setupMenu();
        setListener();
        update();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_FRAGMENT_TAG, mFragmentCurrentTag);
        super.onSaveInstanceState(outState);
    }

    /**
     * 这个是进行app的版本的升级
     */
    private void update() {
        //BmobUpdateAgent.setUpdateOnlyWifi(true);
        BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                // TODO Auto-generated method stub
                if (updateStatus == UpdateStatus.Yes) {//版本有更新
                } else if (updateStatus == UpdateStatus.No) {
                    //Toast.makeText(MainActivity.this, "版本无更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.EmptyField) {//此提示只是提醒开发者关注那些必填项，测试成功后，无需对用户提示
                    //Toast.makeText(MainActivity.this, "请检查你AppVersion表的必填项，1、target_size（文件大小）是否填写；2、path或者android_url两者必填其中一项。", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.IGNORED) {
                    //Toast.makeText(MainActivity.this, "该版本已被忽略更新", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.ErrorSizeFormat) {
                    //Toast.makeText(MainActivity.this, "请检查target_size填写的格式，请使用file.length()方法获取apk大小。", Toast.LENGTH_SHORT).show();
                } else if (updateStatus == UpdateStatus.TimeOut) {
                    Toast.makeText(MainActivity.this, "查询出错或查询超时", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //发起自动更新
        BmobUpdateAgent.update(this);
        //设置对对话框按钮的点击事件的监听
        BmobUpdateAgent.setDialogListener(new BmobDialogButtonListener() {
            @Override
            public void onClick(int status) {
                // TODO Auto-generated method stub
                switch (status) {
                    case UpdateStatus.Update:
                        Toast.makeText(MainActivity.this, "立即更新", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.NotNow:
                        Toast.makeText(MainActivity.this, "以后再说", Toast.LENGTH_SHORT).show();
                        break;
                    case UpdateStatus.Close:
                        //只有在强制更新状态下才会在更新对话框的右上方出现close按钮,
                        // 如果用户不点击”立即更新“按钮，这时候开发者可做些操作，比如直接退出应用等
                        Toast.makeText(MainActivity.this, "对话框关闭按钮", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void setListener() {
        for (int i = 0; i < mLayouts.length; i++) {
            mLayouts[i].setOnClickListener(this);
        }
        if (TextUtils.equals(FRAGMENT_TAG_HOME, mFragmentCurrentTag)) {
            layoutHome.performClick();
        } else if (TextUtils.equals(FRAGMENT_TAG_Categorize, mFragmentCurrentTag)) {
            layoutCategorize.performClick();
        }else if (TextUtils.equals(FRAGMENT_TAG_PERSON, mFragmentCurrentTag)) {
            layoutMore.performClick();
        }
    }

    protected void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.toggleMenu();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = BmobUser.getCurrentUser(this, User.class);
        if (user != null) {
            tvUserName = mMenuFragment.getTvUserName();
            tvUserName.setText(user.getUsername());
            Toast.makeText(MainActivity.this, user.getUsername(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFeed() {

    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
        mMenuFragment.setOnMenuClick(new MenuListFragment.onMenuClick() {
            @Override
            public void setClick(MenuItem menuItem) {
                Toast.makeText(MainActivity.this, menuItem.getTitle() + "这是点击事件", Toast.LENGTH_SHORT).show();
                mDrawer.closeMenu();
            }
        });
        mMenuFragment.setOnUserImageClick(new MenuListFragment.onUserImageClick() {
            @Override
            public void imageClick(View view) {
                LoginOrRegister.startAction(MainActivity.this);
                mDrawer.closeMenu();
            }
        });

//        mDrawer.setOnDrawerStateChangeListener(new ElasticDrawer.OnDrawerStateChangeListener() {
//            @Override
//            public void onDrawerStateChange(int oldState, int newState) {
//                if (newState == ElasticDrawer.STATE_CLOSED) {
//                    Log.i("MainActivity", "Drawer STATE_CLOSED");
//                }
//            }
//
//            @Override
//            public void onDrawerSlide(float openRatio, int offsetPixels) {
//                Log.i("MainActivity", "openRatio=" + openRatio + " ,offsetPixels=" + offsetPixels);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isMenuVisible()) {
            mDrawer.closeMenu();
        } else {
            super.onBackPressed();
        }
    }


    /**
     * 恢复fragment
     */
    private void restoreFragments() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        for (int i = 0; i < mFragmentTags.length; i++) {
            Fragment fragment = manager.findFragmentByTag(mFragmentTags[i]);
            if(fragment != null){
                if (fragment instanceof Main0Fragment) {
                    mHomeFragment = (Main0Fragment)fragment;
                } else if (fragment instanceof Main1Fragment) {
                    mCategorizeFragment = (Main1Fragment)fragment;
                } else if (fragment instanceof Main2Fragment) {
                    mPersonFragment = (Main2Fragment)fragment;
                }
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        onTabSelect((LinearLayout)v);
    }
    /**
     * 切换tab页
     * @param itemLayout
     */
    public void onTabSelect(LinearLayout itemLayout) {
        int id = itemLayout.getId();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(manager, transaction);

        //下面图标颜色变化
        for (int i = 0; i < mLayouts.length; i++) {
            mLayouts[i].setSelected(false);
        }
        itemLayout.setSelected(true);

        if (id == R.id.layout_home) {
            selectedFragment(transaction, mHomeFragment, Main0Fragment.class, FRAGMENT_TAG_HOME);
        } else if (id == R.id.layout_categorize) {
            selectedFragment(transaction, mCategorizeFragment, Main1Fragment.class, FRAGMENT_TAG_Categorize);
        }  else if (id == R.id.layout_more) {
            selectedFragment(transaction, mPersonFragment, Main2Fragment.class, FRAGMENT_TAG_PERSON);
        }
        transaction.commit();
    }
    /**
     * 先全部隐藏
     * @param fragmentManager
     * @param transaction
     */
    private void hideFragments(FragmentManager fragmentManager, FragmentTransaction transaction) {
        for (int i = 0; i < mFragmentTags.length; i++) {
            Fragment fragment = fragmentManager.findFragmentByTag(mFragmentTags[i]);
            if (fragment != null && fragment.isVisible()) {
                transaction.hide(fragment);
            }
        }
    }
    //将fragment与一些属性值进行绑定！比如名字等
    private void selectedFragment(FragmentTransaction transaction, Fragment fragment, Class<?> clazz, String tag) {
        mFragmentCurrentTag = tag;
        if (fragment == null) {
            try {
                Method newInstanceMethod = clazz.getDeclaredMethod("newInstance");
                fragment = (Fragment) newInstanceMethod.invoke(null);
            } catch (Exception ex)   {
                ex.printStackTrace();
            }
            transaction.add(R.id.fragment_container, fragment, tag);
            if(tag.equals(FRAGMENT_TAG_HOME)){
                mHomeFragment = (Main0Fragment) fragment;
            }else if(tag.equals(FRAGMENT_TAG_Categorize)){
                mCategorizeFragment = (Main1Fragment) fragment;
            }else if(tag.equals(FRAGMENT_TAG_PERSON)){
                mPersonFragment = (Main2Fragment) fragment;
            }
        }
        transaction.show(fragment);
    }
}
