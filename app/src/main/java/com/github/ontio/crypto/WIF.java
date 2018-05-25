package com.github.ontio.crypto;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.sdk.exception.SDKException;
import com.google.common.primitives.Bytes;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class WIF
{


    /**
     * Converts a private key in byte array form into a Bitcoin-esque "Wallet Import Format" string. The
     * process is as follows: <br><br>
     *
     * 1) Prepend the decimal value 128 to the private key <br><br>
     * 2) Calculate the double SHA-256 hash of the private key with the extra byte <br><br>
     * 3) Take the first 4 bytes of that hash as a checksum for the private key<br><br>
     * 4) Add the checksum bytes onto the end of the private key with its extra byte<br><br>
     * 5) Convert the byte array containing the private key, extra byte, and checksum into a Base 58 encoded String.<br><br>
     *
     * <b>NOTE:</b> Somewhat confusingly, Bitmessage uses SHA-512 for its address generation and proof of work,
     * but uses SHA-256 for converting private keys into wallet import format.
     *
     * @param privateKey - The private key in byte[] format
     *
     * @return WIFPrivateKey - A String representation of the private key in "Wallet Import Format"
     */
    public static String encodePrivateKeyToWIF (byte[] privateKey) throws NoSuchAlgorithmException {
        // If first byte of the private encryption key generated is zero, remove it.
        if (privateKey[0] == 0)
        {
            privateKey = Arrays.copyOfRange(privateKey, 1, privateKey.length);
        }

        byte[] valueToPrepend = new byte[1];
        valueToPrepend[0] = (byte) 128;

        byte[] privateKeyWithExtraByte = Bytes.concat(valueToPrepend,privateKey);

        byte[] hashOfPrivateKey = Digest.hash256(privateKeyWithExtraByte);

        byte[] checksum = Arrays.copyOfRange(hashOfPrivateKey, 0, 4);

        byte[] convertedPrivateKey = Bytes.concat(privateKeyWithExtraByte, checksum);

        String walletImportFormatPrivateKey = Base58.encode(convertedPrivateKey);

        return walletImportFormatPrivateKey;
    }

    /**
     * Converts a private key "Wallet Import Format" (as used by Bitcoin) into an ECPrivateKey object. The process to do
     * so is as follows: <br><br>
     *
     * 1) Convert the Base58 encoded String into byte[] form.<br><br>
     * 2) Drop the last four bytes, which are the checksum.<br><br>
     * 3) Check that the checksum is valid for the remaining bytes.<br><br>
     * 4) Drop the first byte, which is the special value prepended to the key bytes during the WIF encoding process.<br><br>
     * 5) Check that the first byte equates to the decimal value 128.<br><br>
     * 6) The remaining bytes are the private key in two's complement form. Convert them into a BigInteger <br><br>
     * 7) Use newly created BigInteger value to create a new ECPrivateKey object.<br><br>
     *
     * <b>NOTE:</b> Somewhat confusingly, Bitmessage uses SHA-512 for its address generation and proof of work,
     * but uses SHA-256 for converting private keys into wallet import format.
     *
     * @param wifPrivateKey - A String representation of the private key in "Wallet Import Format"
     *
     * @return An ECPrivateKey object containing the private key
     */
    public static byte[] decodePrivateKeyFromWIF (String wifPrivateKey) throws Exception {
        byte[] privateKeyBytes = Base58.decode(wifPrivateKey);

        byte[] privateKeyWithoutChecksum = Arrays.copyOfRange(privateKeyBytes, 0, (privateKeyBytes.length - 4));

        byte[] checksum = Arrays.copyOfRange(privateKeyBytes, (privateKeyBytes.length - 4), privateKeyBytes.length);

        byte[] hashOfPrivateKey = Digest.hash256(privateKeyWithoutChecksum);

        byte[] testChecksum = Arrays.copyOfRange(hashOfPrivateKey, 0, 4);

        if (Arrays.equals(checksum, testChecksum) == false)
        {
            throw new SDKException(ErrorCode.InvalidParams);
        }

        // Check that the prepended 128 byte is in place
        if (privateKeyWithoutChecksum[0] != (byte) 128)
        {
            throw new SDKException(ErrorCode.InvalidParams);
        }

        // Drop the prepended 128 byte
        byte[] privateKeyFinalBytes = Arrays.copyOfRange(privateKeyWithoutChecksum, 1, privateKeyWithoutChecksum.length);

        // If the decoded private key has a negative value, this means that it originally
        // began with a zero byte which was stripped off during the encodeToWIF process. We
        // must now restore this leading zero byte.
        BigInteger privateKeyBigIntegerValue = new BigInteger(privateKeyFinalBytes);
        if (privateKeyBigIntegerValue.signum() < 1)
        {
            byte[] valueToPrepend = new byte[1];
            valueToPrepend[0] = (byte) 0;

            privateKeyFinalBytes = Bytes.concat(valueToPrepend, privateKeyFinalBytes);
        }

        byte[] resultBytes = Arrays.copyOfRange(privateKeyBytes,1,privateKeyFinalBytes.length-1);

        return resultBytes;
    }
}
