package org.yamcs.hornetq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.ConfigurationException;
import org.yamcs.yarch.Stream;
import org.yamcs.yarch.StreamSubscriber;
import org.yamcs.yarch.Tuple;
import org.yamcs.yarch.YarchDatabase;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;

import org.yamcs.api.YamcsClient;
import org.yamcs.api.YamcsSession;

import com.google.common.util.concurrent.AbstractService;

/**
 * takes data from yarch streams and publishes it to hornetq address (reverse of HornetQTmProvider)
 * 
 *
 */
public class AbstractHornetQTranslatorService extends AbstractService {
    final private TupleTranslator translator;
    YamcsSession yamcsSession;

    List<Stream> streams = new ArrayList<Stream>();
    Map<Stream, StreamSubscriber> streamSubscribers = new HashMap<Stream, StreamSubscriber>();


    Logger log=LoggerFactory.getLogger(this.getClass().getName());
    String instance;

    private final ThreadLocal<YamcsClient> yamcsClient =
            new ThreadLocal<YamcsClient>() {
        YamcsSession yamcsSession;
        YamcsClient yClient;
        @Override protected YamcsClient initialValue() {
            try {
                yamcsSession = YamcsSession.newBuilder().build();
                yClient = yamcsSession.newClientBuilder().setDataProducer(true).build();
                return yClient;
            } catch (Exception e) {
                throw new RuntimeException("Cannot create a hornetq client", e);
            }
        }
    };

    public AbstractHornetQTranslatorService(String instance, List<String> streamNames, TupleTranslator translator) throws ConfigurationException {
        this.instance = instance;
        YarchDatabase db = YarchDatabase.getInstance(instance);

        for(String sn: streamNames) {
            Stream s = db.getStream(sn);
            if(s==null) throw new ConfigurationException("Cannot find stream '"+sn+"'");
            streams.add(s);
        }
        this.translator = translator;

        try {

        } catch (Exception e) {
            throw new ConfigurationException("Cannot create hornetq client", e);
        }
    }


    @Override
    public String toString() {
        return "HornetQTmService";
    }


    @Override
    protected void doStart() {
        for(Stream s:streams) {
            final SimpleString hornetAddress = new SimpleString(instance+"."+s.getName());
            log.debug("Starting providing tuples from stream {} as messages on {} HornetQ address", s.getName(), hornetAddress.toString());
            StreamSubscriber ss = new StreamSubscriber() {
                @Override
                public void onTuple(Stream stream, Tuple tuple) {
                    try {
                        
                        ClientMessage msg=translator.buildMessage(yamcsClient.get().getYamcsSession().createMessage(false), tuple);
                        msg.putIntProperty(StreamAdapter.UNIQUEID_HDR_NAME, StreamAdapter.UNIQUEID);
                        yamcsClient.get().dataProducer.send(hornetAddress, msg);
                    } catch (IllegalArgumentException e) {
                        log.warn(e.getMessage());
                    } catch (HornetQException e) {
                        log.warn("Got exception when sending message:", e);
                    }
                }
                @Override
                public void streamClosed(Stream stream) {
                    log.info("Stream "+stream +" closed");
                }

            }; 
            s.addSubscriber(ss);
            streamSubscribers.put(s, ss);
        }
        notifyStarted();
    }


    @Override
    protected void doStop() {
        for(Stream s:streams) {
            s.removeSubscriber(streamSubscribers.get(s));
        }
    }
}


