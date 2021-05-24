package com.example.androidauthmongodbnodejs.slide_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.androidauthmongodbnodejs.R;

import java.util.Random;
//중간 슬레이드 메뉴

public class My_Class_List extends Fragment {
    // Store instance variables
    private String title;
    private int page;

    private  ImageView gifview;

    int imageResources[] = {R.raw.cat_coding, R.raw.cat_hi,R.raw.doge,R.raw.doge2,
            R.raw.doge_mirage,R.raw.peekaboo_dog,R.raw.pop_cat,R.raw.sad_cat};

    // newInstance constructor for creating fragment with arguments
    public static My_Class_List newInstance(int page, String title) {
        My_Class_List fragment = new My_Class_List();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);

        return fragment;
    }
    // Store instance variables based on arguments passed

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_class_list, container, false);

//gif  리빙포인트 = gif이미지를 추가하고 싶으면 raw디렉토리에 gif파일을 집어넣고 위에서 선언한 imageResources변수 안에다 해당 파일의 경로를 추가시켜주면 된다.
        Random rand = new Random();
        gifview = (ImageView)view.findViewById(R.id.imageView);
        Glide.with(this).load(imageResources[rand.nextInt(imageResources.length)]).into(gifview);
//gif

        return view;
    }

}