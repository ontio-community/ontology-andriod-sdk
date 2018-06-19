package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GovernanceTest {

    public OntSdk sdk;
    String password = "111111";
    String privateKey =  "f1442d5e7f4e2061ff9a6884d6d05212e2aa0f6a6284f0a28ae82a29cdb3d656";
    String privatekey1 = "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d";
    String privatekey9 = "1383ed1fe570b6673351f1a30a66b21204918ef8f673e864769fa2a653401114";
    String privatekey7 = "24ab4d1d345be1f385c75caf2e1d22bdb58ef4b650c0308d9d69d21242ba8618";
    String privatekey6 = "6c2c7eade4c5cb7c9d4d6d85bfda3da62aa358dd5b55de408d6a6947c18b9279";

    private Context appContext;

    @Before
    public void setUp() throws Exception {

        String ip = "http://192.168.50.74";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20385";

        sdk = OntSdk.getInstance();
        sdk.setRpc(rpcUrl);
        sdk.setRestful(restUrl);
        sdk.setDefaultConnect(sdk.getRestful());

        appContext  = InstrumentationRegistry.getTargetContext();
        sdk.openWalletFile(appContext.getSharedPreferences("wallet", Context.MODE_PRIVATE));
    }

    @Test
    public void registerCandidate() throws Exception {
        Account payerAcct = new Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
        Account account1 = new Account(Helper.hexToBytes(privatekey1),SignatureScheme.SHA256WITHECDSA);
        Account account9 = new Account(Helper.hexToBytes(privatekey9),SignatureScheme.SHA256WITHECDSA);
        System.out.println(Helper.toHexString(account1.serializePublicKey()));
        System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
        Account account7 = new Account(Helper.hexToBytes(privatekey7),SignatureScheme.SHA256WITHECDSA);
        Account account6 = new Account(Helper.hexToBytes(privatekey6),SignatureScheme.SHA256WITHECDSA);

        if(true){
//            Identity adminOntid = sdk.getWalletMgr().importIdentity("ET5m04btJ/bhRvSomqfqSY05M1mlmePU74mY+yvpIjY=",password,account.getAddressU160().toBase58());
            String contractAddr = "ff00000000000000000000000000000000000007";
//            String txhash = sdk.nativevm().auth().assignFuncsToRole(adminOntid.ontid,password,contractAddr,"role",new String[]{"registerCandidate"},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//            String txhash = sdk.nativevm().auth().assignOntIDsToRole(adminOntid.ontid,password,contractAddr,"role",new String[]{adminOntid.ontid},1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);

//            String txhash = sdk.nativevm().governance().registerCandidate(account,Helper.toHexString(account6.serializePublicKey()),100000,adminOntid.ontid,password,1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//            String txhash = sdk.nativevm().governance().approveCandidate(adminOntid.ontid,password,Helper.toHexString(account6.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().quitNode(account,Helper.toHexString(account1.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
            String txhash = sdk.nativevm().governance().withdraw(account,new String[]{Helper.toHexString(account6.serializePublicKey())},new long[]{100},account,sdk.DEFAULT_GAS_LIMIT,0);
//                String txhash = sdk.nativevm().governance().commitDpos(adminOntid.ontid,password,account,sdk.DEFAULT_GAS_LIMIT,0);
//            String txhash = sdk.nativevm().governance().voteForPeer(account,new String[]{Helper.toHexString(account6.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
//            String txhash = sdk.nativevm().governance().unVoteForPeer(account,new String[]{Helper.toHexString(account6.serializePublicKey())},new long[]{100},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);

            Thread.sleep(6000);
            System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            System.out.println(sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
        }
        System.out.println("account:" + sdk.getConnect().getBalance(account.getAddressU160().toBase58()));
        String res = sdk.nativevm().governance().getPeerInfoAll();
        JSONObject jsr = JSONObject.parseObject(res);
        System.out.println(jsr.getString(Helper.toHexString(account6.serializePublicKey())));
    }
}
