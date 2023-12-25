package ro.tuc.ds2020.controllers.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;


public class MicroserviceException extends CustomException{
    private static HttpStatus STATUS = HttpStatus.SERVICE_UNAVAILABLE;
    private static String MESSAGE = "There is a problem with the device microservice";
    public MicroserviceException(String resource) {
        super(MESSAGE, STATUS, resource, new ArrayList<>());
    }
}
