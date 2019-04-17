/**
 * This code was generated by
 * \ / _    _  _|   _  _
 *  | (_)\/(_)(_|\/| |(/_  v1.0.0
 *       /       /
 */

package com.twilio.rest.taskrouter.v1.workspace.worker;

import com.twilio.base.Updater;
import com.twilio.converter.Promoter;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.Domains;

import java.net.URI;

public class ReservationUpdater extends Updater<Reservation> {
    private final String pathWorkspaceSid;
    private final String pathWorkerSid;
    private final String pathSid;
    private Reservation.Status reservationStatus;
    private String workerActivitySid;
    private String instruction;
    private String dequeuePostWorkActivitySid;
    private String dequeueFrom;
    private String dequeueRecord;
    private Integer dequeueTimeout;
    private String dequeueTo;
    private URI dequeueStatusCallbackUrl;
    private String callFrom;
    private String callRecord;
    private Integer callTimeout;
    private String callTo;
    private URI callUrl;
    private URI callStatusCallbackUrl;
    private Boolean callAccept;
    private String redirectCallSid;
    private Boolean redirectAccept;
    private URI redirectUrl;

    /**
     * Construct a new ReservationUpdater.
     * 
     * @param pathWorkspaceSid The workspace_sid
     * @param pathWorkerSid The worker_sid
     * @param pathSid The sid
     */
    public ReservationUpdater(final String pathWorkspaceSid, 
                              final String pathWorkerSid, 
                              final String pathSid) {
        this.pathWorkspaceSid = pathWorkspaceSid;
        this.pathWorkerSid = pathWorkerSid;
        this.pathSid = pathSid;
    }

    /**
     * The reservation_status.
     * 
     * @param reservationStatus The reservation_status
     * @return this
     */
    public ReservationUpdater setReservationStatus(final Reservation.Status reservationStatus) {
        this.reservationStatus = reservationStatus;
        return this;
    }

    /**
     * The worker_activity_sid.
     * 
     * @param workerActivitySid The worker_activity_sid
     * @return this
     */
    public ReservationUpdater setWorkerActivitySid(final String workerActivitySid) {
        this.workerActivitySid = workerActivitySid;
        return this;
    }

    /**
     * The instruction.
     * 
     * @param instruction The instruction
     * @return this
     */
    public ReservationUpdater setInstruction(final String instruction) {
        this.instruction = instruction;
        return this;
    }

    /**
     * The dequeue_post_work_activity_sid.
     * 
     * @param dequeuePostWorkActivitySid The dequeue_post_work_activity_sid
     * @return this
     */
    public ReservationUpdater setDequeuePostWorkActivitySid(final String dequeuePostWorkActivitySid) {
        this.dequeuePostWorkActivitySid = dequeuePostWorkActivitySid;
        return this;
    }

    /**
     * The dequeue_from.
     * 
     * @param dequeueFrom The dequeue_from
     * @return this
     */
    public ReservationUpdater setDequeueFrom(final String dequeueFrom) {
        this.dequeueFrom = dequeueFrom;
        return this;
    }

    /**
     * The dequeue_record.
     * 
     * @param dequeueRecord The dequeue_record
     * @return this
     */
    public ReservationUpdater setDequeueRecord(final String dequeueRecord) {
        this.dequeueRecord = dequeueRecord;
        return this;
    }

    /**
     * The dequeue_timeout.
     * 
     * @param dequeueTimeout The dequeue_timeout
     * @return this
     */
    public ReservationUpdater setDequeueTimeout(final Integer dequeueTimeout) {
        this.dequeueTimeout = dequeueTimeout;
        return this;
    }

    /**
     * The dequeue_to.
     * 
     * @param dequeueTo The dequeue_to
     * @return this
     */
    public ReservationUpdater setDequeueTo(final String dequeueTo) {
        this.dequeueTo = dequeueTo;
        return this;
    }

    /**
     * The dequeue_status_callback_url.
     * 
     * @param dequeueStatusCallbackUrl The dequeue_status_callback_url
     * @return this
     */
    public ReservationUpdater setDequeueStatusCallbackUrl(final URI dequeueStatusCallbackUrl) {
        this.dequeueStatusCallbackUrl = dequeueStatusCallbackUrl;
        return this;
    }

    /**
     * The dequeue_status_callback_url.
     * 
     * @param dequeueStatusCallbackUrl The dequeue_status_callback_url
     * @return this
     */
    public ReservationUpdater setDequeueStatusCallbackUrl(final String dequeueStatusCallbackUrl) {
        return setDequeueStatusCallbackUrl(Promoter.uriFromString(dequeueStatusCallbackUrl));
    }

    /**
     * The call_from.
     * 
     * @param callFrom The call_from
     * @return this
     */
    public ReservationUpdater setCallFrom(final String callFrom) {
        this.callFrom = callFrom;
        return this;
    }

    /**
     * The call_record.
     * 
     * @param callRecord The call_record
     * @return this
     */
    public ReservationUpdater setCallRecord(final String callRecord) {
        this.callRecord = callRecord;
        return this;
    }

    /**
     * The call_timeout.
     * 
     * @param callTimeout The call_timeout
     * @return this
     */
    public ReservationUpdater setCallTimeout(final Integer callTimeout) {
        this.callTimeout = callTimeout;
        return this;
    }

    /**
     * The call_to.
     * 
     * @param callTo The call_to
     * @return this
     */
    public ReservationUpdater setCallTo(final String callTo) {
        this.callTo = callTo;
        return this;
    }

