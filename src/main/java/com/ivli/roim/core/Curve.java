/*
 *
 */
package com.ivli.roim.core;

/**
 *
 * @author likhachev
 */
public class Curve extends java.util.ArrayList<Double> {
    public Curve(com.ivli.roim.Series aS, int aC) {
        
        for (com.ivli.roim.Measure m : aS) {
            double val;
            
            switch (aC) {            
                case 1:
                    val = m.getIden();
                    break;
                case 2:
                    val = m.getIden();
                    break;
                case 3:
                    val = m.getIden();
                    break;
                default: throw new IllegalArgumentException();
            }
            
            add(val);
        }
    }
    
    
     
    public void fit() {
        
    }
}
