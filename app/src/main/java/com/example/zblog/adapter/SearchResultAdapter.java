package com.example.zblog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.zblog.R;
import com.example.zblog.domain.User;

import java.util.List;

public class SearchResultAdapter extends ArrayAdapter<User> {

    private int resourceId;

    public SearchResultAdapter(Context context, int textViewResourceId, List<User> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position); // 获取当前项的User实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView tv_item_name = (TextView) view.findViewById(R.id.tv_item_name);
        tv_item_name.setText(user.getUserName());
        return view;
    }
}
