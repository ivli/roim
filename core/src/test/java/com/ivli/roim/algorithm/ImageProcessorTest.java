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

public class ImageProcessorTest {
    static final ImageFrame FRAME = new ImageFrame(TestImage32x32.ROWS, TestImage32x32.COLS, TestImage32x32.BUFFER);
    /////static final ImageFrame FRAME2 = new ImageFrame(TestImage32x32.ROWS, TestImage32x32.COLS, TestImage32x32.BUFFER);
    ImageFrame f1;
        
    public ImageProcessorTest() {               
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
    public void testCompare() {
        System.out.println("testCompare"); 

    }

}
