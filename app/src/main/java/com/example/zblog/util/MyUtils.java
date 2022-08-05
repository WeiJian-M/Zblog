package com.example.zblog.util;

import com.example.zblog.domain.BlogWithUserName;
import com.example.zblog.domain.Comment;
import com.example.zblog.domain.Concern;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MyUtils {
    // 获取系统当前时间
    public static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    // 格式化日期
    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }
    // 对列表按照微博时间排序
    public static void sortData(List<BlogWithUserName> notes) {
        Collections.sort(notes, new Comparator<BlogWithUserName>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(BlogWithUserName lhs, BlogWithUserName rhs) {
                Date date1 = MyUtils.stringToDate(lhs.getBlogTime());
                Date date2 = MyUtils.stringToDate(rhs.getBlogTime());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }

    // 对列表按照评论时间排序
    public static void sortCommentData(List<Comment> notes) {
        Collections.sort(notes, new Comparator<Comment>() {
            /**
             *
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(Comment lhs, Comment rhs) {
                Date date1 = MyUtils.stringToDate(lhs.getCommentTime());
                Date date2 = MyUtils.stringToDate(rhs.getCommentTime());
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.after(date2)) {
                    return 1;
                }
                return -1;
            }
        });
    }
}
