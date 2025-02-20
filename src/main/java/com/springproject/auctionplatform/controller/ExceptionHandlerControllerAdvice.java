package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.exception.BannedException;
import com.springproject.auctionplatform.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex) {
        return buildErrorModel(HttpStatus.NOT_FOUND, "The requested resource could not be found.", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorModel(HttpStatus.BAD_REQUEST, "The given data is not valid.", ex.getMessage());
    }

    @ExceptionHandler(BannedException.class)
    public ModelAndView handleBannedException(BannedException ex) {
        return buildErrorModel(HttpStatus.FORBIDDEN, "You are banned!.", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        return buildErrorModel(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "Internal server error!");
    }


    private ModelAndView buildErrorModel(HttpStatus status, String message, String details) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorCode", status.value());
        modelAndView.addObject("errorMessage", message);
        modelAndView.addObject("errorDetails", details);
        return modelAndView;
    }
}
