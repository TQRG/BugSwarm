package org.yamcs.api;

import java.net.URISyntaxException;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.MessageHandler;
import org.yamcs.api.Protocol;
import org.yamcs.api.YamcsApiException;
import org.yamcs.api.YamcsClient;
import org.yamcs.api.YamcsSession;
import org.yamcs.protobuf.Pvalue.ParameterValue;
import org.yamcs.protobuf.Pvalue.ParameterData;
import org.yamcs.protobuf.Yamcs.NamedObjectId;
import org.yamcs.protobuf.Yamcs.NamedObjectList;
import org.yamcs.protobuf.Yamcs.StringMessage;
import org.yamcs.utils.StringConvertors;
import org.yamcs.utils.TimeEncoding;


public class RealtimeParameterTest {
    final YamcsSession ysession;
    final YamcsClient yclient;
    final SimpleString rpcAddr;
    final String namespace="MDB:OPS Name";
    
    RealtimeParameterTest() throws YamcsApiException, URISyntaxException, HornetQException {
        ysession=YamcsSession.newBuilder().setConnectionParams("yamcs://aces-test:5445").build();
        yclient=ysession.newClientBuilder()
        .setRpc(true)
        .setDataConsumer(null, null).build();

        rpcAddr=Protocol.getParameterRealtimeAddress("aces-test");
    
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    yclient.close();
                    ysession.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));
       
    }
    
    void testSubscribeSome() throws Exception {
        
        yclient.dataConsumer.setMessageHandler(new MessageHandler() {
            @Override
            public void onMessage(ClientMessage msg) {
                decodeAndPrint(msg);
            }
        });
        
        System.out.println("subscribing to aces_SHM_REG_SETH");
        NamedObjectId noi=NamedObjectId.newBuilder().setName("aces_SHM_REG_SETH").setNamespace(namespace).build();
        NamedObjectList nol=NamedObjectList.newBuilder().addList(noi).build();
        yclient.executeRpc(rpcAddr, "subscribe", nol, null);
        
        Thread.sleep(15000);
        
        System.out.println("subscribing to aces_SHM_HK_AcqStatus");
        noi=NamedObjectId.newBuilder().setName("aces_SHM_HK_AcqStatus").setNamespace(namespace).build();
        nol=NamedObjectList.newBuilder().addList(noi).build();
        yclient.executeRpc(rpcAddr, "subscribe", nol, null);
        Thread.sleep(15000);
        
        System.out.println("unsubscribing from aces_SHM_REG_SETH");
        noi=NamedObjectId.newBuilder().setName("aces_SHM_REG_SETH").setNamespace(namespace).build();
        nol=NamedObjectList.newBuilder().addList(noi).build();
        yclient.executeRpc(rpcAddr, "unsubscribe", nol, null);
        Thread.sleep(15000);
    }
    
    
    void testSubscribeAll() throws Exception {
        StringMessage nsmsg=StringMessage.newBuilder().setMessage(namespace).build();
        yclient.executeRpc(rpcAddr, "subscribeAll", nsmsg, null);
        
        while(true) {
            ClientMessage msg=yclient.dataConsumer.receive();
            decodeAndPrint(msg);
        }
    }    
    
    static void decodeAndPrint(ClientMessage msg) {
        try {
            ParameterData pdata=(ParameterData) Protocol.decode(msg, ParameterData.newBuilder());
            System.out.println("-------------received "+pdata.getParameterCount()+" parameters:");
            for(int i=0;i<pdata.getParameterCount();i++) {
                ParameterValue pv=pdata.getParameter(i);
                System.out.println(String.format("%-25s %-30s %s", TimeEncoding.toString(pv.getAcquisitionTime()), pv.getId().getName(), StringConvertors.toString(pv.getEngValue(),false)));
                //System.out.println(pv);
            }
            System.out.println();
        } catch(YamcsApiException e) {
            System.out.println("cannot decode message: "+e);
        }
    }
    
    
    public static void main(String[] args) throws Exception {
        TimeEncoding.setUp();
        RealtimeParameterTest rpt=new RealtimeParameterTest();
        
        rpt.testSubscribeAll();
        //rpt.testSubscribeSome();
    }
}