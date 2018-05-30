
package com.xiaofei.ontologyandroidsdkuse.model;

import java.util.HashMap;
import java.util.Map;

public class TransactionHashVO {

    private String txnHash;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getTxnHash() {
        return txnHash;
    }

    public void setTxnHash(String txnHash) {
        this.txnHash = txnHash;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
