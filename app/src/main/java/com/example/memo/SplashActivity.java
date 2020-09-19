package com.example.memo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private SharedPreferences sp =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SysApplication.getInstance().addActivity(SplashActivity.this);
        sp = getSharedPreferences("visited",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        boolean flag = sp.getBoolean("isvisit",false);
        if (!flag){
            editor.putBoolean("isvisit",true);
            editor.commit();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //跳转至登陆界面
                    Intent intent = new Intent();
                    intent.setClass(SplashActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            },3000);//延迟三秒
        }else {
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
}
