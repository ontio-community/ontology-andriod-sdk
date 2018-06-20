package com.xiaofei.ontologyandroidsdkuse.service;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.core.transaction.Transaction;
import com.xiaofei.ontologyandroidsdkuse.model.OntopassResult;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionBodyVO;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionHashVO;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

public class OntopassService implements IOntopassService {
    private OntopassServiceApi ontopassServiceApi;
    public OntopassService(String url){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        this.ontopassServiceApi = retrofit.create(OntopassServiceApi.class);
    }
    @Override
    public TransactionHashVO assetTransfer(TransactionBodyVO transactionBodyVO) throws IOException {
        TransactionHashVO transactionHashVO = new TransactionHashVO();
        Call<OntopassResult> call = ontopassServiceApi.assetTransfer(transactionBodyVO);
        Response<OntopassResult> response = call.execute();
        OntopassResult ontopassResult = response.body();
        JSONObject jsonObject = (JSONObject) ontopassResult.getResult();
        transactionHashVO = JSONObject.parseObject(jsonObject.toJSONString(),TransactionHashVO.class);
        return transactionHashVO;
    }

    @Override
    public JSONObject ontidRegiste(JSONObject jsonObject) throws IOException {
        Call<OntopassResult> call = ontopassServiceApi.ontidRegiste(jsonObject);
        Response<OntopassResult> response = call.execute();
        OntopassResult ontopassResult = response.body();
        JSONObject jsonObjectResult = (JSONObject) ontopassResult.getResult();
        return jsonObjectResult;
    }

    @Override
    public JSONObject ddoUpdate(JSONObject jsonObject) throws IOException {
        Call<OntopassResult> call = ontopassServiceApi.ddoUpdate(jsonObject);
        Response<OntopassResult> response = call.execute();
        OntopassResult ontopassResult = response.body();
        JSONObject jsonObjectResult = (JSONObject) ontopassResult.getResult();
        return jsonObjectResult;
    }

}
