package com.github.ontio;

import org.junit.Test;

import static org.junit.Assert.*;

public class SmokeTest {
    @Test
    public void smoke(){
        List<Object> params = new ArrayList<Object>();
        UInt256 u = UInt256.parse("2abf22c752d71f8d65b39f3b12159f965602bf22e942e51d08d1023b9a4ab02f");
        params.add(u);
        List<Object> params2 = new ArrayList<Object>();
        params2.add(params);
        byte[] bs = WasmScriptBuilder.createWasmInvokeCode("b3d448d29ea1c501d4bb90a89517a58f3fd7d469", "test", params2);
        Assert.assertEquals("69d4d73f8fa51795a890bbd401c5a19ed248d4b3260474657374012fb04a9a3b02d1081de542e922bf0256969f15123b9fb3658d1fd752c722bf2a",Helper.toHexString(bs));
    }

}