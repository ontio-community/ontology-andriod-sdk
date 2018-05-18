package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.ontio.OntSdk;
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

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class AssetTest {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;
    private OntAssetTx ontAssetTx;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private Context appContext;
    private NativeOntIdTx ontIdTx;
    String password = "111111";

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnectMgr();
        ontAssetTx = ontSdk.nativevm().ont();
        ontIdTx = ontSdk.nativevm().ontId();
        ontSdk.vm().setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");
    }



    @Test
    public void testTransfer() throws Exception {

        Account account1 = ontSdk.getWalletMgr().createAccountFromPriKey(password,"d6aae3603a82499062fe2ddd68840dce417e2e9e7785fbecb3100dd68c4e2d44");
        Account account2 = ontSdk.getWalletMgr().createAccount(password);
        System.out.print("account1 address:" + account2.address);
        System.out.print("account2 address:" + account2.address);

        System.out.print("account1 balance:" + ontAssetTx.sendBalanceOf("ont",account1.address));
        System.out.print("account2 balance:" + ontAssetTx.sendBalanceOf("ont",account2.address));


    }

    @After
    public void tearDown() throws Exception {

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