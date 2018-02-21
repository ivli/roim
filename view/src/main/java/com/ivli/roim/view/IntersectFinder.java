/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.view;

/*
 *   Bezier and stright line intersection finding algorithm is based on following works 
 *   http://mathhelpforum.com/calculus/276396-solving-intersections-between-bezier-curve-straight-line.html
 *   https://www.particleincell.com/2013/cubic-line-intersection/
 */

import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author likhachev
 */
public class IntersectFinder {
    
    static Point2D cubicIntersect(Point2D P0, Point2D P1, Point2D P2, Point2D P3, Line2D aL) {
        double []px = {P0.getX(),P1.getX(),P2.getX(),P3.getX()};
        double []py = {P0.getY(), P1.getY(),P2.getY(),P3.getY()};
        double []lx = {aL.getX1(), aL.getX2()};
        double []ly = {aL.getY1(), aL.getY2()};
        return cubicIntersect(px, py, lx, ly);
    }
    
    static double[] bezierCoeffs(double P0, double P1, double P2, double P3) {
        double[] ret = {-P0 + 3 * P1 + -3 * P2 + P3, 
                        3 * P0 - 6 * P1 + 3 * P2, 
                        -3 * P0 + 3 * P1,
                        P0};             
        return ret;
    }
    //px and py are the coordinates of the start, first tangent, second tangent, end in that order. length = 4
    //lx and ly are the start then end coordinates of the stright line. length = 2
    static Point2D cubicIntersect(double[] px, double[] py, double[] lx, double[] ly) {  
        double[] X = new double[2];

        double A = ly[1] - ly[0];      //A=y2-y1
        double B = lx[0] - lx[1];      //B=x1-x2
        double C = lx[0] * (ly[0] - ly[1]) +
              ly[0] * (lx[1] - lx[0]);  //C=x1*(y1-y2)+y1*(x2-x1)

        double []bx = {     -px[0] + 3 * px[1] + -3 * px[2] + px[3], 
                         3 * px[0] - 6 * px[1] +  3 * px[2], 
                        -3 * px[0] + 3 * px[1],
                             px[0]};
                      
        double []by = {     -py[0] + 3 * py[1] + -3 * py[2] + py[3], 
                         3 * py[0] - 6 * py[1] +  3 * py[2], 
                        -3 * py[0] + 3 * py[1],
                             py[0]};
   
        double[] r = cubicRoots(
                                A * bx[0] + B * by[0],      /*t^3*/
                                A * bx[1] + B * by[1],      /*t^2*/
                                A * bx[2] + B * by[2],      /*t*/
                                A * bx[3] + B * by[3] + C); /*1*/
       
        ArrayList<Point2D> ret = new ArrayList<>();
        /*verify the roots are in bounds of the linear segment*/
        for (int i = 0; i < 3; i++) {
            final double t = r[i];
            final double t2 = t*t;
            final double t3 = t2*t;

            X[0] = bx[0] * t3 + bx[1] * t2 + bx[2] * t + bx[3];
            X[1] = by[0] * t3 + by[1] * t2 + by[2] * t + by[3];

            /*above is intersection point assuming infinitely long line segment,
              make sure we are also in bounds of the line*/
            double s;
            if (lx[1] != lx[0])  /*if not vertical line*/
                s = (X[0] - lx[0]) / (lx[1] - lx[0]);
            else
                s = (X[1] - ly[0]) / (ly[1] - ly[0]);

            /*in bounds?*/
            if (t < .0 || t > 1.0 || s < .0 || s > 1.0) {
                X[0] = -100;  /*move off screen*/
                X[1] = -100;
            }
            
            ret.add(new Point2D.Double(X[0], X[1]));            
        }
        
        LOG.debug("found roots: " + ret.size());
        ret.stream().forEach((a)->LOG.debug("root -(" + a.getX() + ", " + a.getY() + ");"));
        
        return ret.stream().filter((a) -> a.getX() > .0 && a.getY() > .0).findFirst().orElse(null); 
    }

    static final double PI2 = Math.PI*2.;
    static final double PI4 = Math.PI*4.;
    static final double SQRT3 = Math.sqrt(3);
  
