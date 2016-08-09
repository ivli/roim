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

import java.awt.Point;
import java.util.Set;
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
    Histogram h1;
    int v = 0;
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
        v = 0;
        h1 = new Histogram();
        for (int i=0; i < TestImage32x32.SUM_ROWS.length; ++i)
            h1.put(i, TestImage32x32.SUM_ROWS[i]);     
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
        
        Histogram instance = h1;
        
        Set<Integer> keys = instance.keySet();
        
        assertEquals(TestImage32x32.SUM_ROWS.length, keys.size());
                        
        for (int key:keys) {            
            assertEquals((int)TestImage32x32.SUM_ROWS[key], (int)instance.get(key));
        }       
    }

    /**
     * Test of put method, of class Histogram.
     */
    @Test
    public void testPut() {
        System.out.println("put");
       
        Histogram instance = new Histogram();
        
        for (int i = 0; i < TestImage32x32.SUM_ROWS.length; ++i)
            instance.put(i, TestImage32x32.SUM_ROWS[i]);
        
        assertEquals(0, instance.iMin); 
        assertEquals(TestImage32x32.SUM_ROWS.length-1, instance.iMax);
               
    }

    /**
     * Test of rebin method, of class Histogram.
     */
    
    @Test
    public void testRebin() {
        System.out.println("rebin");
        int aNoOfBins = 16;
        Histogram instance = h1;       
        Histogram result = instance.rebin(16);
        v = 0;
        instance.keySet().forEach((k)->{v += instance.get(k);});
        
        assertEquals(TestImage32x32.TOTAL, v);        
    }  
}
