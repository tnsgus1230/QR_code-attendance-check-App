package com.example.androidauthmongodbnodejs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.concurrent.Executor;

import static android.os.SystemClock.sleep;

public class Main_Screen extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_main_screen);

        ImageView img  = findViewById(R.id.mainscreen);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int widthPixels = metrics.widthPixels * 21/10;
        int heightPixels = metrics.widthPixels * 218/100;
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) img.getLayoutParams();
        params.width = widthPixels;
        params.height = heightPixels;
        img.setLayoutParams(params);


        sleep(2000);
        Intent intent = new Intent(Main_Screen.this, MainActivity.class);
        startActivity(intent);
        finish();

//        //지문인식--------------------------------------------------------------------------------------
//        executor = ContextCompat.getMainExecutor(this);
//        biometricPrompt = new BiometricPrompt(this,
//                executor, new BiometricPrompt.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode,
//                                              @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                finishAffinity();
//                System.exit(0);
//            }
//            @Override
//            public void onAuthenticationSucceeded(
//                    @NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//                sleep(0);
//                Intent intent = new Intent(Main_Screen.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//            }
//        });
//        promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("생체 인증")
//                .setSubtitle("기기에 등록된 생체 정보을 이용하여 인증해주세요.")
//                .setNegativeButtonText("Cancel")
//                .build();
//
//        biometricPrompt.authenticate(promptInfo);
    }


}