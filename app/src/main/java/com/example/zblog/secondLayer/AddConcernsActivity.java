package com.example.zblog.secondLayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.adapter.SearchResultAdapter;
import com.example.zblog.domain.BlogWithUserName;
import com.example.zblog.domain.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddConcernsActivity extends AppCompatActivity {

    private EditText et_intent_username;
    private Button btn_search;
    private ListView lv_search_result;
    private String responseData;
    private List<User> resultUserList = new ArrayList<>();
    private TextView tv_no_result;
    private String isAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_concerns);
        et_intent_username = (EditText) findViewById(R.id.et_intent_username);
        btn_search = (Button) findViewById(R.id.btn_search);
        lv_search_result = (ListView) findViewById(R.id.lv_search_result);
        tv_no_result = (TextView) findViewById(R.id.tv_no_result);

        initView();

        initAdd();
    }

    public void initView(){
        resultUserList.clear();
        final String myId = getIntent().getStringExtra("myId");
        btn_search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resultUserList.clear();// 清空结果，便于显示当前真实数据
                final String userName = et_intent_username.getText().toString();
                if("".equals(userName)){
                    Toast.makeText(AddConcernsActivity.this, "请输入想要搜索的用户名", Toast.LENGTH_SHORT).show();
                }else{

                    Thread thread = null;
                    thread = new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                OkHttpClient client = new OkHttpClient();

                                // post请求，客户端向服务器发送想要搜索的用户名或用户名字段，服务器端返回搜索结果(JSON)
                                RequestBody requestBody = new FormBody.Builder()
                                        .add("myId", myId)
                                        .add("userNameItem", userName)
                                        .build();

                                Request request = new Request.Builder()
                                        .url("http://192.168.102.205:8080/zblogserver/searchUsersServlet")
                                        .post(requestBody)
                                        .build();

                                Response response = client.newCall(request).execute();
                                responseData = response.body().string();
                                String msg = response.header("msg");
                                System.out.println(msg);
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
                    parseJSONToList(responseData);

                    // 显示结果
                    SearchResultAdapter adapter = new SearchResultAdapter(AddConcernsActivity.this, R.layout.search_result_item, resultUserList);

                    if(resultUserList.isEmpty()){
//                        Toast.makeText(AddConcernsActivity.this, "")
                        tv_no_result.setVisibility(View.VISIBLE);
                    }else{
                        tv_no_result.setVisibility(View.INVISIBLE);
                        lv_search_result.setAdapter(adapter);
                    }
                }
            }
        });
    }

    public void initAdd(){
        isAdd = "0";// 初始化标签
        lv_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String myId = getIntent().getStringExtra("myId");
                final User user = resultUserList.get(position);
                String userName = user.getUserName();
                final String wantId = "" + user.getUserId();
//                Toast.makeText(AddConcernsActivity.this, user.getUserName(), Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddConcernsActivity.this);
                dialog.setTitle("是否关注" + userName);
                dialog.setCancelable(true);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(AddConcernsActivity.this, myId, Toast.LENGTH_SHORT).show();
//                        final Boolean[] Flag = {false};
                        Thread thread = null;
                        thread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try{
                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody requestBody = new FormBody.Builder()
                                            .add("myId", myId)
                                            .add("wantId", wantId)
                                            .build();

                                    Request request = new Request.Builder()
                                            .url("http://192.168.43.205:8080/zblogserver/addAConcernServlet")
                                            .post(requestBody)
                                            .build();

                                    Response response = client.newCall(request).execute();
                                    String isAddSuccess = response.header("isAddSuccess");
                                    if("success".equals(isAddSuccess)){
                                        isAdd = "1";
                                    }else{
                                        isAdd = "0";
                                    }
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

                        if("1".equals(isAdd)){
                            Toast.makeText(AddConcernsActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
                            AddConcernsActivity.this.finish();
                        }else{
                            Toast.makeText(AddConcernsActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("否", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });
    }

    // 解析并初始化resultUser数据
    private void parseJSONToList(String jsonData){
        // 运用 GSON 解析 JSON 数据
        Gson gson = new Gson();
        resultUserList = gson.fromJson(jsonData, new TypeToken<List<User>>(){}.getType());
    }
}