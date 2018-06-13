package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.SignatureScheme;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class AssetTest {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;
    private Ont ont;
    private Ong ong;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private Context appContext;
    private OntId ontIdTx;
    String password = "111111";
    public static String privatekey1 = "75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf";




    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
//        ontSdk.setRestful("http://192.168.50.74:20334");
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnect();
        ont = ontSdk.nativevm().ont();
        ontIdTx = ontSdk.nativevm().ontId();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void ontidtest() throws Exception {
        com.github.ontio.account.Account payAcct = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.nativevm().ontId().sendRegister(identity,password,identity.controls.get(0).getSalt(),payAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        System.out.println(ontSdk.nativevm().ontId().sendGetDDO(identity.ontid));

        System.exit(0);

        ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid,password,identity.controls.get(0).getSalt(),Helper.toHexString(payAcct.serializePublicKey()),payAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        System.out.println(Helper.toHexString(payAcct.serializePublicKey()));
//        Attribute[] attributes = new Attribute[1];
//        attributes[0] = new Attribute("key1".getBytes(),"String".getBytes(),"value1".getBytes());
//        ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid,password,identity.controls.get(0).getSalt(),attributes,payAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
////        ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity,password,identity.controls.get(0).getSalt(),attributes,payAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        ontSdk.nativevm().ontId().sendRemovePubKey(identity.ontid,password,identity.controls.get(0).getSalt(),Helper.toHexString(payAcct.serializePublicKey()),payAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        System.out.println("resultresultresultresult:");
        System.out.println(ontSdk.nativevm().ontId().sendGetDDO(identity.ontid));
    }

    @Test
    public void addresstest() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.getWalletMgr().importIdentity(identity.controls.get(0).key,password,identity.controls.get(0).getSalt(),identity.ontid.replace(Common.didont,""));
//                Account account = ontSdk.getWalletMgr().createAccount(password);
                ontSdk.getWalletMgr().importAccountWithRandomLabel(identity.controls.get(0).key,password,identity.ontid.replace(Common.didont,""),identity.controls.get(0).getSalt());
//        System.out.println(account.address);
//        int aa = 0;
    }

    @Test
    public void accountDemo() throws Exception {


//        String privateKey = "0bc8c1f75a028672cd42c221bf81709dfc7abbbaf0d87cb6fdeaf9a20492c194";
        com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
        byte[] salt = new byte[]{(byte)251,(byte)155,(byte)65,(byte)228,(byte)3,(byte)251,(byte)77,(byte)136,(byte)106,(byte)44,(byte)2,(byte)255,(byte)194,(byte)185,(byte)234,(byte)196};
        String aa = account1.exportGcmEncryptedPrikey("111111",salt,4096);
        System.out.println(aa);
    }

    @Test
    public void transferTest() throws Exception {
        String privateKey = "0bc8c1f75a028672cd42c221bf81709dfc7abbbaf0d87cb6fdeaf9a20492c194";
        com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privateKey), ontSdk.defaultSignScheme);
        com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);
        com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes("2b5887abb1421ab101714906c8578aac340d2713f3b7b34135fed191686f9087"), SignatureScheme.SHA256WITHECDSA);


        System.out.println("account1：" + ontSdk.getConnect().getBalance(account1.getAddressU160().toBase58()));
//        ontSdk.nativevm().ong().claimOng(acct2,acct2.getAddressU160().toBase58(),1000,acct2,ontSdk.DEFAULT_GAS_LIMIT,0);
        String txhash = "";
        if(true){
            txhash = ontSdk.nativevm().ont().sendTransfer(account1,acct2.getAddressU160().toBase58(),10,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
//            txhash = ontSdk.nativevm().ont().sendApprove(account1,acct1.getAddressU160().toBase58(),10,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
//            txhash = ontSdk.nativevm().ont().sendTransferFrom(acct1,account1.getAddressU160().toBase58(),acct1.getAddressU160().toBase58(),1000,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
//            ontSdk.nativevm().ong().claimOng(account1,account1.getAddressU160().toBase58(),397022742650L,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            Thread.sleep(6000);
            System.out.print(ontSdk.getConnect().getSmartCodeEvent(txhash));

        }
        System.out.println(ontSdk.getConnect().getBalance(account1.getAddressU160().toBase58()));
        System.out.println(account1.getAddressU160().toBase58());
        System.out.println("");
//        System.out.println(ontSdk.nativevm().ong().queryBalanceOf(account1.getAddressU160().toBase58()));
//        System.out.println("********" + ontSdk.nativevm().ong().queryAllowance(account1.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));
        if(false){
            System.out.println(ontSdk.nativevm().ong().queryBalanceOf(account1.getAddressU160().toBase58()));
            System.out.println(ontSdk.nativevm().ong().unclaimOng(account1.getAddressU160().toBase58()));
            ontSdk.nativevm().ong().claimOng(account1,account1.getAddressU160().toBase58(),21071968349040L,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            Thread.sleep(6000);
            System.out.println(ontSdk.nativevm().ong().queryBalanceOf(account1.getAddressU160().toBase58()));
        }

        if(false){
            System.out.print(ontSdk.getConnect().getSmartCodeEvent(Helper.reverse(txhash)));
            long balance2 = ontSdk.nativevm().ont().queryBalanceOf(account1.getAddressU160().toBase58());
            System.out.println(balance2);

            long balance3 = ontSdk.nativevm().ont().queryBalanceOf(acct1.getAddressU160().toBase58());
            System.out.println(balance3);
        }

    }

    @Test
    public void removeAccount() throws Exception {
        List<Account> accounts = wallet.getAccounts();
        int origSize = accounts.size();
        Account account = walletMgr.createAccount("123456");
        assertEquals(accounts.size(),origSize+1);

        wallet.removeAccount(account.address);
        assertEquals(accounts.size(),origSize);

    }

    @Test
    public void removeAccountError(){
        boolean isSuccess =wallet.removeIdentity("");
        assertFalse(isSuccess);
    }

    @Test
    public void removeIdentity() throws Exception {
        List<Identity> identities = wallet.getIdentities();
        int origSize = identities.size();
        Identity identity = walletMgr.createIdentity("123456");
        assertEquals(identities.size(),origSize+1);

        wallet.removeIdentity(identity.ontid);
        assertEquals(identities.size(),origSize);
    }

    @Test
    public void removeIdentityError(){
        boolean isSuccess = wallet.removeIdentity("");
        assertFalse(isSuccess);
    }
}