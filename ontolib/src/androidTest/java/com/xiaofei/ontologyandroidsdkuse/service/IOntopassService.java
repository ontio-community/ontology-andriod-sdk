package com.xiaofei.ontologyandroidsdkuse.service;

import com.alibaba.fastjson.JSONObject;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionBodyVO;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionHashVO;

import java.io.IOException;

public interface IOntopassService {
    TransactionHashVO assetTransfer(TransactionBodyVO transactionBodyVO) throws IOException;

    JSONObject ontidRegiste(JSONObject jsonObject) throws IOException;

    JSONObject ddoUpdate(JSONObject jsonObject) throws  IOException;
}
