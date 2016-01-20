
package com.ivli.roim.core;


/**
 *
 * @author likhachev
 */
public interface IWLManager extends Transformation {           
    
    void    setWindow(Window aNewWindow);
    Window  getWindow();
    
    void    setRange(Range aNewRange);
    Range   getRange();
    
   
    void    openLUT(String aLutFileName);
   
    void    setInverted(boolean aInverted);
    boolean isInverted();
    void    setLinear(boolean aLinear);
    boolean isLinear();   
    void lockRange(boolean aLock);
    void lockWindow(boolean aLock);
    

     //TODO: 
    Histogram getCurve();
}
