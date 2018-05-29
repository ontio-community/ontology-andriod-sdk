package com.github.ontio.crypto;

import android.util.Base64;

import com.crypho.plugins.ScryptPlugin;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.exception.SDKException;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class MnemonicCode {

    public static String generateMnemonicCodesStr(){
        final StringBuilder sb = new StringBuilder();
        byte[] entropy = new byte[Words.TWELVE.byteLength()];
        new SecureRandom().nextBytes(entropy);
        new MnemonicGenerator(English.INSTANCE).createMnemonic(entropy, new MnemonicGenerator.Target() {
            @Override
            public void append(CharSequence string) {
                sb.append(string);
            }
        });
        new SecureRandom().nextBytes(entropy);
        return sb.toString();
    }

    public static byte[] getPrikeyFromMnemonicCodesStr(String mnemonicCodesStr){
        String[] mnemonicCodesArray = mnemonicCodesStr.split(" ");
        byte[] seed = new SeedCalculator()
                .withWordsFromWordList(English.INSTANCE)
                .calculateSeed(Arrays.asList(mnemonicCodesArray), "");
        byte[] prikey = Arrays.copyOfRange(seed,0,32);
        return prikey;
    }

    public static String encryptMnemonicCodesStr(String mnemonicCodesStr, String password, String addr) throws Exception {
        int N = 4096;
        int r = 8;
        int p = 8;
        int dkLen = 64;
        addr = Helper.toHexString(addr.getBytes());
        Address script_hash = Address.addressFromPubKey(addr);
        String address = script_hash.toBase58();

        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] salt = Arrays.copyOfRange(addresshashTmp, 0, 4);
        byte[] derivedkey = ScryptPlugin.scrypt(password.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);

        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[16];
        System.arraycopy(derivedkey, 0, iv, 0, 16);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] encryptedkey = cipher.doFinal(mnemonicCodesStr.getBytes());
        return new String(Base64.encode(encryptedkey, Base64.DEFAULT));

    }

    public static String decryptMnemonicCodesStr(String encryptedMnemonicCodesStr, String password,String addr) throws Exception {
        if (encryptedMnemonicCodesStr == null) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] encryptedkey = Base64.decode(encryptedMnemonicCodesStr, Base64.DEFAULT);

        int N = 4096;
        int r = 8;
        int p = 8;
        int dkLen = 64;

        addr = Helper.toHexString(addr.getBytes());
        Address script_hash = Address.addressFromPubKey(addr);
        String address = script_hash.toBase58();
        byte[] addresshashTmp = Digest.sha256(Digest.sha256(address.getBytes()));
        byte[] salt = Arrays.copyOfRange(addresshashTmp, 0, 4);

        byte[] derivedkey = ScryptPlugin.scrypt(password.getBytes(StandardCharsets.UTF_8), getChars(salt), N, r, p, dkLen);
        byte[] derivedhalf2 = new byte[32];
        byte[] iv = new byte[16];
        System.arraycopy(derivedkey, 0, iv, 0, 16);
        System.arraycopy(derivedkey, 32, derivedhalf2, 0, 32);

        SecretKeySpec skeySpec = new SecretKeySpec(derivedhalf2, "AES");
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
        byte[] rawkey = cipher.doFinal(encryptedkey);

        return new String(rawkey);
    }

    private static char[] getChars(byte[] bytes) {
        return new String(bytes, 0, bytes.length).toCharArray();
    }
}
