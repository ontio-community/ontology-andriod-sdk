package com.xiaofei.ontologyandroidsdkuse.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaofei.ontologyandroidsdkuse.model.OntopassResult;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionBodyVO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OntopassServiceApi {
    @POST("api/v1/ontpass/asset/transfer")
    Call<OntopassResult> assetTransfer(@Body TransactionBodyVO transactionBodyVO);

    @POST("api/v1/ontpass/ontid/register")
    Call<OntopassResult> ontidRegiste(@Body JSONObject jsonObject);

    @POST("api/v1/ontpass/ddo/update")
    Call<OntopassResult> ddoUpdate(@Body JSONObject jsonObject);
}
