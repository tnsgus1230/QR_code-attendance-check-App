package com.example.androidauthmongodbnodejs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import com.example.androidauthmongodbnodejs.common.LoadingDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

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

import static java.lang.Thread.sleep;


public class Main_loggedin extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LoadingDialog loadingDialog;
    IMyService iMyService;
    Boolean signal = true;

    // 아이템 리스트
    //private String[] myDataset;
    ArrayList<item> itemArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main_loggedin);

        transparentStatusAndNavigation();

        loadingDialog = new LoadingDialog(this);
        TextView textView1 = (TextView) findViewById(R.id.name_1);
        textView1.setText(PreferenceManager.getString(this, "department"));

        TextView textView2 = (TextView) findViewById(R.id.name_2);
        textView2.setText(PreferenceManager.getString(this, "name") + "님");


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



        SwipeRefreshLayout swipeRefreshLayout= findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                itemArrayList = new ArrayList<>();
                getMyLectureList(Main_loggedin.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void getMyLectureList(Context mContext) {
        try {
            loadingDialog.show("로딩중", true, false);
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

                            ArrayList<String> lectureList = new ArrayList<>();

                            if (success) {
                                for (int i = 0; i < lectureWeeks.length(); i++) {
                                    Boolean jasd = false;
                                    JSONObject lectureWeeksJSONObject = lectureWeeks.getJSONObject(i);

                                    if (lectureList.isEmpty()) {
                                        lectureList.add(lectureWeeksJSONObject.getString("lectureCode"));
                                    }
                                    for (int j = 0; j < lectureList.size(); j++) {
                                        if (lectureWeeksJSONObject.getString("lectureCode").equals(lectureList.get(j))) {
                                            jasd = true;
                                        }
                                    }
//                                    Toast.makeText(Main_loggedin.this, "asdasd", Toast.LENGTH_SHORT).show();
                                    if (!jasd) {
                                        lectureList.add(lectureWeeksJSONObject.getString("lectureCode"));
                                    }
                                }

//                                if ( lectureList.isEmpty()){
//                                    Toast.makeText(Main_loggedin.this, "asdasd", Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(Main_loggedin.this, lectureList.get(0), Toast.LENGTH_SHORT).show();
//                                }

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
//                                    Toast.makeText(Main_loggedin.this,lectureList.get(i), Toast.LENGTH_SHORT).show();
                                    lectureInfo(lectureList.get(i), lectureWeeksJSONObject.getString("lectureWeekCode"), lectureWeeksJSONObject.getString("attend"), lectureWeeksJSONObject.getString("late"), lectureWeeksJSONObject.getString("run"), lectureList.size(), i);
                                }
                                signal =false;

                            } else {
                                Toast.makeText(Main_loggedin.this, "11", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Main_loggedin.this, "223", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Main_loggedin.this, "33", Toast.LENGTH_SHORT).show();
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

    private void lectureInfo(String code, String lectureWeekCode, String attend, String late, String run, Integer a, Integer b) {
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
                                String result ="";
                                if( (attend.equals("true")) && (late.equals("false"))   && (run.equals("false"))  ){
                                    result = "출석!";
                                }else if( (attend.equals("true")) && (late.equals("true"))   && (run.equals("false"))  ){
                                    result = "지각!";
                                } else if( (run.equals("true"))  ){
                                    result = "도주!";
                                }
                                itemArrayList.add(new item(lecture, lectureCode, professor, lectureClass, lectureWeekCode, type, attend, late, run, result));
                                if ((a-1) == b){
                                    mAdapter = new MyAdapter(Main_loggedin.this, itemArrayList); //스트링 배열 데이터 인자로
                                    mRecyclerView.setAdapter(mAdapter);
                                    loadingDialog.cancel();
                                }
                            } else {
                                Toast.makeText(Main_loggedin.this, "44", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(Main_loggedin.this, "55", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Main_loggedin.this, "66", Toast.LENGTH_SHORT).show();
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
        String result = "";

        public item(String lectureName, String lectureCode, String professorName, String lectureClass, String lectureWeekCode, String type, String attend, String late, String run, String result) {
            this.lectureName = lectureName;
            this.lectureCode = lectureCode;
            this.professorName = professorName;
            this.lectureClass = lectureClass;
            this.lectureWeekCode = lectureWeekCode;
            this.type = type;
            this.attend = attend;
            this.late = late;
            this.run = run;
            this.result = result;

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
            return result;
        }

        public String getLate() {
            return late;
        }

        public String getRun() {
            return run;
        }
    }
    private void transparentStatusAndNavigation() {
        //make full transparent statusBar
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    private void setWindowFlag(final int bits, boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

}