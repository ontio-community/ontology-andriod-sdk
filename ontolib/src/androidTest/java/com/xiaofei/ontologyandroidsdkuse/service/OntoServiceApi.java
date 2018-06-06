package com.xiaofei.ontologyandroidsdkuse.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaofei.ontologyandroidsdkuse.model.AppConfig;
import com.xiaofei.ontologyandroidsdkuse.model.OntoResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.http.GET;

public interface OntoServiceApi {
    @GET("api/v1/onto/appconfig/query")
    Call<OntoResult> getAppConfig();
}
