package com.github.ontio.smartcontract;

import com.github.ontio.OntSdk;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.neovm.Ong;
import com.github.ontio.smartcontract.neovm.Ont;

public class NativeVm {
    private Ont ont = null;
    private Ong ong = null;
    private OntId ontId = null;
    private OntSdk sdk;
    public NativeVm(OntSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get OntAsset Tx
     * @return instance
     */

    public Ont ont() {
        if(ont == null){
            ont = new Ont(sdk);
        }
        return ont;
    }
    public Ong ong() {
        if(ong == null){
            ong = new Ong(sdk);
        }
        return ong;
    }
    public OntId ontId(){
        if (ontId == null){
            ontId = new OntId(sdk);
        }
        return ontId;
    }
}
