
package com.xiaofei.ontologyandroidsdkuse.model;

import java.util.HashMap;
import java.util.Map;

public class OntIdContract {

    private Integer gasPrice;
    private Integer gasLimit;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(Integer gasPrice) {
        this.gasPrice = gasPrice;
    }

    public Integer getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(Integer gasLimit) {
        this.gasLimit = gasLimit;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
