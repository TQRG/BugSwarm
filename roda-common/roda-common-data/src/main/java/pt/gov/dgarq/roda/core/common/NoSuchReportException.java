package pt.gov.dgarq.roda.core.common;

import pt.gov.dgarq.roda.core.data.Report;

/**
 * Thrown to indicate that some specified {@link Report} doesn't exist.
 * 
 * @author Rui Castro
 */
public class NoSuchReportException extends RODAException {
	private static final long serialVersionUID = 6472518809600555367L;

	/**
	 * Constructs a new {@link NoSuchReportException}.
	 */
	public NoSuchReportException() {
	}

	/**
	 * Constructs a new {@link NoSuchReportException} with the given message.
	 * 
	 * @param message
	 */
	public NoSuchReportException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link NoSuchReportException} with the given cause
	 * Exception.
	 * 
	 * @param cause
	 */
	public NoSuchReportException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@link NoSuchReportException} with the given message and
	 * cause Exception.
	 * 
	 * @param message
	 * @param cause
	 */
	public NoSuchReportException(String message, Throwable cause) {
		super(message, cause);
	}

}
