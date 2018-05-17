package com.github.ontio.smartcontract;

import com.github.ontio.OntSdk;
import com.github.ontio.smartcontract.nativevm.NativeOntIdTx;
import com.github.ontio.smartcontract.nativevm.OntAssetTx;

public class NativeVm {

    private OntAssetTx ont = null;
    private NativeOntIdTx nativeOntIdTx = null;
    private OntSdk sdk;
    public NativeVm(OntSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get OntAsset Tx
     * @return instance
     */
    public OntAssetTx ont() {
        if(ont == null){
            ont = new OntAssetTx(sdk);
        }
        return ont;
    }
    public NativeOntIdTx ontId(){
        if (nativeOntIdTx == null){
            nativeOntIdTx = new NativeOntIdTx(sdk);
        }
        return nativeOntIdTx;
    }
}
