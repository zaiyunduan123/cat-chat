package jiang.kaka;

import android.app.Application;
import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

import jiang.kaka.model.Model;

/**
 * Created by 在云端 on 2016/10/28.
 */
public class KaKaApplication extends Application{

    private static Context mContext;//全局上下文对象

    @Override
    public void onCreate() {
        super.onCreate();


        EMOptions options=new EMOptions();
        options.setAcceptInvitationAlways(false);
        options.setAutoAcceptGroupInvitation(false);
        EaseUI.getInstance().init(this,options);
        EMClient.getInstance().setDebugMode(true);

        Model.getInstance().init(this);

      //  初始化全局上下文对象
        mContext=this;

       }
    public static Context getGlobalKaKaApplicaotin()
    {
        return mContext;
    }
}
