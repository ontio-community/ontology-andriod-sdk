package com.xiaofei.ontologyandroidsdkuse.service;

import com.xiaofei.ontologyandroidsdkuse.model.TransactionBodyVO;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionHashVO;

import java.io.IOException;

public interface IOntopassService {
    TransactionHashVO assetTransfer(TransactionBodyVO transactionBodyVO) throws IOException;
}
