package com.khantech.gaming.tms.exception;

import com.khantech.gaming.tms.api.ApiBuilder;
import com.khantech.gaming.tms.api.ApiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler implements ApiBuilder {
    Logger log = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiMessage> handleRuntimeException(RuntimeException ex) {
        log.error("General exception occurred: {}", ex);
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ApiMessage> handleWalletNotFoundException(WalletNotFoundException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ApiMessage> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(TransactionNotAwaitingApprovalException.class)
    public ResponseEntity<ApiMessage> handleTransactionNotAwaitingApprovalException(TransactionNotAwaitingApprovalException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiMessage> handleUserNotFoundException(UserNotFoundException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(InvalidWalletNameException.class)
    public ResponseEntity<ApiMessage> handleInvalidWalletNameException(InvalidWalletNameException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(DuplicateWalletException.class)
    public ResponseEntity<ApiMessage> handleInvalidWalletNameException(DuplicateWalletException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiMessage> handleInvalidMethodArgumentException(MethodArgumentNotValidException ex) {
        ApiMessage apiMessage = generateApiMessage(generateApiInfo(ex));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiMessage);
    }
}
