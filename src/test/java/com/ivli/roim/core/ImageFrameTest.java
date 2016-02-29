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

import java.awt.image.BufferedImage;
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
        
        
    }
    
    @After
    public void tearDown() {
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

            for (int i=0;i<test.length; ++i)
                test[i] = (int)(Math.random()*100);

            System.arraycopy(test, 0, t2, 0, size_x*size_y);

            ImageFrame instance = new ImageFrame(size_x, size_y, t2);
            ImageFrame result = instance.duplicate();

            assertArrayEquals(test, result.getSamples());
            assertEquals(instance.getWidth(), result.getWidth());
            assertEquals(instance.getHeight(), result.getHeight());
            assertEquals(instance.getMin(), result.getMin(), THETA);
            assertEquals(instance.getMax(), result.getMax(), THETA);
            assertEquals(instance.getIden(), result.getIden(), THETA);
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
     * Test of getIden method, of class ImageFrame.
     */
    @Test
    public void testMinMaxGetIden() {
        System.out.println("testGetMinMaxIden");
        for (int k = 0; k < SIZES_X.length; ++k) {
            System.out.printf("X=%d, Y=%d\n", SIZES_X[k], SIZES_Y[k]);
            int size_x = SIZES_X[k];
            int size_y = SIZES_Y[k];
        
            int []test = new int[size_x*size_y];

            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            double sum = .0;

            for (int i=0; i<test.length; ++i) {           
                final int val = (int)(Math.random()*100);
                test[i] = val;
                
                sum += val;
                if (val < min)
                    min = val;
                if (val > max)
                    max = val;
            }

            ImageFrame instance = new ImageFrame(size_x, size_y, test);
            
            assertEquals(min, instance.getMin(),  THETA);
            assertEquals(max, instance.getMax(),  THETA);
            assertEquals(sum, instance.getIden(), THETA);
      }
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
     * Test of getBufferedImage method, of class ImageFrame.
     */
    @Test
    public void testGetBufferedImage() {
        System.out.println("getBufferedImage");
        ImageFrame instance = new ImageFrame(1024, 1024);
        
        BufferedImage result = instance.getBufferedImage();
        assertNotNull(result);
       
    }

    /**
     * Test of getSamples method, of class ImageFrame.
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

            int[] t2 = instance.getSamples();               

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

            int[] t2 = instance.getSamples();          

            assertArrayEquals(test, t2);
        }
    }
  
}
