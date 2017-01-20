package com.pinterest.secor.uploader.exceptions;

/**
 * @author Lorenzo Bugiani (lorenzo.bugiani@gmail.com)
 */
public class HandleException extends Exception {

    public HandleException(){
        super();
    }

    public HandleException(String message){
        super(message);
    }

    public HandleException(Throwable cause){
        super(cause);
    }

    public HandleException(String message, Throwable cause){
        super(message, cause);
    }
}
