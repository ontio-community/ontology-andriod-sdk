package com.github.ontio;

import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class OntSdkTest {
    private OntSdk ontSdk;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
        OntSdk ontSdk = OntSdk.getInstance();
        assertNotNull(ontSdk);
        assertSame(ontSdk,this.ontSdk);
    }

    @Test
    public void getBlockHeight() throws  Exception {
        int blockHeight = ontSdk.getConnectMgr().getBlockHeight();
        assertTrue(blockHeight >0);
    }

    @Test
    public void getBlock() throws  Exception {
        //Block block0 = ontSdk.getConnectMgr().getBlock(0);
        //assertNotNull(block0);
        int blockHeight = ontSdk.getConnectMgr().getBlockHeight();
        Block block = ontSdk.getConnectMgr().getBlock(blockHeight);
        assertNotNull(block);
    }

    @Test
    public void getNodeCount() throws  Exception {
        int nodeCount = ontSdk.getConnectMgr().getNodeCount();
        assertTrue(nodeCount > 0);
    }

    @Test
    public void getGenerateTime() throws  Exception {
        int time = ontSdk.getConnectMgr().getGenerateBlockTime();
        assertTrue(time > 0);
        assertTrue(time == 6);
    }

    @Test
    public void getTransaction() throws  Exception {
        Transaction transaction = ontSdk.getConnectMgr().getTransaction("");
        assertNotNull(transaction);
    }
}