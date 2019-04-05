package sic.controller;

import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import sic.service.BusinessServiceException;

@ControllerAdvice
public class ControllersExceptionHandler {
    
    private static final Logger LOGGER = Logger.getLogger(ControllersExceptionHandler.class.getPackage().getName());
     
    @ExceptionHandler(BusinessServiceException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleServiceException(BusinessServiceException ex) {
        LOGGER.error(ex);
        return ex.getMessage();
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception ex) {
        LOGGER.error(ex);
        return ResourceBundle.getBundle("Mensajes").getString("mensaje_error_request");
    }
}
