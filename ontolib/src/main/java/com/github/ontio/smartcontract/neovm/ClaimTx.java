package com.github.ontio.smartcontract.neovm;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;

public class ClaimTx extends Serializable {
    public byte[] claimId;
    public byte[] issuerOntId;
    public byte[] subjectOntId;
    public byte[] status;

    ClaimTx() {
    }

    ClaimTx(byte[] claimId, byte[] issuerOntId, byte[] subjectOntId, byte[] status) {
        this.claimId = claimId;
        this.issuerOntId = issuerOntId;
        this.subjectOntId = subjectOntId;
        this.status = status;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        byte dataType = reader.readByte();
        long length = reader.readVarInt();
        byte dataType2 = reader.readByte();
        this.claimId = reader.readVarBytes();
        byte dataType3 = reader.readByte();
        this.issuerOntId = reader.readVarBytes();
        byte dataType4 = reader.readByte();
        this.subjectOntId = reader.readVarBytes();
        byte dataType5 = reader.readByte();
        this.status = reader.readVarBytes();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }
}
