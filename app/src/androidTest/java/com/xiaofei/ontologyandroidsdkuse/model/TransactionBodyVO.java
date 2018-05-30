
package com.xiaofei.ontologyandroidsdkuse.model;

import java.util.HashMap;
import java.util.Map;

public class TransactionBodyVO {

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

}
