package com.example.androidauthmongodbnodejs.slide_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.androidauthmongodbnodejs.R;

import android.util.Log;
import com.ramotion.circlemenu.CircleMenuView;

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
//        final CircleMenuView menu = view.findViewById(R.id.circle_menu);
//        menu.setEventListener(new CircleMenuView.EventListener(){
//            @Override
//            public void onMenuOpenAnimationStart(@NonNull CircleMenuView view) {
//                Log.d("D","onMenuOpenAnimationStart");
//            }
//            @Override
//            public void onMenuOpenAnimationEnd(@NonNull CircleMenuView view) {
//                Log.d("D","onMenuOpenAnimationEnd");
//            }
//            @Override
//            public void onMenuCloseAnimationStart(@NonNull CircleMenuView view) {
//                Log.d("D","onMenuCloseAnimationStart");
//            }
//            @Override
//            public void onMenuCloseAnimationEnd(@NonNull CircleMenuView view) {
//                Log.d("D","onMenuCloseAnimationEnd");
//            }
//            @Override
//            public void onButtonClickAnimationStart(@NonNull CircleMenuView view, int index) {
//                Log.d("D","onButtonClickAnimationStart|index: "+index);
//            }
//            @Override
//            public void onButtonClickAnimationEnd(@NonNull CircleMenuView view, int index) {
//                Log.d("D","onButtonClickAnimationEnd|index: "+index);
//            }
//            @Override
//            public boolean onButtonLongClick(@NonNull CircleMenuView view, int buttonIndex) {
//                Log.d("D","onButtonLongClick|index: "+buttonIndex);
//                return true;
//            }
//            @Override
//            public void onButtonLongClickAnimationStart(@NonNull CircleMenuView view, int buttonIndex) {
//                Log.d("D","onButtonLongClickAnimationStart|index: "+buttonIndex);
//            }
//            @Override
//            public void onButtonLongClickAnimationEnd(@NonNull CircleMenuView view, int buttonIndex) {
//                Log.d("D","onButtonLongClickAnimationEnd|index: "+buttonIndex);
//            }
//        });
        return view;
    }

}