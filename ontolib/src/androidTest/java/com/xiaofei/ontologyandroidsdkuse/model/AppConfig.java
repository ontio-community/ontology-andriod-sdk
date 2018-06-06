
package com.xiaofei.ontologyandroidsdkuse.model;

import java.util.HashMap;
import java.util.Map;

public class AppConfig {

    private OntIdContract ontIdContract;
    private Integer loginTimeout;
    private Integer fee;
    private String ontPassAddr;
    private AssetContract assetContract;
    private String testnetAddr;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public OntIdContract getOntIdContract() {
        return ontIdContract;
    }

    public void setOntIdContract(OntIdContract ontIdContract) {
        this.ontIdContract = ontIdContract;
    }

    public Integer getLoginTimeout() {
        return loginTimeout;
    }

    public void setLoginTimeout(Integer loginTimeout) {
        this.loginTimeout = loginTimeout;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getOntPassAddr() {
        return ontPassAddr;
    }

    public void setOntPassAddr(String ontPassAddr) {
        this.ontPassAddr = ontPassAddr;
    }

    public AssetContract getAssetContract() {
        return assetContract;
    }

    public void setAssetContract(AssetContract assetContract) {
        this.assetContract = assetContract;
    }

    public String getTestnetAddr() {
        return testnetAddr;
    }

    public void setTestnetAddr(String testnetAddr) {
        this.testnetAddr = testnetAddr;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
