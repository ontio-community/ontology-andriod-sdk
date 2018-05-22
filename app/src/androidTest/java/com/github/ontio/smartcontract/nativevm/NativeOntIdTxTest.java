package com.github.ontio.smartcontract.nativevm;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class NativeOntIdTxTest {
    private Context appContext;
    OntSdk ontSdk;
    String password = "111111";
    Account payerAcc;
    String encryptedPrikey = "n+LPk9bkvGax7Ffcx8t4imkPuNR3VPafvxIEw1ZEM18=";
    String ontid = "did:ont:TA5YqmVWNKTaiZfnWd9oAzbnShMmqTxStd";
    String address160 = "01330ea541ae96c637e12a91b35ef5f6604d6d84";

String encryptedPrikey2 = "VbSeJligkwCq/+KPtZgYp/+l97J8SQvVqbUtUd3LdXw=";
    String ontid2 = "did:ont:TA7QFNJtMPGVvZa3AJDYTt332cX7pCvnYS";
    String address1602 = "018cb74a294ac70c09b435056f8b8ac35fc7cb8f";

    @Before
    public void setUp() throws Exception {
        String ip = "http://polaris1.ont.io";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet", Context.MODE_PRIVATE));
        ontSdk.getWalletMgr().importAccount("",encryptedPrikey,password, Address.parse(address160).toBase58());
        payerAcc = ontSdk.getWalletMgr().createAccount(password);
    }

    @Test
    public void sendRegister() throws Exception {
       IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo(password);

//
        System.out.print("encryptedPrikey:" + info.encryptedPrikey);
        //
        System.out.print("address:" + info.addressU160);//
        System.out.print("ontid:" + info.ontid);//
        Identity identity = ontSdk.getWalletMgr().getIdentity(info.ontid);
        Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.pubkey));
    }
    @Test
    public void sendAddPubkey() throws Exception {
        IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo(password);
        Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(ontid,password,info.pubkey,payerAcc.address,0);
        ontSdk.signTx(tx, ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(ontid);
        Assert.assertTrue(ddo.contains(info.pubkey));


        Transaction tx2 = ontSdk.nativevm().ontId().makeRemovePubKey(ontid,password,info.pubkey,payerAcc.address,0);
        ontSdk.signTx(tx2,ontid,password);
        ontSdk.addSign(tx2,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx2);
        Thread.sleep(6000);
        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(ontid);
        Assert.assertFalse(ddo2.contains(info.pubkey));
    }

    @Test
    public void sendAddAttributes() throws Exception {
        Map attrsMap = new HashMap<>();
        attrsMap.put("key1","value1");
        Transaction tx = ontSdk.nativevm().ontId().makeAddAttributes(ontid,password,attrsMap,payerAcc.address,0);
        ontSdk.signTx(tx, ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(ontid);
        Assert.assertTrue(ddo.contains("key1"));



        Transaction tx2= ontSdk.nativevm().ontId().makeRemoveAttribute(ontid,password,"key1",payerAcc.address,0);
        ontSdk.signTx(tx2,ontid,password);
        ontSdk.addSign(tx2,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx2);
        Thread.sleep(6000);

        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(ontid);
        Assert.assertFalse(ddo2.contains("key1"));


    }

    @Test
    public void sendAddRecovery() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,0);
        Thread.sleep(6000);

        AccountInfo info = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(identity.ontid,password,info.addressBase58,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.addressBase58));
    }

    @Test
    public void sendChangeRecovery() throws Exception {

        Identity identity = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,0);
        Thread.sleep(6000);

        AccountInfo info = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(identity.ontid,password,info.addressBase58,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx);
        Thread.sleep(6000);

        AccountInfo info2 = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx2 = ontSdk.nativevm().ontId().makeChangeRecovery(identity.ontid,info2.addressBase58,info.addressBase58,password,0);
        ontSdk.signTx(tx2, identity.ontid,password);
        ontSdk.addSign(tx2,info.addressBase58,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx2);

        Thread.sleep(6000);

        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info2.addressBase58));
        Assert.assertFalse(ddo.contains(info.addressBase58));
    }

}