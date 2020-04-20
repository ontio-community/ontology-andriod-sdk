package com.github.ontio.core.payload;

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.IOException;
import java.util.Random;

public class DeployWasmCode extends Transaction {

    public byte[] code;
    public VmType vmType;
    public String name;
    public String version;
    public String author;
    public String email;
    public String description;

    public DeployWasmCode() {
        super(TransactionType.DeployCode);
        this.vmType = VmType.WASMVM;
    }

    public DeployWasmCode(String codeStr, String name, String codeVersion, String author, String email,
                          String description, Address payer, long gasLimit, long gasPrice) {
        super(TransactionType.DeployCode);
        this.vmType = VmType.WASMVM;
        this.attributes = new Attribute[0];
        this.code = Helper.hexToBytes(codeStr);
        this.name = name;
        this.version = codeVersion;
        this.author = author;
        this.email = email;
        this.nonce = new Random().nextInt();
        this.description = description;
        if (payer != null) {
            this.payer = payer;
        }
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
    }

    @Override
    public void deserializeExclusiveData(BinaryReader reader) throws IOException {
        try {
            code = reader.readVarBytes();
            vmType = VmType.valueOf(reader.readByte());
            name = reader.readVarString();
            version = reader.readVarString();
            author = reader.readVarString();
            email = reader.readVarString();
            description = reader.readVarString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void serializeExclusiveData(BinaryWriter writer) throws IOException {
        writer.writeVarBytes(code);
        writer.writeByte(vmType.value());
        writer.writeVarString(name);
        writer.writeVarString(version);
        writer.writeVarString(author);
        writer.writeVarString(email);
        writer.writeVarString(description);
    }

}
