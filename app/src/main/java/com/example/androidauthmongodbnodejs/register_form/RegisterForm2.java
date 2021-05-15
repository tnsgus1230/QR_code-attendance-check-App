package com.example.androidauthmongodbnodejs.register_form;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidauthmongodbnodejs.MainActivity;
import com.example.androidauthmongodbnodejs.R;
import com.example.androidauthmongodbnodejs.Retrofit.IMyService;
import com.example.androidauthmongodbnodejs.Retrofit.RetrofitClient;
import com.example.androidauthmongodbnodejs.ScanQR3;
import com.example.androidauthmongodbnodejs.sharedpreferences.PreferenceManager;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterForm2 extends AppCompatActivity {
    IMyService iMyService;
    Button register_btn1;
    TextView Back_Text2;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        //서비스 초기화
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form_2);
        Context mContext = this;
        Retrofit retrofitClient = RetrofitClient.getInstance(mContext);

        iMyService = retrofitClient.create(IMyService.class);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        View activity_new_register_form2 = LayoutInflater.from(RegisterForm2.this) //뷰생성
                .inflate(R.layout.register_form_2, null);
        new MaterialStyledDialog.Builder(RegisterForm2.this)
                .setCustomView(activity_new_register_form2); //생성된 뷰 사용

        ArrayAdapter babyAdapter = ArrayAdapter.createFromResource(this, R.array.type, R.layout.custom_spinner);
        babyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(babyAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //이 메소드에서 position은 몇번째 값이 클릭됬는지 알 수 있음
                //getItemAtPosition(position)를 통해서 해당 값을 받아올수있

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //선택하지 않았을때

            }
        });
        Back_Text2 = findViewById(R.id.Back_Text2);
        Back_Text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        register_btn1 = (Button) findViewById(R.id.register_btn1);
        register_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialEditText edt_university = (MaterialEditText) findViewById(R.id.edt_university);
                MaterialEditText edt_student_code = (MaterialEditText) findViewById(R.id.edt_student_code);
                MaterialEditText edt_stoudent_department = (MaterialEditText) findViewById(R.id.edt_stoudent_department);

                if (TextUtils.isEmpty(edt_university.getText().toString())) {
                    Toast.makeText(RegisterForm2.this, "학교명을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(edt_student_code.getText().toString())) {
                    Toast.makeText(RegisterForm2.this, "학번을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(edt_stoudent_department.getText().toString())) {
                    Toast.makeText(RegisterForm2.this, "학과를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                String spinner_text = spinner.getSelectedItem().toString(); //스피너로 선택된 값 가져와서 담기
                if (spinner_text == null) {
                    Toast.makeText(RegisterForm2.this, "교수 또는 학생인지 선택하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(RegisterForm2.this, edt_stoudent_department.getText().toString(), Toast.LENGTH_SHORT).show();
                registerUser(mContext, edt_university.getText().toString(), edt_stoudent_department.getText().toString(), edt_student_code.getText().toString(), spinner_text);

            }
        });
    }

    private void registerUser(Context mContext, String university, String department, String number, String type) {

        try {

            String edt_register_email = PreferenceManager.getString(mContext, "email");
            String edt_register_name = PreferenceManager.getString(mContext, "name");
            String edt_register_password = PreferenceManager.getString(mContext, "password");
            String edt_register_phone_number = PreferenceManager.getString(mContext, "phone");

            Call<ResponseBody> repos = (Call<ResponseBody>) iMyService.registerUser(edt_register_email, edt_register_name, edt_register_password, edt_register_phone_number,
                    university, number, department, type);
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
                                Toast.makeText(RegisterForm2.this, edt_register_name + "님 회원가입 되었습니다.", Toast.LENGTH_SHORT).show();
                                PreferenceManager.removeKey(mContext, "email");
                                PreferenceManager.removeKey(mContext, "name");
                                PreferenceManager.removeKey(mContext, "password");
                                PreferenceManager.removeKey(mContext, "phone");
                                Intent intent = new Intent(RegisterForm2.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                return;


                            } else {
                                Toast.makeText(RegisterForm2.this, "회원가입에 실패하였습니다.", Toast.LENGTH_LONG).show();

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


}




























