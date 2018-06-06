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

package com.github.ontio.network.rest;


import com.alibaba.fastjson.JSON;
import com.github.ontio.common.ErrorCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
class Interfaces {
    private String url;

    public Interfaces(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String sendTransaction(boolean preExec, String userid, String action, String version, String data) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (userid != null) {
            params.put("userid", userid);
        }
        if (preExec) {
            params.put("preExec", "1");
        }
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("Action", action);
        body.put("Version", version);
        body.put("Data", data);
        return http.post(url + UrlConsts.Url_send_transaction, params, body);
    }

    public String getTransaction(String txhash, boolean raw) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        if (raw) {
            params.put("raw", "1");
        }
        return http.get(url + UrlConsts.Url_get_transaction + txhash, params);
    }

    public String getGenerateBlockTime() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_generate_block_time, params);
    }

    public String getNodeCount() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_node_count, params);
    }

    public String getBlockHeight() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_block_height, params);
    }

    public String getBlock(int height, String raw) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("raw", raw);
        return http.get(url + UrlConsts.Url_get_block_by_height + height, params);
    }

    public String getBlock(String hash, String raw) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("raw", raw);
        return http.get(url + UrlConsts.Url_get_block_by_hash + hash, params);
    }

    public String getContract(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("raw", "1");
        return http.get(url + UrlConsts.Url_get_contract_state + hash, params);
    }

    public String getContractJson(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_contract_state + hash, params);
    }

    public String getSmartCodeEvent(int height) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_smartcodeevent_txs_by_height + height, params);
    }

    public String getSmartCodeEvent(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_smartcodeevent_by_txhash + hash, params);
    }

    public String getBlockHeightByTxHash(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_block_height_by_txhash + hash, params);
    }

    public String getStorage(String codehash, String key) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_storage + codehash + "/" + key, params);
    }

    public String getMerkleProof(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_merkleproof + hash, params);
    }
    public String getMemPoolTxCount() throws Exception {
        Map<String, String> params = new HashMap<String, String>();

        return http.get(url + UrlConsts.Url_get_mem_pool_tx_count, params);

    }
    public String getMemPoolTxState(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_mem_pool_tx_state + hash, params);
    }

    public String getBalance(String address) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_account_balance + address, params);
    }

    public String getTransactionJson(String txhash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_transaction + txhash, params);
    }

    public String getBlockJson(int height) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_block_by_height + height, params);
    }

    public String getBlockJson(String hash) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_block_by_hash + hash, params);
    }
    public String getAllowance(String asset,String from,String to) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        return http.get(url + UrlConsts.Url_get_allowance + asset+"/"+from+"/"+to, params);
    }
}
