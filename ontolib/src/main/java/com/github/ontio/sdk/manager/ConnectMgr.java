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

package com.github.ontio.sdk.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.connect.IConnector;
import com.github.ontio.network.rest.RestClient;
import com.github.ontio.network.rest.Result;
import com.github.ontio.network.websocket.WebsocketClient;
import com.github.ontio.sdk.exception.SDKException;

/**
 *
 */
public class ConnectMgr {
    private IConnector connector;
    public ConnectMgr(String url, String type, Object lock) {
        if (type.equals("websocket")){
            setConnector(new WebsocketClient(url,lock));
        }
    }
    public ConnectMgr(String url, String type) throws MalformedURLException {
        if (type.equals("rpc")) {
            setConnector(new RpcClient(url));
        } else if (type.equals("restful")){
            setConnector(new RestClient(url));
        }
    }
    public void startWebsocketThread(boolean log){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).startWebsocketThread(log);
        }
    }
    public void setReqId(long n){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).setReqId(n);
        }
    }
    public void send(Map map){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).send(map);
        }
    }
    public void sendHeartBeat(){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).sendHeartBeat();
        }
    }
    public void sendSubscribe(Map map){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).sendSubscribe(map);
        }
    }
    public ConnectMgr(IConnector connector) {
        setConnector(connector);
    }

    public void setConnector(IConnector connector) {
        this.connector = connector;
    }

    private String getUrl() {
        return connector.getUrl();
    }

    public boolean sendRawTransaction(Transaction tx) throws Exception {
        String rs = (String) connector.sendRawTransaction(Helper.toHexString(tx.toArray()));
        if (connector instanceof RpcClient) {
            return true;
        }
        if (connector instanceof WebsocketClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public boolean sendRawTransaction(String hexData) throws Exception {
        String rs = (String) connector.sendRawTransaction(hexData);
        if (connector instanceof RpcClient) {
            return true;
        }
        if (connector instanceof WebsocketClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public Object sendRawTransactionPreExec(String hexData) throws Exception {
        Object rs = connector.sendRawTransaction(true, null, hexData);
        if (connector instanceof RpcClient) {
            return rs;
        }
        if (connector instanceof WebsocketClient) {
            return rs;
        }
        Result rr = JSON.parseObject((String) rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        return null;
    }

    public Transaction getTransaction(String txhash) throws Exception {
        txhash = txhash.replace("0x","");
        return connector.getRawTransaction(txhash);
    }

    public Object getTransactionJson(String txhash) throws Exception {
        txhash = txhash.replace("0x","");
        return connector.getRawTransactionJson(txhash);
    }

    public int getGenerateBlockTime() throws Exception {
        return connector.getGenerateBlockTime();
    }

    public int getNodeCount() throws Exception {
        return connector.getNodeCount();
    }

    public int getBlockHeight() throws Exception {
        return connector.getBlockHeight();
    }

    public Block getBlock(int height) throws Exception {
        if (height < 0){
            throw new SDKException(ErrorCode.BlockHeightLessThanZero);
        }
        return connector.getBlock(height);
    }

    public Block getBlock(String hash) throws Exception {
        return connector.getBlock(hash);

    }

    public Object getBalance(String address) throws Exception {
        return connector.getBalance(address);
    }

    public Object getBlockJson(int height) throws Exception {
        return connector.getBlockJson(height);
    }

    public Object getBlockJson(String hash) throws Exception {
        return connector.getBlockJson(hash);
    }

    public Object getContract(String hash) throws Exception {
        hash = hash.replace("0x","");
        return connector.getContract(hash);
    }
    public Object getContractJson(String hash) throws Exception {
        hash = hash.replace("0x","");
        return connector.getContractJson(hash);
    }

    public Object getSmartCodeEvent(int height) throws Exception {
        return connector.getSmartCodeEvent(height);
    }

    public Object getSmartCodeEvent(String hash) throws Exception {
        return connector.getSmartCodeEvent(hash);
    }

    public int getBlockHeightByTxHash(String hash) throws Exception {
        hash = hash.replace("0x", "");
        return connector.getBlockHeightByTxHash(hash);
    }

    public String getStorage(String codehash, String key) throws Exception {
        codehash = codehash.replace("0x", "");
        return connector.getStorage(codehash, key);
    }

    public Object getMerkleProof(String hash) throws Exception {
        hash = hash.replace("0x", "");
        return connector.getMerkleProof(hash);
    }
    public String getAllowance(String asset,String from,String to) throws Exception {
        return connector.getAllowance(asset,from,to);
    }

    public Object getMemPoolTxCount() throws Exception {
        return connector.getMemPoolTxCount();
    }

    public Object getMemPoolTxState(String hash) throws Exception {
        hash = hash.replace("0x", "");
        return connector.getMemPoolTxState(hash);
    }
}


