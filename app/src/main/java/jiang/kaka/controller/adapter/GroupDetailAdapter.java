package jiang.kaka.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.R;
import jiang.kaka.model.bean.UserInfo;

/**
 * Created by 在云端 on 2016/11/6.
 */
public class GroupDetailAdapter extends BaseAdapter {
    private Context mContext;
    private boolean mIsCanModify;//是否允许添加或者删除成员
    private List<UserInfo> mUsers = new ArrayList<>();
    private boolean mIsDeleteModel;//删除模式
    private onGroupDetailListener mOnGroupDetailListener;

    public GroupDetailAdapter(Context context, boolean isCanModify,onGroupDetailListener onGroupDetailListener) {
        mContext = context;
        mIsCanModify = isCanModify;
        mOnGroupDetailListener=onGroupDetailListener;

    }

    public boolean ismIsDeleteModel() {
        return mIsDeleteModel;
    }

    public void setmIsDeleteModel(boolean mIsDeleteModel) {
        this.mIsDeleteModel = mIsDeleteModel;
    }

    //通过刷新传递数据
    public void refresh(List<UserInfo> users) {
        if (users != null && users.size() >= 0) {
            mUsers.clear();

            initUsers();

            mUsers.addAll(0, users);
        }
        notifyDataSetChanged();
    }

    private void initUsers() {
        UserInfo add = new UserInfo("add");
        UserInfo delete = new UserInfo("delete");

        mUsers.add(delete);
        mUsers.add(0, add);

    }

    @Override
    public int getCount() {
        return mUsers == null ? 0 : mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建viewHolderh
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_groupdetail, null);
            holder.photo =(ImageView) convertView.findViewById(R.id.iv_group_detail_photo);
            holder.delete= (ImageView) convertView.findViewById(R.id.iv_group_detail_delete);
            holder.name= (TextView) convertView.findViewById(R.id.tv_group_detail_name);

            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }


        //获取item数据
       final UserInfo userInfo=mUsers.get(position);

        //显示item
        if (mIsCanModify)
        {//群主或者开放了权限
            //布局处理
           if (position==getCount()-1)//减号的处理
           {
                    if (mIsDeleteModel)
                    {
                        convertView.setVisibility(View.GONE);
                    }else {
                        convertView.setVisibility(View.VISIBLE);
                        holder.photo.setImageResource(R.drawable.em_smiley_minus_btn_pressed);
                        holder.delete.setVisibility(View.GONE);
                        holder.name.setVisibility(View.INVISIBLE);
                    }

           }else if (position==getCount()-2)//加号的处理
           {
               if (mIsDeleteModel)
               {
                   convertView.setVisibility(View.GONE);
               }else {
                   convertView.setVisibility(View.VISIBLE);
                   holder.photo.setImageResource(R.drawable.em_smiley_add_btn_pressed);
                   holder.delete.setVisibility(View.GONE);
                   holder.name.setVisibility(View.INVISIBLE);
               }
           }
            else //群成员的处理
           {
               convertView.setVisibility(View.VISIBLE);
               holder.name.setVisibility(View.VISIBLE);

               holder.name.setText(userInfo.getName());
               holder.photo.setImageResource(R.drawable.em_default_avatar);
               if (mIsDeleteModel)
               {
                   holder.delete.setVisibility(View.VISIBLE);
               }
               else
               {
                   holder.delete.setVisibility(View.GONE);
               }


           }
            //点击事件的处理
            if (position==getCount()-1)//减号
            {
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mIsDeleteModel)
                        {
                            mIsDeleteModel=true;
                            notifyDataSetChanged();
                        }

                    }
                });

            }else if (position==getCount()-2)//加号
            {
                holder.photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnGroupDetailListener.onAddMembers();
                    }
                });

            }else {//群成员
                 holder.delete.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         mOnGroupDetailListener.onDeleteMember(userInfo);
                     }
                 });
            }




        }else {//群成员
            if (position==getCount()-1||position==getCount()-2)
            {
                convertView.setVisibility(View.GONE);
            }else
            {
                convertView.setVisibility(View.VISIBLE);

                holder.name.setText(userInfo.getName());
                holder.photo.setImageResource(R.drawable.em_default_avatar);
                holder.delete.setVisibility(View.GONE);
            }

        }
        return convertView;
    }

    public class ViewHolder {
        private ImageView photo;
        private ImageView delete;
        private TextView name;
    }
    public interface onGroupDetailListener
    {
        void onAddMembers();

        void onDeleteMember(UserInfo user);
    }


}
