
package com.xiaofei.ontologyandroidsdkuse.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.Map;

public class TransactionBodyVO {
    @JSONField(name = "Address")
    private String address;

    @JSONField(name = "TxnStr")
    private String txnStr;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getTxnStr() {
        return txnStr;
    }

    public void setTxnStr(String txnStr) {
        this.txnStr = txnStr;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
