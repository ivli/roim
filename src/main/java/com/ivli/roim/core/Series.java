/*
 * 
 */
package com.ivli.roim.core;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import org.apache.commons.math3.fitting.leastsquares.*;
        
/**
 *
 * @author likhachev
 */
public class Series {     
    private final Measurement iId; 
    private java.util.ArrayList<Double> iData;
    
    public Series(Measurement anId) {        
        iId = anId;        
    }
    
    public Measurement getId() {
        return iId;
    }
    
    public int getNumFrames() {
        return iData.size();
    }
    
    public double get(int anI) {
        return iData.get(anI);
    }
    
    public void add(double aV) {
        iData.add(aV);
    }
    /*
    public void add(int anI, double aV) {
        iData.add(anI, aV);
    }
        
    
    public int getNumFrames() {
        return iData.size();
    }
    */
    /*
    public Series fit() {
        SplineInterpolator ipo = new SplineInterpolator();

        double[] x = new double[this.size()];
        double[] y = new double[this.size()];

        for (int n = 0; n < size(); ++n) {        
            x[n] = (double)n;
            y[n] = get(n);
        }
        
        PolynomialSplineFunction psf = ipo.interpolate(x, y);

        Series ret = new Series(iId);
        
        for (int n = 0; n < size(); ++n)
            ret.add(psf.value(Math.max(.0, n - 0.5)));
        
        return ret;
    }
    
    
    public Series fit2() {
        
        org.apache.commons.math3.linear.LinearProblem problem;// = new LeastSquaresProblem();

        double[] x = new double[this.size()];
        double[] y = new double[this.size()];

        for (int n = 0; n < size(); ++n) {        
            x[n] = (double)n;
            y[n] = get(n);
        }
        
        PolynomialSplineFunction psf = ipo.interpolate(x, y);

        Series ret = new Series(iId, iName);
        
        for (int n = 0; n < size(); ++n)
            ret.add(psf.value(Math.max(.0, n - 0.5)));
        
        return ret;
    }
    
    */
    
    
    
}
