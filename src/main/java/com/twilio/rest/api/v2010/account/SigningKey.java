/**
 * This code was generated by
 * \ / _    _  _|   _  _
 *  | (_)\/(_)(_|\/| |(/_  v1.0.0
 *       /       /
 */

package com.twilio.rest.api.v2010.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import com.twilio.base.Resource;
import com.twilio.converter.DateConverter;
import com.twilio.exception.ApiConnectionException;
import com.twilio.exception.ApiException;
import com.twilio.exception.RestException;
import com.twilio.http.HttpMethod;
import com.twilio.http.Request;
import com.twilio.http.Response;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.Domains;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SigningKey extends Resource {
    private static final long serialVersionUID = 96350302686036L;

    /**
     * Create a SigningKeyFetcher to execute fetch.
     * 
     * @param pathAccountSid The account_sid
     * @param pathSid The sid
     * @return SigningKeyFetcher capable of executing the fetch
     */
    public static SigningKeyFetcher fetcher(final String pathAccountSid, 
                                            final String pathSid) {
        return new SigningKeyFetcher(pathAccountSid, pathSid);
    }

    /**
     * Create a SigningKeyFetcher to execute fetch.
     * 
     * @param pathSid The sid
     * @return SigningKeyFetcher capable of executing the fetch
     */
    public static SigningKeyFetcher fetcher(final String pathSid) {
        return new SigningKeyFetcher(pathSid);
    }

    /**
     * Create a SigningKeyUpdater to execute update.
     * 
     * @param pathAccountSid The account_sid
     * @param pathSid The sid
     * @return SigningKeyUpdater capable of executing the update
     */
    public static SigningKeyUpdater updater(final String pathAccountSid, 
                                            final String pathSid) {
        return new SigningKeyUpdater(pathAccountSid, pathSid);
    }

    /**
     * Create a SigningKeyUpdater to execute update.
     * 
     * @param pathSid The sid
     * @return SigningKeyUpdater capable of executing the update
     */
    public static SigningKeyUpdater updater(final String pathSid) {
        return new SigningKeyUpdater(pathSid);
    }

    /**
     * Create a SigningKeyDeleter to execute delete.
     * 
     * @param pathAccountSid The account_sid
     * @param pathSid The sid
     * @return SigningKeyDeleter capable of executing the delete
     */
    public static SigningKeyDeleter deleter(final String pathAccountSid, 
                                            final String pathSid) {
        return new SigningKeyDeleter(pathAccountSid, pathSid);
    }

    /**
     * Create a SigningKeyDeleter to execute delete.
     * 
     * @param pathSid The sid
     * @return SigningKeyDeleter capable of executing the delete
     */
    public static SigningKeyDeleter deleter(final String pathSid) {
        return new SigningKeyDeleter(pathSid);
    }

    /**
     * Create a SigningKeyReader to execute read.
     * 
     * @param pathAccountSid The account_sid
     * @return SigningKeyReader capable of executing the read
     */
    public static SigningKeyReader reader(final String pathAccountSid) {
        return new SigningKeyReader(pathAccountSid);
    }

    /**
     * Create a SigningKeyReader to execute read.
     * 
     * @return SigningKeyReader capable of executing the read
     */
    public static SigningKeyReader reader() {
        return new SigningKeyReader();
    }

    /**
     * Converts a JSON String into a SigningKey object using the provided
     * ObjectMapper.
     * 
     * @param json Raw JSON String
     * @param objectMapper Jackson ObjectMapper
     * @return SigningKey object represented by the provided JSON
     */
    public static SigningKey fromJson(final String json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, SigningKey.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    /**
     * Converts a JSON InputStream into a SigningKey object using the provided
     * ObjectMapper.
     * 
     * @param json Raw JSON InputStream
     * @param objectMapper Jackson ObjectMapper
     * @return SigningKey object represented by the provided JSON
     */
    public static SigningKey fromJson(final InputStream json, final ObjectMapper objectMapper) {
        // Convert all checked exceptions to Runtime
        try {
            return objectMapper.readValue(json, SigningKey.class);
        } catch (final JsonMappingException | JsonParseException e) {
            throw new ApiException(e.getMessage(), e);
        } catch (final IOException e) {
            throw new ApiConnectionException(e.getMessage(), e);
        }
    }

    private final String sid;
    private final String friendlyName;
    private final DateTime dateCreated;
    private final DateTime dateUpdated;

    @JsonCreator
    private SigningKey(@JsonProperty("sid")
                       final String sid, 
                       @JsonProperty("friendly_name")
                       final String friendlyName, 
                       @JsonProperty("date_created")
                       final String dateCreated, 
                       @JsonProperty("date_updated")
                       final String dateUpdated) {
        this.sid = sid;
        this.friendlyName = friendlyName;
        this.dateCreated = DateConverter.rfc2822DateTimeFromString(dateCreated);
        this.dateUpdated = DateConverter.rfc2822DateTimeFromString(dateUpdated);
    }

    /**
     * Returns The The sid.
     * 
     * @return The sid
     */
    public final String getSid() {
        return this.sid;
    }

    /**
     * Returns The The friendly_name.
     * 
     * @return The friendly_name
     */
    public final String getFriendlyName() {
        return this.friendlyName;
    }

    /**
     * Returns The The date_created.
     * 
     * @return The date_created
     */
    public final DateTime getDateCreated() {
        return this.dateCreated;
    }

    /**
     * Returns The The date_updated.
     * 
     * @return The date_updated
     */
    public final DateTime getDateUpdated() {
        return this.dateUpdated;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SigningKey other = (SigningKey) o;

        return Objects.equals(sid, other.sid) && 
               Objects.equals(friendlyName, other.friendlyName) && 
               Objects.equals(dateCreated, other.dateCreated) && 
               Objects.equals(dateUpdated, other.dateUpdated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sid,
                            friendlyName,
                            dateCreated,
                            dateUpdated);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("sid", sid)
                          .add("friendlyName", friendlyName)
                          .add("dateCreated", dateCreated)
                          .add("dateUpdated", dateUpdated)
                          .toString();
    }
}