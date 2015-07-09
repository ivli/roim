/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;
import java.util.ArrayList;
/**
 *
 * @author likhachev
 */
public class Curve {
   
   private class Pair { 
        double iVal; 
        long   iStart;
        public Pair(double aVal, long aStart) {
            iVal = aVal;
            iStart = aStart;
        }
   }
   
   final String iName; 
   final ArrayList<Pair> iCurve = new ArrayList();
   
   public Curve (String aName) {iName = aName;}
   
   void add(double aValue, long aMillis) {
       iCurve.add(new Pair(aValue, aMillis));
   }
   
   
   
}
