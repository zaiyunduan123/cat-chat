package jiang.kaka.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.R;
import jiang.kaka.model.bean.InvationInfo;
import jiang.kaka.model.bean.UserInfo;

/**
 * Created by 在云端 on 2016/11/3.
 */

//邀请信息列表的适配器
public class InviteAdapter extends BaseAdapter {
    private Context mContext;
    private List<InvationInfo> mInvationInfos = new ArrayList<>();
    private onInviteListener mOnInviteListener;
    private InvationInfo invationInfo;


    public InviteAdapter(Context context,onInviteListener onInviteListener) {
        mContext = context;
        mOnInviteListener=onInviteListener;

    }

    //刷新数据的方法
    public void refresh(List<InvationInfo> invationInfos) {
        if (invationInfos != null && invationInfos.size() >= 0) {
            //全部添加到
            mInvationInfos.clear();

            mInvationInfos.addAll(invationInfos);
            //通知刷新页面
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mInvationInfos == null ? 0 : mInvationInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mInvationInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //获取和创建viewholder
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_invite, null);

            viewHolder.name= (TextView) convertView.findViewById(R.id.tv_invite_name);
            viewHolder.reason= (TextView) convertView.findViewById(R.id.tv_invite_reason);

            viewHolder.accept= (Button) convertView.findViewById(R.id.bt_invite_accept);
            viewHolder.reject= (Button) convertView.findViewById(R.id.bt_invite_reject);

           //将holder保存到convertView
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取item数据
        invationInfo = mInvationInfos.get(position);

        //显示item数据
        UserInfo user = invationInfo.getUser();
        if (user!=null)
        {
            //联系人
            //展示名称
            viewHolder.name.setText(invationInfo.getUser().getName());

            //先隐藏两个按钮
            viewHolder.accept.setVisibility(View.GONE);
            viewHolder.reject.setVisibility(View.GONE);
            //原因
            if(invationInfo.getStatus()== InvationInfo.InvationStatus.NEW_INVITE)//新邀请
            {
                if(invationInfo.getReason()==null)
                {
                    viewHolder.reason.setText("添加好友");
                }else
                {

                    viewHolder.reason.setText(invationInfo.getReason());
                }

                //有新邀请显示两个按钮
                viewHolder.accept.setVisibility(View.VISIBLE);
                viewHolder.reject.setVisibility(View.VISIBLE);

            }else if(invationInfo.getStatus()== InvationInfo.InvationStatus.INVITE_ACCEPT)//接受邀请
            {
                if(invationInfo.getReason()==null)
                {
                    viewHolder.reason.setText("接受邀请");
                }else
                {

                    viewHolder.reason.setText(invationInfo.getReason());
                }

            }else  if (invationInfo.getStatus()== InvationInfo.InvationStatus.INVITE_ACCEPT_BY_PEER)//邀请已经被接受
            {
                if(invationInfo.getReason()==null)
                {
                    viewHolder.reason.setText("邀请被接受");
                }else
                {

                    viewHolder.reason.setText(invationInfo.getReason());
                }
            }

            //按钮的处理(接受与拒绝)
            viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onAccept(invationInfo);
                }
            });
            viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnInviteListener.onReject(invationInfo);
                }
            });

        }
        else
        {// 群组
            // 显示名称
            viewHolder.name.setText(invationInfo.getGroup().getInvatePerson());

            viewHolder.accept.setVisibility(View.GONE);
            viewHolder.reject.setVisibility(View.GONE);

            // 显示原因
            switch(invationInfo.getStatus()){
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    viewHolder.reason.setText("您的群申请请已经被接受");
                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    viewHolder.reason.setText("您的群邀请已经被接收");
                    break;

                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    viewHolder.reason.setText("你的群申请已经被拒绝");
                    break;

                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    viewHolder.reason.setText("您的群邀请已经被拒绝");
                    break;

                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    viewHolder.accept.setVisibility(View.VISIBLE);
                    viewHolder.reject.setVisibility(View.VISIBLE);

                    // 接受邀请
                    viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteAccept(invationInfo);
                        }
                    });

                    // 拒绝邀请
                    viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onInviteReject(invationInfo);
                        }
                    });

                    viewHolder.reason.setText("您收到了群邀请");
                    break;

                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    viewHolder.accept.setVisibility(View.VISIBLE);
                    viewHolder.reject.setVisibility(View.VISIBLE);

                    // 接受申请
                    viewHolder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationAccept(invationInfo);
                        }
                    });

                    // 拒绝申请
                    viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnInviteListener.onApplicationReject(invationInfo);
                        }
                    });

                    viewHolder.reason.setText("您收到了群申请");
                    break;

                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    viewHolder.reason.setText("你接受了群邀请");
                    break;

                // 您批准了群申请
                case GROUP_ACCEPT_APPLICATION:
                    viewHolder.reason.setText("您批准了群申请");
                    break;

                // 您拒绝了群邀请
                case GROUP_REJECT_INVITE:
                    viewHolder.reason.setText("您拒绝了群邀请");
                    break;

                // 您拒绝了群申请
                case GROUP_REJECT_APPLICATION:
                    viewHolder.reason.setText("您拒绝了群申请");
                    break;
            }
        }

        // 4 返回view
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView reason;

        private Button accept;
        private Button reject;
    }
    public interface onInviteListener
    {
        //联系人接受的点击事件
         void onAccept(InvationInfo invationInfo);
        //联系人拒绝的点击事件
        void onReject(InvationInfo invationInfo);
        //接受邀请按钮处理
        void onInviteAccept(InvationInfo invationInfo);
        //拒绝邀请按钮处理
        void onInviteReject(InvationInfo invationInfo);
        //接受申请按钮处理
        void onApplicationAccept(InvationInfo invationInfo);
        //拒绝申请按钮处理
        void onApplicationReject(InvationInfo invationInfo);
    }


}
