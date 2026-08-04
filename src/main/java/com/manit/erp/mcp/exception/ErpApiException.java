package com.manit.erp.mcp.exception;

/**
 * Base RuntimeException for ERP API errors.
 */
public class ErpApiException extends RuntimeException {
    public ErpApiException(String message) {
        super(message);
    }

    public ErpApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
