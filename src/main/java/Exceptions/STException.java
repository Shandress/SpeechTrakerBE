package Exceptions;

/**
 * Created by Dark on 12/14/2016.
 */
public class STException extends Exception {
    public STException(String msg, Exception e) {
        super(msg, e);
    }
}
