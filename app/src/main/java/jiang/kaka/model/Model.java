package jiang.kaka.model;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jiang.kaka.model.bean.UserInfo;
import jiang.kaka.model.dao.UserAccountDao;
import jiang.kaka.model.db.DBManager;

/**
 * Created by 在云端 on 2016/10/30.
 */
public class Model {
    //新建一个对象
    private static Model model = new Model();
    private Context mContext;
    //新建一个线程池
    private ExecutorService executors = Executors.newCachedThreadPool();
    private UserAccountDao userAccountDao;
    private DBManager dbManager;

    //私有化构造方法
    private Model() {

    }

    public static Model getInstance() {
        return model;
    }

    public void init(Context context) {
        mContext = context;
        userAccountDao = new UserAccountDao(mContext);
        //开启全局监听
        EventListener eventListener = new EventListener(mContext);

    }

    //获取全局线程池
    public ExecutorService getGlobalTreadPool() {
        return executors;
    }

    //用户登录成功后的处理
    // 用户登录成功后的处理方法
    public void loginSuccess(UserInfo account) {
        if (account == null) {
            return;
        }
        if (dbManager != null) {
            dbManager.close();
        }
        dbManager = new DBManager(mContext, account.getName());


    }

    public DBManager getDBManager() {
        return dbManager;
    }

    //获取用户帐号数据库的操作类对象
    public UserAccountDao getUserAccountDao() {
        return userAccountDao;
    }

}
