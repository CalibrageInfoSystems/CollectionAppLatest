package com.oilpalm3f.mainapp.common;

/**
 * Created by siva on 06/07/17.
 */
//Exception
public class OilPalmException extends Exception
{

    private static final long serialVersionUID = 1997753363232807009L;

    public OilPalmException()
    {
    }

    public OilPalmException(String message)
    {
        super(message);
    }

    public OilPalmException(Throwable cause)
    {
        super(cause);
    }

    public OilPalmException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public OilPalmException(String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

} 
