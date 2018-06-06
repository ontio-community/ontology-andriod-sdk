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

package com.github.ontio.network.connect;

import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;

import java.io.IOException;

public interface IConnector {

	String getUrl();
	Object sendRawTransaction(boolean preExec, String userid, String hexData) throws Exception;
	Object sendRawTransaction(String hexData) throws Exception;
	Transaction getRawTransaction(String txhash) throws Exception;
	Object getRawTransactionJson(String txhash) throws Exception;
	int getGenerateBlockTime() throws Exception;
	int getNodeCount() throws Exception;
	int getBlockHeight() throws Exception;
	Block getBlock(int height) throws Exception;
	Block getBlock(String hash) throws Exception ;
	Object getBlockJson(int height) throws Exception;
	Object getBlockJson(String hash) throws Exception;

	Object getBalance(String address) throws Exception;

	Object getContract(String hash) throws Exception;
	Object getContractJson(String hash) throws Exception;
	Object getSmartCodeEvent(int height) throws Exception;
	Object getSmartCodeEvent(String hash) throws Exception;
	int getBlockHeightByTxHash(String hash) throws Exception;

	String getStorage(String codehash, String key) throws Exception;
	Object getMerkleProof(String hash) throws Exception;
	String getAllowance(String asset, String from, String to) throws Exception;
    Object getMemPoolTxCount() throws Exception;
    Object getMemPoolTxState(String hash) throws Exception;
}