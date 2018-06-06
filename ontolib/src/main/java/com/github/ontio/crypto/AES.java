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

package com.github.ontio.crypto;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.sdk.exception.SDKException;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] key, byte[] iv) throws Exception {
        if (key.length != 32 || iv.length != 16) {
            throw new SDKException(ErrorCode.ParamError);
        }
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
        params.init(new IvParameterSpec(iv));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "SC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
        return cipher.doFinal(encryptedData);
    }

    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        if (key.length != 32 || iv.length != 16) {
            throw new SDKException(ErrorCode.ParamError);
        }
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        AlgorithmParameters params = AlgorithmParameters.getInstance(KEY_ALGORITHM);
        params.init(new IvParameterSpec(iv));
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "SC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
        return cipher.doFinal(data);

    }

    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(iv);
        return iv;
    }

    public static byte[] generateKey() throws NoSuchAlgorithmException {
        SecretKey key = null;
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
        keyGenerator.init(256);
        key = keyGenerator.generateKey();
        return key.getEncoded();

    }

    public static byte[] generateKey(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        byte[] passwordBytes = null, passwordHash = null;
        try {
            passwordBytes = password.getBytes("UTF-8");
            passwordHash = Digest.sha256(passwordBytes);
            return Digest.sha256(passwordHash);
        } finally {
            if (passwordBytes != null) {
                Arrays.fill(passwordBytes, (byte) 0);
            }
            if (passwordHash != null) {
                Arrays.fill(passwordHash, (byte) 0);
            }
        }
    }
}
