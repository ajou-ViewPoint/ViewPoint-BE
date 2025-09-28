package com.www.viewpoint.global.model.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@JsonPropertyOrder({"success", "data"})
public class ApiResponse<T> {

    private final boolean success = true;
    private final T data;

    // Private constructor to enforce usage of static methods
    private ApiResponse(T data) {
        this.data = data;
    }

    // Static method to create a success response with HTTP 200 OK
    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(T data) {
        return ResponseEntity.ok(new ApiResponse<>(data));
    }

    // Overloaded method for creating a response with a different status (e.g., 201 Created)
    public static <T> ResponseEntity<ApiResponse<T>> onSuccess(HttpStatus status, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(data));
    }
}