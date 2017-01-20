package com.pinterest.secor.uploader;

import com.pinterest.secor.uploader.exceptions.HandleException;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle with retry policy
 *
 * @author Lorenzo Bugiani (lorenzo.bugiani@gmail.com)
 */
public abstract class RetryHandle<T> implements Handle<T>{

    private static final int MAX_BACKOFF_ITERATION = 5; //use 5 or 6 to have a maximum backoff of 32 or 64 seconds
    private static final int MAX_RETRY = 10;
    private static final int MAX_BACKOFF_TIME = 1000 << MAX_BACKOFF_ITERATION;

    private static final Logger LOG = LoggerFactory.getLogger(RetryHandle.class);

    public T get() throws Exception {
        int n = 1000;
        Random randomGenerator = new Random();
        for (int i = 1; i <= MAX_RETRY; i++) {
            try {
                return this._get();
            } catch (Exception e) {
                LOG.warn("Handle attempt " + i + " failed due to:", e);
                //wait (2^i + random) seconds, or (MAX_BACKOFF_TIME + random) if less
                int millis = ((i >= MAX_BACKOFF_ITERATION) ? MAX_BACKOFF_TIME : (n <<= 1)) + randomGenerator.nextInt(1001);
                LOG.info("Waiting for " + millis + "ms before retry");
                try {
                    Thread.sleep(millis);
                }catch(Exception ex){
                    LOG.error("Unable to retry, thread interrupted during sleep");
                    throw new HandleException(ex);
                }
            }
        }
        throw new HandleException("Maximum number of attempt reached, unable to complete");
    }

    protected abstract T _get() throws Exception;

}
