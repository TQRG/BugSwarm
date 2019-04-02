package com.twilio.twiml;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

/**
 * TwiML wrapper for @see https://www.twilio.com/docs/api/twiml/redirect.
 */
@XmlRootElement(name = "Redirect")
public class Redirect extends TwiML {

    @XmlAttribute
    private final Method method;

    @XmlValue
    private final String url;

    private Redirect() {
        this(new Builder());
    }

    private Redirect(Builder b) {
        this.method = b.method;
        this.url = b.url;
    }

    public Method getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Redirect redirect = (Redirect) o;
        return method == redirect.method &&
            Objects.equal(url, redirect.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(method, url);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("method", method)
            .add("url", url)
            .toString();
    }

    public static class Builder {
        private Method method;
        private String url;

        public Builder() {

        }

        public Builder(String url) {
            this.url = url;
        }

        public Builder method(Method method) {
            this.method = method;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Redirect build() {
            return new Redirect(this);
        }
    }
}
