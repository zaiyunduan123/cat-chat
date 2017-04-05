package jiang.kaka.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jiang.kaka.R;
import jiang.kaka.model.bean.PickContactInfo;

/**
 * Created by 在云端 on 2016/11/5.
 */
public class PickContactAdapter extends BaseAdapter {
    private Context mContext;
    private List<PickContactInfo> mPicks = new ArrayList<>();
    List<String> mExitMembers=new ArrayList<>();

    public PickContactAdapter(Context context, List<PickContactInfo> picks,List<String> exitMembers) {
        mContext = context;
        if (picks != null && picks.size() >= 0) {
            mPicks.clear();
            mPicks.addAll(picks);
        }
        mExitMembers.clear();
        mExitMembers.addAll(exitMembers);

    }


    @Override
    public int getCount() {
        return mPicks == null ? 0 : mPicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mPicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (holder == null) {
            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_pick, null);
            holder.cb= (CheckBox) convertView.findViewById(R.id.cb_pick);
            holder.tv_name= (TextView) convertView.findViewById(R.id.tv_pick_name);


            convertView.setTag(holder);
        }else {
            holder  = (ViewHolder) convertView.getTag();
        }

        PickContactInfo pickContactInfo = mPicks.get(position);

        holder.tv_name.setText(pickContactInfo.getUser().getName());
        holder.cb.setChecked(pickContactInfo.isChecked());
        //判断
        if (mExitMembers.contains(pickContactInfo.getUser().getHxid()))
        {
            holder.cb.setChecked(true);
            pickContactInfo.setChecked(true);
        }


        return convertView;
    }

    //获取被选择的联系人
    public List<String> getPickContacts() {
        List<String> picks=new ArrayList<>();
        for (PickContactInfo pick:mPicks)
        {
            if (pick.isChecked())
            {
                picks.add(pick.getUser().getName());
            }
        }
        return picks;
    }

    private class ViewHolder {
        private CheckBox cb;
        private TextView tv_name;
    }
}
