/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.io.spi;

/**
 *
 * @author likhachev
 */
public class ImageServiceException extends Exception {
    public ImageServiceException() {
        super();
    }
    
    public ImageServiceException(String message) {
        super(message);
    }
    
    public ImageServiceException(String message, Throwable cause) {
        super(message, cause);
    }    
}
