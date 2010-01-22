package net.atomique.ksar;


/**
 * @author Daniel Czerwonk <d.czerwonk@googlemail.com>
 */
public class ParsingException extends Exception {

    /**
     * Creates an instance of ParsingException
     */
    public ParsingException() {
    }

    /**
     * Creates an instance of ParsingException
     * @param message
     */
    public ParsingException(String message) {
        super(message);
    }

    /**
     * Creates an instance of ParsingException
     * @param cause
     */
    public ParsingException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates an instance of ParsingException
     * @param message
     * @param cause
     */
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
