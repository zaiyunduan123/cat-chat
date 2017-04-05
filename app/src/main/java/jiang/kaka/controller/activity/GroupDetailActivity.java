package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.R;
import jiang.kaka.controller.adapter.GroupDetailAdapter;
import jiang.kaka.model.Model;
import jiang.kaka.model.bean.UserInfo;
import jiang.kaka.utils.Constant;

/**
 * 群详情页面
 * Created by 在云端 on 2016/10/28.
 */
public class GroupDetailActivity extends Activity {
    private GridView gv_groupdetail;
    private Button bt_groupdetail_out;
    private EMGroup mGroup;
    private GroupDetailAdapter groupDetailAdapter;
    private GroupDetailAdapter.onGroupDetailListener onGroupDetailListener = new GroupDetailAdapter.onGroupDetailListener() {
        @Override
        public void onAddMembers() {
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            //传递群id
            intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

            startActivityForResult(intent, 2);


        }

        @Override
        public void onDeleteMember(final UserInfo user) {
            Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //从环信服务器删除此人
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(), user.getHxid());
                        //刷新页面
                        getMembersByHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "删除失败" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            //获取准备邀请的群成员
            final String[] members = data.getStringArrayExtra("members");
            Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器发送邀请
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(),members);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送群邀请成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this, "发送群邀请失败"+e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        initView();
        //获取数据
        getData();

        initData();
        
        initListener();

    }

    private void initListener() {
        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        //判断是否是素材模式
                        if (groupDetailAdapter.ismIsDeleteModel())
                        {

                            groupDetailAdapter.setmIsDeleteModel(false);
                            //刷新
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                }
                return false;
            }
        });
    }

    private void initData() {
        //初始化button显示
        initButtonDisplay();
        //初始化GridView
        initGridview();
        //去环信服务器获取群成员
        getMembersByHxServer();
    }

    private void getMembersByHxServer() {
        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {

            private List<UserInfo> users;

            @Override
            public void run() {
                try {
                    EMGroup emGroup = EMClient.getInstance().groupManager().getGroupFromServer(mGroup.getGroupId());

                    List<String> members = emGroup.getMembers();
                    if (members != null && members.size() >= 0) {
                        users = new ArrayList<UserInfo>();
                        //转换
                        for (String member : members) {
                            UserInfo userInfo = new UserInfo(member);
                            users.add(userInfo);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            groupDetailAdapter.refresh(users);
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this, "获取群成员信息失败" + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridview() {
        //可不可以邀请其他人加群
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner()) || mGroup.isPublic();

        groupDetailAdapter = new GroupDetailAdapter(this, isCanModify, onGroupDetailListener);

        gv_groupdetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        //判断当前用户是否是群主
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())) {//群主
            bt_groupdetail_out.setText("解散群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器解散群
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());
                                //发送解散群的广播
                                exitGroupBroadcast();
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群成功", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "解散群失败" + e, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                }
            });
        } else {//成员
            bt_groupdetail_out.setText("退群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //去环信服务器退群
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());
                                //发送解散群的广播
                                exitGroupBroadcast();
                                //更新页面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群成功", Toast.LENGTH_SHORT).show();
                                        //结束当前页面
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this, "退群失败" + e, Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    //解散群和退群的广播
    private void exitGroupBroadcast() {
        //首先需要广播的管理者
        LocalBroadcastManager LBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);

        Intent intent = new Intent(Constant.EXIT_GROUP);

        intent.putExtra(Constant.GROUP_ID, mGroup.getGroupId());

        LBM.sendBroadcast(intent);
    }

    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);

        if (groupId == null) {
            return;
        } else {
            mGroup = EMClient.getInstance().groupManager().getGroup(groupId);
        }

    }

    private void initView() {
        gv_groupdetail = (GridView) findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out = (Button) findViewById(R.id.bt_groupdetail_out);
    }
}