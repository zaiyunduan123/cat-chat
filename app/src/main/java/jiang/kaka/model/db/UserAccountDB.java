package jiang.kaka.model.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import jiang.kaka.model.dao.UserAccountTable;

/**
 * Created by 在云端 on 2016/10/30.
 */
//用户信息数据库的操作类
public class UserAccountDB  extends SQLiteOpenHelper {

    public UserAccountDB(Context context) {
        super(context, "account_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL(UserAccountTable.CREATE_TAB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
    
   