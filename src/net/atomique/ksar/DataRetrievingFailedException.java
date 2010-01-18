package net.atomique.ksar;

/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class DataRetrievingFailedException extends Exception {

	private static final long serialVersionUID = 8472821457305791051L;

	/**
	 * Creates an instance of DataRetrievingFailedException
	 */
	public DataRetrievingFailedException() {
		super();
	}

	/**
	 * Creates an instance of DataRetrievingFailedException
	 * @param message
	 * @param cause
	 */
	public DataRetrievingFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates an instance of DataRetrievingFailedException
	 * @param message
	 */
	public DataRetrievingFailedException(String message) {
		super(message);
	}

	/**
	 * Creates an instance of {@link DataRetrievingFailedException}
	 * @param cause
	 */
	public DataRetrievingFailedException(Throwable cause) {
		super(cause);
	}
}