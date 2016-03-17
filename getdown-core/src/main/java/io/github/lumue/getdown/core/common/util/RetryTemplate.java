package io.github.lumue.getdown.core.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lm on 27.08.15.
 *
 * Template for retrying something n times.
 */
public class RetryTemplate {

    private final static Logger LOGGER= LoggerFactory.getLogger(RetryTemplate.class);

    public static interface TryableFunction<T>{
        public T execute() ;
    }

    /**
     *
     * try tries times, or until a result is returned from tryable
     *
     * @param tries
     * @param tryable
     * @param <X>
     * @return
     */
    public static <X> X retry(int tries,TryableFunction<X> tryable){

        int tried=0;
        X result=null;

        while(result==null && tried<tries){
                tried++;
                try {
                    result = tryable.execute();
                }catch(Throwable t){
                    LOGGER.warn(tried+". try failed",t);
                }
        }


        return result;
    }

}
