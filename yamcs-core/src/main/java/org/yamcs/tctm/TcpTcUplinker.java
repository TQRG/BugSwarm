package org.yamcs.tctm;

import org.yamcs.ConfigurationException;

/**
 * Sends raw ccsds packets on Tcp socket.
 * @author nm
 * 
 * @deprecated this class has been renamed to {@TcpTcDataLink}
 */
@Deprecated
public class TcpTcUplinker extends TcpTcDataLink {
    public TcpTcUplinker(String yamcsInstance, String name, String spec) throws ConfigurationException {
      super(yamcsInstance, name, spec);
    }

    protected TcpTcUplinker() {
        super();
    }

    public TcpTcUplinker(String host, int port) {
        super(host, port);
    }
}

