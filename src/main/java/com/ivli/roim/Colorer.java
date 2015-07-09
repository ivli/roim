/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.io.Serializable;
import java.awt.Color;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
/**
 *
 * @author likhachev
 */
public class Colorer implements Serializable {    
    private static final Color [] iCols = new Color[] {Color.RED, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW};
    private static final Map <Class, Color> iHash = new HashMap();
    
        static synchronized Color getNextColor(Object aO) {
        Color ret = iCols[0];
        if (iHash.containsKey(aO.getClass())) {

            ret = iHash.get(aO.getClass());
            
            for (int i=0; i < iCols.length; ++i)
                if (iCols[i] == ret) {
                    if (i  < iCols.length -1)                         
                        ret = iCols[i+1];                     
                    else
                        ret = iCols[0];
                    break;
                }            
        }
        iHash.put(aO.getClass(), ret);
        return ret;
    }
}
