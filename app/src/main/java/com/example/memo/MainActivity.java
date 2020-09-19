package com.example.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
* @author liujiaqi
* @Date   2020/9/18
* */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
private ImageButton showlist_btn,save_btn,canncel_btn,add_btn,delete_btn;
private NavigationView nav;
private EditText editText,editTitle;
private DrawerLayout drawerLayout;
private FileOutputStream fos = null;
private String text,title,data;
private  byte[] buffer;
private FileInputStream fis=null;
private SharedPreferences sp = null;
private  SharedPreferences.Editor editor=null;
private  int i=0;//记录存储个数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SysApplication.getInstance().addActivity(MainActivity.this);
        //注册布局及组件
        drawerLayout = findViewById(R.id.drawer_layout);
        showlist_btn = findViewById(R.id.list);
        save_btn = findViewById(R.id.save);
        canncel_btn = findViewById(R.id.canncel);
        add_btn = findViewById(R.id.add);
        delete_btn = findViewById(R.id.delete);
        nav = findViewById(R.id.nav);
        editText = findViewById(R.id.userinput);
        editTitle = findViewById(R.id.title);

        nav.setItemIconTintList(null);

        //设置按钮、导航栏监听
        showlist_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        canncel_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        nav.setNavigationItemSelectedListener(this);

        sp = getSharedPreferences("userdata",MODE_PRIVATE);
        int index = sp.getInt("amount",0);//获取文件数量
        for (int i=1;i<=index;i++){//便历SharedPreferences，使列表能显示全部文件名
            sp=getSharedPreferences("userdata",MODE_PRIVATE);
            String memoname = sp.getString("name"+i,null);//反回文件名
            if (memoname!=null&&!memoname.equals(" ")){
                nav.getMenu().add(1,i,1,memoname);//添加对应不为空的item
            }
        }

    }



    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.delete:
                    title = editTitle.getText().toString();
                    if (delete(title)){
                        sp = getSharedPreferences("userdata",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        int index = sp.getInt("amount",0);
                        for (int i=1;i<=index;i++){
                            String memoname = sp.getString("name"+i,null);
                            if (title!=null&&memoname!=null){
                                if (memoname.equals(title)) {
                                    editor.remove("name"+i);//删除对应的文件名
                                    editor.commit();
                                    nav.getMenu().removeItem(i);
                                }
                            }
                        }
                        editTitle.setText(null);
                        editText.setText(null);
                        Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this,"文件不存在/删除失败",Toast.LENGTH_SHORT).show();

                    }
                    break;
                case R.id.add://添加
                    editText.setText(null);//将文本清空
                    editTitle.setText(null);
                    break;
                case R.id.save:
                    try {
                        sp=getSharedPreferences("userdata",MODE_PRIVATE);
                        editor = sp.edit();
                        i=sp.getInt("amount",0);
                        text = editText.getText().toString();//获取内容
                        title = editTitle.getText().toString();//获取标题
                        //Toast.makeText(MainActivity.this,"index:"+i,Toast.LENGTH_SHORT).show();
                        if (!isExist(title)){
                            saveFile(title,text);
                            i++;//文件数量加1
                            editor.putString("name"+i,title);//将标题写入SharedPreferences中
                            editor.putInt("amount",i);//记录文件个数
                            //Toast.makeText(MainActivity.this,"index:"+i,Toast.LENGTH_SHORT).show();
                            nav.getMenu().add(1,i,1,title);//添加菜单列表
                            editor.commit();
                            Toast.makeText(MainActivity.this,"文件已创建",Toast.LENGTH_SHORT).show();

                        }else {//若存在则再保存一次
                            saveFile(title,text);
                            Toast.makeText(MainActivity.this,"文件已保存修改",Toast.LENGTH_SHORT).show();

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.list://点击唤出侧滑导航栏
                    drawerLayout.openDrawer(GravityCompat.START);
                    break;
                case R.id.canncel://退出

                    SysApplication.getInstance().exit();
                    break;
            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {//点击列表信息读取文件
        readFile(menuItem.getTitle().toString());
        return true;
    }
    public  boolean isExist(String fileName)  {//判断文件是否存在，存在反回True
        String path = this.getFilesDir().getPath()+"/";
        File file = new File(path+fileName);
        Log.d("path",path+fileName);
        if(file.exists()){
            Toast.makeText(MainActivity.this,"文件存在",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    public void saveFile(String fileName, String str) throws IOException{//写文件
                fos = openFileOutput(fileName, MODE_PRIVATE);
                fos.write(str.getBytes("utf-8"));
                fos.flush();
                if (fos!=null){
                    fos.close();
                }
        }

    public void readFile(String fileName) {//读文件
        if (isExist(fileName)){
            try {
                fis = openFileInput(fileName);
                buffer = new byte[fis.available()];
                fis.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fis!=null){
                        fis.close();
                        data = new String(buffer);
                        editText.setText(data);
                        editTitle.setText(fileName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public boolean delete(String fileName){//删除文件，成功反回true
        String path = this.getFilesDir().getPath()+"/";
        File file = new File(path+fileName);
            if (file.exists()&&file.delete()){
              //  Toast.makeText(this,"delte ok",Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
    }

}
