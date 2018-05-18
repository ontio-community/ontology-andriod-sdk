package com.xiaofei.ontologyandroidsdkuse;

import android.support.test.runner.AndroidJUnit4;

import com.github.ontio.OntSdk;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.manager.ConnectMgr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BlockChainTest {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        ontSdk.setRestful("http://192.168.50.73:20334");
        connectMgr = ontSdk.getConnectMgr();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance(){
        OntSdk ontSdk = OntSdk.getInstance();
        assertNotNull(ontSdk);
        assertSame(ontSdk,this.ontSdk);
    }

    @Test
    public void getBlockHeight() throws Exception {
        int blockHeight = connectMgr.getBlockHeight();
        assertTrue(blockHeight >=0);
    }

    @Test
    public void getFirstBlock() throws Exception {
        Block firstBlock = connectMgr.getBlock(0);
        assertNotNull(firstBlock);
    }

    @Test
    public void getMaxBlock() throws Exception {
        int blockHeight = connectMgr.getBlockHeight();
        Block maxBlock = connectMgr.getBlock(blockHeight);
        assertNotNull(maxBlock);
    }

    @Test
    public void getNodeCount() throws Exception {
        int nodeCount = connectMgr.getNodeCount();
        assertTrue(nodeCount > 0);
    }

    @Test
    public void getGenerateBlockTime() throws Exception {
        int time = connectMgr.getGenerateBlockTime();
        assertTrue(time ==6);
    }

    @Test
    public void getTransaction() throws Exception {
        int blockHeight = connectMgr.getBlockHeight();
        Block block = connectMgr.getBlock(blockHeight);
        Transaction transaction = block.transactions[0];
        String txHash = transaction.hash().toHexString();
        Transaction transaction1 = connectMgr.getTransaction(txHash);
        assertNotNull(transaction1);
    }

    ///////////////////////////////////////////////////////////////////////////////

    @Test
    public void getBlockError() throws Exception {
        String message="";
        try {
            message = "";
            connectMgr.getBlock(-1);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("58009"));
        }

        try {
            message = "";
            int blockHeight = connectMgr.getBlockHeight();
            connectMgr.getBlock(blockHeight+1000);
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertTrue(message.contains("44003"));
        }
    }

    @Test
    public void getTransactionError() throws Exception {
        String message = "";
        try {
            message ="";
            connectMgr.getTransaction("");
        }catch (Exception ex){
            message = ex.getMessage();
        }finally {
            assertNotEquals(message,"");
        }

    }



}