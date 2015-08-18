/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.io.Serializable;

/**
 *
 * @author likhachev
 * @param <T>
 */
public class Measure <T extends Number> implements Serializable {
    T iMin;  //min value 
    T iMax;  //max value
    T iIden; //integral density (a sum of pixel values)  
    
    Measure(T aMin, T aMax, T aIden) {
        iMin  = aMin; 
        iMax  = aMax; 
        iIden = aIden;
    }
    
    Measure(){}    
    
    public T getMin()  {return iMin;}
    public T getMax()  {return iMax;}
    public T getIden() {return iIden;}
}