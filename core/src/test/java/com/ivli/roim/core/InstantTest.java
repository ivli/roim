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
public class InstantTest {
    
    public InstantTest() {
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
     * Test of toLong method, of class Instant.
     */
    @Test
    public void testToLong() {
        System.out.println("toLong");
        Instant instance = new Instant(0);
        long expResult = 0L;
        long result = instance.toLong();
        assertEquals(expResult, result);
    }

    /**
     * Test of format method, of class Instant.
     */
    @Test
    public void testFormat() {
    
    }

    /**
     * Test of compareTo method, of class Instant.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Instant o = new Instant(0);
        Instant instance = new Instant(0);
        int expResult = 0;
        int result = instance.compareTo(o);
        assertEquals(expResult, result);       
    }

    /**
     * Test of isInfinite method, of class Instant.
     */
    @Test
    public void testIsInfinite() {
        System.out.println("isInfinite");
        Instant instance = Instant.INFINITE;
       
        assertEquals(true, instance.isInfinite());
        
        assertEquals(false, new Instant(0L).isInfinite());
     
    }    
}
