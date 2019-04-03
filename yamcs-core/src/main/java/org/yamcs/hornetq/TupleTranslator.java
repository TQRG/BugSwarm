package org.yamcs.hornetq;

import org.hornetq.api.core.client.ClientMessage;
import org.yamcs.yarch.Tuple;
import org.yamcs.yarch.TupleDefinition;


/**
 * Translates between tuples and HornetQ messages.
 * 
 **/

public interface TupleTranslator {
    /**
     * 
     * Can throw InvalidParameterException
     * @return the original msg
     */
    ClientMessage buildMessage(ClientMessage msg, Tuple tuple);
    /**
     * 
     * Can throw InvalidParameterException
     * @param message
     * @return
     */
    Tuple buildTuple(TupleDefinition tdef, ClientMessage message);
}
