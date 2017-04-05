package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import jiang.kaka.R;
import jiang.kaka.model.Model;
import jiang.kaka.model.bean.UserInfo;

/**  登录页面
 * Created by 在云端 on 2016/10/28.
 */
public class LoginActivity extends Activity{
    private Button bt_login_regist;
    private Button bt_login_login;
    private EditText et_login_name;
    private EditText et_login_pwd;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);

        //初始化控件
        initView();
        //初始化监听
        initListener();
    }

    private void initListener() {
        bt_login_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regist();
            }


        });
        bt_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }


        });
    }
    private void login() {

        //1 获取数据
        final String loginName = et_login_name.getText().toString();
        final String loginPwd = et_login_pwd.getText().toString();
        //2 校对数据
        if (TextUtils.isEmpty(loginName)||TextUtils.isEmpty(loginPwd))
        {
            Toast.makeText(LoginActivity.this, "输入的用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return ;
        }
        //3 登录逻辑处理
        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //环信服务器登录账户
                EMClient.getInstance().login(loginName, loginPwd, new EMCallBack() {
                    //登录成功后的处理
                    @Override
                    public void onSuccess() {
                        //对模型层数据的处理
                        Model.getInstance().loginSuccess(new UserInfo(loginName));
                        //保存用户信息到本地数据库
                        Model.getInstance().getUserAccountDao().addAccount(new UserInfo(loginName));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //提示登录成功
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                //跳转到主页面
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                                finish();//关掉登录页面

                            }
                        });

                    }
                    //登录失败后的处理
                    @Override
                    public void onError(int i, final String s) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(LoginActivity.this, "登录失败"+s, Toast.LENGTH_SHORT).show();
                                EMClient.getInstance().logout(true);



                            }
                        });

                    }
                    //登录过程中的处理
                    @Override
                    public void onProgress(int i, String s) {

                    }
                });
            }
        });
    }

    private void regist() {
        //1 获取数据
        final String registName = et_login_name.getText().toString();
        final String registPassword = et_login_pwd.getText().toString();
       //2 校对数据
        if (TextUtils.isEmpty(registName)||TextUtils.isEmpty(registPassword))
        {
            Toast.makeText(LoginActivity.this, "输入的用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return ;
        }
        //3 去服务器注册一个账户
        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //在环信服务器注册账户
                    EMClient.getInstance().createAccount(registName,registPassword);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                }catch (final HyphenateException e)
                {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "注册失败"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }


    private void initView() {
        bt_login_regist=(Button) findViewById(R.id.bt_login_regist);
        bt_login_login=(Button) findViewById(R.id.bt_login_login);
        et_login_name=(EditText) findViewById(R.id.et_login_name);
        et_login_pwd=(EditText) findViewById(R.id.et_login_pwd);

    }
}
