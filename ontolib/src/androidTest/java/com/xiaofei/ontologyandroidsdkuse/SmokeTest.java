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
import com.xiaofei.ontologyandroidsdkuse.model.AppConfig;
import com.xiaofei.ontologyandroidsdkuse.model.OntoResult;
import com.xiaofei.ontologyandroidsdkuse.model.TransactionBodyVO;
import com.xiaofei.ontologyandroidsdkuse.service.OntoService;
import com.xiaofei.ontologyandroidsdkuse.service.OntoServiceApi;
import com.xiaofei.ontologyandroidsdkuse.service.OntopassService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

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
        retrofit = new Retrofit.Builder()
                .baseUrl("https://dev.ont.io/")
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        ontoServiceApi = retrofit.create(OntoServiceApi.class);
        ontoService = new OntoService();
        ontopassService = new OntopassService();
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnect();
        ont = ontSdk.nativevm().ont();
        ong = ontSdk.nativevm().ong();
        ontId = ontSdk.nativevm().ontId();
        payAddr="TA4pCAb4zUifHyxSx32dZRjTrnXtxEWKZr";
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
    public void getAppConfig() throws IOException {
        Call<OntoResult> call = ontoServiceApi.getAppConfig();
        Response<OntoResult> response = call.execute();
        OntoResult ontoResult = response.body();
        JSONObject result = (JSONObject) ontoResult.getResult();
        AppConfig appConfig = JSON.parseObject(result.toJSONString(),AppConfig.class);
        String testNetUrlStr = appConfig.getTestnetAddr();
        assertNotNull(testNetUrlStr);
        assertNotEquals(testNetUrlStr,"");
        assertEquals(testNetUrlStr,"http://polaris1.ont.io");
    }

    @Test
    public void getAppConfig2() throws IOException {
        AppConfig appConfig = ontoService.getAppConfig();
        String testNetUrlStr = appConfig.getTestnetAddr();
        assertNotNull(testNetUrlStr);
        assertNotEquals(testNetUrlStr,"");
        assertEquals(testNetUrlStr,"http://polaris1.ont.io");

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

        Transaction transaction = ontId.makeRegister(identity.ontid,"123456",payAddr,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,identity.ontid.replace(Common.didont,""),"123456");
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
        String mnsStr = MnemonicCode.generateMnemonicCodesStr().toString();
        byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStr(mnsStr);
        String prikeyStr = Helper.toHexString(prikey);
        Account account = walletMgr.createAccountFromPriKey("bbb","123456",prikeyStr);
        String encryptedMnsStr = MnemonicCode.encryptMnemonicCodesStr(mnsStr,"123456",account.address);
        assertNotNull(account);
        assertNotNull(account.address);
        assertNotEquals(account.address,"");
        assertEquals(account.label,"bbb");

        String mnsStrNew = MnemonicCode.decryptMnemonicCodesStr(encryptedMnsStr,"123456",account.address);
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
        com.github.ontio.account.Account acct = new com.github.ontio.account.Account(Helper.hexToBytes(prikeyStrOrig), ontSdk.signatureScheme);
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
        //54670753cc5f20e9a99d21104c1743037891a8aadb62146bdd0fd422edf38166
        //
        String prikey = "9c663937ffcadb1aa196bf08c76b2f8a18f214d4f32f029a06cde1a0ca73208a";
        Account account = walletMgr.importAccount("aa",prikey,"123456");
        assertNotNull(account.address,"AVZVSttsW3kbf9He2YduHSKB4ygLomy7eG");
        //AVZVSttsW3kbf9He2YduHSKB4ygLomy7eG
        //TA8SrRAVUWSiqNzwzriirwRFn6GC4QeADg
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
        //Account account = walletMgr.importAccountFromMnemonicCodes("aa",mnemonicCodes,"123456");
        byte[] prikey = MnemonicCode.getPrikeyFromMnemonicCodesStr(mnemonicCodesStr);
        String prikeyHexStr = Helper.toHexString(prikey);
        Account account = walletMgr.createAccountFromPriKey("123456",prikeyHexStr);
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
    public void sendAddRemoveIdentityAttribute() throws Exception {
        Identity identity = walletMgr.createIdentity("aa","123456");

        Transaction transaction = ontId.makeRegister(identity.ontid,"123456",payAddr,gasLimit,gasPrice);
        transaction = ontSdk.signTx(transaction,identity.ontid.replace(Common.didont,""),"123456");
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
        Transaction transactionAdd = ontId.makeAddAttributes(identity.ontid,"123456",attributes,payAddr,gasLimit,gasPrice);
        transactionAdd = ontSdk.signTx(transactionAdd,identity.ontid.replace(Common.didont,""),"123456");
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
//        TA6qWdLo14aEve5azrYWWvMoGPrpczFfeW---1/gEPy/Uz3Eyl/sjoZ8JDymGX6hU/gi1ufUIg6vDURM= rich
//        TA4pSdTKm4hHtQJ8FbrCk9LZn7Uo96wrPC---Vz0CevSaI9/VNLx03XNEQ4Lrnnkkjo5aM5hdCuicsOE= poor1
//        TA5F9QefsyKvn5cH37VnP5snSru5ZCYHHC---OGaD13Sn/q9gIZ8fmOtclMi4yy34qq963wzpidYDX5k= poor2

        JSONObject balanceObj = (JSONObject) connectMgr.getBalance("TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z");
        assertNotNull(balanceObj);
        long ontBalance = balanceObj.getLongValue("ont");
        long ongBalance = balanceObj.getLongValue("ong");
        assertTrue(ontBalance >= 0);
        assertTrue(ongBalance >= 0);

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
        int richOntBalance = richBalanceObj.getIntValue("ont");
        int poorOntBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richOntBalance > 0);
        assertTrue(poorOntBalance >= 0);

        Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
        Account accountPoor = walletMgr.importAccount("poor",poorKey,"123123",poorPrefix);


        Transaction transactionR2P = ont.makeTransfer(richAddr,poorAddr,1,payAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,"123123");
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
    public void sendTransferOng() throws Exception {
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
        int richOngBalance = richBalanceObj.getIntValue("ong");
        int poorOngBalance = poorBalanceObj.getIntValue("ong");
        assertTrue(richOngBalance > 0);
        assertTrue(poorOngBalance >= 0);

        Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
        Account accountPoor = walletMgr.importAccount("poor",poorKey,"123123",poorPrefix);

        Transaction transactionR2P = ong.makeTransfer(richAddr,poorAddr,1,payAddr,gasLimit,gasPrice);
        transactionR2P = ontSdk.signTx(transactionR2P,richAddr,"123123");
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
        long richOngBalanceAfter = richOntBalanceObjAfter.getLongValue("ong");
        long poorOngBalanceAfter = poorOntBalanceObjAfter.getLongValue("ong");

        assertTrue(richOngBalanceAfter == richOngBalance -amount);
        assertTrue(poorOngBalanceAfter == poorOngBalance +amount);

    }

    @Test
    public void getUnclaimOng() throws Exception {
        final String address = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        long unclaimOng = ong.unclaimOng(address);
        assertTrue(unclaimOng >= 0);
    }

    @Test
    public void claimOng() throws Exception {
//b14757ed---kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=---123123---TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z rich
        final int amount = 1;
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String richPrefixStr = "b14757ed";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        long richOngApprove = ong.unclaimOng(richAddr);
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        long richOng = richBalanceObj.getLongValue("ong");
        assertTrue(richOngApprove > 0);
        assertTrue(richOng >= 0);

        Account account = walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        Transaction transactionClaimOng = ong.makeClaimOng(richAddr,richAddr,amount,payAddr,gasLimit,gasPrice);
        transactionClaimOng = ontSdk.signTx(transactionClaimOng,richAddr,"123123");
        String transactionBodyStr = transactionClaimOng.toHexString();
        TransactionBodyVO transactionBodyVO = new TransactionBodyVO();
        transactionBodyVO.setTxnStr(transactionBodyStr);
        transactionBodyVO.setAddress(account.address);
        ontopassService.assetTransfer(transactionBodyVO);

        Thread.sleep(7000);

        long richOngApproveAfter = ong.unclaimOng(richAddr);
        JSONObject richBalanceAfterObj = (JSONObject) connectMgr.getBalance(richAddr);
        long richOngAfter = richBalanceAfterObj.getLongValue("ong");
        assertTrue(richOngApproveAfter == richOngApprove - amount);
        assertTrue(richOngAfter == richOng + amount);
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
        Account account = walletMgr.importAccount("",encryptedPrikey,password,prefix);

        assertEquals(account.address,addressOrig);

    }

    @Test
    public void getSmartCodeEvent() throws Exception {
        String txnHash = "1f8b9009ff5b59b61a7e00c95fcc455a287b43d423e3d61ed1698fac54bafb16";
        JSONObject jsonObject = (JSONObject) connectMgr.getSmartCodeEvent(txnHash);
        String txnHashNew = jsonObject.getString("TxHash");
        String stateStr = jsonObject.getString("State");
        assertEquals(txnHashNew,txnHash);
        assertEquals(stateStr,"1");
    }
}