package lk.jiat.web.dto;

public record ErrorResponse(
        String error,
        String message,
        int status,
        long timestamp
) {
    public static ErrorResponse of(String error, String message, int status) {
        return new ErrorResponse(error, message, status, System.currentTimeMillis());
    }
}
