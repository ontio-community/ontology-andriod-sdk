package com.xiaofei.ontologyandroidsdkuse;//package com.xiaofei.ontologyandroidsdkuse;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import com.github.ontio.OntSdk;
//import com.github.ontio.common.Helper;
//import com.github.ontio.sdk.manager.ConnectMgr;
//import com.github.ontio.sdk.manager.WalletMgr;
//import com.github.ontio.sdk.wallet.Account;
//import com.github.ontio.sdk.wallet.Wallet;
//import com.github.ontio.smartcontract.nativevm.NativeOntIdTx;
//import com.github.ontio.smartcontract.nativevm.Ong;
//import com.github.ontio.smartcontract.nativevm.Ont;
//import com.github.ontio.smartcontract.nativevm.ont;
//import com.github.ontio.smartcontract.nativevm.OntId;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertTrue;
//
//@RunWith(AndroidJUnit4.class)
//public class SmokeExceptionTest {
//
//    private OntSdk ontSdk;
//    private ConnectMgr connectMgr;
//    private Ont ont;
//    private Ong ong;
//    private WalletMgr walletMgr;
//    private Wallet wallet;
//    private Context appContext;
//    //private OntIdTx ontIdTx;
//    private OntId ontIdTx;
//    private Account payer;
//
//    @Before
//    public void setUp() throws Exception {
//        ontSdk = OntSdk.getInstance();
//        ontSdk.setRestful("http://polaris1.ont.io:20334");
//        ontSdk.setRestful("http://192.168.50.73:20334");
//        ontSdk.setRestful("http://139.219.129.55:20334");
//        appContext  = InstrumentationRegistry.getTargetContext();
//        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
//        walletMgr = ontSdk.getWalletMgr();
//        wallet = walletMgr.getWallet();
//        connectMgr = ontSdk.getConnect();
//        ont = ontSdk.nativevm().ont();
//        ontIdTx = ontSdk.nativevm().ontId();
//        payer = new Account();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void sendTransferError() throws Exception {
//        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
//        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
//        final String poorAddr = "TA9hEJap1EWcAo9DfrKFHCHcuRAG9xRMft";
//        final String poorKey = "6LL8RCFR8lhpkAAyvEXVRKGzs6Q5ZNh4so4SGXrPHMs=";
//        final String richPrefixStr = "b14757ed";
//        final String poorPrefixStr = "4fd1e7fe";
//        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
//        final byte[] poorPrefix = Helper.hexToBytes(poorPrefixStr);
//
//        com.github.ontio.sdk.wallet.Account accountRich = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
//
//        try {
//            ont.sendTransfer(richAddr,"123123",poorAddr,1,payer.address,"123456",0,0);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("58012"));
//        }
//
//        try {
//            ont.sendTransfer(richAddr,"123123",poorAddr,0,payer.address,"123456",0,0);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("58016"));
//        }
//
//        try {
//            ont.sendTransfer(richAddr,"123123",poorAddr,-1,payer.address,"123456",0,0);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("58016"));
//        }
//
//        try {
//            ont.sendTransfer("","123123",poorAddr,1,payer.address,"123456",0,0);
//        } catch (Exception e) {
//            assertTrue(e.getMessage().contains("58004"));
//        }
//        ;
//        try {
//            ont.sendTransfer(richAddr,"123123","",1,payer.address,"123456",0,0);
//        } catch (Exception e) {
//            assertTrue(e.getMessage().contains("58004"));
//        }
//
//        try {
//            ont.sendTransfer(richAddr,"",poorAddr,1,0);
//        } catch (Exception e) {
//            assertTrue(e.getMessage().contains("51015"));
//        }
//
//        try {
//            ont.sendTransfer(richAddr,"123123",poorAddr,1,-1);
//        } catch (Exception e) {
//            assertTrue(e.getMessage().contains("58004"));
//        }
//
//    }
//
//    @Test
//    public void sendOngError() throws Exception {
//        final int amount = 1;
//        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
//        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
//        final String richPrefixStr = "b14757ed";
//        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
//
//        com.github.ontio.sdk.wallet.Account account = walletMgr.importAccount("rich",richKey,"123123",richPrefix);
//
//        String message="";
//        try {
//            ont.sendOngTransferFrom(richAddr,"123123",richAddr,0,0);
//        }catch (Exception ex){
//            message = ex.getMessage();
//        }finally {
//            assertTrue(message.contains("58016"));
//            message="";
//        }
//
//        try {
//            ont.sendOngTransferFrom(richAddr,"123123",richAddr,-1,0);
//        }catch (Exception ex){
//            message = ex.getMessage();
//        }finally {
//            assertTrue(message.contains("58016"));
//            message="";
//        }
//
//        try {
//            ont.sendOngTransferFrom("","123123",richAddr,amount,0);
//        }catch (Exception ex){
//            message = ex.getMessage();
//        }finally {
//            assertTrue(message.contains("58004"));
//            message="";
//        }
//
//        try {
//            ont.sendOngTransferFrom(richAddr,"123123","",amount,0);
//        }catch (Exception ex){
//            message = ex.getMessage();
//        }finally {
//            assertTrue(message.contains("58004"));
//            message="";
//        }
//
//        try {
//            ont.sendOngTransferFrom(richAddr,"",richAddr,amount,0);
//        }catch (Exception ex){
//            message = ex.getMessage();
//        }finally {
//            assertTrue(message.contains("51015"));
//            message="";
//        }
//    }
//
//    @Test
//    public void importIdentityError() throws Exception {
//        //febb25c1---vixZQMNsJHoZLy5AbqIqHrdC+7htA3NVTwo91Kc1swA=---123
//        final String addr = "";
//        final String prikey = "vixZQMNsJHoZLy5AbqIqHrdC+7htA3NVTwo91Kc1swA=";
//        final String password = "123123";
//        final String prefixStr = "febb25c1";
//        final byte[] prefix = Helper.hexToBytes(prefixStr);
//        //walletMgr.importIdentity("aa",prikey,password,prefix);
//        try {
//            walletMgr.importIdentity("aa","",password,prefix);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("51015"));
//        }
//
//        try {
//            walletMgr.importIdentity("aa","abc",password,prefix);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("51015"));
//        }
//
//        try {
//            walletMgr.importIdentity("aa",prikey,"",prefix);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("51015"));
//        }
//
//        try {
//            walletMgr.importIdentity("aa",prikey,password,new byte[]{});
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("58004"));
//        }
//
//    }
//
//    @Test
//    public void importAccount() throws Exception {
//        final String richAddr = "TA6JpJ3hcKa94H164pRwAZuw1Q1fkqmd2z";
//        final String richKey = "kOoJt2p+H4nEMIPBLQe9Mca4Z9IRIMnydGgqG23kh/c=";
//        final String richPrefixStr = "b14757ed";
//        final byte[] richPrefix = Helper.hexToBytes(richPrefixStr);
//
//        //walletMgr.importAccount("rich",richKey,"123123",richPrefix);
//
//        try {
//            walletMgr.importAccount("rich","","123123",richPrefix);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("51015"));
//        }
//
//        try {
//            walletMgr.importAccount("rich",richKey,"",richPrefix);
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("51015"));
//        }
//
//        try {
//            walletMgr.importAccount("rich",richKey,"123123",new byte[]{});
//        }catch (Exception ex){
//            assertTrue(ex.getMessage().contains("58004"));
//        }
//
//    }
//
//    @Test
//    public void getBalanceError(){
//        String message="";
//        try {
//            message="";
//            connectMgr.getBalance("");
//        }catch (Exception ex){
//            message = ex.getMessage();
//        }finally {
//            assertNotEquals(message,"");
//
//        }
//
//    }
//}