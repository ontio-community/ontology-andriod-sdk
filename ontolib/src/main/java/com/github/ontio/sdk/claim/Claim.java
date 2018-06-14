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

package com.github.ontio.sdk.claim;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.crypto.Digest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.crypto.SignatureScheme;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import android.util.Base64;


/**
 * Claim
 */
public class Claim {
    private String context = "";
    private String id = UUID.randomUUID().toString();
    private Map<String, Object> claim = new HashMap<String, Object>();
    String ClaimStr = "";

    public Claim(SignatureScheme scheme, Account acct, String ctx, Map claimMap, Map metadata,String publicKeyId) throws Exception {
        context = ctx;
        claim.put("Context", context);
        if (claimMap != null) {
            claim.put("Content", claimMap);
        }
        claim.put("Metadata", new MetaData(metadata).getJson());
        id = Helper.toHexString(Digest.sha256(JSON.toJSONString(claim).getBytes()));
        claim.put("Id", id);
        claim.put("Version", "v1.0");
        DataSignature sign = new DataSignature(scheme, acct, getClaim());

        byte[] signature = sign.signature();
        SignatureInfo info = new SignatureInfo("", "",publicKeyId, signature);
        claim.put("Signature", info.getJson());

    }

    public Claim(SignatureScheme scheme, Account acct, String ctx, Map clmMap, Map<String,String> metadata,Map clmRevMap,String publicKeyId,long expireTime) throws Exception {
        String iss = metadata.get("Issuer");
        String sub = metadata.get("Subject");
        Header header = new Header("","",publicKeyId);
        Payload payload = new Payload("v1.0",iss,sub,System.currentTimeMillis()/1000,expireTime,ctx,clmMap,clmRevMap);
        String headerStr = JSONObject.toJSONString(header.getJson());
        String payloadStr = JSONObject.toJSONString(payload.getJson());
        byte[] headerBytes = Base64.encode(headerStr.getBytes(),Base64.DEFAULT);
        byte[] payloadBytes = Base64.encode(payloadStr.getBytes(),Base64.DEFAULT);
        DataSignature sign = new DataSignature(scheme, acct, new String(headerBytes) + "." + new String(payloadBytes));
        byte[] signature = sign.signature();
        ClaimStr += new String(headerBytes) + "." + new String(payloadBytes) + "." + new String(Base64.encode(signature,Base64.DEFAULT));
    }

    public String getClaim() {
        Map tmp = new HashMap<String, Object>();
        for (Map.Entry<String, Object> e : claim.entrySet()) {
            tmp.put(e.getKey(), e.getValue());
        }
        return JSONObject.toJSONString(tmp);
    }

    public String getClaimStr() {
        return ClaimStr;
    }
}



class Header {
    public String Alg = "ONT-ES256";
    public String Typ = "JWT-X";
    public String Kid;
    public Header(String alg,String typ, String kid) {
//        Alg = alg;
//        Typ = typ;
        Kid = kid;
    }
    public Object getJson() {
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("alg", Alg);
        header.put("typ", Typ);
        header.put("kid", Kid);
        return header;
    }
}
class Payload {
    public String Ver;
    public String Iss;
    public String Sub;
    public long Iat;
    public long Exp;
    public String Jti;
    @JSONField(name = "@context")
    public String Context;
    public Map<String, Object> ClmMap = new HashMap<String, Object>();
    public Map<String, Object> ClmRevMap = new HashMap<String, Object>();

    public Payload(String ver,String iss,String sub,long iat,long exp,String ctx,Map clmMap,Map clmRevMap) throws NoSuchAlgorithmException {
        Ver = ver;
        Iss = iss;
        Sub = sub;
        Iat = iat;
        Exp = exp;
        Context = ctx;
        ClmMap = clmMap;
        ClmRevMap = clmRevMap;
        Jti = Helper.toHexString(Digest.sha256(JSON.toJSONString(getJson()).getBytes()));
    }

    public Object getJson() {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("ver", Ver);
        payload.put("iss", Iss);
        payload.put("sub", Sub);
        payload.put("iat", Iat);
        payload.put("exp", Exp);
        payload.put("jti", Jti);
        payload.put("@context", Context);
        payload.put("clm",ClmMap);
        payload.put("clm-rev",ClmRevMap);
        return payload;
    }
}
class SignatureInfo {

    private String Format = "pgp";
    private String Algorithm = "ECDSAwithSHA256";
    private byte[] Value;
    private String PublicKeyId;

    public SignatureInfo(String format, String alg ,String publicKeyId,byte[] val) {
        Value = val;
        PublicKeyId = publicKeyId;
    }

    public Object getJson() {
        Map<String, Object> signature = new HashMap<String, Object>();
        signature.put("Format", Format);
        signature.put("Algorithm", Algorithm);
        signature.put("Value", Value);
        signature.put("PublicKeyId", PublicKeyId);
        return signature;
    }
}

class MetaData {

    private String CreateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date());//"2017-08-25T10:03:04Z";
    private Map<String, Object> meta = new HashMap();

    public MetaData(Map map) {
        if (map != null) {
            meta = map;
        }
    }

    public Object getJson() {
        meta.put("CreateTime", CreateTime);
        Map tmp = new HashMap<String, Object>();
        for (Map.Entry<String, Object> e : meta.entrySet()) {
            tmp.put(e.getKey(), e.getValue());
        }
        return tmp;
    }
}