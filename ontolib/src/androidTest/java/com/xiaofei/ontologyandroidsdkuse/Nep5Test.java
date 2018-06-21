package com.xiaofei.ontologyandroidsdkuse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.alibaba.fastjson.JSON;
import com.github.neo.core.Program;
import com.github.neo.core.SmartContract;
import com.github.neo.core.transaction.TransactionNeo;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.smartcontract.nativevm.Ong;
import com.github.ontio.smartcontract.nativevm.Ont;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class Nep5Test {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;
    private Ont ont;
    private Ong ong;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private Context appContext;
    private OntId ontIdTx;
    String password = "111111";
    public static String privatekey = "75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf";


    public static String privatekey1 = "1094e90dd7c4fdfd849c14798d725ac351ae0d924b29a279a9ffa77d5737bd96";
    public static String privatekey2 = "bc254cf8d3910bc615ba6bf09d4553846533ce4403bc24f58660ae150a6d64cf";
    public static String contractAddr = "5bb169f915c916a5e30a3c13a5e0cd228ea26826";
    public static String nodeUrl = "http://seed2.neo.org:20332";
    public static String nep5abi = "{\"hash\":\"0x5bb169f915c916a5e30a3c13a5e0cd228ea26826\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Name\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"Symbol\",\"parameters\":[],\"returntype\":\"String\"},{\"name\":\"Decimals\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"Init\",\"parameters\":[],\"returntype\":\"Boolean\"},{\"name\":\"TotalSupply\",\"parameters\":[],\"returntype\":\"Integer\"},{\"name\":\"Transfer\",\"parameters\":[{\"name\":\"from\",\"type\":\"ByteArray\"},{\"name\":\"to\",\"type\":\"ByteArray\"},{\"name\":\"value\",\"type\":\"Integer\"}],\"returntype\":\"Boolean\"},{\"name\":\"BalanceOf\",\"parameters\":[{\"name\":\"address\",\"type\":\"ByteArray\"}],\"returntype\":\"Integer\"}],\"events\":[{\"name\":\"transfer\",\"parameters\":[{\"name\":\"arg1\",\"type\":\"ByteArray\"},{\"name\":\"arg2\",\"type\":\"ByteArray\"},{\"name\":\"arg3\",\"type\":\"Integer\"}],\"returntype\":\"Void\"}]}";


    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
//        ontSdk.setRestful("http://polaris1.ont.io:20334");
//        ontSdk.setRestful("http://139.219.128.60:20334");
//        ontSdk.setRestful("http://192.168.50.74:20334");
        ontSdk.setRpc(nodeUrl);
        appContext  = InstrumentationRegistry.getTargetContext();
        ontSdk.openWalletFile(appContext.getSharedPreferences("wallet",Context.MODE_PRIVATE));
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        connectMgr = ontSdk.getConnect();
        ont = ontSdk.nativevm().ont();
        ontIdTx = ontSdk.nativevm().ontId();
    }

    @Test
    public void hahahTest(){
        String[] aa = {"dae","cou","aqw","boq"};

        System.out.println(aa);
    }

    @Test
    public void Nep5Test() throws Exception {
        System.out.println("Hi NEO, Nep-5 smartcontract invoke test!");
        com.github.ontio.account.Account acct1 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey1), SignatureScheme.SHA256WITHECDSA);
        com.github.ontio.account.Account acct2 = new com.github.ontio.account.Account(Helper.hexToBytes(privatekey2), SignatureScheme.SHA256WITHECDSA);
        Address multiSignAddr = Address.addressFromMultiPubKeys(2,acct1.serializePublicKey(),acct2.serializePublicKey());

        //read smarcontract abi file
