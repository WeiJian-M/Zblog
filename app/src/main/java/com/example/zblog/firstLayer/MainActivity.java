package com.example.zblog.firstLayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zblog.R;
import com.example.zblog.adapter.BlogAdapter;
import com.example.zblog.domain.BlogWithUserName;
import com.example.zblog.secondLayer.AddABlogActivity;
import com.example.zblog.secondLayer.AddConcernsActivity;
import com.example.zblog.secondLayer.BlogDetailAndCommentActivity;
import com.example.zblog.secondLayer.MyBlogsActivity;
import com.example.zblog.secondLayer.MyConcernsActivity;
import com.example.zblog.util.MyUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends AppCompatActivity {

    private ListView lv_blogs;
    private FloatingActionButton btn_add_blog;
    private List<BlogWithUserName> blogList = new ArrayList<>();
    private String responseData;
    private DrawerLayout drawer_layout;
    private TextView tv_username;
    private long exitTime  = 0; //再按一次退出应用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 侧滑菜单
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        tv_username = headerView.findViewById(R.id.tv_username);
        String username = getIntent().getStringExtra("username");
        tv_username.setText(username);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        }

//        navView.setCheckedItem(R.id.my_blogs);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                if (item.getItemId() == R.id.my_blogs){
//                    Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    String userId = getIntent().getStringExtra("userId");
//                    Toast.makeText(MainActivity.this, userId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MyBlogsActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }else if(item.getItemId() == R.id.my_concerns){
//                    Toast.makeText(MainActivity.this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    String userId = getIntent().getStringExtra("userId");
                    Intent intent = new Intent(MainActivity.this, MyConcernsActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
//                drawer_layout.closeDrawers();
                return false;
            }
        });

        /**
         * 侧滑菜单
         */


        initView();
    }

    // 创建optionMenu
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    // optionMenu的各个点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.btn_add_friends:
                String userId = getIntent().getStringExtra("userId");
//                Toast.makeText(MainActivity.this, userId, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, AddConcernsActivity.class);
                intent.putExtra("myId", userId);
                startActivity(intent);
                break;
            case R.id.btn_fresh:
                initView();
                break;
            case android.R.id.home:
                drawer_layout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    private void initView() {
        // 设置侧滑用户名
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);
        tv_username = headerView.findViewById(R.id.tv_username);
        final String username = getIntent().getStringExtra("username");
        tv_username.setText(username);

        lv_blogs = (ListView) findViewById(R.id.lv_blogs);
        btn_add_blog = (FloatingActionButton) findViewById(R.id.btn_add_blog);

        final String userId = getIntent().getStringExtra("userId");
        System.out.println(userId);


        /**
         * 获取微博列表
         */
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
                            .url("http://192.168.102.205:8080/zblogserver/showMainBlogServlet")
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
        System.out.println(blogList.toString());
        MyUtils.sortData(blogList);
        BlogAdapter adapter = new BlogAdapter(MainActivity.this, R.layout.blog_item, blogList);

        lv_blogs.setAdapter(adapter);

        btn_add_blog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddABlogActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        lv_blogs.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BlogWithUserName blog = blogList.get(position);
                String blogId = "" + blog.getBlogId();
                String blogUserId = "" + blog.getUserId();
                String blogUserName = blog.getUserName();
                String blogContent = blog.getBlogContent();
                String blogTime = blog.getBlogTime();

                Intent intent = new Intent(MainActivity.this, BlogDetailAndCommentActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("blogId", blogId);
                intent.putExtra("blogUserName", blogUserName);
                intent.putExtra("blogContent", blogContent);
                intent.putExtra("blogTime", blogTime);

                startActivity(intent);

//                Toast.makeText(MainActivity.this, blogId+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 解析并初始化blogs数据
    private void parseJSONToList(String jsonData){
        // 运用 GSON 解析 JSON 数据
        Gson gson = new Gson();
        blogList = gson.fromJson(jsonData, new TypeToken<List<BlogWithUserName>>(){}.getType());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}