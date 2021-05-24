package com.example.androidauthmongodbnodejs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidauthmongodbnodejs.Retrofit.IMyService;
import com.example.androidauthmongodbnodejs.Retrofit.RetrofitClient;
import com.example.androidauthmongodbnodejs.sharedpreferences.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class Main_loggedin extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    IMyService iMyService;

    // 아이템 리스트
    //private String[] myDataset;
    private static ArrayList<item> itemArrayList;
    ArrayList<String> lectureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_loggedin);
        TextView textView1 = (TextView) findViewById(R.id.name_1) ;
        textView1.setText(PreferenceManager.getString(this,"department"));

        TextView textView2 = (TextView) findViewById(R.id.name_2) ;
        textView2.setText(PreferenceManager.getString(this,"name")+"님");


        Retrofit retrofitClient = RetrofitClient.getInstance(this);
        iMyService = retrofitClient.create(IMyService.class);

        itemArrayList = new ArrayList<>();
        getMyLectureList(this);

        //데이터준비-실제로는 ArrayList<>등을 사용
        //인터넷이나 폰에 있는 DB에서 아이템을 가져와 배열에 담아
        //myDataset = new String[]{"고지원1", "고지원2", "고지원3","고지원4"};
        //ArrayList 생성

        //ArrayList에 값 추가하기               여기에서 서버로부터 값을 받아서 .add로 추가해주기


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);//옵션
        //Linear layout manager 사용

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();
        mRecyclerView.setLayoutManager(mLayoutManager);
        snapHelper.attachToRecyclerView(mRecyclerView);

        //어답터 세팅
        mAdapter = new MyAdapter(this, itemArrayList); //스트링 배열 데이터 인자로
        mRecyclerView.setAdapter(mAdapter);
    }


    private void getMyLectureList(Context mContext) {
        try {
            Call<ResponseBody> repos = iMyService.myLectureList(PreferenceManager.getString(mContext, "email"));
            repos.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {


                            ResponseBody body = response.body();
                            String Rawbody = body.string();
                            JSONObject jObject = new JSONObject(Rawbody);
                            Boolean success = jObject.getBoolean("success");

                            JSONArray lectureWeeks = jObject.getJSONArray("lectureWeeks");

                            JSONObject lectureWeeksJSONObject2 = lectureWeeks.getJSONObject(0);
                            lectureList = new ArrayList<>();
                            lectureList.add(lectureWeeksJSONObject2.getString("lectureCode"));
                            if (success) {
                                for (int i = 0; i < lectureWeeks.length(); i++) {
                                    Boolean jasd = false;
                                    JSONObject lectureWeeksJSONObject = lectureWeeks.getJSONObject(i);
                                    for (int j = 0; j < lectureList.size(); j++) {
                                        if (lectureWeeksJSONObject.getString("lectureCode").equals(lectureList.get(j))) {
                                            jasd = true;
                                        }
                                    }
                                    if (!jasd) {
                                        lectureList.add(lectureWeeksJSONObject.getString("lectureCode"));
                                    }
                                }

                                for (int i = 0; i < lectureList.size(); i++) {
                                    long temp = 0;
                                    int temp3 = 0;
                                    for (int j = 0; j < lectureWeeks.length(); j++) {
                                        JSONObject lectureWeeksJSONObject = lectureWeeks.getJSONObject(j);
                                        if (lectureWeeksJSONObject.getString("lectureCode").equals(lectureList.get(i))) {
                                            long temp2 = lectureWeeksJSONObject.getLong("lectureStartTime");
                                            if (temp2 > temp) {
                                                temp = temp2;
                                                temp3 = j;
                                            }
                                        }
                                    }
                                    JSONObject lectureWeeksJSONObject = lectureWeeks.getJSONObject(temp3);

                                    lectureInfo(lectureList.get(i), lectureWeeksJSONObject.getString("lectureWeekCode"), lectureWeeksJSONObject.getString("attend"), lectureWeeksJSONObject.getString("late"), lectureWeeksJSONObject.getString("run"));

                                }

                            } else {

                            }
                        } catch (Exception e) {
                        }
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //error
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void lectureInfo(String code, String lectureWeekCode, String attend, String late, String run) {
        try {
            Call<ResponseBody> repos = iMyService.getLectureInfo(code);
            repos.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.body() != null) {
                        try {
                            ResponseBody body = response.body();
                            String Rawbody = body.string();
                            JSONObject jObject = new JSONObject(Rawbody);
                            Boolean success = jObject.getBoolean("success");
                            if (success) {
                                String lecture = jObject.getString("lecture");
                                String professor = jObject.getString("professor");
                                String lectureClass = jObject.getString("lectureClass");
                                String type = jObject.getString("type");
                                String timetable = jObject.getString("timetable");
                                String lectureCode = jObject.getString("lectureCode");

                                itemArrayList.add(new item(lecture, lectureCode, professor, lectureClass, lectureWeekCode, type, attend, late, run));
                            } else {
                                Toast.makeText(Main_loggedin.this, "Asddd", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Main_loggedin.this,"asdasdasd", Toast.LENGTH_SHORT).show();
                        }
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //error
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    //아이템 클라스
    public class item {
        String lectureName = "";
        String lectureCode = "";
        String professorName = "";
        String lectureClass = "";
        String lectureWeekCode = "";
        String type = "";
        String attend = "";
        String late = "";
        String run = "";

        public item(String lectureName, String lectureCode, String professorName, String lectureClass, String lectureWeekCode, String type, String attend, String late, String run) {
            this.lectureName = lectureName;
            this.lectureCode = lectureCode;
            this.professorName = professorName;
            this.lectureClass = lectureClass;
            this.lectureWeekCode = lectureWeekCode;
            this.type = type;
            this.attend = attend;
            this.late = late;
            this.run = run;

        }

        public String getLectureName() {
            return lectureName;
        }

        public String getLectureCode() {
            return lectureCode;
        }

        public String getProfessorName() {
            return professorName;
        }

        public String getLectureClass() {
            return lectureClass;
        }

        public String getLectureWeekCode() {
            return lectureWeekCode;
        }

        public String getType() {
            return type;
        }

        public String getAttend() {
            return attend;
        }

        public String getLate() {
            return late;
        }

        public String getRun() {
            return run;
        }
    }

}