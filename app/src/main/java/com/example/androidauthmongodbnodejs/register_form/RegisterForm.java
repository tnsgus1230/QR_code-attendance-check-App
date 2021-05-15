package com.example.androidauthmongodbnodejs.register_form;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidauthmongodbnodejs.R;
import com.example.androidauthmongodbnodejs.Retrofit.IMyService;
import com.example.androidauthmongodbnodejs.Retrofit.RetrofitClient;
import com.example.androidauthmongodbnodejs.sharedpreferences.PreferenceManager;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Retrofit;

public class RegisterForm extends AppCompatActivity {

    IMyService iMyService;
    Button register_btn;
    TextView back_text1;

    protected void onCreate(Bundle savedInstanceState) {
        Context mContext = this;

        //서비스 초기화
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form_1);
        Retrofit retrofitClient = RetrofitClient.getInstance(this);
        iMyService = retrofitClient.create(IMyService.class);

        View register_layout = LayoutInflater.from(RegisterForm.this) //뷰생성
                .inflate(R.layout.register_form_1, null);
        new MaterialStyledDialog.Builder(RegisterForm.this)
                .setCustomView(register_layout); //생성된 뷰 사용
        back_text1 = (TextView) findViewById(R.id.Back_Text);
        back_text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register_btn = (Button) findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialEditText edt_register_email =  (MaterialEditText) findViewById(R.id.edt_email);
                MaterialEditText edt_register_name =   (MaterialEditText) findViewById(R.id.edt_name);
                MaterialEditText edt_register_password = (MaterialEditText) findViewById(R.id.edt_password);
                MaterialEditText edt_register_phone_number = (MaterialEditText)findViewById(R.id.edt_phone_number);

                if (TextUtils.isEmpty(edt_register_email.getText().toString())) {
                    Toast.makeText(RegisterForm.this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(edt_register_name.getText().toString())) {
                    Toast.makeText(RegisterForm.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(edt_register_password.getText().toString())) {
                    Toast.makeText(RegisterForm.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(edt_register_phone_number.getText().toString())) {
                    Toast.makeText(RegisterForm.this, "전화번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                //입력된 4개의 EditText로 부터 값을 가져와서 PreFerenceManager에 담아두고 화면전환 실행
                PreferenceManager.setString(mContext, "email" , edt_register_email.getText().toString());
                PreferenceManager.setString(mContext, "name" , edt_register_name.getText().toString());
                PreferenceManager.setString(mContext, "password" , edt_register_password.getText().toString());
                PreferenceManager.setString(mContext, "phone" , edt_register_phone_number.getText().toString());


                Intent intent = new Intent(RegisterForm.this, RegisterForm2.class);
                startActivity(intent);
            }
        });
    }

    void JSONParse(String jsonStr) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}




























