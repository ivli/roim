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
    
    static Point2D CubicIntersect(Point2D P0, Point2D P1, Point2D P2, Point2D P3, Line2D aL)  throws java.util.NoSuchElementException {
        double []px = {P0.getX(),P1.getX(),P2.getX(),P3.getX()};
        double []py = {P0.getY(), P1.getY(),P2.getY(),P3.getY()};
        double []lx = {aL.getX1(), aL.getX2()};
        double []ly = {aL.getY1(), aL.getY2()};
        return CubicIntersect(px, py, lx, ly);
    }
    
    //px and py are the coordinates of the start, first tangent, second tangent, end in that order. length = 4
    //lx and ly are the start then end coordinates of the stright line. length = 2
    static Point2D CubicIntersect(double[] px, double[] py, double[] lx, double[] ly) throws java.util.NoSuchElementException {  
        double[] X = new double[2];

        double A = ly[1] - ly[0];      //A=y2-y1
        double B = lx[0] - lx[1];      //B=x1-x2
        double C = lx[0] * (ly[0] - ly[1]) +
              ly[0] * (lx[1] - lx[0]);  //C=x1*(y1-y2)+y1*(x2-x1)

        double []bx = BezierCoeffs(px[0], px[1], px[2], px[3]);
        double []by = BezierCoeffs(py[0], py[1], py[2], py[3]);

        double[] P = new double[4];
        P[0] = A * bx[0] + B * by[0];       /*t^3*/
        P[1] = A * bx[1] + B * by[1];       /*t^2*/
        P[2] = A * bx[2] + B * by[2];       /*t*/
        P[3] = A * bx[3] + B * by[3] + C;   /*1*/

        double []r = CubicRoots(P);

        ArrayList<Point2D> ret = new ArrayList();
        /*verify the roots are in bounds of the linear segment*/
        for (int i = 0; i < 3; i++) {
            double t = r[i];

            X[0] = bx[0] * t * t * t + bx[1] * t * t + bx[2] * t + bx[3];
            X[1] = by[0] * t * t * t + by[1] * t * t + by[2] * t + by[3];

            /*above is intersection point assuming infinitely long line segment,
              make sure we are also in bounds of the line*/
            double s;
            if ((lx[1] - lx[0]) != 0)           /*if not vertical line*/
                s = (X[0] - lx[0]) / (lx[1] - lx[0]);
            else
                s = (X[1] - ly[0]) / (ly[1] - ly[0]);

            /*in bounds?*/
            if (t < 0 || t > 1.0 || s < 0 || s > 1.0) {
                X[0] = -100;  /*move off screen*/
                X[1] = -100;
            }

            /*move intersection point*/
            //I[i] = new Vector2(X[0], X[1]);
            ret.add(new Point2D.Double(X[0], X[1]));
            
        }
        
        LOG.debug("found roots: " + ret.size());
        ret.stream().forEach((a)->LOG.debug("root -(" + a.getX() + ", " + a.getY() + ");"));
        
        return ret.stream().filter((a) -> a.getX() > .0 && a.getY() > .0).findFirst().get(); 
    }

    static double[] CubicRoots(double[] P) {
        double a = P[0];
        double b = P[1];
        double c = P[2];
        double d = P[3];

        double A = b / a;
        double B = c / a;
        double C = d / a;

        double Im;

        double Q = (3f * B - Math.pow(A, 2)) / 9f;
        double R = (9f * A * B - 27f * C - 2 * Math.pow(A, 3)) / 54f;
        double D = Math.pow(Q, 3) + Math.pow(R, 2);    // polynomial discriminant

        double[] t = new double[3];

        if (D >= 0)                                 // complex or duplicate roots POI
        {
            double S = Sign(R + Math.sqrt(D)) * Math.pow(Math.abs(R + Math.sqrt(D)), (1 / 3));
            double T = Sign(R - Math.sqrt(D)) * Math.pow(Math.abs(R - Math.sqrt(D)), (1 / 3));

            t[0] = -A / 3 + (S + T);                    // real root
            t[1] = -A / 3 - (S + T) / 2;                  // real part of complex root
            t[2] = -A / 3 - (S + T) / 2;                  // real part of complex root
            Im = Math.abs(Math.sqrt(3) * (S - T) / 2);    // complex part of root pair   

            //discard complex roots//
            if (Im != 0) {
                t[1] = -1;
                t[2] = -1;
            }

        } else                                          // distinct real roots
          {
            double th = Math.acos(R / Math.sqrt(-Math.pow(Q, 3)));

            t[0] = 2 * Math.sqrt(-Q) * Math.cos(th / 3) - A / 3;
            t[1] = 2 * Math.sqrt(-Q) * Math.cos((th + 2 * Math.PI) / 3) - A / 3;
            t[2] = 2 * Math.sqrt(-Q) * Math.cos((th + 4 * Math.PI) / 3) - A / 3;
            Im = .0;
        }

        /*discard out of spec roots*/
        for (int i = 0; i < 3; i++)
            if (t[i] < 0 || t[i] > 1.0)
                t[i] = -1;

        //Debug.Log(t[0] + " " + t[1] + " " + t[2]);
        return t;
    }

    static int Sign(double x) {
        if (x < 0.0)
            return -1;
        return 1;
    }

    static double[] BezierCoeffs(double P0, double P1, double P2, double P3) {
        double[] Z = new double[4];
        Z[0] = -P0 + 3 * P1 + -3 * P2 + P3;
        Z[1] = 3 * P0 - 6 * P1 + 3 * P2;
        Z[2] = -3 * P0 + 3 * P1;
        Z[3] = P0;
        return Z;
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
                        LOG.debug("an intersection found {}", tmp);
                        return tmp;                                         
                    }    
                    x = vals[0]; y = vals[1];
                } break; 
                case PathIterator.SEG_QUADTO: {                    
                    Point2D tmp = lineIntersect(x, y, vals[0], vals[1], aL.getX1(), aL.getY1(), aL.getX2(), aL.getY2());
                    if (null != tmp) {
                        LOG.debug("an intersection found {}", tmp);
                        return tmp;                                         
                    }    
                    x = vals[2]; y = vals[3];
                } break; 
                case PathIterator.SEG_CUBICTO:  {
                    double []px = {x,vals[0],vals[2],vals[4],};
                    double []py = {y,vals[1],vals[3],vals[5]};
                    double []lx = {aL.getX1(), aL.getX2()};
                    double []ly = {aL.getY1(), aL.getY2()};
                    try {
                        return CubicIntersect(px, py, lx, ly);
                        
                    } catch (java.util.NoSuchElementException ex) {
                    
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
