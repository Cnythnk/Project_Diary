package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRvDiary;              // 리사이클러 뷰 (리스트 뷰)
    DiaryListAdapter mAdapter;          // 리사이클러 뷰와 연동할 어댑터
    ArrayList<DiaryModel> mLstDiary;    // 리스트에 표현할 다이어리 데이터들 (배열)
    DatabaseHelper mDatabaseHelper;     // 데이터베이스 헬퍼 클래스

    @Override
    protected void onCreate(Bundle savedInstanceState) { // onCreate() : Activity (화면) 시작될 때 가장 먼저 실행되는 곳 ! (최초 1회만 호출되는 것에 유의)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // acivity_main.xml 파일과 연동해주는 부분 (xml 파일과 java 파일은 쌍으로 동작)

        // 데이터베이스 객체의 초기화
        mDatabaseHelper = new DatabaseHelper(this);

        mLstDiary = new ArrayList<>();

        mRvDiary = findViewById(R.id.rv_diary);

        mAdapter = new DiaryListAdapter(); // 리사이클러 뷰 어댑터 인스턴스 생성

//        // 다이어리 샘플 아이템 2개 생성
//        DiaryModel item1 = new DiaryModel();
//        item1.setId(0);
//        item1.setTitle("오늘은 행복했다");
//        item1.setContent("내용입니다.");
//        item1.setUserDate("2021/10/11 Mon");
//        item1.setWriteDate("2021/10/11 Mon");
//        item1.setWeatherType(0);
//        mLstDiary.add(item1);
//
//        DiaryModel item2 = new DiaryModel();
//        item2.setId(0);
//        item2.setTitle("오늘은 우울했다");
//        item2.setContent("내용입니다.");
//        item2.setUserDate("2021/10/12 Tue");
//        item2.setWriteDate("2021/10/12 Tue");
//        item2.setWeatherType(3);
//        mLstDiary.add(item2);

        mRvDiary.setAdapter(mAdapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.btn_write);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            // 작성하기 버튼을 누를 때 호출되는 곳
            @Override
            public void onClick(View view) {

                // 작성하기 화면으로 이동!
                Intent intent = new Intent(MainActivity.this, DiaryDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    // 'Ctrl + O' 입력 후 메소드 검색 및 호출 (Override)
    @Override
    protected void onResume() {     // 액티비티의 재개 (onCreate() 메소드는 최초 1회만 호출되므로 즉각적인 갱신을 위해 사용)
        super.onResume();
        // get load list
        setLoadRecentList();
    }

    // 최근 데이터베이스 정보를 가지고 와서 리사이클러 뷰에 갱신해준다.
    private void setLoadRecentList() {

        // 이전 배열 리스트에 저장된 데이터가 있으면 삭제
        if (!mLstDiary.isEmpty()) {
            mLstDiary.clear();
        }

        mLstDiary = mDatabaseHelper.getDiaryListFromDB();   // 데이터베이스로부터 저장되어있는 DB를 확인하여 가지고 온다.
        mAdapter.setListInit(mLstDiary);
    }
}