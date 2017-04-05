package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

import jiang.kaka.R;
import jiang.kaka.controller.adapter.InviteAdapter;
import jiang.kaka.model.Model;
import jiang.kaka.model.bean.InvationInfo;
import jiang.kaka.utils.Constant;

/**
 * 邀请信息列表页面
 * Created by 在云端 on 2016/10/28.
 */
public class InviteActivity extends Activity {
    private ListView lv_invite;
    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager mLBM;
    private InviteAdapter.onInviteListener mOnInviteListener=new InviteAdapter.onInviteListener() {
        @Override
        public void onAccept(final InvationInfo invationInfo) {

            Model.getInstance().getGlobalTreadPool().execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        //通知环信服务器，点击了接受按钮
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxid());
                        //数据库更新
                         Model.getInstance().getDBManager().getInviteTableDao().updateInvitationStatus
                                 (InvationInfo.InvationStatus.INVITE_ACCEPT,invationInfo.getUser().getHxid());

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                //页面发生变化
                                Toast.makeText(InviteActivity.this, "接受了邀请", Toast.LENGTH_SHORT).show();
                                //刷新页面

                                refresh();
                            }
                        });

                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败"+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }



                }
            });
        }

        @Override
        public void onReject(final InvationInfo invationInfo) {
            Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        //通知环信服务器，点击了拒绝按钮
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxid());

                        //数据库变化
                        Model.getInstance().getDBManager().getInviteTableDao().removeInvitation(invationInfo.getUser().getHxid());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //页面变化
                                Toast.makeText(InviteActivity.this, "拒绝了邀请", Toast.LENGTH_SHORT).show();
                                //刷新页面
                                refresh();

                            }
                        });


                    } catch (final HyphenateException e) {
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝失败"+e, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }

        @Override
        public void onInviteAccept(final InvationInfo invationInfo) {
            Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo.getGroup().getGroudId(),
                                invationInfo.getGroup().getInvatePerson());

                        invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invationInfo);
                        //内存变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "接受邀请失败"+e, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onInviteReject(final InvationInfo invationInfo) {
          Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
              @Override
              public void run() {
                  try {
                      EMClient.getInstance().groupManager().declineInvitation(invationInfo.getGroup().getGroudId(),
                              invationInfo.getGroup().getInvatePerson(),"拒绝邀请");

                      invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_REJECT_INVITE);
                      Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invationInfo);

                      //内存变化
                      runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              Toast.makeText(InviteActivity.this, "拒绝邀请", Toast.LENGTH_SHORT).show();
                              refresh();
                          }
                      });
                  } catch (HyphenateException e) {
                      e.printStackTrace();
                  }
              }
          });
        }

        @Override
        public void onApplicationAccept(final InvationInfo invationInfo) {
               Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                   @Override
                   public void run() {

                       try {
                           EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroup().getGroudId(),
                                   invationInfo.getGroup().getInvatePerson());

                           invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_APPLICATION_ACCEPTED);
                           Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invationInfo);
                           //内存变化
                           runOnUiThread(new Runnable() {
                               @Override
                               public void run() {
                                   Toast.makeText(InviteActivity.this, "接受申请", Toast.LENGTH_SHORT).show();
                                   refresh();
                               }
                           });
                       } catch (HyphenateException e) {
                           e.printStackTrace();
                       }
                   }
               });
        }

        @Override
        public void onApplicationReject(final InvationInfo invationInfo) {
            Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        EMClient.getInstance().groupManager().declineApplication(invationInfo.getGroup().getGroudId(),
                                invationInfo.getGroup().getInvatePerson(),"拒绝申请");

                        invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_APPLICATION_DECLINED);
                        Model.getInstance().getDBManager().getInviteTableDao().addInvitation(invationInfo);
                        //内存变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请", Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (final HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this, "拒绝申请失败"+e, Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    }
                }
            });
        }
    };



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_activity);
        
        initView();
        
        initData();
    }

    private void initData() {
        //初始化listView  ListView要适配器

        inviteAdapter = new InviteAdapter(this,mOnInviteListener);
        lv_invite.setAdapter(inviteAdapter);
        
        //刷新方法
        refresh();

        //注册邀请信息变化的广播
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(InviteChangedReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(InviteChangedReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }
    private BroadcastReceiver InviteChangedReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //只要接受到邀请信息变化就刷新页面
            refresh();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(InviteChangedReceiver);
    }

    private void refresh() {
        //获取数据库中所有的邀请信息
        List<InvationInfo> invitations = Model.getInstance().getDBManager().getInviteTableDao().getInvitations();
        inviteAdapter.refresh(invitations);
    }

    private void initView() {
        lv_invite =  (ListView) findViewById(R.id.lv_invite);
    }
}

