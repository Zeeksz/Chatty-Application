package com.example.chatty.Notifications;

import retrofit2.Call;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAARMIpk-o:APA91bGPdB9HFqW02vy9QnUk4sE841czJIhou60ZCsiR_BoodyOlQ1veZKwGGl2CxLAwHbz6P8R4x6D4odGnjm74d9XdWT6oulOaZLw_0JMfI-y-llz2ZbyX9d4C63YKXPxcEFZPn53x"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
