package ro.tuc.ds2020.controllers.handlers.exceptions.model;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;

public class EntityExistsException extends CustomException {
    private static HttpStatus STATUS = HttpStatus.CONFLICT;
    private static String MESSAGE = "Entry already exists";
    public EntityExistsException(String resource) {
        super(MESSAGE, STATUS, resource, new ArrayList<>());
    }
}
