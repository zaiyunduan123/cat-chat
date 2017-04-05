package jiang.kaka.model.db;

import android.content.Context;

import jiang.kaka.model.dao.ContactTableDao;
import jiang.kaka.model.dao.InviteTableDao;

/**
 * Created by 在云端 on 2016/11/2.
 */
//联系人和邀请信息的操作类的管理类
public class DBManager {
    private final DBHelper dbHelper;
    private final ContactTableDao contactTableDao;
    private final InviteTableDao inviteTableDao;

    public DBManager(Context context, String name) {
        dbHelper = new DBHelper(context, name);

        //创建两张表的操作类对象
        contactTableDao = new ContactTableDao(dbHelper);
        inviteTableDao = new InviteTableDao(dbHelper);
    }

    public ContactTableDao getContactTableDao() {
        return contactTableDao;
    }

    public InviteTableDao getInviteTableDao() {
        return inviteTableDao;
    }

    //关闭管理类
    public void close() {
        dbHelper.close();
    }
}
