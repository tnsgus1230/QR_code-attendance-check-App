package com.example.androidauthmongodbnodejs.Retrofit;
import java.util.Observable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import  retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMyService {
    @POST("api/account/signup")
    @FormUrlEncoded
    Call<ResponseBody> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String password,
                                    @Field("phone") String phone,
                                    @Field("university") String university,
                                    @Field("number") String number,
                                    @Field("department") String department,
                                    @Field("type") String type);


    @POST("api/account/signin")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("email") String email,
                             @Field("password") String password);


    @POST("api/account/cert/otp")
    @FormUrlEncoded
    Call<ResponseBody> receiveOtp(@Field("hash") String hash);


    @POST("api/account/cert/verify")
    @FormUrlEncoded
    Call<ResponseBody> loginFido(@Field("signedOtp") String signedOtp,
                                 @Field("hash") String hash);


    @POST("api/account/cert/create")
    @FormUrlEncoded
    Call<ResponseBody> registerFido(@Field("email") String email,
                                    @Field("password") String password,
                                    @Field("hash") String hash,
                                    @Field("pubk") String pubk,
                                    @Field("android") String android,
                                    @Field("usim") String usim);


    @POST("api/attend/passive")
    @FormUrlEncoded
    Call<ResponseBody> attendUser(@Field("hash") String hash,
                                  @Field("lectureCode") String lectureCode);


    @POST("api/lecture/student/lecturelist")
    @FormUrlEncoded
    Call<ResponseBody> myLectureList(@Field("email") String email);


    @POST("api/lecture/student/lectureinfo")
    @FormUrlEncoded
    Call<ResponseBody> getLectureInfo(@Field("code") String code);

}
