package com.icatch.sbcapp.CustomException;


/**
 * Created by b.jiang on 2016/5/10.
 */
public class NullPointerException extends Exception{
    private String exceptionType = "NullPointerException!";
    public NullPointerException() {
        super();
    }

    public NullPointerException(String tag,String describleInfo,String detailInfo) {
        super(describleInfo);
    }
}
