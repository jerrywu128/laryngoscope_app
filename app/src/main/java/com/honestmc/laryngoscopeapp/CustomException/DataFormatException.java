package com.honestmc.laryngoscopeapp.CustomException;


/**
 * Created by b.jiang on 2016/5/10.
 */
public class DataFormatException extends Exception{
    public DataFormatException() {
        super();
    }

    public DataFormatException(String tag,String message) {
        super(message);
    }
}
