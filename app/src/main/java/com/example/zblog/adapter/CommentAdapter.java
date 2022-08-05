package com.example.zblog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.zblog.R;
import com.example.zblog.domain.Comment;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private int resourceId;

    public CommentAdapter(Context context, int textViewResourceId, List<Comment> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Comment comment = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView tv_comment_item_username = (TextView) view.findViewById(R.id.tv_comment_item_username);
        TextView tv_comment_item_time = (TextView) view.findViewById(R.id.tv_comment_item_time);
        TextView tv_comment_item_content = (TextView) view.findViewById(R.id.tv_comment_item_content);

        tv_comment_item_username.setText(comment.getUserName());
        tv_comment_item_time.setText(comment.getCommentTime());
        tv_comment_item_content.setText(comment.getCommentContent());

        return view;
    }
}
