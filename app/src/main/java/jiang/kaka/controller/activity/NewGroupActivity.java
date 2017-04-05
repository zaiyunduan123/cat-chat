package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;

import jiang.kaka.R;
import jiang.kaka.model.Model;

/**
 * 创建新群
 * Created by 在云端 on 2016/10/28.
 */
public class NewGroupActivity extends Activity {
    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox cb_newgroup_public;
    private CheckBox cb_newgroup_invite;
    private Button bt_newgroup_create;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        initView();

        initListener();
    }

    private void initListener() {
        bt_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到选择联系人页面
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);

                startActivityForResult(intent,1);//会带
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //成功获取到联系人
        if (resultCode==RESULT_OK)
        {
            //创建群
            createGroup(data.getStringArrayExtra("members"));
        }
    }

    private void createGroup(final String[] memberses) {

        final String groupName = et_newgroup_name.getText().toString();
        final String groupDesc = et_newgroup_desc.getText().toString();


        Model.getInstance().getGlobalTreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //去环信服务器创建群
                //四个参数，群名称。群描述，群成员，原因
                EMGroupManager.EMGroupOptions options=new EMGroupManager.EMGroupOptions();
                //群最多容纳300人
                options.maxUsers=300;
                EMGroupManager.EMGroupStyle groupStyle=null;//群的类型

                if (cb_newgroup_public.isChecked())
                {
                       if(cb_newgroup_invite.isChecked())
                       {
                          groupStyle=EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                       }else
                       {
                           groupStyle=EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                       }
                }else {
                    if(cb_newgroup_invite.isChecked())
                    {
                        groupStyle=EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else
                    {
                        groupStyle=EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }


                options.style=groupStyle;
                try {
                    EMClient.getInstance().groupManager().createGroup(groupName,groupDesc,memberses,"申请加入群",options);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "群创建成功", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this, "群创建失败"+e, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_newgroup_name = (EditText) findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = (EditText) findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = (CheckBox) findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = (CheckBox) findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create = (Button) findViewById(R.id.bt_newgroup_create);


    }
}

