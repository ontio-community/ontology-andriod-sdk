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
import com.github.ontio.crypto.WIF;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.smartcontract.nativevm.Ong;
import com.github.ontio.smartcontract.nativevm.Ont;
import com.github.ontio.smartcontract.nativevm.OntId;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SmokeTest {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;
    private Ont ont;
    private Ong ong;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private Context appContext;
    private OntId ontIdTx;
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
        connectMgr = ontSdk.getConnect();
        ont = ontSdk.nativevm().ont();
        ong = ontSdk.nativevm().ong();
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
        Block block = ontSdk.getConnect().getBlock(0);

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
        Identity identity = ontIdTx.sendRegisterPreExec(identity0,"123456",payerAcc.address,password,0,0);
        assertNotNull(identity);
        assertNotNull(identity.ontid);
        assertNotEquals(identity.ontid,"");
    }

    @Test
    public void sendRegister() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        Identity identity1 = ontIdTx.sendRegister(identity,"123456",payerAcc.address,password,0,0);
        Thread.sleep(6000);
        String string = ontIdTx.sendGetDDO(identity1.ontid);
        assertTrue(string.contains(identity1.ontid));
    }

    @Test
    public void makeRegister() throws Exception {
        Identity identity = walletMgr.createIdentity("aa","123456");

        Transaction tx22 = ontIdTx.makeRegister(identity.ontid,"123456",payerAcc.address,0,0);
            ontSdk.signTx(tx22,identity.ontid.replace(Common.didont,""),"123456");
            ontSdk.addSign(tx22,payerAcc.address,"123456");
            ontSdk.getConnect().sendRawTransaction(tx22);

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
    public void decryptMnmenoicCodes() throws Exception {
        //TA9nHh56y8tXypxZ8S4vmhycXfBJ2Bzaaa
        //endless online program roof claim parent spider resemble spawn pull repair cube
        //b9fba414c733faa9c226ce7dfce100f72160ed89a62816cbc60cd2d1a7a712e2a387305c3f05051a273db5bade96ef38000570af5ac44611f1d185ef026815fea826dd083f2f911a289a7947326e53
        String address = "TA4eqgfDVvCjfcmVNBwQkrBDpLTtB89GhU";
        String encryptedMnmenoicCodesHexStr = "b9fba414c733faa9c226ce7dfce100f72160ed89a62816cbc60cd2d1a7a712e2a387305c3f05051a273db5bade96ef38000570af5ac44611f1d185ef026815fea826dd083f2f911a289a7947326e53";
        String[] mnmenoicCodesArray = walletMgr.decryptMnemonicCodesStr(encryptedMnmenoicCodesHexStr,"123456");
        assertEquals(mnmenoicCodesArray[0],"endless");
        assertEquals(mnmenoicCodesArray[11],"cube");
    }

    @Test
    public void exportPrikey() throws Exception {
        Account account = walletMgr.createAccount("aaa","123456");
        String prikey = walletMgr.exportPrikey(account,"123456");
        assertNotNull(prikey);
        Account account1 = walletMgr.importAccount("www",prikey,"123456");
        assertEquals(account1.address,account.address);

    }

    @Test
    public void prikeyToWIF() throws Exception {
        String prikey = "54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166";
        String wif = WIF.encodePrivateKeyToWIF(Helper.hexToBytes(prikey));
        assertNotNull(wif);
    }

    @Test
    public void wifToPrikey() throws Exception {
        byte[] prikey = WIF.decodePrivateKeyFromWIF("5JTTXdfPVtGMNybRgyFz7gUD3BpRbCypn6D8zpEPKobGJvhX2jX");
        String prikeyStr = Helper.toHexString(prikey);
        assertNotNull(prikeyStr);
        assertEquals(prikeyStr,"54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166");
    }

    @Test
    public void importAccountByPrikey() throws Exception {
        String prikey = "54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166";
        Account account = walletMgr.importAccount("aa",prikey,"123456");
        assertNotNull(account.address,"TA8SrRAVUWSiqNzwzriirwRFn6GC4QeADg");
    }

    @Test
    public void importAccountByMnmenoicCodes() throws Exception {
        //entropy: 67a144559c029099e66c24175a3143a7
        //MnmenoicCodes: guilt any betray day cinnamon erupt often loyal blanket spice extend exact
        //seed: 54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166358fb8b7253b4abbc0799f386d81e472352da1413eaa817638a4a887db03fdf5
        //prikey: 54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166
        //wif: 5JTTXdfPVtGMNybRgyFz7gUD3BpRbCypn6D8zpEPKobGJvhX2jX
        //address: TA8SrRAVUWSiqNzwzriirwRFn6GC4QeADg
        //password: 123456
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
        Identity identity = ontIdTx.sendRegister(identity0,"123456",payerAcc.address,password,0,0);
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
        String txnId = ontIdTx.sendAddAttributes(identity.ontid,"123456",attrsMap,payerAcc.address,password,0,0);
        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains("key11"));
//        assertTrue(string.contains("issuerlalala"));

        Map attrsMap2 = new HashMap<>();
        attrsMap.put("key22","value22");
        Transaction tx22 = ontIdTx.makeAddAttributes(identity.ontid,"123456",attrsMap,payerAcc.address,0,0);
        ontSdk.signTx(tx22,identity.ontid.replace(Common.didont,""),"123456");
        ontSdk.addSign(tx22,payerAcc.address,"123456");
        connectMgr.sendRawTransaction(tx22);

        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
        assertTrue(string.contains("key22"));

        String txnId1 = ontIdTx.sendRemoveAttribute(identity.ontid,"123456","key11",payerAcc.address,password,0,0);
        assertNotNull(txnId1);
        assertNotEquals(txnId1,"");
        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertFalse(string.contains("key11"));

        Transaction tx33 = ontIdTx.makeRemoveAttribute(identity.ontid,"123456","key22",payerAcc.address,0,0);
        ontSdk.signTx(tx33,identity.ontid.replace(Common.didont,""),"123456");
        ontSdk.addSign(tx33,payerAcc.address,"123456");
        connectMgr.sendRawTransaction(tx33);
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
    public void sendTransferOnt() throws Exception {
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


        Transaction transactionR2P = ont.makeTransfer(richAddr,"123123",poorAddr,1,"TA4pCAb4zUifHyxSx32dZRjTrnXtxEWKZr",30000,0);
        assertNotNull(transactionR2P);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,"123123");
        assertNotNull(transactionR2P);
        boolean isSuccess = connectMgr.sendRawTransaction(transactionR2P);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        String transactionR2PStr = transactionR2P.hash().toHexString();
        transactionR2P = connectMgr.getTransaction(transactionR2PStr);
        transactionR2PStr =transactionR2P.hash().toHexString();
        assertNotNull(transactionR2P);
        assertNotEquals(transactionR2PStr,"");


        JSONObject richBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalanceAfter = richBalanceObjAfter.getIntValue("ont");
        int poorBalanceAfter = poorBalanceObjAfter.getIntValue("ont");

        assertTrue(richBalanceAfter == richBalance -amount);
        assertTrue(poorBalanceAfter == poorBalance +amount);

        String txnIdback = ont.sendTransfer(poorAddr,"123123",richAddr,amount,payerAcc.address,"123456",0,0);

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
    public void claimOng() throws Exception {
//b14757ed---kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=---123123---TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z rich
        final int amount = 1;
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String richPrefixStr = "b14757ed";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        int richOng = richBalanceObj.getIntValue("ong");
        String richOngApproveStr = ong.unclaimOng(richAddr);
        Integer richOngApprove = Integer.parseInt(richOngApproveStr);
        assertTrue(richOngApprove > 0);
        assertTrue(richOng >= 0);

        com.github.ontio.sdk.wallet.Account account = walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        Transaction transactionClaimOng = ong.makeClaimOng(richAddr,"123123",richAddr,amount,payerAcc.address,30000,0);
        assertNotNull(transactionClaimOng);
        transactionClaimOng = ontSdk.signTx(transactionClaimOng,richAddr,"123123");
        assertNotNull(transactionClaimOng);
        boolean isSuccess = connectMgr.sendRawTransaction(transactionClaimOng);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        JSONObject richBalanceAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        String richOngApproveAfterStr = ong.unclaimOng(richAddr);
        Integer richOngApproveAfter = Integer.parseInt(richOngApproveAfterStr);
        int richOngAfter = richBalanceAfterObj.getIntValue("ong");
        assertTrue(richOngApproveAfter == richOngApprove - amount);
        assertTrue(richOngAfter == richOng + amount);

    }
}