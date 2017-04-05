package jiang.kaka.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.R;

/**
 * Created by 在云端 on 2016/11/5.
 */
public class GroupListAdapter extends BaseAdapter {
    private Context mContext;
    private List<EMGroup> mGroups=new ArrayList<>();
    public GroupListAdapter(Context context) {
        mContext=context;
    }
    //通过刷新方法把数据传到mGroup
    public void refresh(List<EMGroup> groups)
    {
        if (groups!=null&&groups.size()>=0)
        {
            mGroups.clear();

            mGroups.addAll(groups);

            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mGroups==null ? 0 : mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建或获取viewHolder
        ViewHolder viewHolder = null;
        if (convertView==null)
        {
            viewHolder=  new ViewHolder();
            convertView=View.inflate(mContext, R.layout.item_grouplist,null);

            viewHolder.groupName= (TextView) convertView.findViewById(R.id.tv_grouplist_name);
            //保存viewholder
            convertView.setTag(viewHolder);
        }else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //获取item数据
        EMGroup emGroup = mGroups.get(position);
        //显示item
        viewHolder.groupName.setText(emGroup.getGroupName());
        //返回数据
        return convertView;
    }

    private class ViewHolder {
      TextView groupName;
    }
}
