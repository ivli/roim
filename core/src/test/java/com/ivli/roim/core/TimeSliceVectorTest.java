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



import java.util.ArrayList;
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
     
    //median frame ordinal since phase
    static final int N1 = NO_OF_FRAMES[0] / 2; 
    static final int N2 = NO_OF_FRAMES[1] / 2;
    static final int N3 = NO_OF_FRAMES[2] / 2;
    //median frame ordinal since image
    static final int M1 = 0 + N1;
    static final int M2 = NO_OF_FRAMES[0] + N2;
    static final int M3 = NO_OF_FRAMES[0] + NO_OF_FRAMES[1] + N3;
    //median frames instant since phase
    static final long L1 = N1 * FRAME_DURATION[0];
    static final long L2 = N2 * FRAME_DURATION[1];
    static final long L3 = N3 * FRAME_DURATION[2];
    //median frames instant since phase
    static final long I1 = 0 + L1;
    static final long I2 = NO_OF_FRAMES[0] * FRAME_DURATION[0] + L2;
    static final long I3 = NO_OF_FRAMES[0] * FRAME_DURATION[0] + NO_OF_FRAMES[1] * FRAME_DURATION[1] + L3;
     
    
    ArrayList<PhaseInformation> phi = null;
    
    
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
            
            int result = instance.frameNumber(uSecFromStart - 1);
            
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
        TimeSliceVector temp = instance.slice(TimeSlice.INFINITY);
        
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

    /**
     * Test of getPhaseVector method, of class TimeSliceVector.
     */
    @Test
    public void testGetPhaseVector() {
        System.out.println("getPhaseVector");
        
    }

    /**
     * Test of getSmallestDuration method, of class TimeSliceVector.
     */
    @Test
    public void testGetSmallestDuration() {
        System.out.println("getSmallestDuration");
       
    }

    /**
     * Test of toString method, of class TimeSliceVector.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        
    }

    /**
     * Test of getSlices method, of class TimeSliceVector.
     */
    @Test
    public void testGetSlices() {
        System.out.println("getSlices");
        TimeSliceVector instance = new TimeSliceVector(phi);
       
        ArrayList<Long> result = instance.getSlices();
        assertEquals(result, result);        
    }

    /**
     * Test of getPhaseDuration method, of class TimeSliceVector.
     */
    @Test
    public void testGetPhaseDuration() {
        System.out.println("getPhaseDuration");
        
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        
        assertEquals(PHASE_DURATION[0], instance.getPhaseDuration(0));
        assertEquals(PHASE_DURATION[1], instance.getPhaseDuration(1));
        assertEquals(PHASE_DURATION[2], instance.getPhaseDuration(2));        
    }

    /**
     * Test of getPhaseFrames method, of class TimeSliceVector.
     */
    @Test
    public void testGetPhaseFrames() {
        System.out.println("getPhaseFrames");
        int aPhaseNumber = 0;
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        assertEquals(NO_OF_FRAMES[0], instance.getPhaseFrames(0));
        assertEquals(NO_OF_FRAMES[1], instance.getPhaseFrames(1));
        assertEquals(NO_OF_FRAMES[2], instance.getPhaseFrames(2));
    }

    /**
     * Test of getFrameDuration method, of class TimeSliceVector.
     */
    @Test
    public void testGetFrameDuration() {
        System.out.println("getFrameDuration");
        
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        assertEquals(instance.getFrameDuration(M1), FRAME_DURATION[0]);
        assertEquals(instance.getFrameDuration(M2), FRAME_DURATION[1]);
        assertEquals(instance.getFrameDuration(M3), FRAME_DURATION[2]);
    }

    /**
     * Test of getNumberOfFrames method, of class TimeSliceVector.
     */
    @Test
    public void testGetNumberOfFrames() {
        System.out.println("getNumberOfFrames");
       
        TimeSliceVector instance = new TimeSliceVector(phi);
       
        assertEquals(instance.getNumberOfFrames(M1), NO_OF_FRAMES[0]);
        assertEquals(instance.getNumberOfFrames(M2), NO_OF_FRAMES[1]);
        assertEquals(instance.getNumberOfFrames(M3), NO_OF_FRAMES[2]);
        
    }

    /**
     * Test of getSlice method, of class TimeSliceVector.
     */
    @Test
    public void testGetSlice() {
        System.out.println("getSlice");
        int FN = 0;
        TimeSliceVector instance = new TimeSliceVector(phi);
        TimeSlice expResult = new TimeSlice(FRAME_DURATION[0] * FN, FRAME_DURATION[0] * (FN + 1));
        TimeSlice result = instance.getSlice(FN);
        assertEquals(0, expResult.compareTo(result));
       
    }

    /**
     * Test of getNumFrames method, of class TimeSliceVector.
     */
    @Test
    public void testGetNumFrames() {
       System.out.println("noOfFrames");
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        int result = instance.getNumFrames();
        assertEquals(TOTAL_NO_OF_FRAMES, result);
    }

    /**
     * Test of getNumPhases method, of class TimeSliceVector.
     */
    @Test
    public void testGetNumPhases() {
         System.out.println("noOfPhases");  
        TimeSliceVector instance = new TimeSliceVector(phi);    
       
        int result = instance.getNumPhases();
        assertEquals(TOTAL_NO_OF_PHASES, result);
    }

    /**
     * Test of framesToPhase method, of class TimeSliceVector.
     */
    @Test
    public void testFramesToPhase() {
        System.out.println("framesToPhase");
       
        TimeSliceVector instance = new TimeSliceVector(phi);
        
        assertEquals(0, instance.framesToPhase(0));
        assertEquals(NO_OF_FRAMES[0], instance.framesToPhase(1));
        assertEquals(NO_OF_FRAMES[0] + NO_OF_FRAMES[1], instance.framesToPhase(2));
       
    }
    
    /**
     * Test of frameStarts method, of class TimeSliceVector.
     */
    @Test
    public void testFrameStarts() {
        System.out.println("frameStarts");
        
        TimeSliceVector instance = new TimeSliceVector(phi);
       
       
        assertEquals(instance.frameStarts(M1), I1);
        assertEquals(instance.frameStarts(M2), I2);
        assertEquals(instance.frameStarts(M3), I3);
    }

    /**
     * Test of duration method, of class TimeSliceVector.
     */
    @Test
    public void testDuration() {
        System.out.println("duration");
        TimeSliceVector instance = new TimeSliceVector(phi);
        long expResult = SERIES_DURATION;
        long result = instance.duration();
        assertEquals(expResult, result);       
    }

    /**
     * Test of sincePhase method, of class TimeSliceVector.
     */
    @Test
    public void testSincePhase() {        
        TimeSliceVector instance = new TimeSliceVector(phi);       
        assertEquals(instance.sincePhase(I1), L1);
        assertEquals(instance.sincePhase(I2), L2);
        assertEquals(instance.sincePhase(I3), L3);
    }

    /**
     * Test of compareTo method, of class TimeSliceVector.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        TimeSliceVector i1 = new TimeSliceVector(phi);
        TimeSliceVector i2 = new TimeSliceVector(phi);
        TimeSliceVector i3 = new TimeSliceVector();
      
        assertTrue(i1.compareTo(i2) == 0);
        assertTrue(i1.compareTo(i3) != 0);
        
    }
    
}
