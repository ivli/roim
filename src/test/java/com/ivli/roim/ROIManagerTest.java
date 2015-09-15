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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
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
public class ROIManagerTest {
    
    public ROIManagerTest() {
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
     * Test of getImage method, of class ROIManager.
     */
    @Test
    public void testGetImage() {
        System.out.println("getImage");
        ROIManager instance = null;
        IMultiframeImage expResult = null;
        IMultiframeImage result = instance.getImage();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clear method, of class ROIManager.
     */
    @Test
    public void testClear() {
        System.out.println("clear");
        ROIManager instance = null;
        instance.clear();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class ROIManager.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        ROIManager instance = null;
        instance.update();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of paint method, of class ROIManager.
     */
    @Test
    public void testPaint() {
        System.out.println("paint");
        Graphics2D aGC = null;
        AffineTransform aT = null;
        ROIManager instance = null;
        instance.paint(aGC, aT);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createRoiFromShape method, of class ROIManager.
     */
    @Test
    public void testCreateRoiFromShape() {
        System.out.println("createRoiFromShape");
        Shape aS = null;
        ROIManager instance = null;
        instance.createRoiFromShape(aS);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of cloneRoi method, of class ROIManager.
     */
    @Test
    public void testCloneRoi() {
        System.out.println("cloneRoi");
        ROI aR = null;
        ROIManager instance = null;
        instance.cloneRoi(aR);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of moveRoi method, of class ROIManager.
     */
    @Test
    public void testMoveRoi() {
        System.out.println("moveRoi");
        Overlay aO = null;
        double adX = 0.0;
        double adY = 0.0;
        ROIManager instance = null;
        instance.moveRoi(aO, adX, adY);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findOverlay method, of class ROIManager.
     */
    @Test
    public void testFindOverlay() {
        System.out.println("findOverlay");
        Point aP = null;
        ROIManager instance = null;
        Overlay expResult = null;
        Overlay result = instance.findOverlay(aP);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteRoi method, of class ROIManager.
     */
    @Test
    public void testDeleteRoi() {
        System.out.println("deleteRoi");
        ROI aR = null;
        ROIManager instance = null;
        boolean expResult = false;
        boolean result = instance.deleteRoi(aR);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteOverlay method, of class ROIManager.
     */
    @Test
    public void testDeleteOverlay() {
        System.out.println("deleteOverlay");
        Overlay aO = null;
        ROIManager instance = null;
        boolean expResult = false;
        boolean result = instance.deleteOverlay(aO);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteAllOverlays method, of class ROIManager.
     */
    @Test
    public void testDeleteAllOverlays() {
        System.out.println("deleteAllOverlays");
        ROIManager instance = null;
        instance.deleteAllOverlays();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOverlaysList method, of class ROIManager.
     */
    @Test
    public void testGetOverlaysList() {
        System.out.println("getOverlaysList");
        ROIManager instance = null;
        Iterator<Overlay> expResult = null;
        Iterator<Overlay> result = instance.getOverlaysList();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of externalize method, of class ROIManager.
     */
    @Test
    public void testExternalize() {
        System.out.println("externalize");
        String aFileName = "";
        ROIManager instance = null;
        //instance.externalize(aFileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of internalize method, of class ROIManager.
     */
    @Test
    public void testInternalize() {
        System.out.println("internalize");
        String aFileName = "";
        ROIManager instance = null;
        //instance.internalize(aFileName);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
