package com.github.ontio.smartcontract.neovm;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;

import java.util.ArrayList;
import java.util.List;

public class ClaimRecordTx {
    private OntSdk sdk;
    private String codeAddress = null;


    public ClaimRecordTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.codeAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return codeAddress;
    }

    public String sendCommit(String ontid,String password,String claimId,long gaslimit, long gas) throws Exception {
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("Commit".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes(claimId));
        tmp.add(did);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info.addressBase58,gaslimit,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendRevoke(String ontid,String password,String claimId,long gaslimit,long gas) throws Exception {
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("Revoke".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes(claimId));
        tmp.add(did);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info.addressBase58,gaslimit,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendGetStatus(String ontid,String password,String claimId) throws Exception {
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("GetStatus".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes(claimId));
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,null,0,0);
        sdk.signTx(tx, addr, password);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        if (obj != null ) {
            return (String) obj;
        }
        return null;
    }
    public Transaction makeInvokeTransaction(List<Object> list,String addr,long gaslimit,long gas) throws Exception {
        byte[] params = sdk.vm().createCodeParamsScript(list);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(codeAddress,null,params, VmType.NEOVM.value(), addr,gaslimit,gas);
        return tx;
    }
}

