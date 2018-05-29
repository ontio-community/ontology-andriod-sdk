package com.github.ontio.common;

import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class WalletQR {
    public static Map exportIdentityQRCode(Wallet walletFile, Identity identity) throws NoSuchAlgorithmException {
        Control control = identity.controls.get(0);
        String address = identity.ontid.substring(8);
        String prefix = Helper.getPrefix(address);
        Map map = new HashMap();
        map.put("type", "I");
        map.put("label",identity.label);
        map.put("key", control.key);
        map.put("parameters", control.parameters);
        map.put("prefix", prefix);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", walletFile.getScrypt());
        return map;
    }

    public static Map exportAccountQRCode(Wallet walletFile,Account account) throws NoSuchAlgorithmException {
        Map map = new HashMap();
        map.put("type", "A");
        map.put("label", account.label);
        map.put("key", account.key);
        map.put("prefix", Helper.getPrefix(account.address));
        map.put("parameters", account.parameters);
        map.put("algorithm", "ECDSA");
        map.put("scrypt", walletFile.getScrypt());
        return map;
    }
}
