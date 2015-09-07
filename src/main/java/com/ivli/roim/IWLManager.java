
package com.ivli.roim;

import org.jfree.data.xy.XYSeries;

/**
 *
 * @author likhachev
 */
public interface IWLManager extends Transformation {       
    void    frameChanged();
    void    setLUT(String aL);
    void    setWindow(Window aW);
    Window  getWindow();
    Range   getRange();
    void    setInverted(boolean aI);
    boolean isInverted();
    void    setLinear(boolean aI);
    boolean isLinear();   
    XYSeries makeXYSeries(XYSeries ret);
}
