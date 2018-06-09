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

package com.github.ontio.smartcontract.nativevm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/5/24
 */
public class Governance {
    private OntSdk sdk;
    private final String contractAddress = "ff00000000000000000000000000000000000007";
    public Governance(OntSdk sdk) {
        this.sdk = sdk;
    }

    public String registerCandidate(Account account, String peerPubkey, int initPos, String ontid,String ontidpwd, long keyNo, Account payerAcct, long gaslimit, long gasprice) throws Exception{
        byte[] params = new RegisterCandidateParam(peerPubkey,account.getAddressU160(),initPos,ontid.getBytes(),keyNo).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"registerCandidate",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        sdk.addSign(tx,ontid,ontidpwd);
        if(!account.getAddressU160().toBase58().equals(payerAcct.getAddressU160().toBase58())){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    public String getPeerPoolMap() throws Exception {
        String view = sdk.getConnect().getStorage(contractAddress,Helper.toHexString("governanceView".getBytes()));
        GovernanceView governanceView = new GovernanceView();
        ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(view));
        BinaryReader br = new BinaryReader(bais);
        governanceView.deserialize(br);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        bw.writeInt(governanceView.view);

        byte[] viewBytes = baos.toByteArray();
        byte[] peerPoolBytes = "peerPool".getBytes();
        byte[] keyBytes = new byte[peerPoolBytes.length + viewBytes.length];
        System.arraycopy(peerPoolBytes,0,keyBytes,0,peerPoolBytes.length);
        System.arraycopy(viewBytes,0,keyBytes,peerPoolBytes.length,viewBytes.length);
        String value = sdk.getConnect().getStorage(contractAddress,Helper.toHexString(keyBytes));
        ByteArrayInputStream bais2 = new ByteArrayInputStream(Helper.hexToBytes(value));
        BinaryReader reader = new BinaryReader(bais2);
        int length = reader.readInt();
        Map peerPoolMap = new HashMap<String,PeerPoolItem>();
        for(int i = 0;i < length;i++){
            PeerPoolItem item = new PeerPoolItem();
            item.deserialize(reader);
            peerPoolMap.put(item.peerPubkey,item.Json());
        }
        return JSON.toJSONString(peerPoolMap);
    }

    /**
     *
     * @param adminOntId
     * @param password
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String approveCandidate(String adminOntId,String password,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new ApproveCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"approveCandidate",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,adminOntId,password);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String rejectCandidate(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new RejectCandidateParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"rejectCandidate",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String voteForPeer(Account account,String peerPubkey[],long[] posList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        byte[] params = new VoteForPeerParam(account.getAddressU160(),peerPubkey,posList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"voteForPeer",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String unVoteForPeer(Account account,String peerPubkey[],long[] posList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != posList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],posList[i]);
        }
        byte[] params = new VoteForPeerParam(account.getAddressU160(),peerPubkey,posList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"unVoteForPeer",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param account
     * @param peerPubkey
     * @param withdrawList
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String withdraw(Account account,String peerPubkey[],long[] withdrawList,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        if(peerPubkey.length != withdrawList.length){
            throw new SDKException(ErrorCode.ParamError);
        }
        Map map = new HashMap();
        for(int i =0;i < peerPubkey.length;i++){
            map.put(peerPubkey[i],withdrawList[i]);
        }
        byte[] params = new WithdrawParam(account.getAddressU160(),peerPubkey,withdrawList).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"withdraw",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param adminOntId
     * @param password
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String commitDpos(String adminOntId,String password,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"commitDpos",new byte[]{}, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,adminOntId,password);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String blackNode(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new BlackNodeParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"blackNode",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String whiteNode(String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new WhiteNodeParam(peerPubkey).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"whiteNode",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param account
     * @param peerPubkey
     * @param payerAcct
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new QuitNodeParam(peerPubkey,account.getAddressU160()).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"quitNode",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String voteCommitDpos(Account account,long pos,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new VoteCommitDposParam(account.getAddressU160().toBase58(),pos).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"voteCommitDpos",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{account}});
        if(!account.equals(payerAcct)){
            sdk.addSign(tx,payerAcct);
        }
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String updateConfig(Configuration config,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = config.toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String updateGlobalParam(int candidateFee,int minInitStake,int candidateNum,int A,int B,int Yita,Account payerAcct,long gaslimit,long gasprice) throws Exception{
        byte[] params = new GovernanceGlobalParam(candidateFee,minInitStake,candidateNum,A,B,Yita).toArray();
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateGlobalParam",params, payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String callSplit(Account payerAcct,long gaslimit,long gasprice) throws Exception{
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,"updateConfig",new byte[]{},payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
        sdk.signTx(tx,new Account[][]{{payerAcct}});
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
}
class PeerPoolItem extends Serializable{
    int index;
    String peerPubkey;
    Address address;
    int status;
    long initPos;
    long totalPos;
    PeerPoolItem(){}
    PeerPoolItem(int index,String peerPubkey,Address address,int status,long initPos,long totalPos){
        this.index = index;
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.status = status;
        this.initPos = initPos;
        this.totalPos = totalPos;
    }

    @Override
    public void deserialize(BinaryReader reader) throws Exception {
        this.index = reader.readInt();
        this.peerPubkey = reader.readVarString();
        try {
            this.address = reader.readSerializable(Address.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        this.status = reader.readByte();
        this.initPos = reader.readLong();
        this.totalPos = reader.readLong();
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeInt(index);
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
        writer.writeByte((byte)status);
        writer.writeLong(initPos);
        writer.writeLong(totalPos);
    }
    public Object Json() {
        Map map = new HashMap();
        map.put("index",index);
        map.put("peerPubkey",peerPubkey);
        try {
            map.put("address",address.toBase58());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        map.put("status",status);
        map.put("initPos",initPos);
        map.put("totalPos",totalPos);
        return map;
    }
}
class GovernanceView extends Serializable{
    int view;
    int height;
    UInt256 txhash;
    GovernanceView(){
    }
    GovernanceView(int view,int height,UInt256 txhash){
        this.view = view;
        this.height = height;
        this.txhash = txhash;
    }
    @Override
    public void deserialize(BinaryReader reader) throws Exception {
        this.view = reader.readInt();
        this.height = reader.readInt();
        try {
            this.txhash = reader.readSerializable(UInt256.class);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeInt(view);
        writer.writeInt(height);
        writer.writeSerializable(txhash);
    }
}

class ApproveCandidateParam extends Serializable {
    public String peerPubkey;
    public ApproveCandidateParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(peerPubkey);
    }
}
class RejectCandidateParam extends Serializable {

    public String peerPubkey;
    RejectCandidateParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(peerPubkey);
    }
}
class RegisterCandidateParam extends Serializable {
    public String peerPubkey;
    public Address address;
    public long initPos;
    public byte[] caller;
    public long keyNo;
    public RegisterCandidateParam(String peerPubkey,Address address,long initPos,byte[] caller,long keyNo){
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.initPos = initPos;
        this.caller = caller;
        this.keyNo = keyNo;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
        writer.writeVarInt(initPos);
        writer.writeVarBytes(caller);
        writer.writeVarInt(keyNo);
    }
}
class VoteForPeerParam extends Serializable {
    public Address address;
    public String[] peerPubkeys;
    public long[] posList;
    public VoteForPeerParam(Address address,String[] peerPubkeys,long[] posList){
        this.address = address;
        this.peerPubkeys = peerPubkeys;
        this.posList = posList;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException{};

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeSerializable(address);
        writer.writeVarInt(peerPubkeys.length);
        for(String peerPubkey: peerPubkeys){
            writer.writeVarString(peerPubkey);
        }
        writer.writeVarInt(posList.length);
        for(long pos: posList){
            writer.writeVarInt(pos);
        }
    }
}
class WithdrawParam extends Serializable {
    public Address address;
    public String[] peerPubkeys;
    public long[] withdrawList;
    public WithdrawParam(Address address,String[] peerPubkeys,long[] withdrawList){
        this.address = address;
        this.peerPubkeys = peerPubkeys;
        this.withdrawList = withdrawList;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException{};

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeSerializable(address);
        writer.writeVarInt(peerPubkeys.length);
        for(String peerPubkey : peerPubkeys){
            writer.writeVarString(peerPubkey);
        }
        writer.writeVarInt(withdrawList.length);
        for(long withdraw : withdrawList){
            writer.writeVarInt(withdraw);
        }
    }
}
class QuitNodeParam extends Serializable {
    public String peerPubkey;
    public Address address;
    public QuitNodeParam(String peerPubkey,Address address){
        this.peerPubkey = peerPubkey;
        this.address = address;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(peerPubkey);
        writer.writeSerializable(address);
    }
}
class BlackNodeParam extends Serializable {
    public String peerPubkey;
    public BlackNodeParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(peerPubkey);
    }
}
class WhiteNodeParam extends Serializable {
    public String peerPubkey;
    public WhiteNodeParam(String peerPubkey){
        this.peerPubkey = peerPubkey;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(peerPubkey);
    }
}
class VoteCommitDposParam extends Serializable {
    public String address;
    public long pos;
    public VoteCommitDposParam(String address,long pos){
        this.pos = pos;
        this.address = address;
    }
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarString(address);
        writer.writeVarString(String.valueOf(pos));
    }
}
class Configuration extends Serializable {
    public long N = 7;
    public long C = 2;
    public long K = 7;
    public long L = 112;
    public long blockMsgDelay = 10000;
    public long hashMsgDelay = 10000;
    public long peerHandshakeTimeout = 10;
    public long maxBlockChangeView = 1000;
    @Override
    public void deserialize(BinaryReader reader) throws IOException {}
    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarInt(N);
        writer.writeVarInt(C);
        writer.writeVarInt(K);
        writer.writeVarInt(L);
        writer.writeVarInt(blockMsgDelay);
        writer.writeVarInt(hashMsgDelay);
        writer.writeVarInt(peerHandshakeTimeout);
        writer.writeVarInt(maxBlockChangeView);
    }
}
class GovernanceGlobalParam extends Serializable {
    public long candidateFee;
    public long minInitStake;
    public long candidateNum;
    public long A;
    public long B;
    public long Yita;
    GovernanceGlobalParam(long candidateFee,long minInitStake,long candidateNum,long A,long B,int Yita){
        this.candidateFee = candidateFee;
        this.minInitStake = minInitStake;
        this.candidateNum = candidateNum;
        this.A = A;
        this.B = B;
        this.Yita = Yita;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {

    }

    @Override
    public void serialize(BinaryWriter writer) throws Exception {
        writer.writeVarInt(candidateFee);
        writer.writeVarInt(minInitStake);
        writer.writeVarInt(candidateNum);
        writer.writeVarInt(A);
        writer.writeVarInt(B);
        writer.writeVarInt(Yita);
    }
}