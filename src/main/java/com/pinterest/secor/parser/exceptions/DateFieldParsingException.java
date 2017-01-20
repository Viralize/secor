package com.pinterest.secor.parser.exceptions;

import scala.tools.nsc.Global;

/**
 * @author Lorenzo Bugiani (lorenzo.bugiani@gmail.com)
 */
public class DateFieldParsingException extends RuntimeException {
    public DateFieldParsingException(){
        super();
    }

    public DateFieldParsingException(String message){
        super(message);
    }

    public DateFieldParsingException(Throwable cause){
        super(cause);
    }

    public DateFieldParsingException(String message, Throwable cause){
        super(message, cause);
    }
}
