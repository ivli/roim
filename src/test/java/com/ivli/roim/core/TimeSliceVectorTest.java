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
public class TimeSliceVectorTest {
    
    static final int TOTAL_NO_OF_PHASES = 3;
    static final int NO_OF_FRAMES[]     = {60, 10000, 1};
    static final int FRAME_DURATION[]   = {1000, 15000, 60000}; 
    
   
    static final int PHASE_DURATION[] = {NO_OF_FRAMES[0]*FRAME_DURATION[0]
                                       , NO_OF_FRAMES[1]*FRAME_DURATION[1]
                                       , NO_OF_FRAMES[2]*FRAME_DURATION[2]};
    
    static final int SERIES_DURATION = PHASE_DURATION[0]+PHASE_DURATION[1]+PHASE_DURATION[2];
    
    static final int TOTAL_NO_OF_FRAMES  = NO_OF_FRAMES[0]+NO_OF_FRAMES[1]+NO_OF_FRAMES[2];
     
     
    java.util.ArrayList<PhaseInformation> phi = null;
    
    
    public TimeSliceVectorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        phi = new  java.util.ArrayList<>();
        
        for (int i = 0; i < TOTAL_NO_OF_PHASES; ++i)
            phi.add(new PhaseInformation(NO_OF_FRAMES[i], FRAME_DURATION[i]));
        
    }
    
    @After
    public void tearDown() {
        phi.clear();
    }

    /**
     * Test of isValidFrameNumber method, of class TimeSliceVector.
     */
    @Test
    public void testIsValidFrameNumber() {
        System.out.println("isValidFrameNumber");
        
        TimeSliceVector instance = new TimeSliceVector(phi);
                
        boolean positive = instance.isValidFrameNumber(0) 
                        && instance.isValidFrameNumber(TOTAL_NO_OF_FRAMES - 1)
                        && instance.isValidFrameNumber(Math.floorDiv(TOTAL_NO_OF_FRAMES, 2));
        
        boolean negative = instance.isValidFrameNumber(-1) 
                        || instance.isValidFrameNumber(TOTAL_NO_OF_FRAMES)
                        || instance.isValidFrameNumber(TOTAL_NO_OF_FRAMES + 1)
                        || instance.isValidFrameNumber(TOTAL_NO_OF_FRAMES + (int)Math.floor(Math.random()));
                
                
        assertTrue(positive);
        assertFalse(negative);
     
    }

    /**
     * Test of noOfFrames method, of class TimeSliceVector.
     */
    @Test
    public void testNoOfFrames() {
        System.out.println("noOfFrames");
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        int result = instance.getNumFrames();
        assertEquals(TOTAL_NO_OF_FRAMES, result);
        
    }

    /**
     * Test of noOfPhases method, of class TimeSliceVector.
     */
    @Test
    public void testNoOfPhases() {
        System.out.println("noOfPhases");  
        TimeSliceVector instance = new TimeSliceVector(phi);    
       
        int result = instance.getNumPhases();
        assertEquals(TOTAL_NO_OF_PHASES, result);
       
    }

    /**
     * Test of phaseDuration method, of class TimeSliceVector.
     */
    @Test
    public void testPhaseDuration() {
        System.out.println("phaseDuration");
        boolean result = true;//new int[TOTAL_NO_OF_PHASES];
        
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        for (int i=0; i < TOTAL_NO_OF_PHASES && result; ++i) {
            result = (instance.phaseDuration(i) == PHASE_DURATION[i]);
        }
        
        assertTrue(result);
 
    }

    /**
     * Test of phaseFrame method, of class TimeSliceVector.
     */
    @Test
    public void testPhaseFrame() {
        System.out.println("phaseFrame");
        int aFrameNumber = 0;
        
        for (int i = 0; i < TOTAL_NO_OF_PHASES; ++i) {                   
            TimeSliceVector instance = new TimeSliceVector(phi);
            
            int result = instance.phaseFrame(aFrameNumber + NO_OF_FRAMES[i] / 2);
            assertEquals(i, result);
            
            result = instance.phaseFrame(aFrameNumber );
            assertEquals(i, result);
            
            result = instance.phaseFrame(aFrameNumber + NO_OF_FRAMES[i] - 1);
            assertEquals(i, result);
            
            
            aFrameNumber += NO_OF_FRAMES[i];
        }
    }

    /**
     * Test of phaseStarts method, of class TimeSliceVector.
     */
    @Test
    public void testPhaseStarts() {
        System.out.println("phaseStarts");
        
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        long expResult = 0L;
        
        
        for (int i = 0; i < TOTAL_NO_OF_PHASES; ++i) {
            String msg = String.format("phase %d", i);
            long result = instance.phaseStarts(i);
            assertEquals(expResult, result);           
            expResult += PHASE_DURATION[i];
        }  
    }

        
    /**
     * Test of frameNumber method, of class TimeSliceVector.
     */
    @Test
    public void testPhaseNumber() {
        System.out.println("phaseNumber");
        long uSecFromStart = 0L;
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        int ret = instance.phaseNumber(0);
        assertEquals(0, ret);
        
        ret = instance.phaseNumber(PHASE_DURATION[0]-1);
        assertEquals(0, ret);
        
        
        ret = instance.phaseNumber(PHASE_DURATION[0]);
        assertEquals(1, ret);
        
        ret = instance.phaseNumber(PHASE_DURATION[0] + PHASE_DURATION[1] - 1);
        assertEquals(1, ret);
        
        ret = instance.phaseNumber(PHASE_DURATION[0] + PHASE_DURATION[1] );
        assertEquals(2, ret);
        
        ret = instance.phaseNumber(PHASE_DURATION[0] + PHASE_DURATION[1] + PHASE_DURATION[2] - 1);        
        assertEquals(2, ret);
        
        ret = instance.phaseNumber(PHASE_DURATION[0] + PHASE_DURATION[1] / 2);        
        assertEquals(1, ret);
        
        //boolean res = false;
        //try {
            ret = instance.phaseNumber(PHASE_DURATION[0] + PHASE_DURATION[1] + PHASE_DURATION[2]);        
            System.out.println(String.format("phaseNumber %d", ret));
        //} catch (Exception ex) {
        //    res = true;
       // }
        
        assertEquals(2, ret);
    }

    
    /**
     * Test of frameNumber method, of class TimeSliceVector.
     */
    @Test
    public void testFrameNumber() {
        System.out.println("frameNumber");
        
        TimeSliceVector instance = new TimeSliceVector(phi);
        int expResult = 0;
        long uSecFromStart = 0L;
        
        for (int i = 0; i < TOTAL_NO_OF_PHASES; ++i) {            
            expResult     += NO_OF_FRAMES[i];
            uSecFromStart += PHASE_DURATION[i];          
            
            int result = instance.frameNumber(new Instant(uSecFromStart - 1));
            
            assertEquals(expResult - 1, result);    
        }
    }

    
     /**
     * Test of resample method, of class TimeSliceVector.
     */
    @Test
    public void testSlice() {
        System.out.println("slice");
        
        final TimeSliceVector instance = new TimeSliceVector(phi);
        
        {
        TimeSliceVector temp = instance.slice(TimeSlice.FOREWER);
        
        assertEquals("duration",  instance.duration(),     temp.duration());
        assertEquals("NumPhases", instance.getNumPhases(), temp.getNumPhases());
        assertEquals("NumFrames", instance.getNumFrames(), temp.getNumFrames());   
        }
        
        int from = 0;
        int to   = 0;
        
        for (int i = 0; i < TOTAL_NO_OF_PHASES; ++i) {
            String msg = String.format("iteration #%d", i);
            to += PHASE_DURATION[i];
        
            TimeSliceVector temp = instance.slice(new TimeSlice(from, to));

            assertEquals(msg + "duration", PHASE_DURATION[i], temp.duration());
            assertEquals(msg + "NumPhases",                1, temp.getNumPhases());
            assertEquals(msg + "NumFrames", NO_OF_FRAMES[i],  temp.getNumFrames());

            from += PHASE_DURATION[i];        
        }
    }
    
    /**
     * Test of resample method, of class TimeSliceVector.
     */
    @Test
    public void testResample_int() {
        System.out.println("resample");
        int newFrameDuration[] = {100, 1000, 2000, 5000, 15000};
        
        for (int i = 0; i < newFrameDuration.length; ++i) {
            TimeSliceVector instance = new TimeSliceVector(phi);
            instance.resample(newFrameDuration[i]);

            assertEquals(instance.duration(), SERIES_DURATION);
            assertEquals( instance.duration() / newFrameDuration[i], instance.getNumFrames());
        }
    }

    /**
     * Test of resample method, of class TimeSliceVector.
     */
    @Test
    public void testResample_int_int() {
        System.out.println("resample");
        
        int newFrameDuration[] = {100, 1000, 2000, 5000, 10000};
        
        for (int p = 0; p < TOTAL_NO_OF_PHASES; ++p ) 
            for (int i = 0; i < newFrameDuration.length; ++i) {
                
                String msg = String.format("resample phase = %d, new length = %d \n", p, newFrameDuration[i] );
                
                TimeSliceVector instance = new TimeSliceVector(phi);                
                instance.resample(p, newFrameDuration[i]);

                
                assertEquals(msg, SERIES_DURATION, instance.duration());
                 
                int nf[] = new int[]{NO_OF_FRAMES[0], NO_OF_FRAMES[1], NO_OF_FRAMES[2]};
                
                nf[p] = (nf[p] * FRAME_DURATION[p]) / newFrameDuration[i];
                
                long newNoOfFrames = 0L;
                
                for (int l=0; l < nf.length; ++l)
                    newNoOfFrames += nf[l];
                 
                assertEquals(msg, newNoOfFrames, instance.getNumFrames());
             }
    }
    
}
