/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.sdk.manager;

import android.content.SharedPreferences;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.crypto.MnemonicCode;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;


/**
 *
 */
public class WalletMgr {
    private Wallet wallet;
    private Map acctPriKeyMap = new HashMap();
    private Map identityPriKeyMap = new HashMap();
    private Wallet walletFile;
    private SignatureScheme scheme = null;
    private String filePath = null;
    private SharedPreferences sp;
    private static final String key = "wallet_file";

    public static boolean priKeyStoreInMem = true;//for dont need decode every time

    public WalletMgr(Wallet wallet,SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.wallet = wallet;
        this.walletFile = wallet;
    }


    public WalletMgr(SharedPreferences sp, SignatureScheme scheme) throws IOException {
        this.sp = sp;
        this.scheme = scheme;
        String text = sp.getString(key, "");
        if (text.isEmpty()) {
            wallet = new Wallet();
            wallet.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
            walletFile = new Wallet();
            writeWallet();
        } else {
            Log.i("ontsdk", "WalletMgr: " + text);
            wallet = JSON.parseObject(text, Wallet.class);
            walletFile = JSON.parseObject(text, Wallet.class);
        }
    }

    private WalletMgr(SharedPreferences sp, String password, KeyType type, Object[] params) throws Exception {
        String text = sp.getString(key, "");
        if (text.isEmpty()) {
            wallet = new Wallet();
            wallet.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
            walletFile = new Wallet();
            writeWallet();
        } else {
            Log.i("ontsdk", "WalletMgr: " + text);
            wallet = JSON.parseObject(text, Wallet.class);
            walletFile = JSON.parseObject(text, Wallet.class);
        }
        wallet = JSON.parseObject(text, Wallet.class);
        walletFile = JSON.parseObject(text, Wallet.class);
        if (getIdentitys().size() == 0) {
            createIdentity(password);
            writeWallet();
            return;
        }
        Identity identity = getDefaultIdentity();
        if (identity != null) {
            String addr = identity.ontid.replace(Common.didont, "");
            String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(identity.controls.get(0).key, password, addr, walletFile.getScrypt().getN(), scheme);
            storePrivateKey(identityPriKeyMap, identity.ontid, password, prikey);
        }
    }

    private static void writeFile(SharedPreferences sp, String sets) throws IOException {
        Log.i("ontsdk", "writeFile: " + sets);
        boolean isSuccess = sp.edit().putString(key, sets).commit();
        if (!isSuccess) {
            throw new IOException("Wallet File Write Error");
        }
    }

