/*
 * Copyright (C) 2016 likhachev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.ivli.roim.calc;

/**
 *
 * @author likhachev
 */
public class Formatter {
    /*BinaryOp iOp;
    
    protected Formatter(BinaryOp aOp) {iOp = aOp;}
        
    protected Formatter(IOperation aOp) {
        if (aOp instanceof BinaryOp)
            
    }
    */
    
    public static String getString(BinaryOp aOp, int aFrame) {
       return String.format("%f", aOp.iLhs.value().get(aFrame)) + 
              " " + 
              aOp.iOp.getOperationChar() + 
              " " +
              String.format("%f", aOp.iRhs.value().get(aFrame));
    }
    
    public static String getCompleteString(BinaryOp aOp, int aFrame) {
        return Formatter.getString(aOp, aFrame) + 
                "=" +
                String.format("%f", aOp.value().value().get(aFrame));
    }   
    
    
    public static String getString(IOperation aOp, int aFrame) {  
        if (aOp instanceof BinaryOp)
            return getString((BinaryOp)aOp, aFrame);
        else
            return "Not implemnted yet";
    }
    
    public static String getCompleteString(IOperation aOp, int aFrame) {  
        if (aOp instanceof BinaryOp)
            return getCompleteString((BinaryOp)aOp, aFrame);
        else
            return "Not implemnted yet";
    }
    
}
