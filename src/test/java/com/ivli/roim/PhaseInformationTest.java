/*
 * Copyright (C) 2015 likhachev
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
package com.ivli.roim;

import com.ivli.roim.core.PhaseInformation;

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
public class PhaseInformationTest {
    
    
    static final int TOTAL_NO_OF_PHASES = 3;
    static final int NO_OF_FRAMES[]     = {6000, 10000, 100000, 10000 };
    static final int FRAME_DURATION[]   = {1000, 1000,  10000,  100000}; 
    
   
    static final int PHASE_DURATION[] = {NO_OF_FRAMES[0]*FRAME_DURATION[0]
                                       , NO_OF_FRAMES[1]*FRAME_DURATION[1]
                                       , NO_OF_FRAMES[2]*FRAME_DURATION[2]};
    
    static final int SERIES_DURATION = PHASE_DURATION[0]+PHASE_DURATION[1]+PHASE_DURATION[2];
    
    static final int TOTAL_NO_OF_FRAMES  = NO_OF_FRAMES[0]+NO_OF_FRAMES[1]+NO_OF_FRAMES[2];
    
    public PhaseInformationTest() {
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
     * Test of duration method, of class PhaseInformation.
     */
    @Test
    public void testDuration() {
        System.out.println("duration");
        
        for (int n = 0; n < TOTAL_NO_OF_PHASES; ++n) {
            PhaseInformation instance = new PhaseInformation(NO_OF_FRAMES[n], FRAME_DURATION[n]);
            long expResult = PHASE_DURATION[n];
            long result = instance.duration();
            assertEquals(expResult, result);
            
        }        
    }

    /**
     * Test of compareTo method, of class PhaseInformation.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        
        final PhaseInformation base = new PhaseInformation(NO_OF_FRAMES[1], FRAME_DURATION[1]);
        
        assertTrue(base.equals(base));
        
        for (int n = 0; n < TOTAL_NO_OF_PHASES; ++n) {
            PhaseInformation instance = new PhaseInformation(NO_OF_FRAMES[n], FRAME_DURATION[n]);
            long result = instance.compareTo(base);
            long expResult;
            
            switch (n) {
                case 0: expResult = -1; break;
                case 1: expResult =  0; break;
                case 2: expResult =  1; break;
                case 3: expResult = -1; break; //special case see comment to the method
                default: expResult = 42;
            }
            
            
            assertEquals(expResult, result);
            assertTrue(instance.equals(instance));
        }
  
    }
    
}
