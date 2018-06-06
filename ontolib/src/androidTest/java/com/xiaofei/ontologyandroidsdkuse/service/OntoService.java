package com.xiaofei.ontologyandroidsdkuse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaofei.ontologyandroidsdkuse.model.AppConfig;
import com.xiaofei.ontologyandroidsdkuse.model.OntoResult;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class OntoService implements IOntoService {
    private OntoServiceApi ontoServiceApi;

    public OntoService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dev.ont.io/")
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        this.ontoServiceApi = retrofit.create(OntoServiceApi.class);
    }
    @Override
    public AppConfig getAppConfig() throws IOException {
        Call<OntoResult> call = ontoServiceApi.getAppConfig();
        Response<OntoResult> response = call.execute();
        OntoResult ontoResult = response.body();
        JSONObject result = (JSONObject) ontoResult.getResult();
        AppConfig appConfig = JSON.parseObject(result.toJSONString(),AppConfig.class);
        return appConfig;
    }
}
