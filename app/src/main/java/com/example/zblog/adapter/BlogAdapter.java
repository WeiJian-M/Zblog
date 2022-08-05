package com.example.zblog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zblog.R;
import com.example.zblog.domain.BlogWithUserName;

import java.util.List;

public class BlogAdapter extends ArrayAdapter<BlogWithUserName> {

    private int resourceId;

    public BlogAdapter(Context context, int textViewResourceId, List<BlogWithUserName> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BlogWithUserName blogWithUserName = getItem(position); // 获取当前项的微博实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView tv_username = (TextView) view.findViewById(R.id.tv_item_userName);
        TextView tv_blog_content = (TextView) view.findViewById(R.id.tv_item_blogContent);
        TextView tv_blog_time = (TextView) view.findViewById(R.id.tv_item_blogTime);

        tv_username.setText(blogWithUserName.getUserName());
        tv_blog_content.setText(blogWithUserName.getBlogContent());
        tv_blog_time.setText(blogWithUserName.getBlogTime());
        return view;
    }
}
