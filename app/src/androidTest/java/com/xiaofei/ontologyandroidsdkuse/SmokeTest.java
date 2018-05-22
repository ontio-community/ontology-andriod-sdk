package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.smartcontract.nativevm.NativeOntIdTx;
import com.github.ontio.smartcontract.nativevm.OntAssetTx;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SmokeTest {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;
    private OntAssetTx ontAssetTx;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private Context appContext;
    private NativeOntIdTx ontIdTx;
    String password = "123456";
    Account payerAcc;
    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
//        ontSdk.setRestful("http://192.168.50.73:20334");
//        ontSdk.setRestful("http://139.219.129.55:20334");
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnectMgr();
        ontAssetTx = ontSdk.nativevm().ont();
        ontIdTx = ontSdk.nativevm().ontId();
        payerAcc = ontSdk.getWalletMgr().createAccount(password);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void smoke(){
        assertTrue(2 == 1+1);
    }

    @Test
    public void ontsdkGetInstance(){
        OntSdk ontSdk = OntSdk.getInstance();
        assertNotNull(ontSdk);
        assertSame(ontSdk,this.ontSdk);
    }


    @Test
    public void ConnectMangerTest() throws Exception {
        Block block = ontSdk.getConnectMgr().getBlock(0);

        System.out.print(block);
    }

    @Test
    public void writeWallet() throws IOException {
        walletMgr.writeWallet();
    }

    @Test
    public void createIdentity() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        assertNotNull(identity);
        assertNotNull(identity.ontid);
        assertNotEquals(identity.ontid,"");
    }

    @Test
    public void sendRegisterPreExec() throws Exception {
        Identity identity0 = walletMgr.createIdentity("123456");
        Identity identity = ontIdTx.sendRegisterPreExec(identity0,"123456",payerAcc.address,password,0);
        assertNotNull(identity);
        assertNotNull(identity.ontid);
        assertNotEquals(identity.ontid,"");
    }

    @Test
    public void sendRegister() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        Identity identity1 = ontIdTx.sendRegister(identity,"123456",payerAcc.address,password,0);
        Thread.sleep(6000);
        String string = ontIdTx.sendGetDDO(identity1.ontid);
        assertTrue(string.contains(identity1.ontid));
    }

    @Test
    public void makeRegister() throws Exception {
        Identity identity = walletMgr.createIdentity("aa","123456");

        Transaction tx22 = ontIdTx.makeRegister(identity.ontid,"123456",payerAcc.address,0);
            ontSdk.signTx(tx22,identity.ontid.replace(Common.didont,""),"123456");
            ontSdk.addSign(tx22,payerAcc.address,"123456");
            ontSdk.getConnectMgr().sendRawTransaction(tx22);

        Thread.sleep(6000);
        String string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
    }

    @Test
    public void importIdentity() throws Exception {
        List<Identity> identities = wallet.getIdentities();
        identities.clear();
        walletMgr.writeWallet();
        assertEquals(identities.size(), 0);

        Identity identity = walletMgr.createIdentity("123456");
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,"123456");
        String prikeyStr = account.exportCtrEncryptedPrikey("123456",4096);
        assertTrue(identities.size() == 1);
        identities.clear();
        walletMgr.writeWallet();
        assertTrue(identities.size() == 0);


        String addr = identity.ontid.substring(8);
        walletMgr.importIdentity("aaa",prikeyStr,"123456",addr);
        assertTrue(identities.size() == 1);
        Identity identity1 = identities.get(0);
        assertEquals(identity.ontid,identity1.ontid);
    }

    @Test
    public void createAccount() throws Exception {
        Account account = walletMgr.createAccount("bbb","123456");
        assertNotNull(account);
        assertNotNull(account.address);
        assertNotEquals(account.address,"");
        assertEquals(account.label,"bbb");
        assertEquals(account.mnemonicCodes.length,12);
    }

    @Test
    public void importAccountByMnmenoicCodes() throws Exception {
        //entropy: 67a144559c029099e66c24175a3143a7
        //MnmenoicCodes: guilt any betray day cinnamon erupt often loyal blanket spice extend exact
        //seed: 54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166358fb8b7253b4abbc0799f386d81e472352da1413eaa817638a4a887db03fdf5
        //prik: 54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166
        //address: TA8SrRAVUWSiqNzwzriirwRFn6GC4QeADg
        String mnemonicCodesStr = "guilt any betray day cinnamon erupt often loyal blanket spice extend exact";
        String[] mnemonicCodes = mnemonicCodesStr.split(" ");
        assertEquals(mnemonicCodes.length,12);
        Account account = walletMgr.importAccount("aa",mnemonicCodes,"123456");
        assertNotNull(account);
        assertEquals(account.address,"TA8SrRAVUWSiqNzwzriirwRFn6GC4QeADg");
    }

    @Test
    public void importAccount() throws Exception {
        List<Account> accounts = walletMgr.getAccounts();
        accounts.clear();
        assertEquals(accounts.size(), 0);
        walletMgr.writeWallet();

        Account account = walletMgr.createAccount("123456");
        com.github.ontio.account.Account accountDiff = walletMgr.getAccount(account.address,"123456");
        String prikeyStr = accountDiff.exportCtrEncryptedPrikey("123456",4096);
        assertTrue(accounts.size() == 1);
        accounts.clear();
        assertTrue(accounts.size() == 0);
        walletMgr.writeWallet();

        Account account1 = walletMgr.importAccount("aaa",prikeyStr,"123456",account.address);
        assertTrue(accounts.size() == 1);
        assertEquals(account.address, account1.address);

    }

    @Test
    public void sendUpdateAttribute() throws Exception {
        Identity identity0 = ontSdk.getWalletMgr().createIdentity("123456");
        Identity identity = ontIdTx.sendRegister(identity0,"123456",payerAcc.address,password,0);
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,"123456");
        String prikey = account.exportCtrEncryptedPrikey("123456", 4096);
        Thread.sleep(6000);
        String string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Context", "claimlalala");
        jsonObject.put("Issuer", "issuerlalala");