//		InputStream is2 = new FileInputStream("nep-5.abi.json");
//		byte[] bys2 = new byte[is2.available()];
//		is2.read(bys2);
//		is2.close();
//		String nep5abi = new String(bys2);

        AbiInfo abiinfo = JSON.parseObject(nep5abi, AbiInfo.class);
        System.out.println("Entrypoint:" + abiinfo.getEntrypoint());
        System.out.println("contractAddress:"+abiinfo.getHash());
        System.out.println("Functions:" + abiinfo.getFunctions());

        System.out.println("acct1 address:" + acct1.getAddressU160().toBase58()+" "+Helper.toHexString(acct1.getAddressU160().toArray()));
        System.out.println("acct2 address:" + acct2.getAddressU160().toBase58()+" "+Helper.toHexString(acct2.getAddressU160().toArray()));
        System.out.println("multi address:" + multiSignAddr.toBase58()+" "+Helper.toHexString(multiSignAddr.toArray()));
        if(true) {
            long balance =  getBalance(nodeUrl, contractAddr, Helper.toHexString(acct1.getAddressU160().toArray()));

            System.out.println("acct1: " + balance);
            balance =  getBalance(nodeUrl, contractAddr, Helper.toHexString(acct2.getAddressU160().toArray()));
            System.out.println("acct2: " + balance);
            balance = getBalance(nodeUrl, contractAddr, Helper.toHexString(multiSignAddr.toArray()));
            System.out.println("");
            System.out.println("multiSignAddr: " + balance);
            System.out.println("");
        }
        if(false) {
            Address recv = multiSignAddr;//acct2.getAddressU160()
            AbiFunction func = abiinfo.getFunction("Transfer");
            func.name = func.name.toLowerCase();
            func.setParamsValue(acct1.getAddressU160().toArray(), recv.toArray(), Long.valueOf(3000));

            //make transaction
            TransactionNeo tx = SmartContract.makeInvocationTransaction(Helper.reverse(contractAddr), acct1.getAddressU160().toArray(), func);
            tx.scripts = new Program[1];
            tx.scripts[0] = new Program();
            tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct1, SignatureScheme.SHA256WITHECDSA)});
            tx.scripts[0].code =  Program.ProgramFromPubKey(acct1.serializePublicKey());
            if(false){
                tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct1, SignatureScheme.SHA256WITHECDSA),tx.sign(acct2, SignatureScheme.SHA256WITHECDSA)});
                tx.scripts[0].code =  Program.ProgramFromMultiPubKey(2,acct1.serializePublicKey(),acct2.serializePublicKey());
            }
            System.out.println(tx.toHexString());
            System.out.println(tx.hash().toString());
            System.out.println(Helper.toHexString(Program.ProgramFromPubKey(acct1.serializePublicKey())));
            //send tx to neo node
            sendRawTransaction(nodeUrl,tx.toHexString());
            Thread.sleep(6000);
        }

        if(false) { //multiSignAddr
            AbiFunction func = abiinfo.getFunction("Transfer");//BalanceOf
            func.name = func.name.toLowerCase();
            func.setParamsValue(multiSignAddr.toArray(), acct2.getAddressU160().toArray(), Long.valueOf(1));

            //make transaction
            TransactionNeo tx = SmartContract.makeInvocationTransaction(Helper.reverse(contractAddr), multiSignAddr.toArray(), func);
            tx.scripts = new Program[1];
            tx.scripts[0] = new Program();
            tx.scripts[0].parameter = Program.ProgramFromParams(new byte[][]{tx.sign(acct1, SignatureScheme.SHA256WITHECDSA),tx.sign(acct2, SignatureScheme.SHA256WITHECDSA)});
            tx.scripts[0].code =  Program.ProgramFromMultiPubKey(2,acct1.serializePublicKey(),acct2.serializePublicKey());

            System.out.println(tx.toHexString());
            System.out.println(tx.hash().toString());
            System.out.println(Helper.toHexString(Program.ProgramFromPubKey(acct1.serializePublicKey())));
            //send tx to neo node
            sendRawTransaction(nodeUrl,tx.toHexString());
        }
    }

    public static Object sendRawTransaction(String url,String sData) throws Exception {
        Object result = call(url,"sendrawtransaction", new Object[]{sData});
        return result;
    }
    public static long getBalance(String url,String contractAddr,String addr) throws Exception {
        Object result = call(url,"getstorage", new Object[]{contractAddr,addr});
        return new BigInteger(Helper.reverse(Helper.hexToBytes((String)result))).longValue();
    }
    public static Object call(String url,String method, Object... params) throws Exception
    {
        Map req = makeRequest(method, params);
        Map response = (Map) send(url,req);
        if (response == null) {
            throw new Exception( ErrorCode.OtherError(  url + "response is null. maybe is connect error"));
        }
        else if (response.get("result")  != null) {
            return response.get("result");
        }
        else if (response.get("Result")  != null) {
            return response.get("Result");
        }
        else if (response.get("error") != null) {
            throw new Exception(JSON.toJSONString(response));
        }
        else {
            throw new IOException();
        }
    }

    private static Map makeRequest(String method, Object[] params) {
        Map request = new HashMap();
        request.put("jsonrpc", "2.0");
        request.put("method", method);
        request.put("params", params);
        request.put("id", 1);
        System.out.println(String.format("POST %s", JSON.toJSONString(request)));
        return request;
    }


    public static Object send(String url,Object request) throws IOException {
        try {
            HttpURLConnection connection = (HttpURLConnection)  new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            try (OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream())) {
                w.write(JSON.toJSONString(request));
            }
            try (InputStreamReader r = new InputStreamReader(connection.getInputStream())) {
                StringBuffer temp = new StringBuffer();
                int c = 0;
                while ((c = r.read()) != -1) {
                    temp.append((char) c);
                }
                //System.out.println("result:"+temp.toString());
                return JSON.parseObject(temp.toString(), Map.class);
            }
        } catch (IOException e) {
        }
        return null;
    }

}
