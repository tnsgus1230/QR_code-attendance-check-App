package com.example.androidauthmongodbnodejs;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.androidauthmongodbnodejs.register_form.RegisterForm;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.androidauthmongodbnodejs.Retrofit.IMyService;
import com.example.androidauthmongodbnodejs.Retrofit.RetrofitClient;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.example.androidauthmongodbnodejs.sharedpreferences.PreferenceManager;
import com.example.androidauthmongodbnodejs.fido.MakeFIDO;
import com.example.androidauthmongodbnodejs.fido.RSA;
import com.example.androidauthmongodbnodejs.fido.VerifyFIDO;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_MULTI = 0; //권한 변수
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Boolean firstSignal = false;
    TextView txt_create_account;
    MaterialEditText edt_login_email, edt_login_password;

    Button btn_login;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService iMyService;

    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    //권한 요청 거부시 설정 창으로 이동하는 팝업메시지 출력-------------------------------------------------
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("앱 권한");
                alertDialog.setMessage("해당 앱의 원할한 기능을 이용하시려면 애플리케이션 정보>권한> 에서 모든 권한을 허용해 주십시오");
                // 권한설정 클릭시 이벤트 발생
                alertDialog.setPositiveButton("권한설정",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                startActivity(intent);
                                dialog.cancel();
                            }
                        });
                //취소
                alertDialog.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        }
    }   //권한 요청 거부시 설정 창으로 이동하는 팝업메시지 출력-------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context mContext = this;


        String text = PreferenceManager.getString(mContext, "first");
        if (text.equals("")) {
            firstSignal = true;
        }

        //권한 여러개 요청-------------------------------------------------------------------------------------------------------------------------------------
        ArrayList<String> permissions = new ArrayList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
            permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_PHONE_STATE);
            permissions.add(Manifest.permission.INTERNET);
            permissions.add(Manifest.permission.LOCATION_HARDWARE);

            if (permissions.size() > 0) {
                String[] reqPermissionArray = new String[permissions.size()];
                reqPermissionArray = permissions.toArray(reqPermissionArray);
                ActivityCompat.requestPermissions(this, reqPermissionArray, MY_PERMISSIONS_REQUEST_MULTI);
            }
        }//권한 여러개 요청-------------------------------------------------------------------------------------------------------------------------------------


        //서비스 초기화
        Retrofit retrofitClient = RetrofitClient.getInstance(this);
        iMyService = retrofitClient.create(IMyService.class);

        //뷰 초기화
        edt_login_email = (MaterialEditText) findViewById(R.id.edt_email);
        edt_login_password = (MaterialEditText) findViewById(R.id.edt_password);

        if (!firstSignal){
            loginFIDO(mContext);
        }

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                try {
                    if (firstSignal) {
                        registerFIDO(edt_login_email.getText().toString(),
                                edt_login_password.getText().toString(), mContext);
                    } else {
                        loginUser(edt_login_email.getText().toString(),
                                edt_login_password.getText().toString(), mContext);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }

            }
        });
        txt_create_account = (TextView) findViewById(R.id.txt_create_account);
        txt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterForm.class);
                startActivity(intent);
               // overridePendingTransition(R.anim.slide_out_right,R.anim.slide_out_right);



            }
        });
    }

    private void loginUser(String email, String password, Context mContext) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Call<ResponseBody> repos = iMyService.login(email, password);
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

                                String type = jObject.getString("type");
                                String name = jObject.getString("name");
                                String email = jObject.getString("email");
                                String uni = jObject.getString("university");
                                String department = jObject.getString("department");
                                String number = jObject.getString("number");

                                PreferenceManager.setString(mContext, "type", type);
                                PreferenceManager.setString(mContext, "name", name);
                                PreferenceManager.setString(mContext, "email", email);
                                PreferenceManager.setString(mContext, "university", uni);
                                PreferenceManager.setString(mContext, "department", department);
                                PreferenceManager.setString(mContext, "number", number);

                                Intent intent = new Intent(MainActivity.this, ScanQR3.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    //error
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void registerFIDO(String email, String password, Context mContext) {
        try {

            String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String usim = tm.getSimSerialNumber();

            MakeFIDO makeFIDO = new MakeFIDO();
            PreferenceManager.setString(mContext, "storedKey", MakeFIDO.storedKey);
            PreferenceManager.setString(mContext, "hashedKey", MakeFIDO.hashedKey);
            PreferenceManager.setString(mContext, "iv", Base64.encodeToString(MakeFIDO.iv, Base64.DEFAULT));

            Call<ResponseBody> repos = iMyService.registerFido(email, password, MakeFIDO.hashedKey, MakeFIDO.publicKEY, android_id, usim);
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
                                PreferenceManager.setString(mContext, "first", "false");
                                finishAffinity();
                                Intent intent = getIntent();
                                startActivity(intent);
                                System.exit(0);
                            } else {
                                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loginFIDO(Context mContext) {

        try {
            String hashedKey = PreferenceManager.getString(mContext, "hashedKey");
            Call<ResponseBody> repos = iMyService.receiveOtp(hashedKey);
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
                                String otp = jObject.getString("otp");
                                PreferenceManager.setString(mContext, "otp", otp);
                                loginFIDOHelper(mContext);
                            } else {
                                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
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

//        try {
//            String hashedKey = PreferenceManager.getString(mContext, "hashedKey");
//            String otp = PreferenceManager.getString(mContext, "otp");
//            String storedKey = PreferenceManager.getString(mContext, "storedKey");
//            String iv = PreferenceManager.getString(mContext, "iv");
//            byte[] byteIv =  Base64.decode(iv.getBytes(), Base64.DEFAULT);
//            VerifyFIDO verifyFIDO  = new VerifyFIDO(otp,storedKey,byteIv);
//            Call<ResponseBody> repos = iMyService.loginFido(VerifyFIDO.signedOtp, hashedKey);
//            repos.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.body() != null) {
//                        try {
//                            ResponseBody body = response.body();
//                            String Rawbody = body.string();
//                            JSONObject jObject = new JSONObject(Rawbody);
//                            Boolean success = jObject.getBoolean("success");
//                            if (success) {
//                                Intent intent = new Intent(MainActivity.this, ScanQR3.class);
//                                startActivity(intent);
//                                finish();
//                            } else {
//                                Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
//
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    //error
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void loginFIDOHelper(Context mContext) {
        try {
            String hashedKey = PreferenceManager.getString(mContext, "hashedKey");
            String otp = PreferenceManager.getString(mContext, "otp");
            String storedKey = PreferenceManager.getString(mContext, "storedKey");
            String iv = PreferenceManager.getString(mContext, "iv");
            byte[] byteIv =  Base64.decode(iv.getBytes(), Base64.DEFAULT);
            VerifyFIDO verifyFIDO  = new VerifyFIDO(otp,storedKey,byteIv);
            Call<ResponseBody> repos = iMyService.loginFido(VerifyFIDO.signedOtp, hashedKey);
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
                                String type = jObject.getString("type");
                                String name = jObject.getString("name");
                                String email = jObject.getString("email");
                                String uni = jObject.getString("university");
                                String department = jObject.getString("department");
                                String number = jObject.getString("number");

                                PreferenceManager.setString(mContext, "type", type);
                                PreferenceManager.setString(mContext, "name", name);
                                PreferenceManager.setString(mContext, "email", email);
                                PreferenceManager.setString(mContext, "university", uni);
                                PreferenceManager.setString(mContext, "department", department);
                                PreferenceManager.setString(mContext, "number", number);

                                Intent intent = new Intent(MainActivity.this, ScanQR3.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "1로그인 실패", Toast.LENGTH_SHORT).show();

                            }
                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "2로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "3로그인 실패", Toast.LENGTH_SHORT).show();
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
}