//        String txnIdPreexec = ontIdTx.sendUpdateAttributePreExec(identity.ontid,"123456",prikey.getBytes(),"Json".getBytes(),jsonObject.toJSONString().getBytes());
//        assertNotNull(txnIdPreexec);
//        assertNotEquals(txnIdPreexec,"");
//        String txnId = ontIdTx.sendUpdateAttribute(identity.ontid,"123456",prikey.getBytes(),"Json".getBytes(), jsonObject.toJSONString().getBytes());
//        assertNotNull(txnId);
//        assertNotEquals(txnId,"");
//        Thread.sleep(6000);
        Map attrsMap = new HashMap<>();
        attrsMap.put("key11","value11");
        String txnId = ontIdTx.sendAddAttributes(identity.ontid,"123456",attrsMap,payerAcc.address,password,0);
        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains("key11"));
//        assertTrue(string.contains("issuerlalala"));

        Map attrsMap2 = new HashMap<>();
        attrsMap.put("key22","value22");
        Transaction tx22 = ontIdTx.makeAddAttributes(identity.ontid,"123456",attrsMap,payerAcc.address,0);
        ontSdk.signTx(tx22,identity.ontid.replace(Common.didont,""),"123456");
        ontSdk.addSign(tx22,payerAcc.address,"123456");
        ontSdk.getConnectMgr().sendRawTransaction(tx22);

        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
        assertTrue(string.contains("key22"));

        String txnId1 = ontIdTx.sendRemoveAttribute(identity.ontid,"123456","key11",payerAcc.address,password,0);
        assertNotNull(txnId1);
        assertNotEquals(txnId1,"");
        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertFalse(string.contains("key11"));

        Transaction tx33 = ontIdTx.makeRemoveAttribute(identity.ontid,"123456","key22",payerAcc.address,0);
        ontSdk.signTx(tx33,identity.ontid.replace(Common.didont,""),"123456");
        ontSdk.addSign(tx33,payerAcc.address,"123456");
        ontSdk.getConnectMgr().sendRawTransaction(tx33);
        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertFalse(string.contains("key22"));
    }


    @Test
    public void getBalance() throws Exception {
//        TA6qWdLo14aEve5azrYWWvMoGPrpczFfeW---1/gEPy/Uz3Eyl/sjoZ8JDymGX6hU/gi1ufUIg6vDURM= rich
//        TA4pSdTKm4hHtQJ8FbrCk9LZn7Uo96wrPC---Vz0CevSaI9/VNLx03XNEQ4Lrnnkkjo5aM5hdCuicsOE= poor1
//        TA5F9QefsyKvn5cH37VnP5snSru5ZCYHHC---OGaD13Sn/q9gIZ8fmOtclMi4yy34qq963wzpidYDX5k= poor2

        JSONObject balanceObj = (JSONObject) connectMgr.getBalance("TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z");
        assertNotNull(balanceObj);
        int ontBalance = balanceObj.getIntValue("ont");
        assertTrue(ontBalance >= 0);

    }
    @Test
    public void sendTransfer() throws Exception {
        final int amount = 1;
//b14757ed---kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=---123123---TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z rich
//4fd1e7fe---6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=---123123---TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft poor
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String poorAddr = "TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft";
        final String poorKey = "6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=";
        final String richPrefixStr = "b14757ed";
        final String poorPrefixStr = "4fd1e7fe";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        final byte[] poorPrefix = Helper.hexToBytes(poorPrefixStr);
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalance = richBalanceObj.getIntValue("ont");
        int poorBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richBalance > 0);
        assertTrue(poorBalance >= 0);

        com.github.ontio.sdk.wallet.Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
        com.github.ontio.sdk.wallet.Account accountPoor = walletMgr.importAccount("poor",poorKey,"123123",poorPrefix);

        String txnId = ontAssetTx.sendTransfer("ont",richAddr,"123123",poorAddr,amount,0);
        assertNotNull(txnId);
        assertNotEquals(txnId,"");

        Thread.sleep(6000);

        Transaction transaction = connectMgr.getTransaction(txnId);
        assertNotNull(transaction);
        assertEquals(transaction.hash().toHexString(),txnId);

        JSONObject richBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalanceAfter = richBalanceObjAfter.getIntValue("ont");
        int poorBalanceAfter = poorBalanceObjAfter.getIntValue("ont");

        assertTrue(richBalanceAfter == richBalance -amount);
        assertTrue(poorBalanceAfter == poorBalance +amount);

        String txnIdback = ontAssetTx.sendTransfer("ont",poorAddr,"123123",richAddr,amount,0);
        assertNotNull(txnIdback);
        assertNotEquals(txnIdback,"");

        Thread.sleep(6000);

        JSONObject richBalanceObjBack = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObjBack = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalanceBack = richBalanceObjBack.getIntValue("ont");
        int poorBalanceBack = poorBalanceObjBack.getIntValue("ont");
        assertEquals(richBalanceBack,richBalance);
        assertEquals(poorBalanceBack,poorBalance);

    }

    @Test
    public void sendTransferFromManyAndBack() throws Exception {
//b14757ed---kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=---123123---TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z rich
//4fd1e7fe---6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=---123123---TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft poor1
//02a147ca---8QOkddIE3WUKz77fvLXFUbJZx6gOWTH3Sdw7Qxn8y/0=---123123---TA6m1gwyz3GyzHSQQRDU2y6biUHjrdFv8H poor2

        final int amount1 = 2;
        final int amount2 = 1;
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String poorAddr1 = "TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft";
        final String poorAddr2 = "TA6m1gwyz3GyzHSQQRDU2y6biUHjrdFv8H";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String poorKey1 = "6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=";
        final String poorKey2 = "8QOkddIE3WUKz77fvLXFUbJZx6gOWTH3Sdw7Qxn8y/0=";
        final String richPrefixStr = "b14757ed";
        final String poor1PrefixStr = "4fd1e7fe";
        final String poor2PrefixStr= "02a147ca";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        final byte[] poor1Prefix = Helper.hexToBytes(poor1PrefixStr);
        final byte[] poor2Prefix = Helper.hexToBytes(poor2PrefixStr);
        JSONObject richOrigObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorOrigObj1 = (JSONObject) connectMgr.getBalance(poorAddr1);
        JSONObject poorOrigObj2 = (JSONObject) connectMgr.getBalance(poorAddr2);
        int richOrig = richOrigObj.getIntValue("ont");
        int poorOrig1 = poorOrigObj1.getIntValue("ont");
        int poorOrig2 = poorOrigObj2.getIntValue("ont");
        assertTrue(richOrig > 0);
        assertTrue(poorOrig1 > 0);
        assertTrue(poorOrig2 >= 0);

        com.github.ontio.sdk.wallet.Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
        com.github.ontio.sdk.wallet.Account accountPoor1 = walletMgr.importAccount("poor1",poorKey1,"123123",poor1Prefix);
        com.github.ontio.sdk.wallet.Account accountPoor2 = walletMgr.importAccount("poor2",poorKey2,"123123",poor2Prefix);


        String txnId =ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,poorAddr1},new String[]{"123123","123123"},poorAddr2,new long[]{amount1,amount2},0);
        assertNotNull(txnId);
        assertNotEquals(txnId,"");

        Thread.sleep(6000);

        JSONObject richAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorAfterObj1 = (JSONObject) connectMgr.getBalance(poorAddr1);
        JSONObject poorAfterObj2 = (JSONObject) connectMgr.getBalance(poorAddr2);
        int richAfter = richAfterObj.getIntValue("ont");
        int poorAfter1 = poorAfterObj1.getIntValue("ont");
        int poorAfter2 = poorAfterObj2.getIntValue("ont");
        assertTrue(richAfter == richOrig - amount1);
        assertTrue(poorAfter1 == poorOrig1 - amount2);
        assertTrue(poorAfter2 == poorOrig2 + amount1 + amount2);

        String txnIdback = ontAssetTx.sendTransferToMany("ont",poorAddr2,"123123",new String[]{richAddr,poorAddr1},new long[]{amount1,amount2},0);
        assertNotNull(txnIdback);
        assertNotEquals(txnIdback,"");

        Thread.sleep(6000);

        JSONObject richBackObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBackObj1 = (JSONObject) connectMgr.getBalance(poorAddr1);
        JSONObject poorBackObj2 = (JSONObject) connectMgr.getBalance(poorAddr2);
        int richBack = richBackObj.getIntValue("ont");
        int poorBack1 = poorBackObj1.getIntValue("ont");
        int poorBack2 = poorBackObj2.getIntValue("ont");
        assertTrue(richBack == richOrig);
        assertTrue(poorBack1 == poorOrig1);
        assertTrue(poorBack2 == poorOrig2);


    }

    @Test
    public void sendOngTransferFromToSelf() throws Exception {
//b14757ed---kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=---123123---TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z rich
        final int amount = 1;
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String richPrefixStr = "b14757ed";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        int richOngApprove = richBalanceObj.getIntValue("ong_appove");
        int richOng = richBalanceObj.getIntValue("ong");
        assertTrue(richOngApprove >= 0);
        assertTrue(richOng >= 0);

        com.github.ontio.sdk.wallet.Account account = walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        String txnId = ontAssetTx.sendOngTransferFrom(richAddr,"123123",richAddr,amount,0);
        assertNotNull(txnId);
        assertNotEquals(txnId,"");

        Thread.sleep(6000);

        JSONObject richBalanceAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        int richOngApproveAfter = richBalanceAfterObj.getIntValue("ong_appove");
        int richOngAfter = richBalanceAfterObj.getIntValue("ong");
        assertTrue(richOngApproveAfter == richOngApprove - amount);
        assertTrue(richOngAfter == richOng + amount);

    }


    @Test
    public void sendOngTransferFromToOther() throws Exception {
//b14757ed---kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=---123123---TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z rich
//4fd1e7fe---6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=---123123---TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft poor
        final int amount = 1;
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String richPrefixStr = "b14757ed";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        final String poorAddr = "TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft";

        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        long richOngApprove = richBalanceObj.getLongValue("ong_appove");
        int poorOng = poorBalanceObj.getIntValue("ong");
        assertTrue(richOngApprove > 0);
        assertTrue(poorOng >= 0);

        com.github.ontio.sdk.wallet.Account account = walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        String txnId = ontAssetTx.sendOngTransferFrom(richAddr,"123123",poorAddr,amount,0);
        assertNotNull(txnId);
        assertNotEquals(txnId,"");

        Thread.sleep(6000);

        JSONObject richBalanceAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceAfterObj = (JSONObject) connectMgr.getBalance(poorAddr);
        long richOngApproveAfter = richBalanceAfterObj.getLongValue("ong_appove");
        int poorOngAfter = poorBalanceAfterObj.getIntValue("ong");
        assertTrue(richOngApproveAfter == richOngApprove - amount);
        assertTrue(poorOngAfter == poorOng + amount);

    }
}