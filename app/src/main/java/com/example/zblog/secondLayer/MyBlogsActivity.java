package com.example.zblog.secondLayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.adapter.BlogAdapter;
import com.example.zblog.domain.BlogWithUserName;
import com.example.zblog.firstLayer.MainActivity;
import com.example.zblog.util.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyBlogsActivity extends AppCompatActivity {

    private String responseData;
    private List<BlogWithUserName> myBlogList = new ArrayList<>();
    private ListView lv_myBlogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_blog);
        lv_myBlogs = (ListView) findViewById(R.id.lv_myBlogs);

        initView();
    }

    public void initView(){
        final String userId = getIntent().getStringExtra("userId");
        Thread thread = null;


        thread =  new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("userId", userId)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://192.168.102.205:8080/zblogserver/myBlogsServlet")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
                    System.out.println(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        parseJSONToList(responseData);
        //将数据显示在ListView中
        System.out.println(myBlogList.toString());
        MyUtils.sortData(myBlogList);
        BlogAdapter adapter = new BlogAdapter(MyBlogsActivity.this, R.layout.blog_item, myBlogList);

        lv_myBlogs.setAdapter(adapter);



//        lv_myBlogs.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                final Boolean[] isDelete = {false};
//
//                AlertDialog.Builder dialog = new AlertDialog.Builder(MyBlogsActivity.this);
//                dialog.setTitle("是否删除这条微博？");
//                dialog.setCancelable(true);
//                dialog.setPositiveButton("是", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        isDelete[0] = false;
//                    }
//                });
//                dialog.setNegativeButton("否", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        isDelete[0] = false;
//                    }
//                });
//                dialog.show();
//
//                // 如果确认删除选择的微博
//                if (isDelete[0]){
//                    BlogWithUserName aBlog = myBlogList.get(position);
//                    final String aBlogId = "" + aBlog.getBlogId();
//
//                    final Boolean[] isDeleteSuccessFlag = {false};
//
//                    Thread thread = null;
//
//
//                    thread =  new Thread(new Runnable(){
//                        @Override
//                        public void run() {
//                            try {
//                                OkHttpClient client = new OkHttpClient();
//                                RequestBody requestBody = new FormBody.Builder()
//                                        .add("aBlogId", aBlogId)
//                                        .build();
//                                Request request = new Request.Builder()
//                                        .url("http://192.168.43.205:8080/zblogserver/deleteABlogServlet")
//                                        .post(requestBody)
//                                        .build();
//                                Response response = client.newCall(request).execute();
//                                String isDeleteSuccess = response.header("isDeleteSuccess");
//                                if("1".equals(isDeleteSuccess)){
//                                    isDeleteSuccessFlag[0] = true;
//                                }else{
//                                    isDeleteSuccessFlag[0] = false;
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    thread.start();
//                    try {
//                        thread.join();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    if(isDeleteSuccessFlag[0]){
//                        Toast.makeText(MyBlogsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
//                        myBlogList.remove(position);
//                    }
//                }
//                return false;
//            }
//        });

        lv_myBlogs.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlogWithUserName blog = myBlogList.get(position);
                String blogId = "" + blog.getBlogId();
                String blogUserId = "" + blog.getUserId();
                String blogUserName = blog.getUserName();
                String blogContent = blog.getBlogContent();
                String blogTime = blog.getBlogTime();

                Intent intent = new Intent(MyBlogsActivity.this, BlogDetailAndCommentActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("blogId", blogId);
                intent.putExtra("blogUserName", blogUserName);
                intent.putExtra("blogContent", blogContent);
                intent.putExtra("blogTime", blogTime);

                startActivity(intent);
//                Toast.makeText(MainActivity.this, blogId+"", Toast.LENGTH_SHORT).show();
            }
        });

//        adapter = new BlogAdapter(MyBlogsActivity.this, R.layout.blog_item, myBlogList);
//        lv_myBlogs.setAdapter(adapter);
    }



    // 解析并初始化blogs数据
    private void parseJSONToList(String jsonData){
        // 运用 GSON 解析 JSON 数据
        Gson gson = new Gson();
        myBlogList = gson.fromJson(jsonData, new TypeToken<List<BlogWithUserName>>(){}.getType());
    }
}