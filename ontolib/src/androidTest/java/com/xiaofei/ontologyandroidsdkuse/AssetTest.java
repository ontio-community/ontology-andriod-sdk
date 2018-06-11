package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
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
    public static String privatekey1 = "49855b16636e70f100cc5f4f42bc20a6535d7414fb8845e7310f8dd065a97221";

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
//        ontSdk.setRestful("http://polaris1.ont.io:20334");
        ontSdk.setRestful("http://192.168.50.74:20334");
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
    public void transferTest() throws Exception {

        com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), ontSdk.defaultSignScheme);

        String privateKey = "0bc8c1f75a028672cd42c221bf81709dfc7abbbaf0d87cb6fdeaf9a20492c194";
        com.github.ontio.account.Account account1 = new com.github.ontio.account.Account(Helper.hexToBytes(privateKey), SignatureScheme.SHA256WITHECDSA);
        System.out.println("*****" + ontSdk.nativevm().ong().queryBalanceOf(account1.getAddressU160().toBase58()));
//        System.out.println(ontSdk.nativevm().ont().queryBalanceOf(acct1.getAddressU160().toBase58()));
        String txhash = "";
        if(false){
//            txhash = ontSdk.nativevm().ong().sendTransfer(account1,acct1.getAddressU160().toBase58(),10,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
//            txhash = ontSdk.nativevm().ong().sendApprove(account1,acct1.getAddressU160().toBase58(),1000,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            txhash = ontSdk.nativevm().ong().sendTransferFrom(acct1,account1.getAddressU160().toBase58(),acct1.getAddressU160().toBase58(),1000,account1,ontSdk.DEFAULT_GAS_LIMIT,0);
            Thread.sleep(6000);
        }
//        System.out.println(ontSdk.nativevm().ong().queryBalanceOf(account1.getAddressU160().toBase58()));
        System.out.println("********" + ontSdk.nativevm().ong().queryAllowance(account1.getAddressU160().toBase58(),acct1.getAddressU160().toBase58()));
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