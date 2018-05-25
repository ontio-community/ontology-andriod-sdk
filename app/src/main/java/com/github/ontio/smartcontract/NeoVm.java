package com.github.ontio.smartcontract;

import com.github.ontio.OntSdk;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.neovm.ClaimRecordTx;
import com.github.ontio.smartcontract.neovm.RecordTx;

public class NeoVm {
    private OntId ontIdTx = null;
    private RecordTx recordTx = null;
    private ClaimRecordTx claimRecordTx = null;

    private OntSdk sdk;
    public NeoVm(OntSdk sdk){
        this.sdk = sdk;
    }

    /**
     * RecordTx
     * @return instance
     */
    public RecordTx record() {
        if(recordTx == null){
            recordTx = new RecordTx(sdk);
        }
        return recordTx;
    }

    public ClaimRecordTx claimRecord(){
        if (claimRecordTx == null){
            claimRecordTx = new ClaimRecordTx(sdk);
        }
        return claimRecordTx;
    }
}
