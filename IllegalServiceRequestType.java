package com.camunda.engine.application;

import lombok.Getter;

@Getter
public class IllegalServiceRequestType extends Exception{

    private final String serviceRequestType;

    public IllegalServiceRequestType(String serviceRequestType) {
        this.serviceRequestType = serviceRequestType;
    }

    public IllegalServiceRequestType(String message, String serviceRequestType) {
        super(message);
        this.serviceRequestType = serviceRequestType;
    }

    public IllegalServiceRequestType(String message, Throwable cause, String serviceRequestType) {
        super(message, cause);
        this.serviceRequestType = serviceRequestType;
    }

    public IllegalServiceRequestType(Throwable cause, String serviceRequestType) {
        super(cause);
        this.serviceRequestType = serviceRequestType;
    }

    public IllegalServiceRequestType(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String serviceRequestType) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.serviceRequestType = serviceRequestType;
    }
}
