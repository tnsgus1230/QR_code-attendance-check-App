package com.example.androidauthmongodbnodejs.Retrofit;
import java.util.Observable;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import  retrofit2.http.POST;

public interface IMyService {
    @POST("register")
    @FormUrlEncoded
    io.reactivex.Observable<String> registerUser(@Field("email") String id,
                                         @Field("name") String name,
                                         @Field("password") String pw);


    @POST("api/account/signin")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("email") String email,
                             @Field("password") String password);

    @POST("api/account/cert/otp")
    @FormUrlEncoded
    Call<ResponseBody> receiveOtp(@Field("hash") String hash);

    @POST("api/account/cert/otp")
    @FormUrlEncoded
    Call<ResponseBody> loginFido(@Field("signedOtp") String signedOtp);



    @POST("api/account/cert/create")
    @FormUrlEncoded
    Call<ResponseBody> registerFido(@Field("email") String email,
                                    @Field("password") String password,
                                    @Field("hash") String hash,
                                    @Field("pubk") String pubk,
                                    @Field("android") String android,
                                    @Field("usim") String usim);



    @POST("api/test")
    @FormUrlEncoded
    io.reactivex.Observable<String> testT(@Field("email") String id,
                                          @Field("password") String pw);

    @POST("att")
    @FormUrlEncoded
    io.reactivex.Observable<String> attrUser(@Field("name") String name,
                                            @Field("lecture") String lecture);

}
