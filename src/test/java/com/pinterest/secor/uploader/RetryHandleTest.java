package com.pinterest.secor.uploader;

import com.pinterest.secor.uploader.exceptions.HandleException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * Created by Lorenzo Bugiani on 19/01/2017.
 */
public class RetryHandleTest {

    @Test
    public void retryAfterAFailure() throws Exception {
        RetryHandle mockRetryHandle = new RetryHandle<String>(){
            private int failure = 0;
            private int maxFailure = new Random().nextInt(2) + 1; // from 1 to 3

            @Override
            protected String _get() throws Exception {
                if(failure < maxFailure){
                    failure++;
                    throw new Exception();
                }else{
                    return "result";
                }
            }
        };
        try{
            mockRetryHandle.get();
        } catch(Exception e) {
            Assert.fail("RetryHandle should retry");
        }
    }

    @Test(expected=HandleException.class)
    public void throwHandleException() throws Exception {
        RetryHandle mockRetryHandle = new RetryHandle<String>(){

            @Override
            protected String _get() throws Exception {
                throw new Exception();
            }
        };
        mockRetryHandle.get();
        Assert.fail("Expected an HandleException");
    }
}
