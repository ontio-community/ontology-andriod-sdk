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

package com.github.ontio.network.rpc;

import java.io.IOException;
import java.net.MalformedURLException;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.sdk.exception.SDKException;

public class RpcClient extends AbstractConnector {
    private Interfaces rpc;

    public RpcClient(String url) {
        try {
            this.rpc = new Interfaces(url);
        } catch (MalformedURLException e) {
            //to do exception logic
        }
    }

    @Override
    public String getUrl() {
        return rpc.getHost();
    }

    @Override
    public Object getBalance(String address) throws Exception {
        Object result = null;
        try {
            result = rpc.call("getbalance", address);
        } catch (IOException e) {
            //to do exception logic
        }
        return result;
    }

    @Override
    public String sendRawTransaction(String sData) throws Exception, IOException {
        Object result = rpc.call("sendrawtransaction", sData);
        return (String) result;
    }

    @Override
    public Object sendRawTransaction(boolean preExec, String userid, String sData) throws Exception, IOException {
        Object result = null;
        if(preExec){
            result = rpc.call("sendrawtransaction", sData,1);
        }else {
            result = rpc.call("sendrawtransaction", sData);
        }
        return result;
    }

    @Override
    public Transaction getRawTransaction(String txhash) throws Exception {
        Object result = rpc.call("getrawtransaction", txhash.toString());
        return Transaction.deserializeFrom(Helper.hexToBytes((String) result));
    }

    @Override
    public String getRawTransactionJson(String txhash) throws Exception {
        Object result = null;
        try {
            result = rpc.call("getrawtransaction", txhash.toString(), 1);
        } catch (IOException e) {
            //to do exception logic
        }
        return result.toString();
    }

    @Override
    public int getGenerateBlockTime() throws Exception, IOException {
        Object result = rpc.call("getgenerateblocktime");
        return (int) result;
    }

    @Override
    public int getNodeCount() throws Exception, IOException {
        Object result = rpc.call("getconnectioncount");
        return (int) result;
    }

    @Override
    public int getBlockHeight() throws Exception, IOException {
        Object result = rpc.call("getblockcount");
        return (int) result;
    }

    @Override
    public Object getBlockJson(int index) throws Exception {
        Object result = null;
        try {
            result = rpc.call("getblock", index, 1);
        } catch (IOException e) {
            //to do exception logic
        }
        return result;
    }

    @Override
    public Object getBlockJson(String hash) throws Exception {
        Object result = null;
        try {
            result = rpc.call("getblock", hash, 1);
        } catch (IOException e) {
            //to do exception logic
        }
        return result;
    }

    @Override
    public Object getContract(String hash) throws Exception {
        Object result = null;
        try {
            result = rpc.call("getcontractstate", hash);
        } catch (IOException e) {
            //to do exception logic
        }
        return result;
    }

    @Override
    public Object getContractJson(String hash) throws Exception {
        Object result = null;
        try {
            result = rpc.call("getcontractstate", hash,1);
        } catch (IOException e) {
            //to do exception logic
        }
        return result;
    }

    public String getRawTransaction(UInt256 txhash) throws Exception, IOException {
        Object result = rpc.call("getrawtransaction", txhash.toString());
        return (String) result;
    }


    public Block getBlock(UInt256 hash) throws Exception {
        Object result = rpc.call("getblock", hash.toString());
        try {
            Block bb = Serializable.from(Helper.hexToBytes((String) result), Block.class);
            return bb;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }

    @Override
    public Block getBlock(int index) throws Exception {
        Object result = rpc.call("getblock", index);
        try {
            Block bb = Serializable.from(Helper.hexToBytes((String) result), Block.class);
            return bb;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }

    public int getBlockCount() throws Exception, IOException {
        Object result = rpc.call("getblockcount");
        return (int) result;
    }

    @Override
    public Block getBlock(String hash) throws Exception {
        Object result = rpc.call("getblock", hash.toString());
        try {
            Block bb = Serializable.from(Helper.hexToBytes((String) result), Block.class);
            return bb;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }

    @Override
    public Object getSmartCodeEvent(int height) throws Exception, IOException, SDKException {
        Object result = rpc.call("getsmartcodeevent", height);
        try {
            return result;
        } catch (Exception e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }

    @Override
    public Object getSmartCodeEvent(String hash) throws Exception, IOException, SDKException {
        Object result = rpc.call("getsmartcodeevent", hash.toString());
        try {
            return result;
        } catch (Exception e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }

    @Override
    public int getBlockHeightByTxHash(String hash) throws Exception, IOException, SDKException {
        Object result = rpc.call("getblockheightbytxhash", hash.toString());
        try {
            return (int)result;
        } catch (Exception e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }

    @Override
    public String getStorage(String codehash, String key) throws Exception, IOException, SDKException {
        Object result = rpc.call("getstorage", codehash, key);
        try {
            return (String)result;
        } catch (Exception e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }
    @Override
    public Object getMerkleProof(String hash) throws Exception, IOException, SDKException {
        Object result = rpc.call("getmerkleproof", hash);
        try {
            return result;
        } catch (Exception e) {
            throw new SDKException(ErrorCode.InvalidData);
        }
    }
}

