package org.example.exception;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FeignException extends Exception {

    private static final long serialVersionUID= 1L;

    @Getter
    private String errorCode;
    @Setter
    private String message;
    @Getter
    private String feignName;
    @Getter
    private String step;


    public FeignException(String feignName, String errorCode, String message) {
        this.errorCode = errorCode;
        this.feignName = feignName;
        this.message = message;
    }

    public FeignException(String feignName, String errorCode, String message, String step) {
        this.errorCode = errorCode;
        this.feignName = feignName;
        this.message = message;
        this.step = step;
    }

    @Override
    public String getMessage() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE).append("feignName", feignName)
                .append("errorCode", errorCode).append("message", message)
                .append("step", step).toString();
    }

}
