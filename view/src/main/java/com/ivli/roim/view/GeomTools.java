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
public class GeomTools {
           
    //px and py are the coordinates of the start, first tangent, second tangent, end in that order. length = 4
    //lx and ly are the start then end coordinates of the stright line. length = 2
    public static Point2D cubicIntersect(double[] px, double[] py, double[] lx, double[] ly) {  
        final double[] X = new double[2];

        final double A = ly[1] - ly[0];      //A=y2-y1
        final double B = lx[0] - lx[1];      //B=x1-x2
        final double C = lx[0] * (ly[0] - ly[1]) + ly[0] * (lx[1] - lx[0]);  //C=x1*(y1-y2)+y1*(x2-x1)
         
         //Bezier coefficients X
        final double []bx = {-px[0] + 3 * px[1] -  3 * px[2] + px[3], 
                          3 * px[0] - 6 * px[1] +  3 * px[2], 
                         -3 * px[0] + 3 * px[1],
                              px[0]};
         //Bezier coefficients Y              
        final double []by = { -py[0] + 3 * py[1] -  3 * py[2] + py[3], 
                           3 * py[0] - 6 * py[1] +  3 * py[2], 
                          -3 * py[0] + 3 * py[1],
                               py[0]};
   
        final double[] r = cubicRoots(A * bx[0] + B * by[0],    /*t^3*/                                     
                                      A * bx[1] + B * by[1],     /*t^2*/
                                      A * bx[2] + B * by[2],     /*t*/
                                      A * bx[3] + B * by[3] + C); /*1*/
       
        ArrayList<Point2D> ret = new ArrayList<>();
        /*verify the roots are in bounds of the linear segment*/
        for (int i = 0; i < 3; i++) {
            final double t  = r[i];
            final double t2 = r[i]*r[i];
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
            } else
            
            ret.add(new Point2D.Double(X[0], X[1]));            
        }
        
        //ret.stream().forEach((a)->LOG.debug("root -(" + a.getX() + ", " + a.getY() + ");"));
        
        return ret.stream().filter(a -> a.getX() > .0 && a.getY() > .0).findFirst().orElse(null); 
    }

    static final double PI2 = Math.PI*2.;
    static final double PI4 = Math.PI*4.;    
    static final double SQRT3 = 1.7320508075688772935274463415059;
  
    static double[] cubicRoots(double a, double b, double c, double d) {
        final double A = b / a;
        final double B = c / a;
        final double C = d / a;

        final double Q = (3. * B - Math.pow(A, 2)) / 9.;
        final double R = (9. * A * B - 27. * C - 2. * Math.pow(A, 3)) / 54.;
        final double D = Math.pow(Q, 3) + Math.pow(R, 2); // polynomial discriminant
        final double Ato3 = A / 3.;
        
        
        double[] t = {.0, -1.0, -1.0};//new double[3];

        if (D >= .0) // complex or duplicate roots POI
        {
            final double S = Sign(R + Math.sqrt(D)) * Math.cbrt(Math.abs(R + Math.sqrt(D)));
            final double T = Sign(R - Math.sqrt(D)) * Math.cbrt(Math.abs(R - Math.sqrt(D)));

            t[0] = -Ato3 + (S + T); // real root
            
            ///t[2] = -Ato3 - (S + T) / 2; // real part of complex root
            //double Im = Math.abs(Math.sqrt(3) * (S - T) / 2); // complex part of root pair   

            //discard complex roots//
            if (S == T)//Math.abs(SQRT3 * (S - T) / 2) != .0) {
            //    t[1] = t[2] = -1;
            //else 
                t[1] = t[2] = -Ato3 - (S + T) / 2; // real part of complex roots

        } else { // distinct real roots          
            final double th = Math.acos(R / Math.sqrt(-Math.pow(Q, 3)));
            final double Qsq2 = 2 * Math.sqrt(-Q);
            
            t[0] = Qsq2 * Math.cos(th / 3.) - Ato3;
            t[1] = Qsq2 * Math.cos((th + PI2) / 3.) - Ato3;
            t[2] = Qsq2 * Math.cos((th + PI4) / 3.) - Ato3;            
        }

        /* discard out of spec roots */
        for (int i = 0; i < 3; i++)
            if (t[i] < 0 || t[i] > 1.0)
                t[i] = -1;

        return t;
    }

    static int Sign(double x) {
        if (x < .0)
            return -1;
        return 1;
    }
    /*
     * euclidean distance between two points on cartesian plane
    */
    public static double euclideanDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
    
    public static double euclideanDistance(Point2D beg, Point2D end){//double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(beg.getX() - end.getX(), 2) + Math.pow(beg.getY() - end.getY(), 2));
    }
    /*
     * angle between x-axis and a line connecting two points on cartesian plane
    */    
    public static double angle(double x1, double y1, double x2, double y2) {
        return Math.atan(-(y1 - y2)/(x2 - x1));
    }
    
    public static double angle(Point2D beg, Point2D end) {
        return Math.atan(-(beg.getY() - end.getY())/(end.getX() - beg.getX()));
    }
    /*
     * return point of intersection of two stright lines set by endpoints
    */
    public static Point2D lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
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
    
    /*
     * return point of intersection of two shapes
    */    
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
