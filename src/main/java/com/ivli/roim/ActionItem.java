
package com.ivli.roim;


import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author likhachev
 */
public abstract class ActionItem {
    protected int iX;
    protected int iY;

    public ActionItem(int aX, int aY) {
        iX = aX; 
        iY = aY;
    }

    public final boolean release(int aX, int aY) {
       return DoRelease(aX, aY);
    }

    public final void action(int aX, int aY) {
        DoAction(aX, aY); iX = aX; iY = aY;
    }

    public final void wheel(int aX) {
        DoWheel(aX);
    }     

    public final void paint(Graphics2D gc) {
        Color oc = gc.getColor();
        gc.setColor(Settings.ACTIVE_ROI_COLOR);
        DoPaint(gc);
        gc.setColor(oc);
    }

    protected abstract void DoAction(int aX, int aY); 
    protected  boolean DoWheel(int aX){return false;}
    // return true if action shall be continued
    protected  boolean DoRelease(int aX, int aY) {return false;}
    protected  void DoPaint(Graphics2D aGC) {}   
}

