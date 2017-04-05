package jiang.kaka.controller.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseContactListFragment;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jiang.kaka.R;
import jiang.kaka.controller.activity.AddContactActivity;
import jiang.kaka.controller.activity.ChatActivity;
import jiang.kaka.controller.activity.GroupListActivity;
import jiang.kaka.controller.activity.InviteActivity;
import jiang.kaka.model.Model;
import jiang.kaka.model.bean.UserInfo;
import jiang.kaka.utils.Constant;
import jiang.kaka.utils.SpUtils;

/**
 * 联系人列表页面
 * Created by 在云端 on 2016/10/28.
 */
public class ContactListFragment extends EaseContactListFragment {
    private LocalBroadcastManager LBM;
    private LinearLayout ll_contact_invite;
    private String mHxid;

    private ImageView iv_contact_red;
    private BroadcastReceiver contactInviteChangeReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //设置红点显示
            iv_contact_red.setVisibility(View.VISIBLE);
            //更新
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, true);
        }
    };
    private BroadcastReceiver contactChangeReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            refreshContact();

        }
    };
    private BroadcastReceiver GroupChangeReciver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //显示红点
            iv_contact_red.setVisibility(View.VISIBLE);
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

        }
    };

    @Override
    protected void initView() {
        super.initView();

        titleBar.setRightImageResource(R.drawable.em_add);
        View headerView = View.inflate(getActivity(), R.layout.haeder_contact_fragment, null);
        listView.addHeaderView(headerView);
        iv_contact_red = (ImageView) headerView.findViewById(R.id.iv_contact_red);
        //获取联系人条目的点击事件
        ll_contact_invite = (LinearLayout) headerView.findViewById(R.id.ll_contact_invite);

        //设置listView条目的点击事件
        setContactListItemClickListener(new EaseContactListItemClickListener() {
            @Override
            public void onListItemClicked(EaseUser user) {
                if(user==null)
                {
                    return;
                }
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //传入参数
                intent.putExtra(EaseConstant.EXTRA_USER_ID,user.getUsername());
                startActivity(intent);
            }
        });

        //跳转到群组列表页面
        LinearLayout ll_contact_group = (LinearLayout) headerView.findViewById(R.id.ll_contact_group);
        ll_contact_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GroupListActivity.class);
                startActivity(intent);

            }
        });
    }


    @Override
    protected void setUpView() {
        super.setUpView();
        //添加按钮的点击事件
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                startActivity(intent);
            }
        });


        //初始化红点显示
        boolean isNewInvite = SpUtils.getInstance().getBoolean(SpUtils.IS_NEW_INVITE, false);
        iv_contact_red.setVisibility(isNewInvite ? View.VISIBLE : View.GONE);


        //获取联系人条目的点击事件
        ll_contact_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //红点处理
                iv_contact_red.setVisibility(View.GONE);
                SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE, false);
                //跳转到邀请人信息页面
                Intent intent = new Intent(getActivity(), InviteActivity.class);
                startActivity(intent);
            }
        });

        // 注册广播
        LBM = LocalBroadcastManager.getInstance(getActivity());
        LBM.registerReceiver(contactInviteChangeReciver, new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        LBM.registerReceiver(contactChangeReciver, new IntentFilter(Constant.CONTACT_CHANGED));
        LBM.registerReceiver(GroupChangeReciver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));

        //从环信服务器获取所有的联系人信息
        getContactByHxServer();

        //绑定listView和contextMenu
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
           //获取环信id
        int position = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
        //强转为easeUser
        EaseUser easeUser = (EaseUser) listView.getItemAtPosition(position);

        mHxid = easeUser.getUsername();
        //添加布局
        getActivity().getMenuInflater().inflate(R.menu.delete, menu);
    }
    //具体删除操作

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contact_delete) {
            //执行删除选中的联系人
            deleteContact();

            return true;//将事件消费掉
        }
        return super.onContextItemSelected(item);
    }

    //执行删除选中的联系人
    private void deleteContact() {

        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器中删除联系人
                    EMClient.getInstance().contactManager().deleteContact(mHxid);
                    //本地数据库的更新
                   Model.getInstance().getDBManager().getContactTableDao().deleteContactByHxId(mHxid);

                    if (getActivity()==null)
                    {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //内存的变化，刷新,加Toast
                            Toast.makeText(getActivity(), "删除"+mHxid+"成功", Toast.LENGTH_SHORT).show();
                            refreshContact();

                        }
                    });

                } catch (HyphenateException e) {
                    e.printStackTrace();

                    if (getActivity()==null)
                    {
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //内存的变化，刷新,加Toast
                            Toast.makeText(getActivity(), "删除"+mHxid+"失败", Toast.LENGTH_SHORT).show();


                        }
                    });

                }
            }
        });
    }

    private void getContactByHxServer() {
        //只要访问网络就要到Model
        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //去服务器获取联系人信息,获取所有好友的环信id
                    List<String> hxids = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    //进行id校验
                    if (hxids != null && hxids.size() >= 0) {
                        List<UserInfo> contacts = new ArrayList<UserInfo>();
                        //将环信id转换成userinfo
                        for (String hxid : hxids) {
                            UserInfo userInfo = new UserInfo(hxid);
                            contacts.add(userInfo);
                        }

                        //拿到id后，把好友id保存到本地数据库
                        Model.getInstance().getDBManager().getContactTableDao().saveContacts(contacts, true);

                        if (getActivity() == null) {
                            return;
                        }
                        //刷新页面
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshContact();
                            }
                        });

                    }
                } catch (HyphenateException e) {


                }
            }
        });
    }

    private void refreshContact() {
        //刷新肯定要更新数据库
        List<UserInfo> contacts = Model.getInstance().getDBManager().getContactTableDao().getContacts();
        //校验
        if (contacts != null && contacts.size() >= 0) {
            //设置数据
            Map<String, EaseUser> contactMap = new HashMap<>();
            //将用户列表转换成Map
            for (UserInfo contact : contacts) {
                EaseUser easeUser = new EaseUser(contact.getHxid());
                contactMap.put(contact.getHxid(), easeUser);

            }
            setContactsMap(contactMap);
            //刷新页面
            refresh();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LBM.unregisterReceiver(contactInviteChangeReciver);
        LBM.unregisterReceiver(contactChangeReciver);
        LBM.unregisterReceiver(GroupChangeReciver);
    }
}

