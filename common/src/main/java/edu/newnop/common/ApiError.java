package edu.newnop.common;


public record ApiError<T>(
        String error,
        String message,
        T data,
        long timestamp
) {
    public static <T> ApiError<T> error(String error, String message, T data) {
        return new ApiError<>(error, message, data, System.currentTimeMillis());
    }
}
