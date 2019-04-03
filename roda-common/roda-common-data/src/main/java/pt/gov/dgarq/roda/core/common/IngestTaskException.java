package pt.gov.dgarq.roda.core.common;


/**
 * Thrown to indicate that some task could not be performed on a SIP.
 * 
 * @author Rui Castro
 */
public class IngestTaskException extends IngestException {
	private static final long serialVersionUID = 2180665382800465305L;

	/**
	 * Constructs a new {@link IngestTaskException}.
	 */
	public IngestTaskException() {
	}

	/**
	 * Constructs a new {@link IngestTaskException} with the given error
	 * message.
	 * 
	 * @param message
	 *            the error message
	 */
	public IngestTaskException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link IngestTaskException} with the given error message
	 * and cause exception.
	 * 
	 * @param message
	 *            the error message.
	 * @param cause
	 *            the cause exception.
	 */
	public IngestTaskException(String message, Throwable cause) {
		super(message, cause);
	}

}