    public Wallet openWallet() {
        return walletFile;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public Wallet writeWallet() throws IOException {
        writeFile(sp, JSON.toJSONString(wallet));
        walletFile = wallet;
        return walletFile;
    }

    public SignatureScheme getSignatureScheme() {
        return scheme;
    }

    public void setSignatureScheme(SignatureScheme scheme) {
        this.scheme = scheme;
    }

    private void storePrivateKey(Map map, String key, String password, String prikey) {
        if(priKeyStoreInMem){
            map.put(key + "," + password, prikey);
        }
    }



    public String exportPrikey(Account account, String password) throws Exception {
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(account.key, password, account.address, walletFile.getScrypt().getN(), scheme);
        return prikey;
    }

    public Identity importIdentity(String label, String encryptedPrikey, String password, String address) throws Exception {
        byte[] prefix = Helper.hexToBytes(Helper.getPrefix(address));
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(encryptedPrikey, password, prefix, walletFile.getScrypt().getN(), scheme);
        IdentityInfo info = createIdentity(label, password, Helper.hexToBytes(prikey));
        storePrivateKey(identityPriKeyMap, info.ontid, password, prikey);
        return getIdentity(info.ontid);
    }

    /**
     * @param encryptedPrikey
     * @param password
     * @param prefix
     * @return
     * @throws Exception
     */
    public Identity importIdentity(String label, String encryptedPrikey, String password, byte[] prefix) throws Exception {
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(encryptedPrikey, password, prefix, walletFile.getScrypt().getN(), scheme);
        IdentityInfo info = createIdentity(label, password, Helper.hexToBytes(prikey));
        storePrivateKey(identityPriKeyMap, info.ontid, password, prikey);
        return getIdentity(info.ontid);
    }


    public Identity createIdentity(String password) throws Exception {
        IdentityInfo info = createIdentity("", password, ECC.generateKey());
        return getIdentity(info.ontid);
    }

    public Identity createIdentity(String label, String password) throws Exception {
        IdentityInfo info = createIdentity(label, password, ECC.generateKey());
        return getIdentity(info.ontid);
    }

    public Identity createIdentityFromPriKey(String password, String prikey) throws Exception {
        IdentityInfo info = createIdentity("", password, Helper.hexToBytes(prikey));
        return getIdentity(info.ontid);
    }

    public IdentityInfo createIdentityInfo(String password) throws Exception {
        IdentityInfo info = createIdentity("", password, ECC.generateKey());
        return info;
    }

    public IdentityInfo createIdentityInfo(String label, String password) throws Exception {
        IdentityInfo info = createIdentity(label, password, ECC.generateKey());
        return info;
    }

    public IdentityInfo getIdentityInfo(String ontid, String password) throws Exception {
        com.github.ontio.account.Account acct = getAccountByAddress(Address.decodeBase58(ontid.replace(Common.didont, "")), password);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toString();
        return info;
    }

    private IdentityInfo createIdentity(String label, String password, byte[] prikey) throws Exception {
        com.github.ontio.account.Account acct = createAccount(label, password, prikey, false);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toHexString();
        storePrivateKey(identityPriKeyMap, info.ontid, password, Helper.toHexString(prikey));
        return info;
    }

    public Account importAccount(String label, String encryptedPrikey, String password, String address) throws Exception {
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(encryptedPrikey, password, address, walletFile.getScrypt().getN(), scheme);
        AccountInfo info = createAccount(label, password, Helper.hexToBytes(prikey));
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, prikey);
        return getAccount(info.addressBase58);
    }

