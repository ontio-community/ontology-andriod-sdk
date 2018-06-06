package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BuildParams {
    public static  byte[] buildParams(Object ...params) throws SDKException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            for (Object param : params) {
                if(param instanceof Integer){
                    bw.writeInt(((Integer) param).intValue());
                }else if(param instanceof byte[]){
                    bw.writeVarBytes((byte[])param);
                }else if(param instanceof String){
                    bw.writeVarString((String) param);
                }else if(param instanceof Attribute[]){
                    bw.writeSerializableArray((Attribute[])param);
                }else if(param instanceof Address){
                    bw.writeSerializable((Address)param);
                }
            }
        } catch (IOException e) {
            throw new SDKException(ErrorCode.WriteVarBytesError);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }
}
