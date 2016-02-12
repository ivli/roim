
package com.ivli.roim;

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

    public final void paint(java.awt.Graphics2D gc) {
        java.awt.Color oc = gc.getColor();
        gc.setColor(Settings.ACTIVE_ROI_COLOR);
        DoPaint(gc);
        gc.setColor(oc);
    }

    protected abstract void DoAction(int aX, int aY); 
    
    protected boolean DoWheel(int aX) {
        return false;
    }
    
     // it shall return true if action is not completed and ought to be continued
    protected boolean DoRelease(int aX, int aY)  {
        return false;
    }
    
    protected void DoPaint(java.awt.Graphics2D aGC) {}   
}

