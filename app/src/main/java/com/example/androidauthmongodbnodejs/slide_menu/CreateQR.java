package com.example.androidauthmongodbnodejs.slide_menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.androidauthmongodbnodejs.R;
import com.example.androidauthmongodbnodejs.sharedpreferences.PreferenceManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//중간 슬레이드 메뉴

public class CreateQR extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private ImageView iv;
    private String text;
    public static String key = "";
    public static String hashed = "";

    // newInstance constructor for creating fragment with arguments
    public static CreateQR newInstance(int page, String title, String receivedKey, String hash) {
        CreateQR fragment = new CreateQR();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        key = receivedKey + receivedKey + receivedKey + receivedKey + receivedKey + receivedKey + receivedKey + receivedKey;
        hashed = hash;
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_qr, container, false);
        iv = (ImageView) view.findViewById(R.id.qrcode1);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!Thread.interrupted())
                    try
                    {
                        getActivity().runOnUiThread(new Runnable() // start actions in UI thread
                        {
                            @Override
                            public void run()
                            {
                                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                                try {
                                    BitMatrix bitMatrix = multiFormatWriter.encode( getJwt(), BarcodeFormat.QR_CODE, 400, 400);
                                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                                    iv.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                }
                            }
                        });
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e)
                    {
                        // ooops
                    }
            }
        }).start();
        return view;
    }
    public String getJwt(){
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("sub", hashed);

        Long expiredTime = 1000 * 5L;
        Date ext = new Date();
        ext.setTime(ext.getTime() + expiredTime);

        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setExpiration(ext)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
        return  jwt;
    }
}