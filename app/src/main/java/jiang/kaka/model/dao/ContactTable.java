package jiang.kaka.model.dao;

/**
 * Created by 在云端 on 2016/11/1.
 */
public class ContactTable {

    public  static  final  String TAB_NAME="tab_contact";
    public  static  final  String COL_NAME="name";
    public  static  final  String COL_HXID="hxid";
    public  static  final  String COL_NICK="nick";
    public  static  final  String COL_PHOTO="photo";
    public  static  final  String COL_IS_CONTACT="is_contact";

    public  static  final  String CREATE_TAB="create table "
            + TAB_NAME +" ("
            + COL_HXID +" text primary key,"
            + COL_NAME +" text,"
            + COL_NICK +" text,"
            + COL_IS_CONTACT +" integer,"
            + COL_PHOTO +" text);";
}
