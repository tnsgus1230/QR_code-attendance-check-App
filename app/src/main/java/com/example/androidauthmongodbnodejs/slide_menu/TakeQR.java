package com.example.androidauthmongodbnodejs.slide_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.example.androidauthmongodbnodejs.R;
//중간 슬레이드 메뉴

public class TakeQR extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    // newInstance constructor for creating fragment with arguments
    public static TakeQR newInstance(int page, String title) {
        TakeQR fragment = new TakeQR();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);

        return fragment;
    }
    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle2");

    }
    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.take_qr, container, false);
        return view;
    }
}