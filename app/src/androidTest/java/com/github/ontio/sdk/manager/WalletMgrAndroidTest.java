//package com.github.ontio.sdk.manager;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import com.alibaba.fastjson.JSON;
//import com.github.ontio.OntSdk;
//import com.github.ontio.sdk.wallet.Account;
//import com.github.ontio.sdk.wallet.Identity;
//
//import org.json.JSONObject;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.IOException;
//
//import static org.junit.Assert.*;
//
//@RunWith(AndroidJUnit4.class)
//public class WalletMgrAndroidTest {
//    private Context appContext = InstrumentationRegistry.getTargetContext();
//    private OntSdk ontSdk;
//    private WalletMgr walletMgr;
//    private String walletName;
//    private OntIdTx ontIdTx;
//
//
//    @Before
//    public void setUp() throws Exception {
//        ontSdk = OntSdk.getInstance();
//        ontSdk.setRestful("http://polaris1.ont.io:20334");
//        ontSdk.setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");
//        walletName = "wallet";
//        ontSdk.openWalletFile(appContext.getSharedPreferences(walletName, Context.MODE_PRIVATE));
//        walletMgr = ontSdk.getWalletMgr();
//        ontIdTx = ontSdk.getOntIdTx();
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void openWalletFile() throws IOException {
//        ontSdk.openWalletFile(appContext.getSharedPreferences(walletName, Context.MODE_PRIVATE));
//    }
//
//    @Test
//    public void getWalletMgr(){
//        assertNotNull(walletMgr);
//    }
//
//    @Test
//    public void writeWallet() throws IOException {
//        walletMgr.writeWallet();
//    }
//
//    @Test
//    public void createIdentity() throws Exception {
//        Identity identity = walletMgr.createIdentity("123456");
//        assertNotNull(identity);
//    }
//
//    @Test
//    public void createIdentity2() throws Exception {
//        String label = "testLabel";
//        Identity identity = walletMgr.createIdentity(label,"123456");
//        assertNotNull(identity);
//        assertEquals(identity.label,label);
//    }
//
//    @Test
//    public void createAccount() throws Exception {
//        Account account = walletMgr.createAccount("123456");
//        assertNotNull(account);
//    }
//
//    @Test
//    public void createAccount2() throws Exception{
//        String label = "testLabel";
//        Account account = walletMgr.createAccount(label,"123456");
//        assertNotNull(account);
//        assertEquals(account.label,label);
//    }
//
//    @Test
//    public void sendRegister() throws Exception {
//        Identity identity = ontIdTx.sendRegister("123456");
//        assertNotNull(identity);
//        Thread.sleep(6000);
//        String string = ontIdTx.sendGetDDO(identity.ontid);
//        assertNotEquals(string,"");
//    }
//
//    @Test
//    public void sendRegister2() throws Exception {
//        String label = "testLabel";
//        Identity identity = ontIdTx.sendRegister(label,"123456");
//        assertNotNull(identity);
//        assertEquals(identity.label,label);
//        Thread.sleep(6000);
//        String string = ontIdTx.sendGetDDO(identity.ontid);
//        assertNotEquals(string,"");
//    }
//
//    @Test
//    public void importIdentity() throws Exception {
//        com.github.ontio.account.Account acct = new com.github.ontio.account.Account(ontSdk.signatureScheme);
//        Identity identity = ontIdTx.sendRegister("cjf","123456");
//        Thread.sleep(6000);
//        String string = ontIdTx.sendGetDDO(identity.ontid);
//
//    }
//
//    @Test
//    public void exportAccount() throws Exception {
//        Account account = walletMgr.createAccount("123456");
//        com.alibaba.fastjson.JSONObject jsonObject = walletMgr.exportAccount(account);
//    }
//
//    @Test
//    public void exportIdentity() throws Exception {
//        Identity identity = walletMgr.createIdentity("123456");
//        com.alibaba.fastjson.JSONObject jsonObject = walletMgr.exportIdentity(identity);
//    }
//}