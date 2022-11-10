package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 딜레이를 발생시킨 후 MainActivity 로 이동시키는 부분
        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {     // MainActivity 로 이동하는 구간
                Intent mainIntent = new Intent(SplashActivity.this , MainActivity.class);
                startActivity(mainIntent);
                finish();           // xml 파일까지 포함하여 전부 파괴하는 메소드 (잠깐 띄우는 용도의 화면이기 때문)
            }
        }, 1500);         // 딜레이 해주고 싶은 밀리초 (1000밀리초 = 1초)
    }
}