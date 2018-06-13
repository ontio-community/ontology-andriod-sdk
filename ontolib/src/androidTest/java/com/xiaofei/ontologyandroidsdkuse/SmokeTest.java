package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.MnemonicCode;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.smartcontract.nativevm.Ong;
import com.github.ontio.smartcontract.nativevm.Ont;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionBodyVO;
import com.xiaofei.ontologyandroidsdkuse.service.OntoService;
import com.xiaofei.ontologyandroidsdkuse.service.OntoServiceApi;
import com.xiaofei.ontologyandroidsdkuse.service.OntopassService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;
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
    private OntId ontId;
    private String payAddr;
    private String payPassword;
    private long gasLimit;
    private long gasPrice;
    private Retrofit retrofit;
    private OntoServiceApi ontoServiceApi;
    private OntoService ontoService;
    private OntopassService ontopassService;
    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        ontopassService = new OntopassService();
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnect();
        ont = ontSdk.nativevm().ont();
        ong = ontSdk.nativevm().ong();
        ontId = ontSdk.nativevm().ontId();
        payAddr="AWc6N2Yawk12Jt14F7sjGGos4nFc8UztVe";
        payPassword = "passwordtest";
        gasLimit = 30000;
        gasPrice = 0;
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
    public void createIdentity() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        assertNotNull(identity);
        assertNotNull(identity.ontid);
        assertNotEquals(identity.ontid,"");
    }

    @Test
    public void makeRegister() throws Exception {
        Identity identity = walletMgr.createIdentity("aa","123456");
        byte[] salt = identity.controls.get(0).getSalt();

        Transaction transaction = ontId.makeRegister(identity.ontid,"123456",salt,payAddr,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,identity.ontid.replace(Common.didont,""),"123456",salt);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OwnerOntId",identity.ontid);
        jsonObject.put("TxnStr",transaction.toHexString());
        JSONObject jsonObjectResult = ontopassService.ontidRegiste(jsonObject);
        String devicecode = jsonObjectResult.getString("DeviceCode");
        assertNotEquals(devicecode,"");

        Thread.sleep(7000);

        String string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
    }

    @Test
    public void importIdentity() throws Exception {
        List<Identity> identities = wallet.getIdentities();
        identities.clear();
        walletMgr.writeWallet();
        assertEquals(identities.size(), 0);

        Identity identity = walletMgr.createIdentity("123456");
        byte[] salt = identity.controls.get(0).getSalt();
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,"123456",salt);
        String prikeyStr = account.exportCtrEncryptedPrikey("123456",4096);
        assertTrue(identities.size() == 1);
        identities.clear();
        walletMgr.writeWallet();
        assertTrue(identities.size() == 0);


        String addr = identity.ontid.substring(8);
        walletMgr.importIdentity("aaa",prikeyStr,"123456",salt,addr);
        assertTrue(identities.size() == 1);
        Identity identity1 = identities.get(0);
        assertEquals(identity.ontid,identity1.ontid);
    }

    @Test
    public void createAccount() throws Exception {
        String mnsStr = MnemonicCode.generateMnemonicCodesStr().toString();
        byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStr(mnsStr);
        String prikeyStr = Helper.toHexString(prikey);
        String password = "123456";
        Account account = walletMgr.createAccountFromPriKey("bbb",password,prikeyStr);
        String encryptedMnsStr = MnemonicCode.encryptMnemonicCodesStr(mnsStr,password,account.address);
        assertNotNull(account);
        assertNotNull(account.address);
        assertNotEquals(account.address,"");
        assertEquals(account.label,"bbb");

        String mnsStrNew = MnemonicCode.decryptMnemonicCodesStr(encryptedMnsStr,password,account.address);
        assertEquals(mnsStrNew,mnsStr);
    }

    @Test
    public void encryptMnemonicCodesStrCase() throws Exception {
        String address = "TA6A9RDSbrVxfyYvRqg21fSNpYDZHZfge7";
        String mnsStr = "doll remember harbor resource desert curious fatigue nature arrest fix nation rhythm";
        String encryptedMnsStrOrig = "XlXmqnJfnbjUT+kHyHHRXszBXUiW1VtQpXdVcj4zHVQ430QtGDPMk/gbnVyZkXjYrE2+sIQbcVGxRadrWlufLCOsz7BdOIcZm/Ikn/WnGN0Ggy9/";
        String prikeyStr = "2ab720ff80fcdd31a769925476c26120a879e235182594fbb57b67c0743558d7";
        String encryptedMnsStr = MnemonicCode.encryptMnemonicCodesStr(mnsStr,"123456",address);
        assertEquals(encryptedMnsStr,encryptedMnsStrOrig);
    }

    @Test
    public void prikeyToWIF() throws Exception {
        String prikeyStrOrig = "e467a2a9c9f56b012c71cf2270df42843a9d7ff181934068b4a62bcdd570e8be";
        String wifStrOrig = "L4shZ7B4NFQw2eqKncuUViJdFRq6uk1QUb6HjiuedxN4Q2CaRQKW";
        com.github.ontio.account.Account acct = new com.github.ontio.account.Account(Helper.hexToBytes(prikeyStrOrig), ontSdk.defaultSignScheme);
        String wif1 = acct.exportWif();
        assertNotNull(wif1);
        assertEquals(wif1,wifStrOrig);
    }

    @Test
    public void wifToPrikey() throws Exception {
        String prikeyStrOrig = "e467a2a9c9f56b012c71cf2270df42843a9d7ff181934068b4a62bcdd570e8be";
        String wifStrOrig = "L4shZ7B4NFQw2eqKncuUViJdFRq6uk1QUb6HjiuedxN4Q2CaRQKW";
        byte[] prikey2  = com.github.ontio.account.Account.getPrivateKeyFromWIF(wifStrOrig);
        String prikeyStr2 = Helper.toHexString(prikey2);
        assertNotNull(prikeyStr2);
        assertEquals(prikeyStr2,prikeyStrOrig);
    }

    @Test
    public void importAccountByPrikey() throws Exception {
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
        String prikey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        Account account = walletMgr.createAccountFromPriKey("aa","123456",prikey);
        assertEquals(account.address,"AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW");
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

//        economy utility unlock library awful proof episode where skirt autumn toilet prison
//        87ba38545e2b5392b2d9356d36927caf969113f62a9eded366a0b8035441ea8d
//        ASLN3uW6fsHc7hStfE2XBnMqb5MQJigLK9
        String mnemonicCodesStr = "economy utility unlock library awful proof episode where skirt autumn toilet prison";
        String[] mnemonicCodes = mnemonicCodesStr.split(" ");
        assertEquals(mnemonicCodes.length,12);
        byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStr(mnemonicCodesStr);
        String prikeyHexStr = Helper.toHexString(prikey);
        String prikeyHexStrOrig = "87ba38545e2b5392b2d9356d36927caf969113f62a9eded366a0b8035441ea8d";
        assertEquals(prikeyHexStrOrig,prikeyHexStr);
        Account account = walletMgr.createAccountFromPriKey("123456",prikeyHexStr);
        assertNotNull(account);
        assertEquals(account.address,"ASLN3uW6fsHc7hStfE2XBnMqb5MQJigLK9");
    }

    @Test
    public void importAccountByKeystore() throws Exception {


        String keystore = "{\"scrypt\":{\"dkLen\":64,\"n\":4096,\"p\":8,\"r\":8},\"prefix\":\"0d1d4d73\",\"key\":\"6aoszVlHicmvbvyU5L1Ehu0Lm2hmgSyCa3HfsFgSqnM=\",\"type\":\"A\",\"algorithm\":\"ECDSA\",\"parameters\":{\"curve\":\"P-256\"},\"label\":\"巨款\"}";
        String password = "111111";
        String addressOrig = "TA9fnuAZyrsZtCJoRBQUvGiDAG4ufgUf3t";
        JSONObject jsonObject = JSON.parseObject(keystore);
        String prefixHexStr = jsonObject.getString("prefix");
        byte[] prefix = Helper.hexToBytes(prefixHexStr);
        final String encryptedPrikey = jsonObject.getString("key");
        Account account = walletMgr.importAccount("",encryptedPrikey,password,addressOrig,null);

        assertEquals(account.address,addressOrig);

    }

    @Test
    public void importAccount() throws Exception {
        List<Account> accounts = wallet.getAccounts();
        int sizeOrig = accounts.size();
        String password = "123456";
        Account account = walletMgr.createAccount(password);
        int sizeNew = accounts.size();
        assertTrue(sizeNew == sizeOrig + 1);
    }

    @Test
    public void writeWallet() throws Exception {
        walletMgr.createAccount("123456");
        walletMgr.writeWallet();
    }

    @Test
    public void openWallet(){
        int size = wallet.getAccounts().size();
        assertTrue(size > 0);
    }

    @Test
    public void sendAddRemoveIdentityAttribute() throws Exception {
        Identity identity = walletMgr.createIdentity("aa","123456");
        byte[] salt = identity.controls.get(0).getSalt();

        Transaction transaction = ontId.makeRegister(identity.ontid,"123456",salt,payAddr,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,identity.ontid.replace(Common.didont,""),"123456",salt);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("OwnerOntId",identity.ontid);
        jsonObject.put("TxnStr",transaction.toHexString());
        JSONObject jsonObjectResult = ontopassService.ontidRegiste(jsonObject);
        String devicecode = jsonObjectResult.getString("DeviceCode");
        assertNotEquals(devicecode,"");

        Thread.sleep(7000);

        String string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));

        Attribute[] attributes = new Attribute[]{new Attribute("lalala".getBytes(),"String".getBytes(),"hahaha".getBytes())};
        Transaction transactionAdd = ontId.makeAddAttributes(identity.ontid,"123456",salt,attributes,payAddr,gasLimit,gasPrice);
        transactionAdd = ontSdk.signTx(transactionAdd,identity.ontid.replace(Common.didont,""),"123456",salt);
        JSONObject jsonObjectAdd = new JSONObject();
        jsonObjectAdd.put("OwnerOntId",identity.ontid);
        jsonObjectAdd.put("DeviceCode",devicecode);
        jsonObjectAdd.put("TxnStr",transactionAdd.toHexString());
        jsonObjectAdd.put("ClaimId","");
        ontopassService.ddoUpdate(jsonObjectAdd);

        Thread.sleep(7000);
        System.out.println(ontSdk.getConnect().getTransactionJson(transactionAdd.hash().toHexString()));
        string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
        assertTrue(string.contains("lalala"));
        assertTrue(string.contains("hahaha"));
    }


    @Test
    public void getBalance() throws Exception {
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
//        AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn 4671d05f1aa520066457efa62a0cbba012dda64e7d5c0555c2a6b29407713fce poor

        JSONObject balanceObj = (JSONObject) connectMgr.getBalance("AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW");
        assertNotNull(balanceObj);
        long ontBalance = balanceObj.getLongValue("ont");
        long ongBalance = balanceObj.getLongValue("ong");
        assertTrue(ontBalance >= 0);
        assertTrue(ongBalance >= 0);

    }
    @Test
    public void sendTransferOnt() throws Exception {
        final int amount = 1;
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
//        AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn 4671d05f1aa520066457efa62a0cbba012dda64e7d5c0555c2a6b29407713fce poor
        final String richAddr = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        final String richKey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        final String richPassword = "123456";
        final String poorAddr = "AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richOntBalance = richBalanceObj.getIntValue("ont");
        int poorOntBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richOntBalance > 0);
        assertTrue(poorOntBalance >= 0);

        Account accountRich = walletMgr.createAccountFromPriKey(richPassword, richKey);
        byte[] saltRich = accountRich.getSalt();
        //walletMgr.writeWallet();


        Transaction transactionR2P = ont.makeTransfer(richAddr,poorAddr,1,payAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,richPassword,saltRich);
        String transactionBodyStr = transactionR2P.toHexString();
        TransactionBodyVO transactionBodyVO = new TransactionBodyVO();
        transactionBodyVO.setTxnStr(transactionBodyStr);
        transactionBodyVO.setAddress(accountRich.address);
        ontopassService.assetTransfer(transactionBodyVO);

        Thread.sleep(7000);

        //get transaction from chain
        //get smartcode event from chain

        JSONObject richOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        long richOntBalanceAfter = richOntBalanceObjAfter.getLongValue("ont");
        long poorOntBalanceAfter = poorOntBalanceObjAfter.getLongValue("ont");

        assertTrue(richOntBalanceAfter == richOntBalance -amount);
        assertTrue(poorOntBalanceAfter == poorOntBalance +amount);

    }

    @Test
    public void sendTransferOntWithSelfPay() throws Exception {
        final int amount = 1;
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
//        AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn 4671d05f1aa520066457efa62a0cbba012dda64e7d5c0555c2a6b29407713fce poor
        final String richAddr = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        final String richKey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        final String richPassword = "123456";
        final String poorAddr = "AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richOntBalance = richBalanceObj.getIntValue("ont");
        int poorOntBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richOntBalance > 0);
        assertTrue(poorOntBalance >= 0);

        Account accountRich = walletMgr.createAccountFromPriKey(richPassword, richKey);
        byte[] saltRich = accountRich.getSalt();

        Transaction transactionR2P = ont.makeTransfer(richAddr,poorAddr,1,richAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,richPassword,saltRich);
        String transactionBodyStr = transactionR2P.toHexString();
        boolean isSuccess = connectMgr.sendRawTransaction(transactionBodyStr);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        JSONObject richOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        long richOntBalanceAfter = richOntBalanceObjAfter.getLongValue("ont");
        long poorOntBalanceAfter = poorOntBalanceObjAfter.getLongValue("ont");

        assertTrue(richOntBalanceAfter == richOntBalance -amount);
        assertTrue(poorOntBalanceAfter == poorOntBalance +amount);

    }

    @Test
    public void sendTransferOng() throws Exception {
        final int amount = 1;
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
//        AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn 4671d05f1aa520066457efa62a0cbba012dda64e7d5c0555c2a6b29407713fce poor
        final String richAddr = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        final String richKey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        final String richPassword = "123456";
        final String poorAddr = "AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richOngBalance = richBalanceObj.getIntValue("ong");
        int poorOngBalance = poorBalanceObj.getIntValue("ong");
        assertTrue(richOngBalance > 0);
        assertTrue(poorOngBalance >= 0);

        Account accountRich = walletMgr.createAccountFromPriKey(richPassword, richKey);
        byte[] salt = accountRich.getSalt();

        Transaction transactionR2P = ong.makeTransfer(richAddr,poorAddr,1,payAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,richPassword,salt);
        String transactionBodyStr = transactionR2P.toHexString();
        TransactionBodyVO transactionBodyVO = new TransactionBodyVO();
        transactionBodyVO.setTxnStr(transactionBodyStr);
        transactionBodyVO.setAddress(richAddr);
        ontopassService.assetTransfer(transactionBodyVO);

        Thread.sleep(7000);

        //get transaction from chain
        //get smartcode event from chain

        JSONObject richOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        long richOngBalanceAfter = richOntBalanceObjAfter.getLongValue("ong");
        long poorOngBalanceAfter = poorOntBalanceObjAfter.getLongValue("ong");

        assertTrue(richOngBalanceAfter == richOngBalance -amount);
        assertTrue(poorOngBalanceAfter == poorOngBalance +amount);

    }

    @Test
    public void sendTransferOngWithSelfPay() throws Exception {
        final int amount = 1;
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
//        AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn 4671d05f1aa520066457efa62a0cbba012dda64e7d5c0555c2a6b29407713fce poor
        final String richAddr = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        final String richKey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        final String richPassword = "123456";
        final String poorAddr = "AGRVFoguJnKa1Uu9gBMGCtvyoFfZhwTCVn";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richOngBalance = richBalanceObj.getIntValue("ong");
        int poorOngBalance = poorBalanceObj.getIntValue("ong");
        assertTrue(richOngBalance > 0);
        assertTrue(poorOngBalance >= 0);

        Account accountRich = walletMgr.createAccountFromPriKey(richPassword, richKey);
        byte[] salt = accountRich.getSalt();

        Transaction transactionR2P = ong.makeTransfer(richAddr,poorAddr,1,richAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,richPassword,salt);
        String transactionBodyStr = transactionR2P.toHexString();
        boolean isSuccess = connectMgr.sendRawTransaction(transactionBodyStr);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        JSONObject richOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorOntBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        long richOngBalanceAfter = richOntBalanceObjAfter.getLongValue("ong");
        long poorOngBalanceAfter = poorOntBalanceObjAfter.getLongValue("ong");

        assertTrue(richOngBalanceAfter == richOngBalance -amount);
        assertTrue(poorOngBalanceAfter == poorOngBalance +amount);

    }

    @Test
    public void getUnclaimOng() throws Exception {
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
        final String address = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        long unclaimOng = ong.unclaimOng(address);
        assertTrue(unclaimOng >= 0);
    }

    @Test
    public void claimOng() throws Exception {
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
        final int amount = 1;
        final String richAddr = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        final String richKey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        final String richPassword = "123456";
        long richOngApprove = ong.unclaimOng(richAddr);
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        long richOng = richBalanceObj.getLongValue("ong");
        assertTrue(richOngApprove > 0);
        assertTrue(richOng >= 0);

        Account account = walletMgr.createAccountFromPriKey(richPassword, richKey);

        Transaction transactionClaimOng = ong.makeClaimOng(richAddr,richAddr,amount,payAddr,gasLimit,gasPrice);
        transactionClaimOng = ontSdk.signTx(transactionClaimOng,richAddr,richPassword,account.getSalt());
        String transactionBodyStr = transactionClaimOng.toHexString();
        TransactionBodyVO transactionBodyVO = new TransactionBodyVO();
        transactionBodyVO.setTxnStr(transactionBodyStr);
        transactionBodyVO.setAddress(richAddr);
        ontopassService.assetTransfer(transactionBodyVO);

        Thread.sleep(7000);

        long richOngApproveAfter = ong.unclaimOng(richAddr);
        JSONObject richBalanceAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        long richOngAfter = richBalanceAfterObj.getLongValue("ong");
        assertTrue(richOngApproveAfter == richOngApprove - amount);
        assertTrue(richOngAfter == richOng + amount);
    }

    @Test
    public void claimOngWithSelfPay() throws Exception {
//        AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW 2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087 rich
        final int amount = 1;
        final String richAddr = "AUYkVYChHVmzFw6PKuZ9FKxX6LdTRvKJkW";
        final String richKey = "2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087";
        final String richPassword = "123456";
        long richOngApprove = ong.unclaimOng(richAddr);
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        long richOng = richBalanceObj.getLongValue("ong");
        assertTrue(richOngApprove > 0);
        assertTrue(richOng >= 0);

        Account account = walletMgr.createAccountFromPriKey(richPassword, richKey);
        byte[] salt = account.getSalt();

        Transaction transactionClaimOng = ong.makeClaimOng(richAddr,richAddr,amount,richAddr,gasLimit,gasPrice);
        transactionClaimOng = ontSdk.signTx(transactionClaimOng,richAddr,richPassword,salt);
        String transactionBodyStr = transactionClaimOng.toHexString();
        boolean isSuccess = connectMgr.sendRawTransaction(transactionBodyStr);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        long richOngApproveAfter = ong.unclaimOng(richAddr);
        JSONObject richBalanceAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        long richOngAfter = richBalanceAfterObj.getLongValue("ong");
        assertTrue(richOngApproveAfter == richOngApprove - amount);
        assertTrue(richOngAfter == richOng + amount);
    }
}