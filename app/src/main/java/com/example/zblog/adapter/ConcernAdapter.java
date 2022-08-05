package com.example.zblog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zblog.R;
import com.example.zblog.domain.Concern;

import java.util.List;

public class ConcernAdapter extends ArrayAdapter<Concern> {

    private int resourceId;

    public ConcernAdapter(Context context, int textViewResourceId, List<Concern> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Concern concern = getItem(position); // 获取当前关注实例
        String concernUsername = concern.getUserName();
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView tv_concern_item_username = (TextView) view.findViewById(R.id.tv_concern_item_username);

        tv_concern_item_username.setText(concernUsername);

        return view;
    }
}
