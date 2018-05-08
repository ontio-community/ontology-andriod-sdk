package com.crypho.plugins;

public class ScryptPlugin {
    private static final String TAG = "Scrypt";

    static {
        System.loadLibrary("scrypt_crypho");
    }

    public static native byte[] scrypt(byte[] pass, char[] salt, Integer N, Integer r, Integer p, Integer dkLen);
}