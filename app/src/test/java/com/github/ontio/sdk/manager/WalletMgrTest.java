package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class WalletMgrTest {
    private OntSdk ontSdk;
    private WalletMgr walletMgr;
    private  OntIdTx ontIdTx;
    private Wallet wallet;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        //ontSdk.openWalletFile("cjf.json");
        ontSdk.setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");
        walletMgr = ontSdk.getWalletMgr();
        ontIdTx = ontSdk.getOntIdTx();
        wallet = walletMgr.getWallet();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getWallet() {
        assertNotNull(this.walletMgr);
    }

    @Test
    public void writeWallet() throws Exception {
        Wallet wallet = walletMgr.writeWallet();
        assertNotNull(wallet);
    }

    @Test
    public void createIdentity() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        assertNotNull(identity);
        List<Identity> identities = wallet.getIdentities();
        assertTrue(identities.contains(identity));
        int origSize = identities.size();
        Identity lastIdentity = identities.get(origSize - 1);
        assertTrue(lastIdentity.equals(identity));
        walletMgr.importIdentity(identity.controls.get(0).key,"123456");
        int newSize = identities.size();
        assertEquals(origSize,newSize);
        //判断密码是否正确
        //
    }

    @Test
    public void sendRegister() throws Exception {
        Identity identity = ontIdTx.sendRegister("123456");
        assertNotNull(identity);
        wallet.getIdentities().contains(identity);
        walletMgr.importIdentity(identity.controls.get(0).key,"123456");
    }

    @Test
    public void sendRegister2() throws Exception {
        String label = "chenjiefei";
        Identity identity = ontIdTx.sendRegister(label,"112233");
        assertEquals(identity.label,label);
        assertNotNull(identity);
        walletMgr.importIdentity(identity.controls.get(0).key,"112233");

        Thread.sleep(6000);
        String ddo = ontIdTx.sendGetDDO(identity.ontid);
        assertNotEquals(ddo,"");
    }

    @Test
    public void sendRegister3() throws Exception {
        Identity identityLocal = walletMgr.createIdentity("123456");
        Identity identity = ontIdTx.sendRegister(identityLocal,"654321");
        assertNotNull(identity);
    }

    @Test
    public void removeIdentity() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        assertTrue(wallet.getIdentities().contains(identity));
        wallet.removeIdentity(identity.ontid);
        assertFalse(wallet.getIdentities().contains(identity));

    }

    @Test
    public void setDefaultIdentity(){
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.substring(0,8);
    }
}