package com.example.zblog.thirdLayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.firstLayer.MainActivity;
import com.example.zblog.secondLayer.AddABlogActivity;
import com.example.zblog.secondLayer.BlogDetailAndCommentActivity;
import com.example.zblog.util.MyUtils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddCommentActivity extends AppCompatActivity {

    private EditText et_write_comment;
    private Button btn_send_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        et_write_comment = (EditText) findViewById(R.id.et_write_comment);
        btn_send_comment = (Button) findViewById(R.id.btn_send_comment);
        initView();
    }

    public void initView(){
        btn_send_comment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String userId = getIntent().getStringExtra("userId");
                final String blogId = getIntent().getStringExtra("blogId");
                final String blogUserName = getIntent().getStringExtra("blogUserName");
                final String blogContent = getIntent().getStringExtra("blogContent");
                final String blogTime = getIntent().getStringExtra("blogTime");
                final String commentContent = et_write_comment.getText().toString();
                final String commentTime = MyUtils.getTime();
                if("".equals(commentContent)){
                    Toast.makeText(AddCommentActivity.this, "请输入评论内容", Toast.LENGTH_SHORT).show();
                }else{

                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                OkHttpClient client = new OkHttpClient();

                                // 发送评论内容
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("userId", userId)
                                        .add("blogId", blogId)
                                        .add("commentContent", commentContent)
                                        .add("commentTime", commentTime)
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://192.168.102.205:8080/zblogserver/addACommentServlet")
                                        .post(requestBody)
                                        .build();
                                Response response = client.newCall(request).execute();

                                String isSendCommentSuccess = response.header("isSendCommentSuccess");
                                if("1".equals(isSendCommentSuccess)){
                                    // 发布评论成功
                                    Looper.prepare();
                                    Toast.makeText(AddCommentActivity.this, "发布评论成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddCommentActivity.this, BlogDetailAndCommentActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("blogId", blogId);
                                    intent.putExtra("blogUserName", blogUserName);
                                    intent.putExtra("blogContent", blogContent);
                                    intent.putExtra("blogTime", blogTime);
                                    startActivity(intent);
                                    AddCommentActivity.this.finish();
                                    Looper.loop();
                                }else{
                                    // 发布评论失败
                                    Looper.prepare();
                                    Toast.makeText(AddCommentActivity.this, "发布评论失败", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }

                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        final String userId = getIntent().getStringExtra("userId");
        final String blogId = getIntent().getStringExtra("blogId");
        final String blogUserName = getIntent().getStringExtra("blogUserName");
        final String blogContent = getIntent().getStringExtra("blogContent");
        final String blogTime = getIntent().getStringExtra("blogTime");
        Intent intent = new Intent(AddCommentActivity.this, BlogDetailAndCommentActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("blogId", blogId);
        intent.putExtra("blogUserName", blogUserName);
        intent.putExtra("blogContent", blogContent);
        intent.putExtra("blogTime", blogTime);
        startActivity(intent);
        AddCommentActivity.this.finish();
    }
}