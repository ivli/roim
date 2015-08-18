/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.util.ArrayList;
import java.io.Serializable;
/**
 *
 * @author likhachev
 */



public class Curve extends ArrayList<Measure<Double>> implements Serializable {
    String iName;
    
    Curve(String aName) {
        iName = aName;
    }
    
    
}
