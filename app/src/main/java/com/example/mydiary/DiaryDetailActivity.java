package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.radiobutton.MaterialRadioButton;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 상세보기 화면 or 작성하기 화면이다.
 */

public class DiaryDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvDate;               // 일시 설정 텍스트
    private EditText mEtTitle, mEtContent;  // 일기 제목, 일기 내용
    private RadioGroup mRgWeather;

    private String mDetailMode = "";        // intent로 받아낸 게시글 모드
    private String mBeforeDate = "";        // intent로 받아낸 게시글 기준 작성일자
    private String mSelectedUserDate = "";  // 선택된 일시 값
    private int mSelectedWeatherType = -1;  // 선택된 날짜 값 (1~6)

    private DatabaseHelper mDatabaseHelper; // Database Util 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);

        // Database 객체 생성
        mDatabaseHelper = new DatabaseHelper(this);

        mTvDate = findViewById(R.id.tv_date);               // 일시 설정 텍스트
        mEtTitle = findViewById(R.id.et_title);             // 제목 입력필드
        mEtContent = findViewById(R.id.et_content);         // 내용 입력필드
        mRgWeather = findViewById(R.id.rg_weather);         // 날씨 선택 라디오 그룹

        ImageView iv_back = findViewById(R.id.iv_back);     // 뒤로가기 버튼
        ImageView iv_check = findViewById(R.id.iv_check);   // 작성완료 버튼

        mTvDate.setOnClickListener(this);   // 클릭 기능 부여
        iv_back.setOnClickListener(this);   // 클릭 기능 부여
        iv_check.setOnClickListener(this);  // 클릭 기능 부여

        // 기본으로 설정될 날짜의 값을 지정 (디바이스 현재 시간 기준)
        mSelectedUserDate = new SimpleDateFormat("yyyy/MM/dd E요일", Locale.KOREAN).format(new Date());
        mTvDate.setText(mSelectedUserDate);

        // 이전 액티비티로부터 값을 전달받기
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            // intent putExtra 했던 데이터가 존재한다면 내부를 수행..!
            DiaryModel diaryModel = (DiaryModel) intent.getSerializableExtra("diaryModel");
            mDetailMode = intent.getStringExtra("mode");
            mBeforeDate = diaryModel.getWriteDate();    // 게시글 Database update 쿼리문 처리를 위해서 여기서 받아둠.

            // 넘겨받은 값을 활용해서 각 필드들에 설정해주기
            mEtTitle.setText(diaryModel.getTitle());
            mEtContent.setText(diaryModel.getContent());
            int weatherType = diaryModel.getWeatherType();
            ((MaterialRadioButton) mRgWeather.getChildAt(weatherType)).setChecked(true);
            mSelectedUserDate = diaryModel.getUserDate();
            mTvDate.setText(diaryModel.getUserDate());

            if (mDetailMode.equals("modify")) {
                // 수정 모드
                TextView tv_header_title = findViewById(R.id.tv_header_title);
                tv_header_title.setText("일기 수정");

            } else if (mDetailMode.equals("detail")) {
                // 상세 보기 모드
                TextView tv_header_title = findViewById(R.id.tv_header_title);
                tv_header_title.setText("일기 상세보기");

                // 읽기 전용 화면으로 표시
                mEtTitle.setEnabled(false);
                mEtContent.setEnabled(false);
                mTvDate.setEnabled(false);
                for (int i = 0; i < mRgWeather.getChildCount(); i++) {
                    // 라디오 그룹 내의 6개 버튼들을 반복하여 비활성화 처리
                    mRgWeather.getChildAt(i).setEnabled(false);
                }
                // 작성완료 버튼을 invisible (투명) 처리함.
                iv_check.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        // setOnClickListener가 붙어있는 뷰들은 클릭이 발생하면 모두 이곳을 수행
        switch (view.getId()) {
            case R.id.iv_back:
                finish();   // 현재 액티비티 종료
                break;

            case R.id.iv_check:
                // 작성 완료

                // 라디오 그룹의 버튼 클릭 현재 상황 가지고 오기
                mSelectedWeatherType = mRgWeather.indexOfChild(findViewById(mRgWeather.getCheckedRadioButtonId()));

                // 입력필드 비어있는지 체크
                if (mEtTitle.getText().length() == 0 || mEtContent.getText().length() == 0) {
                    // error
                    Toast.makeText(this, "입력되지 않은 필드가 존재합니다.", Toast.LENGTH_SHORT).show();
                    return; // 밑의 로직을 태우지 않고 되돌려보냄..!
                }

                // 날씨 선택이 되어있는지 체크
                if (mSelectedWeatherType == -1) {
                    // error
                    Toast.makeText(this, "날씨를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return; // 밑의 로직을 태우지 않고 되돌려보냄..!
                }

                ////////////////////// 이 곳까지 도달했다면 에러상황이 없으므로 데이터 저장..!

                String title = mEtTitle.getText().toString();       // 제목 입력 값
                String content = mEtContent.getText().toString();   // 내용 입력 값
                String userDate = mSelectedUserDate;                // 사용자가 선택한 일시
                if (userDate.equals("")) {
                    // 별도 날짜 설정을 하지 않은 채로 작성완료를 누를 경우
                    userDate = mTvDate.getText().toString();
                }
                String writeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.KOREAN).format(new Date());   // 작성완료 누른 시점의 일시

                // 액티비티의 현재 모드에 따라서 데이터베이스에 저장 또는 업데이트
                if (mDetailMode.equals("modify")) {
                    // 게시글 수정 모드
                    mDatabaseHelper.setUpdateDiaryList(title, content, mSelectedWeatherType, userDate, writeDate, mBeforeDate);
                    Toast.makeText(this, "다이어리 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 게시글 작성 모드
                    mDatabaseHelper.setInsertDiaryList(title, content, mSelectedWeatherType, userDate, writeDate);
                    Toast.makeText(this, "다이어리 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }
                finish();   // 현재 액티비티 종료
                break;

            case R.id.tv_date:
                // 일시 설정 텍스트

                // 달력을 띄워서 사용자에게 임시로 입력 받는다.

                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // 달력에 선택된 (년, 월, 일)을 가지고 캘린더 함수에 넣어 사용자가 선택한 요일을 알아낸다.
                        Calendar innerCal = Calendar.getInstance();
                        innerCal.set(Calendar.YEAR, year);
                        innerCal.set(Calendar.MONTH, month);
                        innerCal.set(Calendar.DAY_OF_MONTH, day);

                        mSelectedUserDate = new SimpleDateFormat("yyyy/MM/dd E요일", Locale.KOREAN).format(innerCal.getTime());
                        mTvDate.setText(mSelectedUserDate);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();  // 다디얼로그 (팝업) 활성화

                break;
        }
    }
}