package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.OntAssetTx;
import com.github.ontio.sdk.manager.OntIdTx;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SmokeExceptionTest {

    private OntSdk ontSdk;
    private ConnectMgr connectMgr;
    private OntAssetTx ontAssetTx;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private Context appContext;
    private OntIdTx ontIdTx;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnectMgr();
        ontAssetTx = ontSdk.getOntAssetTx();
        ontIdTx = ontSdk.getOntIdTx();
        ontSdk.setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sendTransferError() throws Exception {
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String poorAddr = "TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft";
        final String poorKey = "6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=";
        final String richPrefixStr = "b14757ed";
        final String poorPrefixStr = "4fd1e7fe";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
        final byte[] poorPrefix = Helper.hexToBytes(poorPrefixStr);

        com.github.ontio.sdk.wallet.Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        try {
            ontAssetTx.sendTransfer("aaa",richAddr,"123123",poorAddr,1);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58012"));
        }

        try {
            ontAssetTx.sendTransfer("ont",richAddr,"123123",poorAddr,0);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58016"));
        }

        try {
            ontAssetTx.sendTransfer("ont",richAddr,"123123",poorAddr,-1);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58016"));
        }

        try {
            ontAssetTx.sendTransfer("ont","","123123",poorAddr,1);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("58004"));
        }
        ;
        try {
            ontAssetTx.sendTransfer("ont",richAddr,"123123","",1);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransfer("ont",richAddr,"",poorAddr,1);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("51015"));
        }

    }

    @Test
    public void sendTransferManyError() throws Exception {
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

        com.github.ontio.sdk.wallet.Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
        com.github.ontio.sdk.wallet.Account accountPoor1 = walletMgr.importAccount("poor1",poorKey1,"123123",poor1Prefix);
        com.github.ontio.sdk.wallet.Account accountPoor2 = walletMgr.importAccount("poor2",poorKey2,"123123",poor2Prefix);

        try {
            ontAssetTx.sendTransferFromMany("aaa",new String[]{richAddr,poorAddr1},new String[]{"123123","123123"},poorAddr2,new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58012"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,poorAddr1},new String[]{"123123","123123"},poorAddr2,new long[]{0,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58016"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,poorAddr1},new String[]{"123123","123123"},poorAddr2,new long[]{amount1,-1});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58016"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{"",poorAddr1},new String[]{"123123","123123"},poorAddr2,new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,""},new String[]{"123123","123123"},poorAddr2,new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,poorAddr1},new String[]{"123123","123123"},"",new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,poorAddr1},new String[]{"","123123"},poorAddr2,new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        try {
            ontAssetTx.sendTransferFromMany("ont",new String[]{richAddr,poorAddr1},new String[]{"123123",""},poorAddr2,new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        ////////////////////////////////////////////////////////////////////////////////////////

        try {
            ontAssetTx.sendTransferToMany("aaa",poorAddr2,"123123",new String[]{richAddr,poorAddr1},new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58012"));
        }

        try {
            ontAssetTx.sendTransferToMany("ont",poorAddr2,"123123",new String[]{richAddr,poorAddr1},new long[]{0,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58016"));
        }

        try {
            ontAssetTx.sendTransferToMany("ont",poorAddr2,"123123",new String[]{richAddr,poorAddr1},new long[]{amount1,-1});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58016"));
        }

        try {
            ontAssetTx.sendTransferToMany("ont","","123123",new String[]{richAddr,poorAddr1},new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransferToMany("ont",poorAddr2,"123123",new String[]{"",poorAddr1},new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransferToMany("ont",poorAddr2,"123123",new String[]{richAddr,""},new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("58004"));
        }

        try {
            ontAssetTx.sendTransferToMany("ont",poorAddr2,"",new String[]{richAddr,poorAddr1},new long[]{amount1,amount2});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }
    }

    @Test
    public void sendOngError() throws Exception {
        final int amount = 1;
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String richPrefixStr = "b14757ed";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);

        com.github.ontio.sdk.wallet.Account account = walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        String message="";
        try {
            ontAssetTx.sendOngTransferFrom(richAddr,"123123",richAddr,0);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("58016"));
            message="";
        }

        try {
            ontAssetTx.sendOngTransferFrom(richAddr,"123123",richAddr,-1);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("58016"));
            message="";
        }

        try {
            ontAssetTx.sendOngTransferFrom("","123123",richAddr,amount);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("58004"));
            message="";
        }

        try {
            ontAssetTx.sendOngTransferFrom(richAddr,"123123","",amount);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("58004"));
            message="";
        }

        try {
            ontAssetTx.sendOngTransferFrom(richAddr,"",richAddr,amount);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("51015"));
            message="";
        }
    }

    @Test
    public void importIdentityError() throws Exception {
        //febb25c1---vixZQMNsJHoZLy5AbqIqHrdC+7htA3NVTwo91Kc1swA=---123
        final String addr = "";
        final String prikey = "vixZQMNsJHoZLy5AbqIqHrdC+7htA3NVTwo91Kc1swA=";
        final String password = "123123";
        final String prefixStr = "febb25c1";
        final byte[] prefix = Helper.hexToBytes(prefixStr);
        //walletMgr.importIdentity("aa",prikey,password,prefix);
        try {
            walletMgr.importIdentity("aa","",password,prefix);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        try {
            walletMgr.importIdentity("aa","abc",password,prefix);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        try {
            walletMgr.importIdentity("aa",prikey,"",prefix);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        try {
            walletMgr.importIdentity("aa",prikey,password,new byte[]{});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

    }

    @Test
    public void importAccount() throws Exception {
        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
        final String richPrefixStr = "b14757ed";
        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);

        //walletMgr.importAccount("rich",richKey,"123123",richPrefix);

        try {
            walletMgr.importAccount("rich","","123123",richPrefix);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        try {
            walletMgr.importAccount("rich",richKey,"",richPrefix);
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

        try {
            walletMgr.importAccount("rich",richKey,"123123",new byte[]{});
        }catch (Exception ex){
            assertTrue(ex.getMessage().contains("51015"));
        }

    }
}