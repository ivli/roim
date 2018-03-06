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
package com.ivli.roim.algorithm;

import com.ivli.roim.core.Histogram;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.TestImage32x32;
import java.awt.Rectangle;
import java.awt.Shape;
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
public class FrameProcessorTest {
    static final ImageFrame FRAME   = new ImageFrame(TestImage32x32.ROWS, TestImage32x32.COLS, TestImage32x32.BUFFER);
    static final Histogram  PROFILE = new Histogram(TestImage32x32.SUM_COLS);
    
    ImageFrame f1;
        
    public FrameProcessorTest() {         
       // for (int i=0; i < TestImage32x32.SUM_ROWS.length; ++i)
       //     PROFILE.put(i, TestImage32x32.SUM_ROWS[i]);        
    }
    
    @BeforeClass
    public static void setUpClass() {            
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        f1 = FRAME.duplicate(); 
    }
    
    @After
    public void tearDown() {
        f1 = null;
    }

    /**
     * Test of setInterpolation method, of class FrameProcessor.
     */
    @Test
    public void testSetInterpolation() {
        System.out.println("setInterpolation");        
    }

    /**
     * Test of getPixelsCopy method, of class FrameProcessor.
     */
    @Test
    public void testGetPixelsCopy() {
        System.out.println("getPixelsCopy");
        FrameProcessor instance = f1.processor();
        int[] expResult = f1.getPixelData();
        int[] result = instance.getPixelsCopy();
        assertArrayEquals(expResult, result);       
    }

    /**
     * Test of add method, of class FrameProcessor.
     */
    @Test
    public void testAdd() {
        System.out.println("add");        
        
        ImageFrame aF = f1;
        FrameProcessor instance = f1.processor();
        instance.add(aF);
        
        for (int i = 0; i < f1.getWidth(); ++i)
            for(int j = 0; j < f1.getHeight(); ++j)
                assertEquals ((int)f1.get(i, j), (int)(FRAME.get(i, j) * 2));
    
    }

    /**
     * Test of flipVert method, of class FrameProcessor.
     */
    @Test
    public void testFlipVert() {
        System.out.println("flipVert");
        FrameProcessor instance = f1.processor();
        instance.flipVert();        
    }

    /**
     * Test of flipHorz method, of class FrameProcessor.
     */
    @Test
    public void testFlipHorz() {
        System.out.println("flipHorz");
        FrameProcessor instance = f1.processor();
        instance.flipHorz();        
    }

    /**
     * Test of rotate method, of class FrameProcessor.
     */
    @Test
    public void testRotate() {
        System.out.println("rotate");
        double anAngle = 0.0;
        FrameProcessor instance = f1.processor();
        instance.rotate(anAngle);
        // TODO review the generated test code and remove the default call to fail.       
    }

    /**
     * Test of histogram method, of class FrameProcessor.
     */
    @Test
    public void testHistogram() {
        System.out.println("histogram");
        Histogram expResult = PROFILE;
        Histogram result = f1.processor().profile(new Rectangle(0, 0, TestImage32x32.ROWS, TestImage32x32.COLS));
        
        assertEquals(expResult.getNoOfBins(), result.getNoOfBins());
        assertEquals(expResult.getBinSize(), result.getBinSize(), 1.);
        
        for (int i = 0; i < result.getNoOfBins(); ++i)
            assertEquals(expResult.get(i), result.get(i));       
    }

    /**
     * Test of map method, of class FrameProcessor.
     */
    @Test
    public void testMap() {
        System.out.println("map");
        double aKey = 0.0;
        FrameProcessor instance = f1.processor();
        instance.map(aKey);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of density method, of class FrameProcessor.
     */
    @Test
    public void testDensity() {
        System.out.println("density");
        Shape aR = null;
        FrameProcessor instance = f1.processor();
       
        assertEquals(TestImage32x32.TOTAL, instance.density(null));
        assertEquals(TestImage32x32.NORD_HALF_TOTAL,  instance.density(TestImage32x32.NORD_HALF));
        assertEquals(TestImage32x32.SOUTH_HALF_TOTAL, instance.density(TestImage32x32.SOUTH_HALF));
        assertEquals(TestImage32x32.ATLANTIC_TOTAL,   instance.density(TestImage32x32.ATLANTIC));
        assertEquals(TestImage32x32.NORD_WEST_ONE_PIXEL_SOUTH_TOTAL, instance.density(TestImage32x32.NORD_WEST_ONE_PIXEL_SOUTH));
        assertEquals(TestImage32x32.NORD_WEST_ONE_PIXEL_SOUTH_EAST_TOTAL, instance.density(TestImage32x32.NORD_WEST_ONE_PIXEL_SOUTH_EAST));
        
        //assertEquals(TestImage32x32.TOTAL, instance.density(null));
        //assertEquals(TestImage32x32.TOTAL, instance.density(null));
        
    }
    
}
