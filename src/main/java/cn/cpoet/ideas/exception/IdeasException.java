package cn.cpoet.ideas.exception;

/**
 * 异常
 *
 * @author CPoet
 */
public class IdeasException extends RuntimeException {

    private static final long serialVersionUID = -6045503054658482670L;

    public IdeasException(String message) {
        super(message);
    }

    public IdeasException(String message, Throwable cause) {
        super(message, cause);
    }
}
