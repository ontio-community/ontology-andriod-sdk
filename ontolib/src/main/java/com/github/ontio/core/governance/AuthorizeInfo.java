package com.github.ontio.core.governance;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class AuthorizeInfo extends Serializable {
    public String peerPubkey;
    public Address address;
    public long consensusPos;
    public long freezePos;
    public long newPos;
    public long withdrawPos;
    public long withdrawFreezePos;
    public long withdrawUnfreezePos;

    public AuthorizeInfo(){}

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.peerPubkey = reader.readVarString();
        try {
            this.address = reader.readSerializable(Address.class);
            this.consensusPos = reader.readLong();
            this.freezePos = reader.readLong();
            this.newPos = reader.readLong();
            this.withdrawPos = reader.readLong();
            this.withdrawFreezePos = reader.readLong();
            this.withdrawUnfreezePos = reader.readLong();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }

    public String json() throws NoSuchAlgorithmException {
        Map map = new HashMap();
        map.put("peerPubkey",peerPubkey);
        map.put("address",address.toBase58());
        map.put("consensusPos",consensusPos);
        map.put("freezePos",freezePos);
        map.put("newPos",newPos);
        map.put("withdrawPos",withdrawPos);
        map.put("withdrawFreezePos",withdrawFreezePos);
        map.put("withdrawUnfreezePos",withdrawUnfreezePos);
        return JSON.toJSONString(map);
    }
}
