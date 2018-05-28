package com.github.ontio.smartcontract.neovm;

import com.github.ontio.OntSdk;
import com.github.ontio.crypto.KeyType;

import java.util.LinkedHashMap;

public class Record {
    private OntSdk sdk;
    private String codeAddress = null;


    public Record(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.codeAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return codeAddress;
    }


//    public String sendPut(String addr,String password,String key,String value) throws Exception {
//        if (codeAddress == null) {
//            throw new SDKException(ErrorCode.NullCodeHash);
//        }
//        if (key == null || value == null || key == "" || value == ""){
//            throw new SDKException(ErrorCode.NullKeyOrValue);
//        }
//        addr = addr.replace(Common.didont,"");
//        byte[] did = (Common.didont + addr).getBytes();
//        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
//        byte[] pk = Helper.hexToBytes(info.pubkey);
//        List list = new ArrayList<Object>();
//        list.add("Put".getBytes());
//        List tmp = new ArrayList<Object>();
//        tmp.add(key.getBytes());
//        tmp.add(JSON.toJSONString(constructRecord(value)).getBytes());
//        list.add(tmp);
//        Transaction tx = makeInvokeTransaction(list,info);
//        sdk.signTx(tx, addr, password);
//        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
//        if (b) {
//            return tx.hash().toString();
//        }
//        return null;
//    }
//    public String sendGet(String addr,String password,String key) throws Exception {
//        if (codeAddress == null) {
//            throw new SDKException(ErrorCode.NullCodeHash);
//        }
//        if (key == null || key == ""){
//            throw new SDKException(ErrorCode.NullKey);
//        }
//        byte[] did = (Common.didont + addr).getBytes();
//        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
//        byte[] pk = Helper.hexToBytes(info.pubkey);
//        List list = new ArrayList<Object>();
//        list.add("Get".getBytes());
//        List tmp = new ArrayList<Object>();
//        tmp.add(key.getBytes());
//        list.add(tmp);
//        Transaction tx = makeInvokeTransaction(list,info,0);
//        sdk.signTx(tx, addr, password);
//        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
//        return new String(Helper.hexToBytes((String)obj));
//    }
//
//    public Transaction makeInvokeTransaction(List<Object> list,AccountInfo acctinfo,long gas) throws Exception {
//        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
//        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(codeAddress,null,params, VmType.NEOVM.value(), acctinfo.addressBase58,gas);
//        return tx;
//    }

    private LinkedHashMap<String, Object> constructRecord(String text) {
        LinkedHashMap<String, Object> recordData = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("Algrithem", KeyType.SM2.name());
        data.put("Hash", "");
        data.put("Text", text);
        data.put("Signature", "");

        recordData.put("Data", data);
        recordData.put("CAkey", "");
        recordData.put("SeqNo", "");
        recordData.put("Timestamp", 0);
        return recordData;
    }
}
