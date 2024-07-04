package codesquad.http;

public enum HttpStatus {
    OK(200, "OK"),

    SEE_OTHER(303, "See Other"),

    BAD_REQUEST(400, "Bad Request"),
    CONFLICT(409, "Conflict"),
    NOT_FOUND(404, "Not Found"),

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
