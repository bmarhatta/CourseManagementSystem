package com.application.ems.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class HttpResponse implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "mm-dd-yyy hh:mm:ss", timezone = "America/New_York")
    private Date timeStamp;
    private int httpStatusCode; //
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp=new Date();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }
}
