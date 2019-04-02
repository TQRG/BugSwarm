package betsy.bpel.virtual.host.comm;

import betsy.bpel.virtual.common.Protocol;
import betsy.bpel.virtual.common.exceptions.CommunicationException;
import betsy.bpel.virtual.common.exceptions.ConnectionException;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesRequest;
import betsy.bpel.virtual.common.messages.collect_log_files.LogFilesResponse;
import betsy.bpel.virtual.common.messages.deploy.DeployRequest;
import betsy.bpel.virtual.common.messages.deploy.DeployResponse;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * The {@link HostTcpClient} is a implementation of the CommClient using a TCP
 * end-to-end connection.
 *
 * @author Cedric Roeck
 * @version 1.0
 */
public class HostTcpClient implements Protocol, AutoCloseable {

    private static final Logger log = Logger.getLogger(HostTcpClient.class);

    private final HostLowLevelTcpClient client;

    public HostTcpClient(final String host, final int port) {
        client = new HostLowLevelTcpClient(host, port);
    }

    @Override
    public LogFilesResponse collectLogFilesOperation(LogFilesRequest request) throws Exception {
        ensureConnection();

        // send the request
        client.sendMessage(request);

        // receive the answer
        Object o = client.receive();

        client.disconnect();

        if (o instanceof LogFilesResponse) {
            log.debug("Logfiles received, now save them");

            return (LogFilesResponse) o;
        } else if (o instanceof CommunicationException) {
            throw (CommunicationException) o;
        } else if (o instanceof Exception) {
            throw (Exception) o;
        } else {
            throw new CommunicationException("Invalid response received: " + o);
        }
    }

    public boolean isReachable(int timeout) {
        try {
            client.reconnect(timeout);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void ensureConnection() {
        try {
            client.reconnect();
        } catch (IOException e) {
            throw new ConnectionException("could not reconnect", e);
        }
    }

    @Override
    public DeployResponse deployOperation(DeployRequest request) throws Exception {
        ensureConnection();

        client.sendMessage(request);

        // wait for response
        Object o = client.receive();

        client.disconnect();

        if (o instanceof DeployResponse) {
            log.debug("deployment successful");
            return (DeployResponse) o;
        } else if (o instanceof CommunicationException) {
            throw (CommunicationException) o;
        } else if (o instanceof Exception) {
            throw (Exception) o;
        } else {
            throw new CommunicationException("Invalid response received: " + o);
        }
    }


    @Override
    public void close() throws Exception {
        client.close();
    }
}