    public Account importAccount(String label, String prikey, String password) throws Exception {
        AccountInfo info = createAccount(label,password,Helper.hexToBytes(prikey));
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, prikey);
        return getAccount(info.addressBase58);
    }

    public Account importAccountFromMnemonicCodes(String label, String[] mnemonicCodes, String password) throws Exception {
        List<String> mnemonicCodesArray = Arrays.asList(mnemonicCodes);
        MnemonicValidator.ofWordList(English.INSTANCE).validate(mnemonicCodesArray);
        byte[] seed = new SeedCalculator()
                .withWordsFromWordList(English.INSTANCE)
                .calculateSeed(mnemonicCodesArray, "");
        byte[] prikey = Arrays.copyOfRange(seed,0,32);
        String prikeyStr = Helper.toHexString(prikey);
        Account account = importAccount(label,prikeyStr,password);
        return account;
    }

    /**
     * @param encryptedPrikey
     * @param password
     * @param prefix
     * @return
     * @throws Exception
     */
    public Account importAccount(String label, String encryptedPrikey, String password, byte[] prefix) throws Exception {
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(encryptedPrikey, password, prefix, walletFile.getScrypt().getN(), scheme);
        AccountInfo info = createAccount(label, password, Helper.hexToBytes(prikey));
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, prikey);
        return getAccount(info.addressBase58);
    }

    public Account createAccount(String password) throws Exception {
        Account account = createAccount("", password);
        return account;
    }



    public Account createAccount(String label, String password) throws Exception {
        byte[] prikey = new byte[256];
        new SecureRandom().nextBytes(prikey);
        AccountInfo info = createAccount(label, password,prikey);
        new SecureRandom().nextBytes(prikey);
        Account account = getAccount(info.addressBase58);

        return account;
    }



    private AccountInfo createAccount(String label, String password, byte[] prikey) throws Exception {
        com.github.ontio.account.Account acct = createAccount(label, password, prikey, true);
        AccountInfo info = new AccountInfo();
        info.addressBase58 = Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toHexString();
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, Helper.toHexString(prikey));
        return info;
    }

    public Account getDefaultAccount() {
        for (Account e : wallet.getAccounts()) {
            if (e.isDefault) {
                return e;
            }
        }
        return null;
    }

    public Account createAccountFromPriKey(String password, String prikey) throws Exception {
        AccountInfo info = createAccount("", password, Helper.hexToBytes(prikey));
        return getAccount(info.addressBase58);
    }

    public Account createAccountFromPriKey(String label,String password, String prikey) throws Exception {
        AccountInfo info = createAccount(label,password, Helper.hexToBytes(prikey));
        return getAccount(info.addressBase58);
    }

    public AccountInfo createAccountInfo(String password) throws Exception {
        AccountInfo info = createAccount("", password, ECC.generateKey());
        return info;
    }

    public AccountInfo createAccountInfo(String label,String password) throws Exception {
        AccountInfo info = createAccount(label,password, ECC.generateKey());
        return info;
    }

    public AccountInfo createAccountInfoFromPriKey(String password, String prikey) throws Exception {
        return createAccount("", password, Helper.hexToBytes(prikey));
    }

    public IdentityInfo createIdentityInfoFromPriKey(String password, String prikey) throws Exception {
        return createIdentity("", password, Helper.hexToBytes(prikey));
    }

    public String privateKeyToWif(String privateKey) throws Exception {
        com.github.ontio.account.Account act = new com.github.ontio.account.Account(Helper.hexToBytes(privateKey), scheme);
        return act.exportWif();
    }

    public byte[] signatureData(com.github.ontio.account.Account acct, String str) throws Exception {
        DataSignature sign = null;
        sign = new DataSignature(getSignatureScheme(), acct, str);
        return sign.signature();
    }

    public boolean verifySign(String pubkeyStr, byte[] data, byte[] signature) throws Exception {
        DataSignature sign = null;

        sign = new DataSignature();
        return sign.verifySignature(new com.github.ontio.account.Account(false, Helper.hexToBytes(pubkeyStr)), data, signature);

    }

    public com.github.ontio.account.Account getAccount(String address, String password) throws Exception {
        address = address.replace(Common.didont, "");
        return getAccountByAddress(Address.decodeBase58(address), password);
    }

    private com.github.ontio.account.Account createAccount(String label, String password, String prikey) throws Exception {
        return createAccount(label, password, Helper.hexToBytes(prikey), true);
    }

    private Identity addIdentity(String ontid) {
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(ontid)) {
                return e;
            }
        }
        Identity identity = new Identity();
        identity.ontid = ontid;
        identity.controls = new ArrayList<Control>();
        wallet.getIdentities().add(identity);
        return identity;
    }

    private void addIdentity(Identity idt) {
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(idt.ontid)) {
                return;
            }
        }
        wallet.getIdentities().add(idt);
    }

    public List<Account> getAccounts() {
        return wallet.getAccounts();
    }

    public AccountInfo getAccountInfo(String address, String password) throws Exception {
        address = address.replace(Common.didont, "");
        AccountInfo info = new AccountInfo();
        com.github.ontio.account.Account acc = getAccountByAddress(Address.decodeBase58(address), password);
        info.addressBase58 = address;
        info.pubkey = Helper.toHexString(acc.serializePublicKey());
        info.setPrikey(Helper.toHexString(acc.serializePrivateKey()));
        info.encryptedPrikey = acc.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.setPriwif(acc.exportWif());
        info.addressU160 = acc.getAddressU160().toString();
        return info;
    }

    public List<Identity> getIdentitys() {
        return wallet.getIdentities();
    }

    public Identity getIdentity(String ontid) {
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(ontid)) {
                return e;
            }
        }
        return null;
    }

    public Identity getDefaultIdentity() {
        for (Identity e : wallet.getIdentities()) {
            if (e.isDefault) {
                return e;
            }
        }
        return null;
    }

    public Account getAccount(String address) {
        for (Account e : wallet.getAccounts()) {
            if (e.address.equals(address)) {
                return e;
            }
        }
        return null;
    }

    public Identity addOntIdController(String ontid, String key, String id) {
        Identity identity = getIdentity(ontid);
        if (identity == null) {
            identity = addIdentity(ontid);
        }
        for (Control e : identity.controls) {
            if (e.key.equals(key)) {
                return identity;
            }
        }
        Control control = new Control(key, id);
        identity.controls.add(control);
        return identity;
    }

    private com.github.ontio.account.Account createAccount(String label, String password, byte[] privateKey, boolean saveAccountFlag) throws Exception {
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(privateKey, scheme);
        Account acct;
        switch (scheme) {
            case SHA256WITHECDSA:
                acct = new Account("ECDSA", new Object[]{Curve.P256.toString()}, "aes-256-ctr", "SHA256withECDSA", "sha256");
                break;
            case SM3WITHSM2:
                acct = new Account("SM2", new Object[]{Curve.SM2P256V1.toString()}, "aes-256-ctr", "SM3withSM2", "sha256");
                break;
            default:
                throw new SDKException(ErrorCode.TypeError);
        }
        if (password != null) {
            acct.key = account.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        } else {
            acct.key = Helper.toHexString(account.serializePrivateKey());
        }
        acct.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();
        if (label == null || label.equals("")) {
            String uuidStr = UUID.randomUUID().toString();
            label = uuidStr.substring(0, 8);
        }
        if (saveAccountFlag) {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(acct.address)) {
                    throw new SDKException(ErrorCode.ParamErr("wallet account exist"));
                }
            }
            if (wallet.getAccounts().size() == 0) {
                acct.isDefault = true;
                wallet.setDefaultAccountAddress(acct.address);
            }
            acct.label = label;
            acct.passwordHash = Helper.toHexString(Digest.sha256(password.getBytes()));
            wallet.getAccounts().add(acct);
        } else {
            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals(Common.didont + acct.address)) {
                    return account;
                }
            }
            Identity idt = new Identity();
            idt.ontid = Common.didont + acct.address;
            idt.label = label;
            if (wallet.getIdentities().size() == 0) {
                idt.isDefault = true;
                wallet.setDefaultOntid(idt.ontid);
            }
            idt.controls = new ArrayList<Control>();
            Control ctl = new Control(acct.key, "");
            idt.controls.add(ctl);
            wallet.getIdentities().add(idt);
        }
        return account;
    }

    private com.github.ontio.account.Account getAccountByAddress(Address address, String password) throws Exception {
        for (Account e : wallet.getAccounts()) {
            if (e.address.equals(address.toBase58())) {
                String prikey = (String) acctPriKeyMap.get(e.address + "," + password);
                if (prikey == null) {
                    prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(e.key, password, e.address, walletFile.getScrypt().getN(), scheme);
                    storePrivateKey(acctPriKeyMap, e.address, password, prikey);
                }
                return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), scheme);
            }
        }
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(Common.didont + address.toBase58())) {
                String prikey = (String) identityPriKeyMap.get(e.ontid + "," + password);
                if (prikey == null) {
                    String addr = e.ontid.replace(Common.didont, "");
                    prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(e.controls.get(0).key, password, addr, walletFile.getScrypt().getN(), scheme);
                    storePrivateKey(identityPriKeyMap, e.ontid, password, prikey);
                }
                return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), scheme);
            }
        }
        throw new SDKException(ErrorCode.GetAccountByAddressErr);
    }
}
