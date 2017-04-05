package jiang.kaka.utils;

import android.content.Context;
import android.content.SharedPreferences;

import jiang.kaka.KaKaApplication;

/**
 * Created by 在云端 on 2016/11/2.
 */
//用于保存数据和获取数据
public class SpUtils {

    public static final String IS_NEW_INVITE = "is_new_invite";
    private static SpUtils intance=new SpUtils();
    private static SharedPreferences mSp;
    //    private static SharedPreferences mSp;

    private SpUtils() {
    }


    //单例
    public static SpUtils getInstance()
    {
        if(mSp==null)
        {
            mSp = KaKaApplication.getGlobalKaKaApplicaotin().getSharedPreferences("KaKa", Context.MODE_PRIVATE);
        }

        return intance;
    }
    //保存
    public void save(String key,Object value)
    {
        if (value instanceof String)
        {
            mSp.edit().putString(key, (String) value).commit();

        }else if(value instanceof Boolean)
        {
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        }
        else  if (value instanceof Integer)
        {
            mSp.edit().putInt(key, (Integer) value).commit();
        }

    }
    // 获取数据的方法
    public String getString(String key, String defValue) {
        return mSp.getString(key, defValue);
    }

    // 获取boolean数据
    public boolean getBoolean(String key, boolean defValue) {
        return mSp.getBoolean(key, defValue);
    }

    // 获取int类型数据
    public int getInt(String key, int defValue) {
        return mSp.getInt(key, defValue);
    }

}
