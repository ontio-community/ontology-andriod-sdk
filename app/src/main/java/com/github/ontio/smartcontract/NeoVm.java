package com.github.ontio.smartcontract;

import com.github.ontio.OntSdk;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.neovm.BuildParams;
import com.github.ontio.smartcontract.neovm.ClaimRecord;
import com.github.ontio.smartcontract.neovm.Record;

public class NeoVm {
    private OntId ontIdTx = null;
    private Record recordTx = null;
    private ClaimRecord claimRecordTx = null;

    private OntSdk sdk;
    public NeoVm(OntSdk sdk){
        this.sdk = sdk;
    }

    /**
     * Record
     * @return instance
     */
    public Record record() {
        if(recordTx == null){
            recordTx = new Record(sdk);
        }
        return recordTx;
    }

    public ClaimRecord claimRecord(){
        if (claimRecordTx == null){
            claimRecordTx = new ClaimRecord(sdk);
        }
        return claimRecordTx;
    }

    public Object sendTransaction(String contractAddr, String payer, String password, long gaslimit, long gas, AbiFunction func, boolean preExec) throws Exception {
        byte[] params = BuildParams.serializeAbiFunction(func);
        if (preExec) {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, VmType.NEOVM.value(), null,0, 0);
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            return obj;
        } else {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, VmType.NEOVM.value(), payer,gaslimit, gas);
            sdk.signTx(tx, payer, password);
            boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
            return tx.hash().toHexString();
        }
    }
}
