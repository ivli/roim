/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;
import java.io.Serializable;
import java.awt.image.*;

/**
 *
 * @author likhachev
 */
public class PValueTransform implements Serializable, Transformation {
        private final double iSlope;
        private final double iIntercept;

        public PValueTransform(double aS, double aI) {iSlope = aS; iIntercept = aI;}
        public PValueTransform() {iSlope = 1.0; iIntercept = 0.0;}
        
        public final double transform(double aV) {return iSlope*aV + iIntercept;}
        
        public final BufferedImage transform(BufferedImage aSrc, BufferedImage aDst) {
            RescaleOp rop = new RescaleOp((float)iSlope, (float)iIntercept, null);            
            return rop.filter(aSrc, aDst);
        }      
    }
