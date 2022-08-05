package com.example.zblog.secondLayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.adapter.BlogAdapter;
import com.example.zblog.adapter.CommentAdapter;
import com.example.zblog.domain.BlogWithUserName;
import com.example.zblog.domain.Comment;
import com.example.zblog.firstLayer.MainActivity;
import com.example.zblog.thirdLayer.AddCommentActivity;
import com.example.zblog.util.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BlogDetailAndCommentActivity extends AppCompatActivity {

    private TextView tv_detail_username;
    private TextView tv_detail_time;
    private TextView tv_detail_content;
    private ListView lv_comment;
    private Button btn_add_comment;
    private List<Comment> commentList = new ArrayList<>();
    private String responseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail_and_comment);

        tv_detail_username = (TextView) findViewById(R.id.tv_detail_username);
        tv_detail_time = (TextView) findViewById(R.id.tv_detail_time);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);
        lv_comment = (ListView) findViewById(R.id.lv_comment);
        btn_add_comment = (Button) findViewById(R.id.btn_add_comment);

        initView();
    }

    public void initView(){
        final String userId = getIntent().getStringExtra("userId");
        final String blogId = getIntent().getStringExtra("blogId");
        final String blogUserName = getIntent().getStringExtra("blogUserName");
        final String blogContent = getIntent().getStringExtra("blogContent");
        final String blogTime = getIntent().getStringExtra("blogTime");

        tv_detail_username.setText(blogUserName);
        tv_detail_time.setText(blogTime);
        tv_detail_content.setText(blogContent);

        // 初始化或刷新评论列表
        initComment(blogId);

        btn_add_comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Toast.makeText(BlogDetailAndCommentActivity.this, userId + " " + blogId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BlogDetailAndCommentActivity.this, AddCommentActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("blogId", blogId);
                intent.putExtra("blogUserName", blogUserName);
                intent.putExtra("blogContent", blogContent);
                intent.putExtra("blogTime", blogTime);
                startActivity(intent);
                BlogDetailAndCommentActivity.this.finish();
            }
        });
    }

    public void initComment(final String blogId){

        commentList.clear();

        Thread thread = null;

        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("blogId", blogId)
                            .build();

                    Request request = new Request.Builder()
                            .url("http://192.168.102.205:8080/zblogserver/showCommentServlet")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    responseData = response.body().string();
//                    System.out.println(responseData);
                    parseJSONToList(responseData);
                    MyUtils.sortCommentData(commentList);
                    CommentAdapter adapter = new CommentAdapter(BlogDetailAndCommentActivity.this, R.layout.comment_item, commentList);
                    lv_comment.setAdapter(adapter);
                }catch(Exception e){
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
    }

    // 解析并初始化comment数据
    private void parseJSONToList(String jsonData){
        // 运用 GSON 解析 JSON 数据
        Gson gson = new Gson();
        commentList = gson.fromJson(jsonData, new TypeToken<List<Comment>>(){}.getType());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initView();
    }

}