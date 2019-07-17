package com.github.ontio.core.governance;

import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.utils;

import java.io.IOException;

public class NodeToSideChainParams extends Serializable {
    public String peerPubkey;
    public Address address;
    public String sideChainId;
    public NodeToSideChainParams(){}

    @Override
    public void deserialize(BinaryReader reader) throws Exception {
        this.peerPubkey = reader.readVarString();
        this.address = utils.readAddress(reader);
        this.sideChainId = reader.readVarString();
    }

    @Override
    public void serialize(BinaryWriter binaryWriter) throws IOException {

    }
}
