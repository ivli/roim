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


import com.ivli.roim.core.Window;
import java.awt.Graphics2D;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Point;

import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBoxMenuItem;

import javax.swing.SwingUtilities;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;        


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 *
 * @author likhachev
 */        
class Controller implements ActionListener,  KeyListener,
        MouseListener, MouseMotionListener, MouseWheelListener {
    public static final int MOUSE_ACTION_NONE   =  00;
    public static final int MOUSE_ACTION_SELECT =  01;
    public static final int MOUSE_ACTION_ZOOM   =  02;
    public static final int MOUSE_ACTION_PAN    =  03;
    public static final int MOUSE_ACTION_WINDOW =  04;
    public static final int MOUSE_ACTION_LIST   =  05; //multiframe: scroll through frames
    public static final int MOUSE_ACTION_WHEEL  =  15;
   
    public static final int MOUSE_ACTION_TOOL   = 100;
    public static final int MOUSE_ACTION_MENU   = 200;
    public static final int MOUSE_ACTION_ROI    = 500;
    
    protected int iLeftAction   = Settings.get(Settings.MOUSE_DEFAULT_ACTION_LEFT, Controller.MOUSE_ACTION_ZOOM);
    protected int iMiddleAction = Settings.get(Settings.MOUSE_DEFAULT_ACTION_MIDDLE, Controller.MOUSE_ACTION_PAN);
                       ;
    protected int iRightAction  = Settings.get(Settings.MOUSE_DEFAULT_ACTION_RIGHT, Controller.MOUSE_ACTION_WINDOW);
    protected int iWheelAction  = Settings.get(Settings.MOUSE_DEFAULT_ACTION_WHEEL, Controller.MOUSE_ACTION_LIST);

    private  final double iZoomStep;// = Settings.get(Settings.ZOOM_STEP_FACTOR, 10.);
    
    abstract class BaseActionItem extends ActionItem {
        BaseActionItem(int aX, int aY) {
            super(aX, aY);
        }
       
        protected boolean DoWheel(int aX) {
            iControlled.zoom(-aX/iZoomStep);
            iControlled.repaint();
            return true;
        }
    }
    
    class RectangularRoiCreator extends BaseActionItem {

        final RectangularShape iShape;
        boolean first = true;
        
        RectangularRoiCreator(RectangularShape aShape) {
            super(-1, -1); 
            iShape = aShape;
        }

        public void DoAction(int aX, int aY) {
            if (!first) {
                final double x = iShape.getX();
                final double y = iShape.getY();
                iShape.setFrame(x, y, aX - x, aY - y);                             
                iControlled.repaint();
            } else {
                iShape.setFrame(aX, aY, 0,0); 
                first = false;
            }                        
        }

        public boolean DoRelease(int aX, int aY) {
            iControlled.getROIMgr().createRoiFromShape(iShape);
            iControlled.repaint();
            return false;
        }
        
        public void DoPaint(Graphics2D gc) {
            if (null != iShape)
                gc.draw(iShape);
        }
    }
    
    ActionItem NewAction(int aType, int aX, int aY) {
        switch (aType){   
            case MOUSE_ACTION_WINDOW: 
                 return new BaseActionItem(aX, aY) {
                     public void DoAction(int aX, int aY) {                        
                        iControlled.setWindow(new Window(iControlled.getWindow().getLevel() + iY - aY, iControlled.getWindow().getWidth() + aX - iX));
                        iControlled.repaint();
                 }}; 
            case MOUSE_ACTION_ZOOM: return new BaseActionItem(aX, aY) {
                                        public void DoAction(int aX, int aY) {
                                            iControlled.zoom((aX-iX)/iZoomStep);
                                            iControlled.repaint();
                }};  
            case MOUSE_ACTION_PAN: 
                return new BaseActionItem(aX, aY) {
                    public void DoAction(int aX, int aY) {
                        iControlled.pan(aX-iX, aY-iY);
                        iControlled.repaint();
                }};                 
            case MOUSE_ACTION_LIST: return new BaseActionItem(aX, aY) {
                    public void DoAction(int aX, int aY) {
                        try {                            
                            if (iControlled.loadFrame(iX + aX)) {
                                iX += aX; 
                                iControlled.repaint();
                            }
                        }catch (IndexOutOfBoundsException ex) {
                            logger.info(ex);
                        }
                }}; 
            case MOUSE_ACTION_WHEEL: 
            case MOUSE_ACTION_ROI: 
            case MOUSE_ACTION_MENU:
            case MOUSE_ACTION_NONE: 
            default: throw new UnsupportedOperationException();//return new BaseActionItem(aX, aY);      
        }        
    }  
    
    private final ImageView iControlled;
    private ActionItem iAction;   
    private Overlay  iSelected;
    private final ActionItem iWheel;
    
    public Controller(ImageView aC) {
        iControlled = aC;
        iZoomStep = Math.min(aC.getImage().getWidth(), aC.getImage().getHeight()) / Settings.get(Settings.ZOOM_STEP_FACTOR, 10.);
        iWheel = NewAction(iWheelAction, 0, 0);        
        iControlled.addMouseListener(this);
        iControlled.addMouseMotionListener(this);
        iControlled.addMouseWheelListener(this);
        iControlled.addKeyListener(this);
    }

    public void paint(Graphics2D gc) {
        if (null != iAction) 
            iAction.paint(gc);
    }
     
    protected Overlay findActionTarget(Point aP) {
       Overlay ret = iControlled.getROIMgr().findOverlay(aP);
       if (null != ret && true == ret.isSelectable())
           return ret;
       return null;
    }
    
    protected void addSelection(Overlay aO) {
        if (null != aO) {
            iSelected = aO;
            iSelected.setEmphasized(true);
        }
    }
    
    protected void releaseSelection(Overlay aO) {
        if (null != aO) {
            aO.setEmphasized(false);                    
        } else if (null != iSelected) {
            iSelected.setEmphasized(false);
        }
        
        iSelected = null;
    }
    
    boolean blockObjectSpecificPopupMenu() {
        return false;
    }


    public void mouseEntered(MouseEvent e) {
        iControlled.requestFocusInWindow(); //gain focus
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {    
        if (null != iAction)
            iAction.wheel(e.getWheelRotation());
        else
            iWheel.DoAction(e.getWheelRotation(), 0);
            //iControlled.zoom(e.getWheelRotation()/Settings.ZOOM_SENSITIVITY_FACTOR, 0,0);
    }


    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            Overlay tmp = iControlled.getROIMgr().findOverlay(e.getPoint());

            if (null != tmp) {
                addSelection(tmp);

                if (iSelected.hasMenu() && !blockObjectSpecificPopupMenu()) {
                    buildObjectSpecificPopupMenu(iSelected).show(iControlled, e.getX(), e.getY());
                   // mnu.;
                }
            } else 
                buildContextPopupMenu().show(iControlled, e.getX(), e.getY());

        }
    }

    public void mouseDragged(MouseEvent e) {
        if (null != iAction) 
            iAction.action(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        Overlay tmp = null;
        if (null != iAction) {
            iAction.action(e.getX(), e.getY());             
        } else if (null != (tmp = findActionTarget(e.getPoint()) )) { // Object specific handling                    
            addSelection(tmp);
            iAction = new BaseActionItem(e.getX(), e.getY()) {
                protected void DoAction(int aX, int aY) {
                    iControlled.getROIMgr().moveRoi(iSelected, aX-iX, aY-iY);
                    iControlled.repaint();//old.createIntersection(iSelected.iShape.getBounds2D())); 
                }    
                protected boolean DoRelease(int aX, int aY) {    
                    //iSelected.select(false);
                    //iSelected = null;
                    releaseSelection(null);
                    iControlled.repaint();
                    return false;
                }  
                protected boolean DoWheel(int aX) {
                    if (iSelected instanceof Overlay.IRotate) {
                        ((Overlay.IRotate)iSelected).rotate(aX);
                        iControlled.repaint();
                    }
                    return true;
                }
            };
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            iAction = NewAction(iLeftAction, e.getX(), e.getY());
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            iAction = NewAction(iMiddleAction, e.getX(), e.getY());
        } else if (SwingUtilities.isRightMouseButton(e)) {                
            iAction = NewAction(iRightAction, e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (null != iAction && !iAction.release(e.getX(), e.getY())) 
            iAction = null;               
    }

    public void mouseMoved(MouseEvent e) {   
        final Overlay r = findActionTarget(e.getPoint());

        if (null != r ) { 
            if (r.isMovable())            
                iControlled.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));                
        } else {               
            iControlled.setCursor(Cursor.getDefaultCursor());
        }                
    }

       
    @Override
    public void keyPressed(KeyEvent e) {        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_ALT:
            default: break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {      
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SHIFT: break; 
            case KeyEvent.VK_ALT: break;
            case KeyEvent.VK_R: break;
            case KeyEvent.VK_1: break;
            case KeyEvent.VK_2: break;
            case KeyEvent.VK_3: break;
            case KeyEvent.VK_4: break;
            case KeyEvent.VK_5: break;
            case KeyEvent.VK_6: break;
            case KeyEvent.VK_7: break;
            default: break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
     // System.out.print("\n\t keyTyped");    
    }
    
    private static final String KCommandRoiCreateRect = "COMMAND_ROI_CREATE_RECT"; // NOI18N
    private static final String KCommandRoiCreateOval = "COMMAND_ROI_CREATE_OVAL"; // NOI18N
    private static final String KCommandRoiCreateFree = "COMMAND_ROI_CREATE_FREE"; // NOI18N
    private static final String KCommandRoiCreateIso  = "COMMAND_ROI_CREATE_ISO"; // NOI18N
    private static final String KCommandRoiCreateProfile = "COMMAND_ROI_CREATE_PROFILE"; // NOI18N
    private static final String KCommandRoiCreateRuler = "COMMAND_ROI_CREATE_RULER"; // NOI18N
    private static final String KCommandRoiCreate = "COMMAND_ROI_OPERATIONS_CREATE"; // NOI18N
    private static final String KCommandRoiDelete = "COMMAND_ROI_OPERATIONS_DELETE"; // NOI18N
    private static final String KCommandRoiMove   = "COMMAND_ROI_OPERATIONS_MOVE"; // NOI18N
    private static final String KCommandRoiPin  = "COMMAND_ROI_OPERATIONS_PIN"; // NOI18N
    private static final String KCommandRoiClone  = "COMMAND_ROI_OPERATIONS_CLONE"; // NOI18N
    private static final String KCommandRoiDeleteAll = "COMMAND_ROI_OPERATIONS_DELETE_ALL"; // NOI18N
    private static final String KCommandRoiFlipVert  = "COMMAND_ROI_OPERATIONS_FLIP_V"; // NOI18N
    private static final String KCommandRoiFlipHorz  = "COMMAND_ROI_OPERATIONS_FLIP_H"; // NOI18N
    private static final String KCommandRoiRotate90CW   = "COMMAND_ROI_OPERATIONS_ROTATE_90_CW"; // NOI18N
    private static final String KCommandRoiRotate90CCW  = "COMMAND_ROI_OPERATIONS_ROTATE_90_CCW"; // NOI18N
    private static final String KCommandRoiConvertToIso = "COMMAND_ROI_OPERATIONS_CONVERT_TO_ISO"; // NOI18N
    private static final String KCommandProfileShow     = "COMMAND_ROI_OPERATIONS_PROFILE_SHOW_ON-OFF"; // NOI18N
    private static final String KCommandCustomCommand = "COMMAND_ROI_OPERATIONS_CUSTOM_COMMAND"; // NOI18N

    @Override
    public void actionPerformed(ActionEvent aCommand) {  
       // onAction(e.getActionCommand());
   // }
    
   // protected void onAction(final String aCommand) {
        switch (aCommand.getActionCommand()) {
            case KCommandRoiCreateFree: ///amazingly but it does work
                iAction = new BaseActionItem(-1, -1) {                           
                    Path2D iPath = new Path2D.Double();
                    int first = 0;
                    public void DoAction(int aX, int aY) {
                        if (0 == first)
                            iPath.moveTo(aX, aY);
                        else
                            iPath.lineTo(aX, aY);
                        ++first; 
                        iControlled.repaint();//iPath.getBounds()); 
                    }

                    public boolean DoRelease(int aX, int aY) {
                        if (first < 4)
                            return true;
                        else {
                            iPath.closePath();
                            iControlled.getROIMgr().createRoiFromShape(iPath);
                            iControlled.repaint();
                        }
                        return false;
                    }
                    
                    public void DoPaint(Graphics2D gc) {
                        gc.draw(iPath);
                    }
                }; break;
                
            case KCommandRoiCreateOval:
                iAction = new RectangularRoiCreator(new Ellipse2D.Double());
                break;
                
            case KCommandRoiCreateRect: 
                iAction = new RectangularRoiCreator(new Rectangle2D.Double());
                break;
                
            case KCommandRoiCreateProfile: //
                iAction = new RectangularRoiCreator(new Rectangle2D.Double()) {
                    
                    public boolean DoRelease(int aX, int aY) {
                        iControlled.getROIMgr().createProfile(iShape);
                        iControlled.repaint();
                        return false;
                    }
                    public void DoPaint(Graphics2D gc) {                       
                           final java.awt.Rectangle bn = iShape.getBounds();                           
                           gc.drawLine(0, bn.y, iControlled.getWidth(), bn.y);                           
                           gc.drawLine(0, bn.y+bn.height, iControlled.getWidth(), bn.y + bn.height);                       
                    }
                }; break;
                            
            case KCommandRoiCreateRuler: {
                iAction = new BaseActionItem(-1, -1) {
                    //boolean first = true;
                    Point start =  null;
                    Point finish = null;
                    
                    public void DoAction(int aX, int aY) {
                        if (start == null && finish == null) {
                            start = new Point(aX, aY);
                        } else //if (start != null && finish ==null)
                            finish = new Point(aX, aY);
                         
                        iControlled.repaint();//iPath.getBounds()); 
                    }
                    
                    public boolean DoRelease(int aX, int aY) {
                        iControlled.getROIMgr().createRuler(start, finish);
                        iControlled.repaint();
                        return false;
                    }
        
                    public void DoPaint(Graphics2D gc) {                                              
                        if (start != null && finish != null)
                            gc.drawLine(start.x, start.y, finish.x, finish.y);
                    }
                       
                };
                } break;
            case KCommandRoiClone:   
                ROI r = iControlled.getROIMgr().cloneRoi((ROI)iSelected);
                iControlled.repaint();
                //iSelected.select(false);
                //iSelected = null;
                releaseSelection(null);
                //addSelection(r);
                break;
            case KCommandRoiMove: break;            
            case KCommandRoiPin: 
                iSelected.setPinned(!iSelected.isPinned());
                break;
            case KCommandRoiDelete: 
                iControlled.getROIMgr().deleteOverlay(iSelected); 
                //iSelected = null; 
                releaseSelection(null);
                iControlled.repaint(); 
                break;                
            case KCommandRoiFlipHorz:
                ((Overlay.IFlip)iSelected).flip(false);
                iControlled.repaint();
                break;
            case KCommandRoiFlipVert:
                ((Overlay.IFlip)iSelected).flip(true);
                iControlled.repaint();
                break;
            case KCommandRoiRotate90CW:
                ((Overlay.IRotate)iSelected).rotate(90);
                iControlled.repaint();
                break;
            case KCommandRoiRotate90CCW:
                ((Overlay.IRotate)iSelected).rotate(-90);
                iControlled.repaint();
                break;
            case KCommandRoiConvertToIso:
                ((Overlay.IIsoLevel)iSelected).isolevel(0);
                iControlled.repaint();
                ;break;
            case KCommandRoiDeleteAll: 
                iSelected = null; 
                releaseSelection(null);
                iControlled.getROIMgr().clear();//deleteAllOverlays(); 
                iControlled.repaint();
                break;
            case KCommandProfileShow:
                ((Profile)iSelected).showHistogram();
                iControlled.repaint();
                break;
            case KCommandCustomCommand: {
                if (iSelected instanceof Annotation.Static) {
                    com.ivli.roim.controls.AnnotationPanel panel = new com.ivli.roim.controls.AnnotationPanel((Annotation.Static)iSelected);
                    javax.swing.JDialog dialog = new javax.swing.JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);

                    dialog.setContentPane(panel);
                    dialog.validate();
                    dialog.pack();
                    dialog.setResizable(false);
                    dialog.setVisible(true);
                } else {
                    
                }
            } break;
            default: 
                if(!handleCustomCommand(aCommand)) {
                 //TODO: raise an exception ???
                }
                break;
        }
    }

    protected boolean handleCustomCommand(ActionEvent aCommand) {
        return false;
    }
        
    JPopupMenu buildContextPopupMenu() {
        JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_OVERLAY_CREATE")); 
        {
            JMenu m1 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CREATE_OVERLAY"));
            {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CREATE_OVERLAY.RECTANGULAR"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiCreateRect);
            m1.add(mi);
            } 
            { 
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CREATE_OVERLAY.OVAL"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiCreateOval); 
            m1.add(mi);
            } 
            { 
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CREATE_OVERLAY.FREE"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiCreateFree);
            m1.add(mi);
            } 
            
            mnu.add(m1);
        }
        { 
        JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CREATE_OVERLAY.RULER"));
        mi.addActionListener(this);
        mi.setActionCommand(KCommandRoiCreateRuler);
        mnu.add(mi);
        } 
        {            
        JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CREATE_OVERLAY.PROFILE"));
        mi.addActionListener(this);
        mi.setActionCommand(KCommandRoiCreateProfile);
        mnu.add(mi);
        }
        {       
        JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.DELETE_ALL"));
        mi.addActionListener(this);
        mi.setActionCommand(KCommandRoiDeleteAll);
        mnu.add(mi);
        }
        
        return mnu;//mnu.show(iControlled, aX, aY);
    }
    
    JPopupMenu buildObjectSpecificPopupMenu(Overlay aO) {
       JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS")); 
        
       buildPopupMenu_Roi(mnu);
        
        if(iSelected instanceof Profile)
            buildPopupMenu_Profile(mnu);
                        
        return mnu;    
    }
    
    void buildPopupMenu_Roi(JPopupMenu mnu) {
        if (!iSelected.isPermanent()) {
            JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.DELETE"));
            mi11.addActionListener(this);
            mi11.setActionCommand(KCommandRoiDelete);
            mnu.add(mi11);
        }
        
        /* DON'T POLLUTE MENUS
        if (iSelected.isMovable()) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.MOVE"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiMove); 
            mnu.add(mi);
        }
        */
        if (iSelected.isPinnable()) {
            JCheckBoxMenuItem mi = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.PIN"));
            //boolean b = iSelected.isPinned();
            mi.setState(iSelected.isPinned());
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiPin); 
            mnu.add(mi);
        }
        
        if (iSelected.isCloneable()) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.CLONE"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiClone); 
            mnu.add(mi);
        }
        
        if (iSelected instanceof Overlay.IFlip/*iSelected.canFlip()*/) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.FLIP_HORZ"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiFlipHorz);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.FLIP_VERT"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiFlipVert);
            mnu.add(mi);         
        }
        
        if (iSelected instanceof Overlay.IRotate/*iSelected.canRotate()*/) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.ROTATE_90_CW"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiRotate90CW);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.ROTATE_90_CCW"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiRotate90CCW);
            mnu.add(mi);
        }
         
        if (iSelected instanceof Overlay.IIsoLevel/*iSelected.canFlip()*/) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.CONVERT_TO_ISO"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiConvertToIso);
            mnu.add(mi);           
        }
        
        if (0 != (iSelected.getCaps() & Overlay.HASCUSTOMMENU)) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.CUSTOMMENU"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandCustomCommand); 
            mnu.add(mi);
        }
    }

    void buildPopupMenu_Profile(JPopupMenu mnu) {
        {
            JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.PROFILE_SHOW"));
            mi11.addActionListener(this);
            mi11.setActionCommand(KCommandProfileShow);
            mnu.add(mi11);
        } 
    }
 
    private static final Logger logger = LogManager.getLogger(Controller.class);
} 

