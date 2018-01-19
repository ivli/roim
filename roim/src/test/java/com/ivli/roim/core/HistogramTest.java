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
package com.ivli.roim.core;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author likhachev
 */
public class HistogramTest {    
    Histogram histogram = new Histogram(TestImage32x32.SUM_ROWS);
    
    
    public HistogramTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
       
    }
    
    @After
    public void tearDown() {
    }

    
    /**
     * Test of get method, of class Histogram.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        
        Histogram instance = histogram;
        
        for (int key = 0; key < TestImage32x32.SUM_ROWS.length; ++key ) {            
            assertEquals((int)TestImage32x32.SUM_ROWS[key], (int)instance.get(key));
        }       
    }

    /**
     * Test of put method, of class Histogram.
     */
    @Test
    public void testPut() {
        System.out.println("put");
       
        Histogram instance = new Histogram(TestImage32x32.SUM_ROWS.length);
        
        for (int i = 0; i < TestImage32x32.SUM_ROWS.length; ++i)
            instance.put(i, TestImage32x32.SUM_ROWS[i]);
        
        
        for (int i = 0; i < TestImage32x32.SUM_ROWS.length; ++i)
            assertEquals((int)instance.get(i), TestImage32x32.SUM_ROWS[i]);
   
    }

    /**
     * Test of rebin method, of class Histogram.
     */
    
    @Test
    public void testRebin() {
        System.out.println("rebin");
        /*
        int aNoOfBins = 16;
        Histogram instance = h1;       
        Histogram result = instance.rebin(16);
        v = 0;
        instance.keySet().forEach((k)->{v += instance.get(k);});
        
        assertEquals(TestImage32x32.TOTAL, v); 
        */
    }  

    /**
     * Test of getNoOfBins method, of class Histogram.
     */
    @Test
    public void testGetNoOfBins() {
        System.out.println("getNoOfBins");
        Histogram instance = new Histogram(TestImage32x32.SUM_ROWS);
        int expResult = TestImage32x32.SUM_ROWS.length;
        int result = instance.getNoOfBins();
        assertEquals(expResult, result);
      
    }

    /**
     * Test of getBinSize method, of class Histogram.
     */
    @Test
    public void testGetBinSize() {
        System.out.println("getBinSize");
        Histogram instance = histogram;
        double expResult = 1.;
        double result = instance.getBinSize();
        assertEquals(expResult, result, 0.0);        
    }

    /**
     * Test of inc method, of class Histogram.
     */
    @Test
    public void testInc() {
        System.out.println("inc");
        
        final int bin = 10;
        final int iterations = 11;
        
        Histogram instance = new Histogram(TestImage32x32.SUM_ROWS);
        Integer expResult = TestImage32x32.SUM_ROWS[bin] + iterations;
        
        for (int i = 0; i < iterations; ++i)            
            instance.inc(bin);
        
        assertEquals(expResult, instance.get(bin));
        
    }

    /**
     * Test of min method, of class Histogram.
     */
    @Test
    public void testMin() {
        System.out.println("min");
        Histogram instance = new Histogram(TestImage32x32.SUM_ROWS);
        double expResult = TestImage32x32.SUM_ROWS_MIN;
        double result = instance.min();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of max method, of class Histogram.
     */
    @Test
    public void testMax() {
        System.out.println("max");
        Histogram instance = new Histogram(TestImage32x32.SUM_ROWS);
        double expResult = TestImage32x32.SUM_ROWS_MAX;
        double result = instance.max();
        assertEquals(expResult, result, 0.0);       
    }
}
