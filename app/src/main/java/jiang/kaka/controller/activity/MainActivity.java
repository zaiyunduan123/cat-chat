package jiang.kaka.controller.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.widget.RadioGroup;

import jiang.kaka.R;
import jiang.kaka.controller.fragment.ChatFragment;
import jiang.kaka.controller.fragment.ContactListFragment;
import jiang.kaka.controller.fragment.SettingFragment;

/**
 * 主页面
 * Created by 在云端 on 2016/10/28.
 */
public class MainActivity extends FragmentActivity {

    private ChatFragment chatFragment;
    private ContactListFragment contactListFragment;
    private SettingFragment settingFragment;
    private RadioGroup radioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();

        initListener();
    }

    private void initListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                switch (checkedId) {
                    case R.id.rb_main_chat:
                        fragment = chatFragment;
                        break;

                    case R.id.rb_main_contact:
                        fragment = contactListFragment;
                        break;

                    case R.id.rb_main_setting:
                        fragment = settingFragment;
                        break;

                }
                //实现Fragment的切换
                switchFragment(fragment);

            }
        });
        radioGroup.check(R.id.rb_main_chat);
    }
    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fl_main,fragment).commit();
    }


    private void initData() {
        //创建三个Fragment
        chatFragment = new ChatFragment();
        contactListFragment = new ContactListFragment();
        settingFragment = new SettingFragment();

    }

    private void initView() {
        radioGroup = (RadioGroup) findViewById(R.id.rg_main);
    }
}