    static double[] cubicRoots(double a, double b, double c, double d) {
        double A = b / a;
        double B = c / a;
        double C = d / a;

        double Q = (3. * B - Math.pow(A, 2)) / 9.;
        double R = (9. * A * B - 27. * C - 2. * Math.pow(A, 3)) / 54.;
        double D = Math.pow(Q, 3) + Math.pow(R, 2); // polynomial discriminant

        double[] t = {.0, .0, .0};//new double[3];

        if (D >= 0)                                 // complex or duplicate roots POI
        {
            double S = Sign(R + Math.sqrt(D)) * Math.cbrt(Math.abs(R + Math.sqrt(D)));
            double T = Sign(R - Math.sqrt(D)) * Math.cbrt(Math.abs(R - Math.sqrt(D)));

            t[0] = -A / 3 + (S + T);                    // real root
            t[1] = -A / 3 - (S + T) / 2;                // real part of complex root
            t[2] = -A / 3 - (S + T) / 2;                // real part of complex root
            //double Im = Math.abs(Math.sqrt(3) * (S - T) / 2); // complex part of root pair   

            //discard complex roots//
            if (Math.abs(Math.sqrt(3) * (S - T) / 2) != .0) {
                t[1] = -1;
                t[2] = -1;
            } 

        } else { // distinct real roots          
            double th = Math.acos(R / Math.sqrt(-Math.pow(Q, 3)));

            t[0] = 2 * Math.sqrt(-Q) * Math.cos(th / 3) - A / 3;
            t[1] = 2 * Math.sqrt(-Q) * Math.cos((th + PI2) / 3) - A / 3;
            t[2] = 2 * Math.sqrt(-Q) * Math.cos((th + PI4) / 3) - A / 3;            
        }

        /* discard out of spec roots */
        for (int i = 0; i < 3; i++)
            if (t[i] < 0 || t[i] > 1.0)
                t[i] = -1;

        return t;
    }

    static int Sign(double x) {
        if (x < 0.0)
            return -1;
        return 1;
    }
 
    public static double euclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
    
    static Point2D lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0.0) { // Lines are parallel.
           return null;
        }
        
        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3))/denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3))/denom;
        
        if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
            // Get the intersection point.
            return new Point2D.Double( (x1 + ua*(x2 - x1)), (y1 + ua*(y2 - y1)));
        }

        return null;
    }
        
    static Point2D intersect(Shape aS, Line2D aL) {
        PathIterator pi = aS.getPathIterator(null);
        double x=.0, y=.0;

        while (!pi.isDone()) {            
            double [] vals = new double[6]; 
            
            switch (pi.currentSegment(vals)) {
                case PathIterator.SEG_MOVETO: {
                    x = vals[0];
                    y = vals[1];
                } break;         
                case PathIterator.SEG_LINETO: {                                       
                    Point2D tmp = lineIntersect(x, y, vals[0], vals[1], aL.getX1(), aL.getY1(), aL.getX2(), aL.getY2());

                    if (null != tmp) {                        
                        return tmp;                                         
                    }    
                    x = vals[0]; y = vals[1];
                } break; 
                case PathIterator.SEG_QUADTO: {                    
                    Point2D tmp = lineIntersect(x, y, vals[0], vals[1], aL.getX1(), aL.getY1(), aL.getX2(), aL.getY2());
                    if (null != tmp) {                        
                        return tmp;                                         
                    }    
                    x = vals[2]; y = vals[3];
                } break; 
                case PathIterator.SEG_CUBICTO:  {
                    double []px = {x,vals[0],vals[2],vals[4],};
                    double []py = {y,vals[1],vals[3],vals[5]};
                    double []lx = {aL.getX1(), aL.getX2()};
                    double []ly = {aL.getY1(), aL.getY2()};
                   
                    Point2D tmp = cubicIntersect(px, py, lx, ly);
                        
                    if (null != tmp) {                        
                        return tmp;                                         
                    }    
                  
                    x = vals[4]; y = vals[5];
                } break;
               
                case PathIterator.SEG_CLOSE:  break;
                default: 
                    throw new IllegalArgumentException("Illeagl segment type");
                    //break;
            } 
            
            pi.next(); 
        }
        return null;
    }
    
    
    
    static org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}
