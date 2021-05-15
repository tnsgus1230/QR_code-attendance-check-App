package com.example.androidauthmongodbnodejs;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.drawable.AnimationDrawable;
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
import com.google.zxing.Result;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import com.example.androidauthmongodbnodejs.slide_menu.CreateQR;
import com.example.androidauthmongodbnodejs.slide_menu.TakeQR;

import java.util.Timer;
import java.util.TimerTask;

public class ScanQR3 extends AppCompatActivity {

    public static Boolean changer = true;
    private CodeScanner mCodeScanner;
    String name;
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
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        super.onCreate(savedInstanceState);

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
                if(changer){
                    button1.setBackgroundResource(R.drawable.qr_take_icon);
                    
                    changer =false;
                }else{
                    button1.setBackgroundResource(R.drawable.qr_code_icon);
                    changer =true;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        button1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (changer){
                    button1.setBackgroundResource(R.drawable.qr_take_icon);
                    vpPager.setCurrentItem(1, true);
                    changer =false;
                }else{
                    button1.setBackgroundResource(R.drawable.qr_code_icon);
                    vpPager.setCurrentItem(0, true);
                    changer =true;
                }

            }
        });

        name = PreferenceManager.getString(this, "name");
        hash = PreferenceManager.getString(this, "hashedKey");
        key = PreferenceManager.getString(this, "otp");

        if (ContextCompat.checkSelfPermission(ScanQR3.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(ScanQR3.this, new String[] {Manifest.permission.CAMERA}, 123);
        } else {
            startScanning();
        }
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {
        private  int NUM_ITEMS = 2;

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
                default:
                    return null;
            }
        }

        private void chaingingIcon(Boolean sel) {
            ImageButton button1 = findViewById(R.id.button1);
            if(sel){
                button1.setBackgroundResource(R.drawable.qr_take_icon);
                changer =false;
            }else{
                button1.setBackgroundResource(R.drawable.qr_code_icon);
                changer =true;
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
//                        attUser(name, result.getText());
                        Intent intent = new Intent(ScanQR3.this, Main_loggedin.class);
                        startActivity(intent);
                        finish();
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

    private void attUser(String name, String qrdata) {
        compositeDisposable.add(iMyService.attrUser(name, qrdata)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(ScanQR3.this, "" + response, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void testTd() {
        compositeDisposable.add(iMyService.testT("As","asd")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        Toast.makeText(ScanQR3.this, "" + response, Toast.LENGTH_SHORT).show();
                    }
                }));
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
        if(mCodeScanner != null) {
            mCodeScanner.startPreview();
        }
    }

    @Override
    protected void onPause() {
        if(mCodeScanner != null) {
            mCodeScanner.releaseResources();
        }
        super.onPause();
    }
}