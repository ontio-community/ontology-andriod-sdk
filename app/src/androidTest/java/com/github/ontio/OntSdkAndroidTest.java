package com.github.ontio;

import android.support.test.runner.AndroidJUnit4;

import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.WalletMgr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class OntSdkAndroidTest {
    private OntSdk ontSdk;
    private ConnectMgr connectMgr;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        connectMgr = ontSdk.getConnectMgr();
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
    public void getBlockHeight() throws ConnectorException, IOException {
        int blockHeight = connectMgr.getBlockHeight();
        assertTrue(blockHeight >= 0);
    }

    @Test
    public void getNodeCount() throws ConnectorException, IOException {
        int nodeCount = connectMgr.getNodeCount();
        assertTrue(nodeCount > 0);
    }

    @Test
    public void getGenerateTime() throws ConnectorException, IOException {
        int time = connectMgr.getGenerateBlockTime();
        assertTrue(time > 0);
        assertTrue(time == 6);
    }

    @Test
    public void getFirstBlock() throws ConnectorException, IOException {
        Block block = connectMgr.getBlock(0);
        assertNotNull(block);
    }

    @Test
    public void getBlock() throws ConnectorException, IOException {
        Block block1 = connectMgr.getBlock(1);
        assertNotNull(block1);
        String txhash1 = block1.transactions[0].hash().toHexString();
        Transaction transaction1 = connectMgr.getTransaction(txhash1);
        assertNotNull(transaction1);
        int blockHeight = connectMgr.getBlockHeight();
        Block blockMax = connectMgr.getBlock(blockHeight);
        assertNotNull(blockMax);
    }
}