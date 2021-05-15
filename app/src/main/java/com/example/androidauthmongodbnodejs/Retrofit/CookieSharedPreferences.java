package com.example.androidauthmongodbnodejs.Retrofit;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;

public class CookieSharedPreferences {

    public static final String KEY_COOKIE = "token";

///////////////////////////////////////////////////////////////////////////////////////////

    private static CookieSharedPreferences dsp = null;

    public static CookieSharedPreferences getInstanceOf(Context c){
        if(dsp==null){
            dsp = new CookieSharedPreferences(c);
        }
        return dsp;
    }

///////////////////////////////////////////////////////////////////////////////////////////

    private Context mContext;
    private SharedPreferences pref;

    public CookieSharedPreferences(Context c) {
        mContext = c;
        final String PREF_NAME = c.getPackageName();
        pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
    }
    public void putHashSet(String key, HashSet<String> set){
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(key, set);
        editor.commit();
    }

    public HashSet<String> getHashSet(String key, HashSet<String> dftValue){
        try {
            return (HashSet<String>)pref.getStringSet(key, dftValue);
        } catch (Exception e) {
            e.printStackTrace();
            return dftValue;
        }
    }
}