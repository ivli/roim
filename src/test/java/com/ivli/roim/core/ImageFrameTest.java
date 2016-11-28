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

import com.ivli.roim.algorithm.FrameProcessor;
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
public class ImageFrameTest {
    
    static final double THETA = .001;
    static final int[] SIZES_X = {0, 1, 2, 32, 1024, 2000,   20, 1024};
    static final int[] SIZES_Y = {0, 1, 2, 32, 1024, 2000, 1000,  512};
    
    ImageFrame f1;


    public ImageFrameTest() {

    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {        
       f1 = new ImageFrame(TestImage32x32.ROWS, TestImage32x32.COLS, TestImage32x32.BUFFER);
    }
    
    @After
    public void tearDown() {
        f1 = null;
    }

    /**
     * Test of duplicate method, of class ImageFrame.
     */
    @Test
    public void testDuplicate() {
        System.out.println("duplicate");
        
        for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];

            int []test = new int[size_x*size_y];
            int []t2 = new int[size_x*size_y];

            for (int i=0; i<test.length; ++i)
                test[i] = (int)(Math.random()*100);

            System.arraycopy(test, 0, t2, 0, size_x*size_y);

            ImageFrame instance = new ImageFrame(size_x, size_y, t2);
            ImageFrame result = instance.duplicate();

            assertArrayEquals(test, result.getPixelData());
            assertEquals(instance.getWidth(), result.getWidth());
            assertEquals(instance.getHeight(), result.getHeight());
            //assertEquals(instance.getMin(), result.getMin(), THETA);
            //assertEquals(instance.getMax(), result.getMax(), THETA);
           // assertEquals(instance.getRange(), result.getRange(), THETA);
           // assertEquals(instance.getIden(), result.getIden(), THETA);
        }
    }

    /**
     * Test of getWidth method, of class ImageFrame.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        ImageFrame instance = new ImageFrame(42,42);
        int expResult = 42;
        int result = instance.getWidth();
        assertEquals(expResult, result);        
    }

    /**
     * Test of getHeight method, of class ImageFrame.
     */
    @Test
    public void testGetHeight() {
        System.out.println("getHeight");
        ImageFrame instance = new ImageFrame(42,42);
        int expResult = 42;
        int result = instance.getHeight();
        assertEquals(expResult, result);      
    }
   
    /**
     * Test of get method, of class ImageFrame.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        
        for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
        int []test = new int[size_x*size_y];
        int []t2 = new int[size_x*size_y];
        
        for (int i=0;i<test.length; ++i)
            test[i] = (int)(Math.random()*100);
        
        System.arraycopy(test, 0, t2, 0, size_x*size_y);
       
        ImageFrame instance = new ImageFrame(size_x, size_y, t2);
               
        boolean result = true;
        
        for (int i=0; i<size_x; ++i)
            for (int j=0; j<size_y; ++j)
               result = result && test[j*size_x + i] == instance.get(i, j) ;
        
        assertEquals(true, result);
        }
    }

    /**
     * Test of set method, of class ImageFrame.
     */
    @Test
    public void testSet() {        
        System.out.println("set");
       for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
        int []test = new int[size_x*size_y];
        int []t2 = new int[size_x*size_y];
        for (int i=0;i<test.length; ++i)
            test[i] = (int)(Math.random()*100);
        
        ///System.arraycopy(test, 0, t2, 0, size_x*size_y);
       
        ImageFrame instance = new ImageFrame(size_x, size_y, t2);
        
        for (int i=0; i<size_x; ++i)
            for (int j=0; j<size_y; ++j)
                instance.set(i, j, test[j*size_x + i]); 
   
        assertArrayEquals(test, t2);
       }
    }

    /**
     * Test of isValidIndex method, of class ImageFrame.
     */
    
    @Test
    public void testIsValidIndex() {
        System.out.println("isValidIndex");
        boolean result = true;
        for (int k=0; k < SIZES_X.length; ++k) {
            ImageFrame instance = new ImageFrame(SIZES_X[k], SIZES_Y[k]);
            for (int i=0; i < SIZES_X[k]; ++i) {
                for (int j=0; j < SIZES_Y[k]; ++j) {
                    result = result && instance.isValidIndex(i, j); //all must pass                    
                }
            }
            //all must fail
            result = result && !instance.isValidIndex(-1, 0);
            result = result && !instance.isValidIndex(-1, -1);
            result = result && !instance.isValidIndex(SIZES_X[k], SIZES_Y[k]);
            result = result && !instance.isValidIndex(SIZES_X[k]+1, SIZES_Y[k]);
            result = result && !instance.isValidIndex(SIZES_X[k], SIZES_Y[k]+1);
            result = result && !instance.isValidIndex(SIZES_X[k]+1, SIZES_Y[k]+1);
        }    
       
        assertEquals(true, result);
       
    }

    /**
     * Test of getPixel method, of class ImageFrame.
     */
    @Test
    public void testGetPixel() {
        System.out.println("getPixel");
        for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
            int []test = new int[size_x*size_y];
            int []t2 = new int[size_x*size_y];

            for (int i=0;i<test.length; ++i)
                test[i] = (int)(Math.random()*100);

            System.arraycopy(test, 0, t2, 0, size_x*size_y);

            ImageFrame instance = new ImageFrame(size_x, size_y, t2);

            boolean result = true;

            for (int i=0; i<size_x; ++i)
                for (int j=0; j<size_y; ++j)
                   result = result && test[j*size_x + i] == instance.getPixel(i, j) ;

            assertEquals(true, result);
        }
    }

    /**
     * Test of setPixel method, of class ImageFrame.
     */
    @Test
    public void testSetPixel() {
        System.out.println("setPixel");
        for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
        int []test = new int[size_x*size_y];
        int []t2   = new int[size_x*size_y];
       
        for (int i=0;i<test.length; ++i)
            test[i] = (int)(Math.random()*100);
        
        ///System.arraycopy(test, 0, t2, 0, size_x*size_y);
       
        ImageFrame instance = new ImageFrame(size_x, size_y, t2);
        
        for (int i=0; i<size_x; ++i)
            for (int j=0; j<size_y; ++j)
                instance.setPixel(i, j, test[j*size_x + i]); 
                       
        assertArrayEquals(test, t2);
       }
    }

    /**
     * Test of getImageDataType method, of class ImageFrame.
     */
    @Test
    public void testGetImageDataType() {
        System.out.println("getImageDataType");
        ImageFrame instance = new ImageFrame(1024, 1024);
        ImageDataType expResult = ImageDataType.GRAYS32;
        ImageDataType result = instance.getImageDataType();
        assertEquals(expResult, result);        
    }

    /**
     * Test of getPixelData method, of class ImageFrame.
     */
    @Test
    public void testGetSamples() {
        System.out.println("getSamples");
        
      for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
            int []test = new int[size_x*size_y];


            for (int i=0;i<test.length; ++i)
                test[i] = (int)(Math.random()*100);

            ///System.arraycopy(test, 0, t2, 0, size_x*size_y);

            ImageFrame instance = new ImageFrame(size_x, size_y);

            for (int i=0; i<size_x; ++i)
                for (int j=0; j<size_y; ++j)
                    instance.setPixel(i, j, test[j*size_x + i]); 

            int[] t2 = instance.getPixelData();               

            assertArrayEquals(test, t2);
        }
    }

    /**
     * Test of setPixelData method, of class ImageFrame.
     */
    @Test
    public void testSetPixelData() {
        System.out.println("setPixelData");
        
        for (int k = 0; k < SIZES_X.length; ++k) {
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
            int []test = new int[size_x*size_y];

            for (int i=0;i<test.length; ++i)
                test[i] = (int)(Math.random()*100);

            ImageFrame instance = new ImageFrame(size_x, size_y);

            instance.setPixelData(size_x, size_y, test);        

            int[] t2 = instance.getPixelData();          

            assertArrayEquals(test, t2);
        }
    }

    /**
     * Test of getRange method, of class ImageFrame.
    */ 
    @Test
    public void testGetRange() {
        System.out.println("getRange");
        ImageFrame instance = new ImageFrame(TestImage32x32.ROWS, TestImage32x32.COLS, TestImage32x32.BUFFER);
        Measure m = instance.processor().measure(null);
        assertEquals(TestImage32x32.MIN, m.getMin(), .0);  
        assertEquals(TestImage32x32.MAX, m.getMax(), .0); 
    }

    /**
     * Test of getIden method, of class ImageFrame.
     */
    @Test
    public void testGetIden() {
        System.out.println("getIden");
        ImageFrame instance = f1;
        double expResult = TestImage32x32.TOTAL;
        double result = instance.processor().density(null);
        assertEquals(expResult, result, 0.0);       
    }

    /**
     * Test of getPixelData method, of class ImageFrame.
     */
    @Test
    public void testGetPixelData() {
        System.out.println("getPixelData");
        ImageFrame instance = f1;
        int[] expResult = TestImage32x32.BUFFER;
        int[] result = instance.getPixelData();
        assertArrayEquals(expResult, result);        
    }

    /**
     * Test of extract method, of class ImageFrame.
     */
    @Test
    public void testExtract() {
        System.out.println("extract");
       
    }

    /**
     * Test of processor method, of class ImageFrame.
     */
    @Test
    public void testProcessor() {
        System.out.println("processor");
        ImageFrame instance = f1;
        ///FrameProcessor expResult = null;
        FrameProcessor result = instance.processor();
        assertNotNull(result);
       
    }
  
}
