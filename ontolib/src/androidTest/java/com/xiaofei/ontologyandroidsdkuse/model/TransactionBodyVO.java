
package com.xiaofei.ontologyandroidsdkuse.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

public class TransactionBodyVO {

    @JSONField(name = "SendAddress")
    private String sendAddress;
    @JSONField(name = "ReceiveAddress")
    private String receiveAddress;
    @JSONField(name = "AssetName")
    private String assetName;
    @JSONField(name = "Amount")
    private Integer amount;
    @JSONField(name = "TxnStr")
    private String txnStr;

    public String getSendAddress() {
        return sendAddress;
    }

    public void setSendAddress(String sendAddress) {
        this.sendAddress = sendAddress;
    }

    public String getReceiveAddress() {
        return receiveAddress;
    }

    public void setReceiveAddress(String receiveAddress) {
        this.receiveAddress = receiveAddress;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getTxnStr() {
        return txnStr;
    }

    public void setTxnStr(String txnStr) {
        this.txnStr = txnStr;
    }

}
