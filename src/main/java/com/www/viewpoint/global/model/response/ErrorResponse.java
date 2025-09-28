package com.www.viewpoint.global.model.response;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import com.www.viewpoint.global.exception.ErrorDetails;
import com.www.viewpoint.global.exception.BaseException;

@Getter
public class ErrorResponse {
    private final boolean success = false;
    private final ErrorDetails error;

    // Private constructor
    private ErrorResponse(ErrorDetails error) {
        this.error = error;
    }

    // Static method to create and return the full error ResponseEntity
    public static ResponseEntity<ErrorResponse> of(BaseException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .httpStatus(ex.getHttpStatus())
                .build();

        ErrorResponse errorResponse = new ErrorResponse(errorDetails);

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }
}