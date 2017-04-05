package jiang.kaka.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.R;
import jiang.kaka.controller.adapter.PickContactAdapter;
import jiang.kaka.model.Model;
import jiang.kaka.model.bean.PickContactInfo;
import jiang.kaka.model.bean.UserInfo;
import jiang.kaka.utils.Constant;

/**选择联系人页面
 * Created by 在云端 on 2016/10/28.
 */
public class PickContactActivity extends Activity {
    private TextView tv_pick_save;
    private ListView lv_pick;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> mExitMembers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);

        //获取传递过来的数据
        getData();
        
        initView();

        initData();
        
        initListener();

    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);

        if (groupId!=null){
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            //获取群中已经存在的成员
            mExitMembers = group.getMembers();

        }
        if (mExitMembers==null)
        {
            mExitMembers=new ArrayList<>();
        }


    }

    private void initListener() {
        //listView条目的点击事件
        lv_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //CheckBox的切换
                CheckBox cb_pick = (CheckBox) view.findViewById(R.id.cb_pick);
                cb_pick.setChecked(!cb_pick.isChecked());
                //更新数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setChecked(cb_pick.isChecked());

                //刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });
        //设置保存按钮的点击事件
        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取已经选择的联系人
              List<String> names=  pickContactAdapter.getPickContacts();
                //给前一个页面返回数据
                Intent intent = new Intent();

                intent.putExtra("members", names.toArray(new String[0]));
                //给前一个页面返回数据
                setResult(RESULT_OK,intent);

                finish();
            }
        });

    }

    private void initData() {
        //从本地数据库获取所有联系人信息
        List<UserInfo> contacts = Model.getInstance().getDBManager().getContactTableDao().getContacts();

        mPicks = new ArrayList<>();
        if (contacts!=null&&contacts.size()>=0)
        {
           for (UserInfo contact :contacts)
           {
               PickContactInfo pickContactInfo = new PickContactInfo(false, contact);
               mPicks.add(pickContactInfo);
           }
        }

        //初始化listView
        pickContactAdapter = new PickContactAdapter(this, mPicks,mExitMembers);
        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initView() {
        tv_pick_save = (TextView) findViewById(R.id.tv_pick_save);
        lv_pick = (ListView) findViewById(R.id.lv_pick);
    }
}

