package cn.cpoet.tool.exception;

/**
 * 异常
 *
 * @author CPoet
 */
public class ToolException extends RuntimeException {

    private static final long serialVersionUID = -6045503054658482670L;

    public ToolException(String message) {
        super(message);
    }

    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }
}
