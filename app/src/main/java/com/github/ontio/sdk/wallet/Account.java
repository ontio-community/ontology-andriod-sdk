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

package com.github.ontio.sdk.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.sdk.exception.SDKException;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 */
public class Account {
    public String label = "";
    public String address = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public String algorithm = "";
    public Map parameters = new HashMap() ;
    public String key = "";
    @JSONField(name = "enc-alg")
    public String encAlg = "aes-256-ctr";
    public String hash = "sha256";
    public String signatureScheme = "SHA256withECDSA";
    public String passwordHash = "";
    public Object extra = null;
    public String[] mnemonicCodes;
    public String encryptedMnemonicCodesStr;
    public Account(){

    }
    public Account(String alg,Object[] params,String encAlg,String scheme,String hash){
        this.algorithm = alg;
        this.parameters.put("curve",params[0]);
        this.signatureScheme = scheme;
        this.encAlg = encAlg;
        this.hash = hash;
        this.extra = null;
    }

    public Object getExtra(){
        return extra;
    }

    public void setExtra(Object extra){
        this.extra = extra;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public String[] decryptedMnemonicCodesStr(String encryptedMnemonicCodesHexStr,String password) throws Exception {
        String passwordHashNew = Helper.toHexString(Digest.sha256(password.getBytes()));
        if (!passwordHashNew.equals(this.passwordHash)){
            throw new SDKException(ErrorCode.InvalidParams("password error"));
        }
        byte[] encryptedkey = Helper.hexToBytes(encryptedMnemonicCodesStr);
        byte[] derivedhalf2 = Digest.sha256(password.getBytes());
        byte[] iv = new byte[16];
        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] rawkey = cipher.doFinal(encryptedkey);
        String mnemonicCodesStr = new String(rawkey);
        String[] mnemonicCodesArray = mnemonicCodesStr.split(" ");
        return mnemonicCodesArray;
    }
}

