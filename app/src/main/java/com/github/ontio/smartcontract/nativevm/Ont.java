package com.github.ontio.smartcontract.nativevm;

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



import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.AttributeUsage;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.*;
import com.github.ontio.core.payload.Vote;
import com.github.ontio.io.BinaryWriter;

import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;


/**
 *
 */
public class Ont {
    private OntSdk sdk;
    private final String ontContract = "ff00000000000000000000000000000000000001";
    private int precision = 1;
    public Ont(OntSdk sdk) {
        this.sdk = sdk;
    }
    public String getContractAddress() {
        return ontContract;
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param payer
     * @param amount
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransfer(String sendAddr, String password, String recvAddr, long amount,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if (amount <= 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        Transaction tx = makeTransfer(sendAddr, password, recvAddr, amount,payer,gaslimit,gasprice);
        sdk.signTx(tx, sendAddr, password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeTransfer(String sendAddr, String password, String recvAddr, long amount,String payer,long gaslimit,long gasprice) throws Exception {
        if (amount <= 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        amount = amount * precision;
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr), amount);
        Transfers transfers = new Transfers(new State[]{state});
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"transfer",transfers.toArray(), VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param address
     * @return
     * @throws Exception
     */
    public long queryBalanceOf(String address) throws Exception {
        byte[] parabytes = buildParams(Address.decodeBase58(address).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"balanceOf", parabytes, VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    /**
     *
     * @param fromAddr
     * @param toAddr
     * @return
     */
    public long queryAllowance(String fromAddr,String toAddr) throws Exception {
        byte[] parabytes = buildParams(Address.decodeBase58(fromAddr).toArray(),Address.decodeBase58(toAddr).toArray());
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"allowance", parabytes, VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendApprove(String sendAddr, String password, String recvAddr, long amount,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if (amount <= 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        Transaction tx = makeApprove(sendAddr,password,recvAddr,amount,payer,gaslimit,gasprice);
        sdk.signTx(tx,sendAddr,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param payer
     * @param amount
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public Transaction makeApprove(String sendAddr,String password,String recvAddr,long amount,String payer,long gaslimit,long gasprice) throws Exception {
        if (amount <= 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        State state = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr), amount);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"approve", state.toArray(), VmType.Native.value(), payer,gaslimit,gasprice);
        return tx;
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param fromAddr
     * @param toAddr
     * @param payer
     * @param payerpwd
     * @param amount
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
    public String sendTransferFrom(String sendAddr, String password, String fromAddr, String toAddr,long amount,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
        if (amount <= 0 || gasprice < 0) {
            throw new SDKException(ErrorCode.AmountError);
        }
        Transaction tx = makeTransferFrom(sendAddr,password,fromAddr,toAddr,payer,amount,gaslimit,gasprice);
        sdk.signTx(tx,sendAddr,password);
        sdk.addSign(tx,payer,payerpwd);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if(b){
            return tx.hash().toHexString();
        }
        return null;
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param fromAddr
     * @param toAddr
     * @param payer
     * @param amount
     * @param gaslimit
     * @param gas
     * @return
     * @throws Exception
     */
    public Transaction makeTransferFrom(String sendAddr, String password, String fromAddr, String toAddr,String payer, long amount,long gaslimit,long gas) throws Exception {
        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
        TransferFrom transferFrom = new TransferFrom(Address.addressFromPubKey(sender.pubkey),Address.decodeBase58(fromAddr), Address.decodeBase58(toAddr), amount);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"transferFrom", transferFrom.toArray(), VmType.Native.value(), payer,gaslimit,gas);
        return tx;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public String queryName() throws Exception {
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"name", "".getBytes(), VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        return new String(Helper.hexToBytes(res));
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public String querySymbol() throws Exception {
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"symbol", "".getBytes(), VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        return new String(Helper.hexToBytes(res));
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public long queryDecimals() throws Exception {
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"decimals", "".getBytes(), VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public long queryTotalSupply() throws Exception {
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"totalSupply", "".getBytes(), VmType.Native.value(), null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject)obj).getString("Result");
        if (("").equals(res)) {
            return 0;
        }
        return Long.valueOf(res,16);
    }

    private byte[] buildParams(byte[] ...params) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BinaryWriter writer = new BinaryWriter(byteArrayOutputStream);
        try {
            for (byte[] param : params) {
                writer.writeVarBytes(param);
            }
        } catch (IOException e) {
            throw new SDKException(ErrorCode.WriteVarBytesError);
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
//    public String sendTransferToMany(String sendAddr, String password, String[] recvAddr, long[] amount,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
//        if(gasprice < 0){
//            throw new SDKException(ErrorCode.AmountError);
//        }
//        if (recvAddr.length != amount.length){
//            throw new SDKException(ErrorCode.ParamLengthNotSame);
//        }
//        for (long amou : amount) {
//            if (amou <= 0) {
//                throw new SDKException(ErrorCode.AmountError);
//            }
//        }
//        Transaction tx = makeTransferToMany(sendAddr,password,recvAddr,amount,payer,gaslimit,gasprice);
//        sdk.signTx(tx, new Account[][]{{sdk.getWalletMgr().getAccount(sendAddr, password)}});
//        sdk.addSign(tx,payer,payerpwd);
//        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
//        if (b) {
//            return tx.hash().toString();
//        }
//        return null;
//    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
//    public Transaction makeTransferToMany(String sendAddr, String password, String[] recvAddr, long[] amount,String payer,long gaslimit,long gasprice) throws Exception {
//        if(gasprice < 0){
//            throw new SDKException(ErrorCode.AmountError);
//        }
//        if (recvAddr.length != amount.length){
//            throw new SDKException(ErrorCode.ParamLengthNotSame);
//        }
//        for (long amou : amount) {
//            if (amou <= 0) {
//                throw new SDKException(ErrorCode.AmountError);
//            }
//        }
//        AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr, password);
//        State[] states = new State[recvAddr.length];
//        if (amount.length != recvAddr.length) {
//            throw new Exception(ErrorCode.ParamError);
//        }
//        for (int i = 0; i < recvAddr.length; i++) {
//            amount[i] = amount[i] * precision;
//            states[i] = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr[i]), amount[i]);
//        }
//        Transfers transfers = new Transfers(states);
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"transfer",transfers.toArray(), VmType.Native.value(), sender.addressBase58,gaslimit,gasprice);
//        return tx;
//    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @param payer
     * @param payerpwd
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
//    public String sendTransferFromMany(String[] sendAddr, String[] password, String recvAddr, long[] amount,String payer,String payerpwd,long gaslimit,long gasprice) throws Exception {
//        if(gasprice < 0){
//            throw new SDKException(ErrorCode.AmountError);
//        }
//        if (sendAddr.length != password.length){
//            throw new SDKException(ErrorCode.ParamLengthNotSame);
//        }
//        for (long amou : amount) {
//            if (amou <= 0) {
//                throw new SDKException(ErrorCode.AmountError);
//            }
//        }
//        if (sendAddr == null || sendAddr.length != password.length) {
//            throw new Exception(ErrorCode.SenderAmtNotEqPasswordAmt);
//        }
//        Transaction tx = makeTransferFromMany(sendAddr,password,recvAddr,amount,payer,gaslimit,gasprice);
//        Account[][] acct = Arrays.stream(sendAddr).map(p -> {
//            for (int i = 0; i < sendAddr.length; i++) {
//                if (sendAddr[i].equals(p)) {
//                    try {
//                        return new Account[]{sdk.getWalletMgr().getAccount(p, password[i])};
//                    } catch (Exception e) {
//                    }
//                }
//            }
//            return null;
//        }).toArray(Account[][]::new);
//        sdk.signTx(tx, acct);
//        sdk.addSign(tx,payer,payerpwd);
//        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
//        if (b) {
//            return tx.hash().toString();
//        }
//        return null;
//    }

    /**
     *
     * @param sendAddr
     * @param password
     * @param recvAddr
     * @param amount
     * @param payer
     * @param gaslimit
     * @param gasprice
     * @return
     * @throws Exception
     */
//    public Transaction makeTransferFromMany(String[] sendAddr, String[] password, String recvAddr, long[] amount,String payer,long gaslimit,long gasprice) throws Exception {
//        if(gasprice < 0){
//            throw new SDKException(ErrorCode.AmountError);
//        }
//        if (sendAddr.length != password.length){
//            throw new SDKException(ErrorCode.ParamLengthNotSame);
//        }
//        for (long amou : amount) {
//            if (amou <= 0) {
//                throw new SDKException(ErrorCode.AmountError);
//            }
//        }
//        if (sendAddr == null || sendAddr.length != password.length) {
//            throw new Exception(ErrorCode.SenderAmtNotEqPasswordAmt);
//        }
//        State[] states = new State[sendAddr.length];
//        for (int i = 0; i < sendAddr.length; i++) {
//            AccountInfo sender = sdk.getWalletMgr().getAccountInfo(sendAddr[i], password[i]);
//            amount[i] = amount[i] * precision;
//            states[i] = new State(Address.addressFromPubKey(sender.pubkey), Address.decodeBase58(recvAddr), amount[i]);
//        }
//
//        Transfers transfers = new Transfers(states);
//        Transaction tx = sdk.vm().makeInvokeCodeTransaction(ontContract,"transfer", transfers.toArray(), VmType.Native.value(), sendAddr[0],gaslimit,gasprice);
//        return tx;
//    }
}

