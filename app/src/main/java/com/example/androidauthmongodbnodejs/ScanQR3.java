package com.example.androidauthmongodbnodejs;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.androidauthmongodbnodejs.Retrofit.IMyService;
import com.example.androidauthmongodbnodejs.Retrofit.RetrofitClient;
import com.example.androidauthmongodbnodejs.scantool.CodeScanner;
import com.example.androidauthmongodbnodejs.scantool.CodeScannerView;
import com.example.androidauthmongodbnodejs.scantool.DecodeCallback;
import com.example.androidauthmongodbnodejs.sharedpreferences.PreferenceManager;
import com.example.androidauthmongodbnodejs.slide_menu.My_Class_List;
import com.google.zxing.Result;

import io.reactivex.disposables.CompositeDisposable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.example.androidauthmongodbnodejs.slide_menu.CreateQR;
import com.example.androidauthmongodbnodejs.slide_menu.TakeQR;
import com.example.androidauthmongodbnodejs.slide_menu.My_Class_List;

import org.json.JSONObject;


public class ScanQR3 extends AppCompatActivity {

    public static Boolean changer = true;
    private CodeScanner mCodeScanner;
    public static String key;
    public static String hash;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparentStatusAndNavigation();
        FragmentPagerAdapter adapterViewPager;

        setContentView(R.layout.activity_scan_q_r3);

        ImageButton button1 = findViewById(R.id.button1);
        Retrofit retrofitClient = RetrofitClient.getInstance(this);
        iMyService = retrofitClient.create(IMyService.class);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (changer) {
                    button1.setBackgroundResource(R.drawable.qr_take_icon);

                    changer = false;
                } else {
                    button1.setBackgroundResource(R.drawable.qr_code_icon);
                    changer = true;
                }

                if (position == 2) {
                    Intent intent = new Intent(ScanQR3.this, Main_loggedin.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changer) {
                    button1.setBackgroundResource(R.drawable.qr_take_icon);
                    vpPager.setCurrentItem(1, true);
                    changer = false;
                } else {
                    button1.setBackgroundResource(R.drawable.qr_code_icon);
                    vpPager.setCurrentItem(0, true);
                    changer = true;
                }

            }
        });


        hash = PreferenceManager.getString(this, "hashedKey");
        key = PreferenceManager.getString(this, "otp");

        if (ContextCompat.checkSelfPermission(ScanQR3.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ScanQR3.this, new String[]{Manifest.permission.CAMERA}, 123);
        } else {
            startScanning();
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

        }

        // Returns total number of pages
        @Override
        public int getCount() {

            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TakeQR.newInstance(0, "Page # 1");
                case 1:
                    return CreateQR.newInstance(1, "Page # 2", key, hash);
                case 2:
                    return My_Class_List.newInstance(2,"page # 3");
                default:
                    return null;
            }
        }

        private void chaingingIcon(Boolean sel) {
            ImageButton button1 = findViewById(R.id.button1);
            if (sel) {
                button1.setBackgroundResource(R.drawable.qr_take_icon);
                changer = false;
            } else {
                button1.setBackgroundResource(R.drawable.qr_code_icon);
                changer = true;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }


    private void startScanning() {
        CodeScannerView scannerView = findViewById(R.id.scanner_view1);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        attendUserFunc(result.getText());
                        Intent intent = new Intent(ScanQR3.this, Main_loggedin.class);
                        startActivity(intent);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }


    private void attendUserFunc(String qrdata) {
        try {
            String hashedKey = PreferenceManager.getString(this, "hashedKey");
            Call<ResponseBody> repos = iMyService.attendUser(hashedKey, qrdata);
            repos.enqueue(new Callback<ResponseBody>() {

                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.body() != null) {
                        try {
                            ResponseBody body = response.body();
                            String Rawbody = body.string();
                            JSONObject jObject = new JSONObject(Rawbody);
                            String data = jObject.getString("data");

                            if (data.equals("attend")) {
                                Toast.makeText(ScanQR3.this, Rawbody + "출석 되었습니다", Toast.LENGTH_SHORT).show();
                            } else if (data.equals("late")) {
                                Toast.makeText(ScanQR3.this, "지각 되었습니다", Toast.LENGTH_SHORT).show();
                            } else if (data.equals("already")) {
                                Toast.makeText(ScanQR3.this, "이미 출석되었습니다", Toast.LENGTH_SHORT).show();
                            } else if (data.equals("absent")) {
                                Toast.makeText(ScanQR3.this, "결석 되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(ScanQR3.this, "출석 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ScanQR3.this, "2 출석 실패", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                startScanning();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if (mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
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