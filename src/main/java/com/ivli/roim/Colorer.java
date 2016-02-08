
package com.ivli.roim;


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author likhachev
 */
public class Colorer {    
    private static final Color [] COLORS = new Color[] {Color.RED, Color.PINK, Color.ORANGE, 
                                                        Color.YELLOW, Color.GREEN, Color.MAGENTA,
                                                        Color.CYAN, Color.BLUE, Color.GRAY
                                                       };
    
    private static final Map <Class, Integer> iHash = new HashMap();
    
    static synchronized Color getNextColor(Object aO) {        
        int ret = 0;
        
        if (iHash.containsKey(aO.getClass())) {
            int ndx = iHash.get(aO.getClass()) + 1;
            if (ndx >= COLORS.length)
                ret = 0;
            else
                ret = ndx;
        }
        
        iHash.put(aO.getClass(), ret);
        
        return COLORS[ret];
    }
    
    private Colorer() {}
}
