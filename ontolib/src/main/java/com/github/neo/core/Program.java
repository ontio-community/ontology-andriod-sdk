package com.github.neo.core;


import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 */
public class Program extends Serializable {
    public byte[] parameter;
    public byte[] code;
    public Program(){}
    @Override
    public void deserialize(BinaryReader reader) throws IOException {
    	parameter = reader.readVarBytes();	// sign data
    	code = reader.readVarBytes();		// pubkey
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
    	writer.writeVarBytes(parameter);
    	writer.writeVarBytes(code);
    }
//    public static byte[] ProgramFromParams(byte[][] sigData) throws IOException {
//        ScriptBuilder sb = new ScriptBuilder();
//        sigData = Arrays.stream(sigData).sorted((o1, o2) -> {
//            return Helper.toHexString(o1).compareTo(Helper.toHexString(o2));
//        }).toArray(byte[][]::new);
//        byte[][] sigData2 = new byte[sigData.length][];
//        for(int i= 0;i<sigData.length;i++) {
//            for(int j=i;j<sigData.length;j++){
//                if(sigData[j])
//            }
//        }
//        for (byte[] sig : sigData) {
//            sb.push(sig);
//        }
//        return sb.toArray();
//    }

    public static byte[] ProgramFromParams(byte[][] sigData) throws IOException, SDKException {
        ScriptBuilder sb = new ScriptBuilder();
        Arrays.sort(sigData, new Comparator<byte[]>() {
            @Override
            public int compare(byte[] a, byte[] b) {
                return Helper.toHexString(a).compareTo(Helper.toHexString(b));
            }
        });
        for (byte[] sig : sigData) {
            sb.push(sig);
        }
        return sb.toArray();
    }


    public static byte[] ProgramFromPubKey(byte[] publicKey) throws Exception {
        ScriptBuilder sb = new ScriptBuilder();
        sb.push(publicKey);
        sb.add(ScriptOp.OP_CHECKSIG);
        return sb.toArray();
    }

    public static byte[] ProgramFromMultiPubKey(int m, byte[]... publicKeys) throws Exception {
        int n = publicKeys.length;

        if (m <= 0 || m > n || n > 24) {
            throw new SDKException(ErrorCode.ParamError);
        }
        try (ScriptBuilder sb = new ScriptBuilder()) {
            sb.push(BigInteger.valueOf(m));
            Arrays.sort(publicKeys, new Comparator<byte[]>() {
                @Override
                public int compare(byte[] a, byte[] b) {
                    return Helper.toHexString(a).compareTo(Helper.toHexString(b));
                }
            });


            for (byte[] publicKey : publicKeys) {
                sb.push(publicKey);
            }
            sb.push(BigInteger.valueOf(publicKeys.length));
            sb.add(ScriptOp.OP_CHECKMULTISIG);
            return sb.toArray();
        }
    }

//    public static byte[] ProgramFromMultiPubKey(int m, byte[]... publicKeys) throws Exception {
//        int n = publicKeys.length;
//
//        if (m <= 0 || m > n || n > 24) {
//            throw new SDKException(ErrorCode.ParamError);
//        }
//        try (ScriptBuilder sb = new ScriptBuilder()) {
//            sb.push(BigInteger.valueOf(m));
//            publicKeys = Arrays.stream(publicKeys).sorted((o1, o2) -> {
//                return Helper.toHexString(o1).compareTo(Helper.toHexString(o2));
//            }).toArray(byte[][]::new);
//
//            for (byte[] publicKey : publicKeys) {
//                sb.push(publicKey);
//            }
//            sb.push(BigInteger.valueOf(publicKeys.length));
//            sb.add(ScriptOp.OP_CHECKMULTISIG);
//            return sb.toArray();
//        }
//    }

}
