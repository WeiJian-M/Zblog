package com.example.zblog.secondLayer;

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
import com.example.zblog.util.MyUtils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddABlogActivity extends AppCompatActivity {

    private EditText et_write_blog;
    private Button btn_send_out;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_a_blog);
        initView();
    }

    public void initView(){
        et_write_blog = (EditText) findViewById(R.id.et_write_blog);
        btn_send_out = (Button) findViewById(R.id.btn_send_out);

        final String userId = getIntent().getStringExtra("userId");
        final String username = getIntent().getStringExtra("username");


        btn_send_out.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String blogTime = MyUtils.getTime();
                final String blogContent = et_write_blog.getText().toString();
                if("".equals(blogContent)){
                    Toast.makeText(AddABlogActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();
                }else{
                    System.out.println(userId);
                    System.out.println(blogTime);
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("userId", userId)
                                        .add("blogContent", blogContent)
                                        .add("blogTime", blogTime)
                                        .build();
                                Request request = new Request.Builder()
                                        .url("http://192.168.102.205:8080/zblogserver/addABlogServlet")
                                        .post(requestBody)
                                        .build();
                                Response response = client.newCall(request).execute();
                                String isSend = response.header("isSend");
                                System.out.println(isSend);
                                if("1".equals(isSend)){
                                    Looper.prepare();
                                    Toast.makeText(AddABlogActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddABlogActivity.this, MainActivity.class);
                                    intent.putExtra("userId", userId);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    AddABlogActivity.this.finish();
                                    Looper.loop();
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(AddABlogActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
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
        String userId = getIntent().getStringExtra("userId");
        String username = getIntent().getStringExtra("username");
        Intent intent = new Intent(AddABlogActivity.this, MainActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("username", username);
        startActivity(intent);
        AddABlogActivity.this.finish();
    }
}