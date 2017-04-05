package jiang.kaka.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.model.bean.GroupInfo;
import jiang.kaka.model.bean.InvationInfo;
import jiang.kaka.model.bean.UserInfo;
import jiang.kaka.model.db.DBHelper;

/**
 * Created by 在云端 on 2016/11/2.
 */
public class InviteTableDao {
    private DBHelper mHelper;

    public InviteTableDao(DBHelper helper) {
        mHelper = helper;
    }

    //添加邀请
    public void addInvitation(InvationInfo invationInfo) {
        //获取数据库连接
        SQLiteDatabase db = mHelper.getReadableDatabase();

        //执行添加语句
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_REASON, invationInfo.getReason());
        values.put(InviteTable.COL_STATUS, invationInfo.getStatus().ordinal());//ordinal枚举的序号

        UserInfo user = invationInfo.getUser();

        if (user != null) {
            values.put(InviteTable.COL_USER_HXID, invationInfo.getUser().getHxid());
            values.put(InviteTable.COL_USER_NAME, invationInfo.getUser().getName());
        } else {
            values.put(InviteTable.COL_GROUP_HXID, invationInfo.getGroup().getGroudId());
            values.put(InviteTable.COL_GROUP_NAME, invationInfo.getGroup().getGroupName());
            values.put(InviteTable.COL_USER_HXID, invationInfo.getGroup().getInvatePerson());//邀请人
        }

        db.replace(InviteTable.TAB_NAME, null, values);

    }

    //获取所有信息邀请信息
    public List<InvationInfo> getInvitations() {

        List<InvationInfo> invitations=new ArrayList<>() ;

        SQLiteDatabase db = mHelper.getReadableDatabase();

        String sql="select * from "+InviteTable.TAB_NAME;

        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()){

            InvationInfo invationInfo = new InvationInfo();

            invationInfo.setReason(cursor.getString(cursor.getColumnIndex(InviteTable.COL_REASON)));
            invationInfo.setStatus(intTOinvationStatus(cursor.getInt(cursor.getColumnIndex(InviteTable.COL_STATUS))));

            String groupId = cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID));
            if (groupId==null)//联系人邀请信息
            {
                UserInfo userInfo = new UserInfo();

                userInfo.setHxid(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));
                userInfo.setName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));
                userInfo.setNick(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_NAME)));

                invationInfo.setUser(userInfo);


            }else//群组的邀请信息
            {
                GroupInfo groupInfo =  new GroupInfo();
                groupInfo.setGroudId(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_HXID)));
                groupInfo.setGroupName(cursor.getString(cursor.getColumnIndex(InviteTable.COL_GROUP_NAME)));
                groupInfo.setInvatePerson(cursor.getString(cursor.getColumnIndex(InviteTable.COL_USER_HXID)));

                invationInfo.setGroup(groupInfo);

            }
            invitations.add(invationInfo);



        }
        cursor.close();

        return  invitations;
    }

    //将int类型状态转换成邀请状态
    private InvationInfo.InvationStatus intTOinvationStatus(int intStatus) {

        if (intStatus == InvationInfo.InvationStatus.NEW_INVITE.ordinal()) {
            return InvationInfo.InvationStatus.NEW_INVITE;

        }

        if (intStatus == InvationInfo.InvationStatus.INVITE_ACCEPT.ordinal()) {
            return InvationInfo.InvationStatus.INVITE_ACCEPT;
        }

        if (intStatus == InvationInfo.InvationStatus.INVITE_ACCEPT_BY_PEER.ordinal()) {
            return InvationInfo.InvationStatus.INVITE_ACCEPT_BY_PEER;
        }

        if (intStatus == InvationInfo.InvationStatus.NEW_GROUP_INVITE.ordinal()) {
            return InvationInfo.InvationStatus.NEW_GROUP_INVITE;
        }

        if (intStatus == InvationInfo.InvationStatus.NEW_GROUP_APPLICATION.ordinal()) {
            return InvationInfo.InvationStatus.NEW_GROUP_APPLICATION;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_INVITE_ACCEPTED.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_INVITE_ACCEPTED;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_APPLICATION_ACCEPTED.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_APPLICATION_ACCEPTED;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_INVITE_DECLINED.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_INVITE_DECLINED;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_APPLICATION_DECLINED.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_APPLICATION_DECLINED;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_ACCEPT_INVITE.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_ACCEPT_INVITE;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_ACCEPT_APPLICATION.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_ACCEPT_APPLICATION;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_REJECT_APPLICATION.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_REJECT_APPLICATION;
        }

        if (intStatus == InvationInfo.InvationStatus.GROUP_REJECT_INVITE.ordinal()) {
            return InvationInfo.InvationStatus.GROUP_REJECT_INVITE;
        }

        return null;
    }

    //删除邀请
    public void removeInvitation(String hxId) {
          if (hxId==null)
        {
            return;
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();


        db.delete(InviteTable.TAB_NAME,InviteTable.COL_USER_HXID+"=?",new String[]{hxId});


    }

    //更新邀请状态
    public void updateInvitationStatus(InvationInfo.InvationStatus invationStatus,String hxId)
    {
        if (hxId==null)
        {
            return;
        }
        SQLiteDatabase db = mHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(InviteTable.COL_STATUS,invationStatus.ordinal());

        db.update(InviteTable.TAB_NAME,values,InviteTable.COL_USER_HXID+"=?",new String[]{hxId});
    }

}
