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

package com.github.ontio.core.block;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.Inventory;
import com.github.ontio.core.InventoryType;
import com.github.ontio.core.payload.Bookkeeping;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.TransactionType;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * block
 */
public class Block extends Inventory {

    public int version;
    public UInt256 prevBlockHash;
    public UInt256 transactionsRoot;
    public UInt256 blockRoot;
    public int timestamp;
    public int height;
    public long consensusData;
    public byte[] consensusPayload;
    public Address nextBookkeeper;
    public String[] sigData;
    public byte[][] bookkeepers;
    public Transaction[] transactions;
    public UInt256 hash;
    private Block _header = null;

    public Block header() {
        if (isHeader()) {
            return this;
        }
        if (_header == null) {
            _header = new Block();
            _header.prevBlockHash = prevBlockHash;
            _header.transactionsRoot = this.transactionsRoot;
            _header.blockRoot = this.blockRoot;
            _header.timestamp = this.timestamp;
            _header.height = this.height;
            _header.consensusData = this.consensusData;
            _header.nextBookkeeper = this.nextBookkeeper;
            _header.sigData = this.sigData;
            _header.bookkeepers = this.bookkeepers;
            _header.transactions = new Transaction[0];
        }
        return _header;
    }

    @Override
    public InventoryType inventoryType() {
        return InventoryType.Block;
    }

    public boolean isHeader() {
        return transactions.length == 0;
    }

    @Override
    public void deserialize(BinaryReader reader) throws Exception {
        deserializeUnsigned(reader);
        int len = (int) reader.readVarInt();
        sigData = new String[len];
        for (int i = 0; i < len; i++) {
            this.sigData[i] = Helper.toHexString(reader.readVarBytes());
        }

        len = reader.readInt();
        transactions = new Transaction[len];
        for (int i = 0; i < transactions.length; i++) {
            transactions[i] = Transaction.deserializeFrom(reader);
        }
    }

    @Override
    public void deserializeUnsigned(BinaryReader reader) throws Exception {
        version = reader.readInt();
        prevBlockHash = reader.readSerializable(UInt256.class);
        transactionsRoot = reader.readSerializable(UInt256.class);
        blockRoot = reader.readSerializable(UInt256.class);
        timestamp = reader.readInt();
        height = reader.readInt();
        consensusData = Long.valueOf(reader.readLong());
        consensusPayload = reader.readVarBytes();
        nextBookkeeper = reader.readSerializable(Address.class);
        int len = (int) reader.readVarInt();
        bookkeepers = new byte[len][];
        for (int i = 0; i < len; i++) {
            this.bookkeepers[i] = reader.readVarBytes();
        }
        transactions = new Transaction[0];
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        serializeUnsigned(writer);
        writer.writeVarInt(bookkeepers.length);
        for (int i = 0; i < bookkeepers.length; i++) {
            writer.writeVarBytes(bookkeepers[i]);
        }
        writer.writeVarInt(sigData.length);
        for (int i = 0; i < sigData.length; i++) {
            writer.writeVarBytes(Helper.hexToBytes(sigData[i]));
        }
        writer.writeInt(transactions.length);
        for (int i = 0; i < transactions.length; i++) {
            writer.writeSerializable(transactions[i]);
        }
    }

    @Override
    public void serializeUnsigned(BinaryWriter writer) throws Exception {
        writer.writeInt(version);
        writer.writeSerializable(prevBlockHash);
        writer.writeSerializable(transactionsRoot);
        writer.writeSerializable(blockRoot);
        writer.writeInt(timestamp);
        writer.writeInt(height);
        writer.writeLong(consensusData);
        writer.writeVarBytes(consensusPayload);
        writer.writeSerializable(nextBookkeeper);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Block)) {
            return false;
        }
        try {
            return this.hash().equals(((Block) obj).hash());
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public int hashCode() {
        try {
            return hash().hashCode();
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public Address[] getAddressU160ForVerifying() {
        return null;
    }

    public Object json() throws Exception {
        Map json = new HashMap();
        Map head = new HashMap();
        json.put("Hash", hash().toString());

        head.put("Version", version);
        head.put("PrevBlockHash", prevBlockHash.toString());
        head.put("TransactionsRoot", transactionsRoot.toString());
        head.put("BlockRoot", blockRoot.toString());
        head.put("Timestamp", timestamp);
        head.put("Height", height);
        head.put("ConsensusData", consensusData & Long.MAX_VALUE);
        head.put("NextBookkeeper", nextBookkeeper.toBase58());
        head.put("Hash", hash().toString());
        Object[] bks = new Object[bookkeepers.length];
        for (int i = 0; i < bookkeepers.length; i++) {
            bks[i] = Helper.toHexString(bookkeepers[i]);
        }
        head.put("SigData", sigData);
        head.put("Bookkeepers", bks);

        json.put("Header", head);
        Object[] txs = new Object[transactions.length];
        for (int i = 0; i < transactions.length; i++) {
            Object obj = new Object();
            if (transactions[i] instanceof InvokeCode) {
                obj = ((InvokeCode) transactions[i]).json();
            } else if (transactions[i] instanceof DeployCode) {
                obj = ((DeployCode) transactions[i]).json();
            } else if (transactions[i] instanceof Bookkeeping) {
                obj = ((Bookkeeping) transactions[i]).json();
            } else {
                obj = transactions[i].json();
            }
            txs[i] = obj;
        }
        json.put("Transactions", txs);
        return JSON.toJSONString(json);
    }

    public byte[] trim() throws Exception {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                serializeUnsigned(writer);
                writer.writeByte((byte) 1);
                UInt256[] txs = new UInt256[transactions.length];
                for (int i = 0; i < transactions.length; i++) {
                    txs[i] = transactions[i].hash();
                }
                writer.writeSerializableArray(txs);
                writer.flush();
                return ms.toByteArray();
            }
        }
    }

    @Override
    public boolean verify() {
        return true;
    }

}
