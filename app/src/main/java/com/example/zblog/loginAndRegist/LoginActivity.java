package com.example.zblog.loginAndRegist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.firstLayer.MainActivity;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText et_username;
    private EditText et_password;
    private Button btn_login;
    private TextView tv_click_regist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    public void initView(){
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_click_regist = (TextView) findViewById(R.id.tv_click_regist);

        btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            String username = et_username.getText().toString();
                            String password = et_password.getText().toString();

                            if("".equals(username) || "".equals(password)){
                                Looper.prepare();
                                Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else{
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", username)
                                        .add("password", password)
                                        .build();

                                Request request = new Request.Builder()
                                        .url("http://192.168.102.205:8080/zblogserver/loginServlet")
                                        .post(requestBody)
                                        .build();
                                Response response = client.newCall(request).execute();
                                String msg = response.header("msg");
                                if(!"0".equals(msg)){
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("userId", msg);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    LoginActivity.this.finish();
                                    Looper.loop();
                                }else{
                                    System.out.println("TAG");
                                    Looper.prepare();
                                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        tv_click_regist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}