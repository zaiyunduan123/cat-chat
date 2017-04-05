package jiang.kaka.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.model.bean.UserInfo;
import jiang.kaka.model.db.DBHelper;

/**
 * Created by 在云端 on 2016/11/1.
 */
public class ContactTableDao {
    private DBHelper dbHelper;

    public ContactTableDao(DBHelper helper) {
        dbHelper = helper;
    }

    // 获取所有联系人

    public List<UserInfo> getContacts() {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //执行查询语句
        String sql = "select * from " + ContactTable.TAB_NAME + " where " + ContactTable.COL_IS_CONTACT + "=1";
        Cursor cursor = db.rawQuery(sql, null);

        List<UserInfo> users = new ArrayList<>();
        //判断cursor下面是否有数据
        while (cursor.moveToNext()) {
            UserInfo userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));
            //封装对象
            users.add(userInfo);
        }
        cursor.close();

        return users;


    }

    // 通过环信id获取用户联系人信息
    public UserInfo getContactsByHx(String hxId) {
              if (hxId==null)
              {
                  return null;
              }
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //执行查询语句

        String sql="select * from "+ContactTable.TAB_NAME+" where "+ContactTable.COL_HXID+"=?";
        Cursor cursor = db.rawQuery(sql, new String[]{hxId});
        UserInfo userInfo=null;
        while (cursor.moveToNext())
        {
             userInfo = new UserInfo();
            userInfo.setHxid(cursor.getString(cursor.getColumnIndex(ContactTable.COL_HXID)));
            userInfo.setName(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NAME)));
            userInfo.setNick(cursor.getString(cursor.getColumnIndex(ContactTable.COL_NICK)));
            userInfo.setPhoto(cursor.getString(cursor.getColumnIndex(ContactTable.COL_PHOTO)));

        }


        cursor.close();

        return  userInfo;


    }
    // 通过环信id获取用户联系人信息
    public List<UserInfo> getContactsByHx(List<String> hxIds) {
          if (hxIds==null||hxIds.size()<0)
          {
              return  null;
          }
        List<UserInfo> contacts=new ArrayList<>();
        for(String hxid:hxIds)
        {
             UserInfo contact= getContactsByHx(hxid);
            contacts.add(contact);
        }
        return  contacts;
    }

    // 保存单个联系人
    public void saveContact(UserInfo user, boolean isMyContact) {
        if(user==null){
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues values=new ContentValues();
        values.put(ContactTable.COL_HXID,user.getHxid());
        values.put(ContactTable.COL_NAME,user.getName());
        values.put(ContactTable.COL_NICK,user.getNick());
        values.put(ContactTable.COL_PHOTO,user.getPhoto());
        values.put(ContactTable.COL_IS_CONTACT,isMyContact ? 1 : 0 );

        db.replace(ContactTable.TAB_NAME,null,values);
    }


    // 保存联系人信息
    public void saveContacts(List<UserInfo> contacts, boolean isMyContact) {
        if (contacts==null||contacts.size()<0)
        {
            return;
        }
        for (UserInfo contact:contacts)
        {
            saveContact(contact,isMyContact);
        }

    }

    // 删除联系人信息
    public void deleteContactByHxId(String hxId) {
            if (hxId==null)
            {
                return;
            }
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        db.delete(ContactTable.TAB_NAME,ContactTable.COL_HXID+"=?",new String[]{hxId});
    }
}