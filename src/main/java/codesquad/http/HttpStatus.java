package codesquad.http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),

    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),
    PAYLOAD_TOO_LARGE(413, "Payload Too Large"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),;

    private final int statusCode;
    private final String status;

    HttpStatus(int statusCode, String status) {
        this.statusCode = statusCode;
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatus() {
        return status;
    }
}
