package com.example.zblog.secondLayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.adapter.BlogAdapter;
import com.example.zblog.adapter.ConcernAdapter;
import com.example.zblog.domain.BlogWithUserName;
import com.example.zblog.domain.Concern;
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

public class MyConcernsActivity extends AppCompatActivity {
    
    private ListView lv_myConcerns;
    private List<Concern> concernsList = new ArrayList<>();
    private String responseData;
    private String responseIsDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_concerns);

        lv_myConcerns = (ListView) findViewById(R.id.lv_myConcerns);

        initView();
        cancelSpace();
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
                            .url("http://192.168.102.205:8080/zblogserver/showMyConcernsServlet")
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
        //??????????????????ListView???
        System.out.println(concernsList.toString());
        ConcernAdapter adapter = new ConcernAdapter(MyConcernsActivity.this, R.layout.concern_item, concernsList);

        lv_myConcerns.setAdapter(adapter);
    }

    // ??????????????????
    public void cancelSpace(){
        lv_myConcerns.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                responseIsDelete = "0"; //???????????????

                Concern concern = concernsList.get(position);
                final String meId = "" + concern.getMeId();
                final String heId = "" + concern.getHeId();
                String concernUserName = concern.getUserName();


                AlertDialog.Builder dialog = new AlertDialog.Builder(MyConcernsActivity.this);
                dialog.setTitle("??????????????????"+concernUserName);
                dialog.setCancelable(true);
                dialog.setPositiveButton("???", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = null;


                        thread =  new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody requestBody = new FormBody.Builder()
                                            .add("meId", meId)
                                            .add("heId", heId)
                                            .build();
                                    Request request = new Request.Builder()
                                            .url("http://192.168.43.205:8080/zblogserver/deleteAConcernServlet")
                                            .post(requestBody)
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    String isDelete = response.header("isDelete");
                                    if("1".equals(isDelete)){
                                        responseIsDelete = "1";
                                    }else{
                                        responseIsDelete = "0";
                                    }
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

                        if("1".equals(responseIsDelete)){
                            Toast.makeText(MyConcernsActivity.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            initView();
                        }else{
                            Toast.makeText(MyConcernsActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.setNegativeButton("???", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                dialog.show();
            }
        });


    }


    // ??????????????????blogs??????
    private void parseJSONToList(String jsonData){
        // ?????? GSON ?????? JSON ??????
        Gson gson = new Gson();
        concernsList = gson.fromJson(jsonData, new TypeToken<List<Concern>>(){}.getType());
    }
}