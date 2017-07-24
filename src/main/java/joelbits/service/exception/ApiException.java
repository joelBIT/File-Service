package joelbits.service.exception;

import static javax.ws.rs.core.Response.Status;

public class ApiException extends Exception {
    private static final long serialVersionUID = 3256530530626870231L;
    private final Status status;
    private final String message;

    public ApiException(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