    /**
     * The call_url.
     * 
     * @param callUrl The call_url
     * @return this
     */
    public ReservationUpdater setCallUrl(final URI callUrl) {
        this.callUrl = callUrl;
        return this;
    }

    /**
     * The call_url.
     * 
     * @param callUrl The call_url
     * @return this
     */
    public ReservationUpdater setCallUrl(final String callUrl) {
        return setCallUrl(Promoter.uriFromString(callUrl));
    }

    /**
     * The call_status_callback_url.
     * 
     * @param callStatusCallbackUrl The call_status_callback_url
     * @return this
     */
    public ReservationUpdater setCallStatusCallbackUrl(final URI callStatusCallbackUrl) {
        this.callStatusCallbackUrl = callStatusCallbackUrl;
        return this;
    }

    /**
     * The call_status_callback_url.
     * 
     * @param callStatusCallbackUrl The call_status_callback_url
     * @return this
     */
    public ReservationUpdater setCallStatusCallbackUrl(final String callStatusCallbackUrl) {
        return setCallStatusCallbackUrl(Promoter.uriFromString(callStatusCallbackUrl));
    }

    /**
     * The call_accept.
     * 
     * @param callAccept The call_accept
     * @return this
     */
    public ReservationUpdater setCallAccept(final Boolean callAccept) {
        this.callAccept = callAccept;
        return this;
    }

    /**
     * The redirect_call_sid.
     * 
     * @param redirectCallSid The redirect_call_sid
     * @return this
     */
    public ReservationUpdater setRedirectCallSid(final String redirectCallSid) {
        this.redirectCallSid = redirectCallSid;
        return this;
    }

    /**
     * The redirect_accept.
     * 
     * @param redirectAccept The redirect_accept
     * @return this
     */
    public ReservationUpdater setRedirectAccept(final Boolean redirectAccept) {
        this.redirectAccept = redirectAccept;
        return this;
    }

    /**
     * The redirect_url.
     * 
     * @param redirectUrl The redirect_url
     * @return this
     */
    public ReservationUpdater setRedirectUrl(final URI redirectUrl) {
        this.redirectUrl = redirectUrl;
        return this;
    }

    /**
     * The redirect_url.
     * 
     * @param redirectUrl The redirect_url
     * @return this
     */
    public ReservationUpdater setRedirectUrl(final String redirectUrl) {
        return setRedirectUrl(Promoter.uriFromString(redirectUrl));
    }

    /**
     * Make the request to the Twilio API to perform the update.
     * 
     * @param client TwilioRestClient with which to make the request
     * @return Updated Reservation
     */
    @Override
    @SuppressWarnings("checkstyle:linelength")
    public Reservation update(final TwilioRestClient client) {
        Request request = new Request(
            HttpMethod.POST,
            Domains.TASKROUTER.toString(),
            "/v1/Workspaces/" + this.pathWorkspaceSid + "/Workers/" + this.pathWorkerSid + "/Reservations/" + this.pathSid + "",
            client.getRegion()
        );

        addPostParams(request);
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Reservation update failed: Unable to connect to server");
        } else if (!TwilioRestClient.SUCCESS.apply(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }

            throw new ApiException(
                restException.getMessage(),
                restException.getCode(),
                restException.getMoreInfo(),
                restException.getStatus(),
                null
            );
        }

        return Reservation.fromJson(response.getStream(), client.getObjectMapper());
    }

    /**
     * Add the requested post parameters to the Request.
     * 
     * @param request Request to add post params to
     */
    private void addPostParams(final Request request) {
        if (reservationStatus != null) {
            request.addPostParam("ReservationStatus", reservationStatus.toString());
        }

        if (workerActivitySid != null) {
            request.addPostParam("WorkerActivitySid", workerActivitySid);
        }

        if (instruction != null) {
            request.addPostParam("Instruction", instruction);
        }

        if (dequeuePostWorkActivitySid != null) {
            request.addPostParam("DequeuePostWorkActivitySid", dequeuePostWorkActivitySid);
        }

        if (dequeueFrom != null) {
            request.addPostParam("DequeueFrom", dequeueFrom);
        }

        if (dequeueRecord != null) {
            request.addPostParam("DequeueRecord", dequeueRecord);
        }

        if (dequeueTimeout != null) {
            request.addPostParam("DequeueTimeout", dequeueTimeout.toString());
        }

        if (dequeueTo != null) {
            request.addPostParam("DequeueTo", dequeueTo);
        }

        if (dequeueStatusCallbackUrl != null) {
            request.addPostParam("DequeueStatusCallbackUrl", dequeueStatusCallbackUrl.toString());
        }

        if (callFrom != null) {
            request.addPostParam("CallFrom", callFrom);
        }

        if (callRecord != null) {
            request.addPostParam("CallRecord", callRecord);
        }

        if (callTimeout != null) {
            request.addPostParam("CallTimeout", callTimeout.toString());
        }

        if (callTo != null) {
            request.addPostParam("CallTo", callTo);
        }

        if (callUrl != null) {
            request.addPostParam("CallUrl", callUrl.toString());
        }

        if (callStatusCallbackUrl != null) {
            request.addPostParam("CallStatusCallbackUrl", callStatusCallbackUrl.toString());
        }

        if (callAccept != null) {
            request.addPostParam("CallAccept", callAccept.toString());
        }

        if (redirectCallSid != null) {
            request.addPostParam("RedirectCallSid", redirectCallSid);
        }

        if (redirectAccept != null) {
            request.addPostParam("RedirectAccept", redirectAccept.toString());
        }

        if (redirectUrl != null) {
            request.addPostParam("RedirectUrl", redirectUrl.toString());
        }
    }
}