package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.common.WalletQR;
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
import java.util.Map;

import retrofit2.Retrofit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SmokeTest2 {
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
        ontopassService = new OntopassService("https://app.ont.io/S1/");
//        ontopassService = new OntopassService("http://localhost:9099/");
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnect();
        ont = ontSdk.nativevm().ont();
        ong = ontSdk.nativevm().ong();
        ontId = ontSdk.nativevm().ontId();
        payAddr="AbG3ZgFrMK6fqwXWR1WkQ1d1EYVunCwknu";
        //payPassword = "passwordtest";
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
        List<Identity> identities = wallet.getIdentities();
        int sizeOrig = identities.size();
        Identity identity = walletMgr.createIdentity("123456");
        assertNotNull(identity);
        assertNotNull(identity.ontid);
        int sizeNew = identities.size();
        assertTrue(sizeNew == sizeOrig + 1);
    }

    @Test
    public void makeRegister() throws Exception {
        String label = "aa";
        String password = "123456";
        Identity identity = walletMgr.createIdentity(label,password);
        String address = identity.ontid.replace(Common.didont,"");
        byte[] salt = identity.controls.get(0).getSalt();

        Transaction transaction = ontId.makeRegister(identity.ontid,password,salt,payAddr,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,address,password,salt);
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
    public void makeRegisterWithSelfPay() throws Exception {
        String label = "aa";
        String password = "123456";
        Identity identity = walletMgr.createIdentity(label,password);
        String address = identity.ontid.replace(Common.didont,"");
        byte[] salt = identity.controls.get(0).getSalt();

        Transaction transaction = ontId.makeRegister(identity.ontid,password,salt,address,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,address,password,salt);
        String transactionBodyStr = transaction.toHexString();
        boolean isSuccess = connectMgr.sendRawTransaction(transactionBodyStr);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        String string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
    }

    @Test
    public void sendAddRemoveIdentityAttribute() throws Exception {
        String label = "aa";
        String password = "123456";
        Identity identity = walletMgr.createIdentity(label,password);
        String address = identity.ontid.replace(Common.didont,"");
        byte[] salt = identity.controls.get(0).getSalt();

        Transaction transaction = ontId.makeRegister(identity.ontid,password,salt,payAddr,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,address,password,salt);
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
        Transaction transactionAdd = ontId.makeAddAttributes(identity.ontid,password,salt,attributes,payAddr,gasLimit,gasPrice);
        transactionAdd = ontSdk.signTx(transactionAdd,address,password,salt);
        JSONObject jsonObjectAdd = new JSONObject();
        jsonObjectAdd.put("OwnerOntId",identity.ontid);
        jsonObjectAdd.put("DeviceCode",devicecode);
        jsonObjectAdd.put("TxnStr",transactionAdd.toHexString());
        jsonObjectAdd.put("ClaimId","");
        ontopassService.ddoUpdate(jsonObjectAdd);

        Thread.sleep(7000);

        string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
        assertTrue(string.contains("lalala"));
        assertTrue(string.contains("hahaha"));
    }

    @Test
    public void sendAddRemoveIdentityAttributeWithSelfPay() throws Exception {
        String label = "aa";
        String password = "123456";
        Identity identity = walletMgr.createIdentity(label,password);
        String address = identity.ontid.replace(Common.didont,"");
        byte[] salt = identity.controls.get(0).getSalt();

        Transaction transaction = ontId.makeRegister(identity.ontid,password,salt,address,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,address,password,salt);
        String transactionBodyStr = transaction.toHexString();
        boolean isSuccess = connectMgr.sendRawTransaction(transactionBodyStr);
        assertTrue(isSuccess);

        Thread.sleep(6000);

        String string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));

        Attribute[] attributes = new Attribute[]{new Attribute("lalala".getBytes(),"String".getBytes(),"hahaha".getBytes())};
        Transaction transactionAdd = ontId.makeAddAttributes(identity.ontid,password,salt,attributes,address,gasLimit,gasPrice);
        transactionAdd = ontSdk.signTx(transactionAdd,address,password,salt);
        String transactionAddBodyStr = transactionAdd.toHexString();
        boolean isAddSuccess = connectMgr.sendRawTransaction(transactionAddBodyStr);
        assertTrue(isAddSuccess);

        Thread.sleep(6000);

        string = ontId.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));
        assertTrue(string.contains("lalala"));
        assertTrue(string.contains("hahaha"));
    }

    @Test
    public void importIdentity() throws Exception {
        String password = "123456";
        Identity identity = walletMgr.createIdentity(password);
        Map map = WalletQR.exportIdentityQRCode(wallet, identity);
        wallet.getIdentities().clear();
        String encryptedKey = (String) map.get("key");
        String address = (String) map.get("address");
        String saltStr = (String) map.get("salt");
        String label = (String) map.get("label");
        byte[] salt = Base64.decode(saltStr,Base64.NO_WRAP);

        Identity identityNew = walletMgr.importIdentity(label, encryptedKey, password, salt, address);
        assertEquals(identityNew.ontid,identity.ontid);
        assertEquals(identityNew.label,identity.label);
    }

    @Test
    public void createAccount() throws Exception {
        String mnsStr = MnemonicCode.generateMnemonicCodesStr().toString();
        byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(mnsStr);
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
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
        String prikey = "59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc";
        Account account = walletMgr.createAccountFromPriKey("aa","123456",prikey);
        assertEquals(account.address,"ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG");
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
        byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStrBip44(mnemonicCodesStr);
        String prikeyHexStr = Helper.toHexString(prikey);
        String prikeyHexStrOrig = "87ba38545e2b5392b2d9356d36927caf969113f62a9eded366a0b8035441ea8d";
        assertEquals(prikeyHexStrOrig,prikeyHexStr);
        Account account = walletMgr.createAccountFromPriKey("123456",prikeyHexStr);
        assertNotNull(account);
        assertEquals(account.address,"ASLN3uW6fsHc7hStfE2XBnMqb5MQJigLK9");
    }

    @Test
    public void importAccountByKeystore() throws Exception {
        String password = "123456";
        Account account = walletMgr.createAccount(password);
        Map map = WalletQR.exportAccountQRCode(wallet, account);
        wallet.getAccounts().clear();
        String encryptedKey = (String) map.get("key");
        String address = (String) map.get("address");
        String saltStr = (String) map.get("salt");
        String label = (String) map.get("label");
        byte[] salt = Base64.decode(saltStr,Base64.NO_WRAP);

        Account accountNew = walletMgr.importAccount(label,encryptedKey,password,address,salt);
        assertEquals(account.address,accountNew.address);
        assertEquals(account.label,accountNew.label);
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
        walletMgr.createIdentity("123456");
        walletMgr.writeWallet();
    }

    @Test
    public void openWallet(){
        int sizeAccounts = wallet.getAccounts().size();
        int sizeIdentities = wallet.getIdentities().size();
        assertTrue(sizeAccounts > 0);
        assertTrue(sizeIdentities > 0);
    }

    @Test
    public void getBalance() throws Exception {
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
//        AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe 6da9f512db2991bcfd963d9073b0d6541a3f9dff139b7b0959f79778d6f4e870 poor

        JSONObject balanceObj = (JSONObject) connectMgr.getBalance("AGgQU6yC4CJs6nd9v26LZLdP6LpkaUwY1s");
        assertNotNull(balanceObj);
        long ontBalance = balanceObj.getLongValue("ont");
        long ongBalance = balanceObj.getLongValue("ong");
        assertTrue(ontBalance >= 0);
        assertTrue(ongBalance >= 0);

    }
    @Test
    public void sendTransferOnt() throws Exception {
        final int amount = 1;
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
//        AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe 6da9f512db2991bcfd963d9073b0d6541a3f9dff139b7b0959f79778d6f4e870 poor
        final String richAddr = "AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve";
        final String richEncryptedKey = "HJ/2rbSeYFqBrqa/ra78MOuWt1qFcJMAYY4sso0Bf0KYCTe+XQVyr1rFPn04cvX3";
        final String richPassword = "\"111111'";
        final String richSaltStr = "LkKNGCWN8ziDmnERyoMP6Q==";
        final byte[] richSalt = Base64.decode(richSaltStr,Base64.NO_WRAP);
        final String poorAddr = "AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richOntBalance = richBalanceObj.getIntValue("ont");
        int poorOntBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richOntBalance > 0);
        assertTrue(poorOntBalance >= 0);

        Account accountRich = walletMgr.importAccount(richEncryptedKey,richPassword,richAddr, richSalt);
        byte[] saltRich = accountRich.getSalt();

        Transaction transactionR2P = ont.makeTransfer(richAddr,poorAddr,1,payAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,richPassword,saltRich);
        String transactionBodyStr = transactionR2P.toHexString();
        TransactionBodyVO transactionBodyVO = new TransactionBodyVO();
        transactionBodyVO.setTxnStr(transactionBodyStr);
        transactionBodyVO.setSendAddress(accountRich.address);
        transactionBodyVO.setReceiveAddress(poorAddr);
        transactionBodyVO.setAssetName("ont");
        transactionBodyVO.setAmount(1);
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
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
//        AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe 6da9f512db2991bcfd963d9073b0d6541a3f9dff139b7b0959f79778d6f4e870 poor
        final String richAddr = "AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve";
        final String richEncryptedKey = "HJ/2rbSeYFqBrqa/ra78MOuWt1qFcJMAYY4sso0Bf0KYCTe+XQVyr1rFPn04cvX3";
        final String richPassword = "\"111111'";
        final String richSaltStr = "LkKNGCWN8ziDmnERyoMP6Q==";
        final byte[] richSalt = Base64.decode(richSaltStr,Base64.NO_WRAP);
        final String poorAddr = "AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richOntBalance = richBalanceObj.getIntValue("ont");
        int poorOntBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richOntBalance > 0);
        assertTrue(poorOntBalance >= 0);

        Account accountRich = walletMgr.importAccount(richEncryptedKey,richPassword,richAddr, richSalt);

        Transaction transactionR2P = ont.makeTransfer(richAddr,poorAddr,1,richAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,richPassword,richSalt);
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
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
//        AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe 6da9f512db2991bcfd963d9073b0d6541a3f9dff139b7b0959f79778d6f4e870 poor
        final String richAddr = "ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG";
        final String richKey = "59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc";
        final String richPassword = "123456";
        final String poorAddr = "AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe";
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
        transactionBodyVO.setSendAddress(richAddr);
        transactionBodyVO.setReceiveAddress(poorAddr);
        transactionBodyVO.setAssetName("ong");
        transactionBodyVO.setAmount(1);
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
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
//        AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe 6da9f512db2991bcfd963d9073b0d6541a3f9dff139b7b0959f79778d6f4e870 poor
        final String richAddr = "ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG";
        final String richKey = "59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc";
        final String richPassword = "123456";
        final String poorAddr = "AX2kRrJWLqdcrC9fq7CUswPjdXz6hGLBRe";
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
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
        final String address = "ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG";
        long unclaimOng = ong.unclaimOng(address);
        assertTrue(unclaimOng >= 0);
    }

    @Test
    public void claimOng() throws Exception {
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
        final int amount = 1;
        final String richAddr = "ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG";
        final String richKey = "59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc";
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
        transactionBodyVO.setSendAddress(richAddr);
        transactionBodyVO.setReceiveAddress(richAddr);
        transactionBodyVO.setAssetName("ong");
        transactionBodyVO.setAmount(amount);
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
//        ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG 59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc rich
        final int amount = 1;
        final String richAddr = "ATc5gXifZQ1C1gMCoRMrGEvhWxhvQ5w1RG";
        final String richKey = "59fc435e3955d9eece982713e287549e19aeb33ebc7f7b70c28dc0959a16efdc";
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