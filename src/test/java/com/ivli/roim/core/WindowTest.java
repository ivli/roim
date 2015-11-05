/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class WindowTest {
    
    public WindowTest() {
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
     * Test of getLevel method, of class Window.
     */
    @Test
    public void testGetLevel() {
        System.out.println("getLevel");
        Window instance = new Window(0.5, 1.0);
        double expResult = 0.5;
        double result = instance.getLevel();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getWidth method, of class Window.
     */
    @Test
    public void testGetWidth() {
        System.out.println("getWidth");
        Window instance = new Window(0.5, 1.0);;
        double expResult = 1.0;
        double result = instance.getWidth();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        
    }

    /**
     * Test of setLevel method, of class Window.
     */
    @Test
    public void testSetLevel() {
        System.out.println("setLevel");
        double level = 100.0;
        double expResult = level;
        Window instance = new Window(0.5, 1.0);;
        instance.setLevel(level);
        
        assertEquals(expResult, instance.getLevel(), 0.0);
        // TODO review the generated test code and remove the default call to fail.
        
    }

    /**
     * Test of setWidth method, of class Window.
     */
    @Test
    public void testSetWidth() {
        System.out.println("setWidth");
        double aW = 100.0;
        double expResult = aW;
        Window instance =  new Window(0.5, 1.0);
        instance.setWidth(aW);
        assertEquals(expResult, instance.getWidth(), 0.0);
        //assertEquals(expResult/2.0, instance.getLevel(), 0.0);
    }

    /**
     * Test of getTop method, of class Window.
     */
    @Test
    public void testGetTop() {
        System.out.println("getTop");
        Window instance = new Window(70, 10);
        double expResult = 75.0;
        double result = instance.getTop();
        assertEquals(expResult, result, 0.0);
        
    }

    /**
     * Test of getBottom method, of class Window.
     */
    @Test
    public void testGetBottom() {
        System.out.println("getBottom");
        Window instance = new Window(70, 10);
        double expResult = 65.0;
        double result = instance.getBottom();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setTop method, of class Window.
     */
    @Test
    public void testSetTop() {
        System.out.println("setTop");
        double aT = 100.0;
        Window instance = new Window(70, 10);
        double expResult = aT;
        instance.setTop(aT);
        double result = instance.getTop();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setBottom method, of class Window.
     */
    @Test
    public void testSetBottom() {
        System.out.println("setBottom");
        double aB = 0.0;
        Window instance = new Window(70, 10);
        double expResult = aB;
        instance.setBottom(aB);
        double result = instance.getBottom();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setWindow method, of class Window.
     */
    @Test
    public void testSetWindow() {
        System.out.println("setWindow");
        double aW = 256;
        double aL = aW / 2.0;
        
        Window instance = new Window();
        instance.setWindow(aL, aW);
        // TODO review the generated test code and remove the default call to fail.
        assertEquals(aW, instance.getWidth(), 0.0);
        assertEquals(aL, instance.getLevel(), 0.0);
    }

    /**
     * Test of inside method, of class Window.
     */
    @Test
    public void testInside() {
        System.out.println("inside");
        double aV = 0.0;
        Window instance = new Window(2, 1);
        boolean expResult = false;
        boolean result = instance.inside(aV);
        assertEquals(expResult, result);
    }

    /**
     * Test of compare method, of class Window.
     */
    @Test
    public void testCompare() {
        System.out.println("compare");
        Window aW = new Window();
        Window instance = new Window();
        boolean expResult = true;
        boolean result = instance.compare(aW);
        assertEquals(expResult, result);
    }

    /**
     * Test of contains method, of class Window.
     */
    @Test
    public void testContains() {
        System.out.println("contains");
        Window aW = new Window();
        Window instance = new Window(2, 1);
        boolean expResult = false;
        boolean result = instance.contains(aW);
        assertEquals(expResult, result);
        
        instance.setWindow(1, 2);
        assertEquals(instance.contains(aW), true);
    }

    /**
     * Test of toString method, of class Window.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Window instance = new Window();
        String expResult = String.format("[%.1f, %.1f]", 0.5, 1.0);
        String result = instance.toString();
        assertEquals(expResult, result);
       
    }
    
}
