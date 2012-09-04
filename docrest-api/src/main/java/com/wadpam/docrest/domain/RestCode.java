/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wadpam.docrest.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Use this annotation for each possible response code
 * @author os
 */
@Target(value={ElementType.METHOD})
public @interface RestCode {
    /**
     * The HTTP response code
     * @return 
     */
    public int code();
    
    /**
     * The HTTP response message, e.g. "Not Found"
     * @return 
     */
    public String message() default "";
    
    /**
     * When this response code is returned
     * @return 
     */
    public String description();
}
