package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;

import jiang.kaka.R;
import jiang.kaka.model.Model;
import jiang.kaka.model.bean.UserInfo;

/**     欢迎界面
 * Created by 在云端 on 2016/10/28.
 */

public class SplashActivity extends Activity{

    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            //如果activity已经退出，那就不处理handler
            if(isFinishing())
            {
                return;
            }
            //判断加入主页面还是登录页面
            toMainOrLogin();
        }


    };


    private void toMainOrLogin() {


        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //判断是否已经登录过
                if(EMClient.getInstance().isLoggedInBefore())
                {
                    //获取当前用户信息
                    UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxid(EMClient.getInstance().getCurrentUser());
                    if (account==null) {
                        Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }else {
                        Model.getInstance().loginSuccess(account);
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }else
                {
                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
