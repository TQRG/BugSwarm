package org.yamcs;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ObjectInputStream;

import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.utils.ActiveMQBufferInputStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yamcs.api.YamcsSession;
import org.yamcs.api.artemis.Protocol;
import org.yamcs.api.artemis.YamcsClient;
import org.yamcs.management.ManagementService;
import org.yamcs.protobuf.YamcsManagement.MissionDatabaseRequest;
import org.yamcs.xtce.XtceDb;

public class YamcsServerTest {
    static EmbeddedActiveMQ hornetServer;
    
    @BeforeClass
    public static void setupYamcs() throws Exception {
        YConfiguration.setup("YamcsServer");
        ManagementService.setup(false, false);
        org.yamcs.yarch.management.JMXService.setup(false);
        hornetServer=YamcsServer.setupArtemis();
        YamcsServer.setupYamcsServer();
    }
    
    @AfterClass
    public static void shutDownYamcs()  throws Exception {
	YamcsServer.stopArtemis();
    }
    
    @Test
    public void testRetrieveMdb() throws Exception {
        YamcsSession ys=YamcsSession.newBuilder().build();
        YamcsClient yc=ys.newClientBuilder().setRpc(true).setDataConsumer(null, null).build();
        MissionDatabaseRequest mdr = MissionDatabaseRequest.newBuilder().setDbConfigName("refmdb").build();
        yc.executeRpc(Protocol.YAMCS_SERVER_CONTROL_ADDRESS, "getMissionDatabase", mdr, null);
        ClientMessage msg=yc.dataConsumer.receive(5000);
        assertNotNull(msg);
        ObjectInputStream ois=new ObjectInputStream(new ActiveMQBufferInputStream(msg.getBodyBuffer()));
        Object o=ois.readObject();
        assertTrue(o instanceof XtceDb);
        XtceDb xtcedb=(XtceDb) o;
        assertNotNull(xtcedb.getSequenceContainer("/REFMDB/SUBSYS1/PKT1_1"));
        ois.close();
        
        yc.close();
        ys.close();
    }
    
}
