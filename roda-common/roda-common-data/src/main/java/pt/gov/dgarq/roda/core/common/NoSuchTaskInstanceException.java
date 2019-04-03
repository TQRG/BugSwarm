package pt.gov.dgarq.roda.core.common;

import pt.gov.dgarq.roda.core.data.TaskInstance;

/**
 * Thrown to indicate that the specified {@link TaskInstance} doesn't exist.
 * 
 * @author Rui Castro
 */
public class NoSuchTaskInstanceException extends RODAException {
	private static final long serialVersionUID = 7114718367954823005L;

	/**
	 * Constructs a new {@link NoSuchTaskInstanceException}.
	 */
	public NoSuchTaskInstanceException() {
	}

	/**
	 * Constructs a new {@link NoSuchTaskInstanceException} with the given error
	 * message.
	 * 
	 * @param message
	 *            the error message
	 */
	public NoSuchTaskInstanceException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link NoSuchTaskInstanceException} with the given cause
	 * exception.
	 * 
	 * @param cause
	 *            the cause exception
	 */
	public NoSuchTaskInstanceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@link NoSuchTaskInstanceException} with the given error
	 * message and cause exception.
	 * 
	 * @param message
	 *            the error message
	 * @param cause
	 *            the cause exception
	 */
	public NoSuchTaskInstanceException(String message, Throwable cause) {
		super(message, cause);
	}

}
