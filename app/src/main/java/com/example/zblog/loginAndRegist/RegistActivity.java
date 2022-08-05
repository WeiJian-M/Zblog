package com.example.zblog.loginAndRegist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zblog.R;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistActivity extends AppCompatActivity {

    private EditText et_regist_username;
    private EditText et_regist_password;
    private Button btn_regist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
    }

    public void initView(){
        et_regist_username = (EditText) findViewById(R.id.et_regist_username);
        et_regist_password = (EditText) findViewById(R.id.et_regist_password);
        btn_regist = (Button) findViewById(R.id.btn_regist);

        btn_regist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String r_username = et_regist_username.getText().toString();
                final String r_password = et_regist_password.getText().toString();
                if("".equals(r_username)){
                    Toast.makeText(RegistActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                }else if("".equals(r_password)){
                    Toast.makeText(RegistActivity.this, "请输入要设置的密码", Toast.LENGTH_SHORT).show();
                }else{

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //发起请求
                                OkHttpClient client = new OkHttpClient();
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("username", r_username)
                                        .add("password", r_password)
                                        .build();

                                Request request = new Request.Builder()
                                        .url("http://192.168.102.205:8080/zblogserver/registServlet")
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();
                                String msgRegister = response.header("msgRegister");
                                if(msgRegister.equals("-1")){
                                    Looper.prepare();
                                    Toast.makeText(RegistActivity.this, "用户名已存在", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else if (msgRegister.equals("0")){
                                    Looper.prepare();
                                    Toast.makeText(RegistActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else{
//                                    Looper.prepare();
//                                    Toast.makeText(RegistActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
//                                    Looper.loop();
                                    Looper.prepare();
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegistActivity.this);
                                    dialog.setTitle("注册成功");
                                    dialog.setCancelable(false);
                                    dialog.setPositiveButton("返回登录", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(RegistActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            RegistActivity.this.finish();//返回后销毁注册页面
                                        }
                                    });
                                    dialog.show();
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
}