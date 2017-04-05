package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import jiang.kaka.R;
import jiang.kaka.controller.adapter.GroupListAdapter;
import jiang.kaka.model.Model;

/**
 * 群组列表页面
 * Created by 在云端 on 2016/10/28.
 */
public class GroupListActivity extends Activity {
    private ListView lv_group_list;
    private GroupListAdapter groupListAdapter;
    private LinearLayout ll_grouplist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        initView();

        initData();

        initListener();
    }

    private void initListener() {
        lv_group_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position==0)
                {
                    return;
                }
                Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                //传递会话类型
                intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                //传递群id

                EMGroup emGroup = EMClient.getInstance().groupManager().getAllGroups().get(position - 1);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,emGroup.getGroupId());

                startActivity(intent);
            }
        });
        //跳转到新建群
        ll_grouplist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupListActivity.this, NewGroupActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initData() {
        groupListAdapter = new GroupListAdapter(this);

        lv_group_list.setAdapter(groupListAdapter);
        //从环信服务器获取所有群的信息
        getGroupsFromHxServer();
    }

    private void getGroupsFromHxServer() {
        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从网络获取数据
                    List<EMGroup> mGroups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    //内存数据变化，也就是更新页面
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息成功", Toast.LENGTH_SHORT).show();
                            //内存数据变化，也就是更新页面
                            refresh();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(GroupListActivity.this, "加载群信息失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    private void refresh() {
        groupListAdapter.refresh(EMClient.getInstance().groupManager().getAllGroups());
    }

    private void initView() {
        lv_group_list = (ListView) findViewById(R.id.lv_grouplist);
        //创建头布局
        View headerView = View.inflate(this, R.layout.header_group_list, null);


        lv_group_list.addHeaderView(headerView);
        ll_grouplist = (LinearLayout) headerView.findViewById(R.id.ll_grouplist);

    }
//可见的时候再次刷新页面
    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }
}

