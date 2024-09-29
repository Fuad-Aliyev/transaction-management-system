package com.khantech.gaming.tms.api;

import com.khantech.gaming.tms.exception.BaseApiRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * ApiBuilder provides common utility methods for constructing API response messages,
 * error information, and logging. It handles the generation of API error and info messages,
 * and helps build standardized responses for single and collection data.
 */
public interface ApiBuilder {
    Logger log = LoggerFactory.getLogger(ApiBuilder.class);


    /**
     * Generates the current timestamp in ISO_LOCAL_DATE_TIME format for the UTC timezone.
     *
     * @return a formatted string representing the current timestamp in UTC.
     */
    default String timeStamp(){
        return LocalDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Generates an {@link ApiInfo} object representing a failed operation due to a runtime exception.
     * This method is used to wrap the error details and provide consistent information in API responses.
     * It includes a failure status, the current timestamp, and a list of errors generated from the exception.
     *
     * @param ex the {@link RuntimeException} that caused the failure, which will be used to generate the error details.
     * @return an instance of {@link ApiInfo} containing the failure status, timestamp, and the error generated from the exception.
     */
    default ApiInfo generateApiInfo(RuntimeException ex){
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setTimestamp(timeStamp());
        apiInfo.setSuccess(false);
        apiInfo.setStatus(ApiConstants.STATUS_FAILED);
        apiInfo.setErrors(Collections.singletonList(generateApiError(ex)));

        return apiInfo;
    }

    /**
     * Generates a generic {@link ApiError} based on a runtime exception.
     * This method will mask internal details by returning a generic error code and description,
     * but includes the original exception message in the reason field for logging purposes.
     *
     * @param ex the {@link RuntimeException} that occurred, which will be used to generate the error details.
     * @return an instance of {@link ApiError} containing the generic error code, description, and the original error message as the reason.
     */
    default ApiError generateApiError(RuntimeException ex){
        ApiError apiError = new ApiError();
        String detailedMessage = ex.getMessage() != null ? ex.getMessage() : "Unknown runtime error";
        apiError.setCode("GENERAL_ERROR");
        apiError.setDescription("An unexpected error occurred. Please contact support.");
        apiError.setReason(detailedMessage);
        return apiError;
    }

    /**
     * Generates an {@link ApiInfo} object representing a failed operation caused by a custom {@link BaseApiRuntimeException}.
     * This method wraps the exception details into an {@link ApiError} and attaches it to the {@link ApiInfo}.
     * The {@link ApiInfo} contains a failure status, the current timestamp, and the specific error generated from the exception.
     *
     * @param ex the {@link BaseApiRuntimeException} that triggered the failure, used to generate detailed error information.
     * @return an instance of {@link ApiInfo} containing the failure status, timestamp, and the error generated from the custom exception.
     */
    default ApiInfo generateApiInfo(BaseApiRuntimeException ex){
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setTimestamp(timeStamp());
        apiInfo.setSuccess(false);
        apiInfo.setStatus(ApiConstants.STATUS_FAILED);
        apiInfo.setErrors(Collections.singletonList(generateApiError(ex)));

        return apiInfo;
    }

    /**
     * Generates an {@link ApiError} object based on the provided {@link BaseApiRuntimeException}.
     * This method uses the code, message, and reason from the exception to populate the fields in the {@link ApiError}.
     * It provides detailed error information, making it easier to trace and resolve issues.
     *
     * @param ex the {@link BaseApiRuntimeException} from which the error details (code, message, and reason) are extracted.
     * @return an instance of {@link ApiError} containing the error code, message, and reason derived from the exception.
     */
    default ApiError generateApiError(BaseApiRuntimeException ex){
        ApiError apiError = new ApiError();
        apiError.setCode(ex.getCode());
        apiError.setDescription(ex.getMessage());
        apiError.setReason(ex.getReason());
        return apiError;
    }

    default ApiInfo generateApiInfo(MethodArgumentNotValidException ex){
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setTimestamp(timeStamp());
        apiInfo.setSuccess(false);
        apiInfo.setStatus(ApiConstants.STATUS_FAILED);
        apiInfo.setErrors(generateApiErrors(ex));

        return apiInfo;
    }

    default List<ApiError> generateApiErrors(MethodArgumentNotValidException ex) {
        List<ApiError> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ApiError apiError = new ApiError();
            apiError.setCode("INVALID_ARGUMENT");
            apiError.setDescription(error.getDefaultMessage());
            apiError.setReason(String.format("Invalid value for field '%s'", error.getField()));

            errors.add(apiError);
        }

        return errors;
    }

    /**
     * Generates a default {@link ApiInfo} for successful operations.
     *
     * @return an instance of {@link ApiInfo} with success status and current timestamp.
     */
    default ApiInfo generateApiInfo(){
        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setTimestamp(timeStamp());
        apiInfo.setSuccess(true);
        apiInfo.setStatus(ApiConstants.STATUS_OK);
        return apiInfo;
    }

    /**
     * Generates an {@link ApiMessage} that wraps the provided {@link ApiInfo}.
     *
     * @param apiInfo the API info to be included in the message.
     * @return an instance of {@link ApiMessage} containing the API info.
     */
    default ApiMessage generateApiMessage(ApiInfo apiInfo){
        ApiMessage apiMessage = new ApiMessage();
        apiMessage.setInfo(apiInfo);
        return apiMessage;
    }


    /**
     * Generates a {@link SingleMessage} response for single-item data, wrapping the item and success information.
     *
     * @param data the data item to be wrapped.
     * @param <L>  the type of the data item.
     * @return an instance of {@link SingleMessage} containing the data item and success info.
     */
    default <L extends Serializable> SingleMessage<L> generateSingleMessage(L data){
        SingleMessage<L> singleMessage = new SingleMessage<L>();
        singleMessage.setItem(data);
        singleMessage.setInfo(generateApiInfo());
        return singleMessage;
    }

    /**
     * Generates a {@link CollectionMessage} response for a collection of data items, wrapping the items and success information.
     *
     * @param collection the collection of data items to be wrapped.
     * @param <L>        the type of the data items.
     * @return an instance of {@link CollectionMessage} containing the collection of data and success info.
     */
    default <L extends Serializable> CollectionMessage<L> generateCollectionMessage(Collection<L> collection){
        CollectionMessage<L> collectionMessage = new CollectionMessage<L>();
        collectionMessage.setItems(collection);
        collectionMessage.setInfo(generateApiInfo());
        return collectionMessage;
    }
}
