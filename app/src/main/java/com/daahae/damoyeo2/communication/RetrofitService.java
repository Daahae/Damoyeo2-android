package com.daahae.damoyeo2.communication;

import com.daahae.damoyeo2.model.Friend;
import com.google.gson.JsonObject;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitService {

    @FormUrlEncoded
    @POST("renewPos")
    Call<JsonObject> getUserLatLng(@Field("userPos") String user);

    //@Multipart
    @FormUrlEncoded
    @POST("usersToMid")
    Call<JsonObject> getMidTransportData(@Field("userArr") String user);

    //@Multipart
    @FormUrlEncoded
    @POST("midCategory")
    Call<JsonObject> getBuildingData(@Field("userRequest") String type);

    //@Multipart
    @FormUrlEncoded
    @POST("midDetailCategory")
    Call<JsonObject> getBuildingDetail(@Field("buildingRequest") String name);
    //@Multipart
    @FormUrlEncoded
    @POST("midTransportInfo")
    Call<JsonObject> getLandMarkTransportData(@Field("userArr") String user);

    //@Multipart
    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> getLoginInfo(@Field("userLoginInfo") String user);

    //@Multipart
    @FormUrlEncoded
    @POST("category")
    Call<JsonObject> getCategoryInformation(@Field("categoryInfoArr") String categoryInfo);


    /**
    * Friend List Function
    */
    //@Multipart
    @FormUrlEncoded
    @POST("friendRequest")
    Call<JsonObject> getFriendsList(@Field("friend") String user);


    //@Multipart
    @FormUrlEncoded
    @POST("friendAdd")
    Call<JsonObject> getAddFriend(@Field("friend") String user);

    //@Multipart
    @FormUrlEncoded
    @POST("friendAccept")
    Call<JsonObject> getAcceptFriend(@Field("friend") String user);


    /**
     * Chatting List Function
     */

    //@Multipart
    @FormUrlEncoded
    @POST("chatRoom")
    Call<JsonObject> getRoomList(@Field("chatRoom") String user);

    //@Multipart
    @FormUrlEncoded
    @POST("detailChatRoom")
    Call<JsonObject> getChattingRoomInformation(@Field("chatRoom") String request);


    /**
     * Schedule List Function
     */

    //@Multipart
    @FormUrlEncoded
    @POST("scheduleResult")
    Call<JsonObject> getScheduleResult(@Field("schedule") String request);
}