package Exceptions;

/**
 * Created by Dark on 12/14/2016.
 */
public class DBException extends STException {
    public DBException(String msg, Exception e) {
        super(msg, e);
    }
}
