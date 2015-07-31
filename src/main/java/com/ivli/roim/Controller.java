/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim;

import java.awt.Graphics2D;
import java.awt.Cursor;
import java.awt.Point;

import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.event.*;
import java.util.NoSuchElementException;
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
class Controller implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ActionListener {
    static final int MOUSE_ACTION_NONE   =  00;
    static final int MOUSE_ACTION_SELECT =  01;
    static final int MOUSE_ACTION_ZOOM   =  02;
    static final int MOUSE_ACTION_PAN    =  03;
    static final int MOUSE_ACTION_WINDOW =  04;
    static final int MOUSE_ACTION_LIST   =  05; //multiframe: scroll through frames
    static final int MOUSE_ACTION_WHEEL  =  15;
   
    static final int MOUSE_ACTION_TOOL   = 100;
    static final int MOUSE_ACTION_MENU   = 200;
    static final int MOUSE_ACTION_ROI    = 500;
    
    protected int iLeftAction   = MOUSE_ACTION_PAN;
    protected int iMiddleAction = MOUSE_ACTION_ZOOM;
    protected int iRightAction  = MOUSE_ACTION_WINDOW;
    protected int iWheelAction  = MOUSE_ACTION_LIST;

    class BaseActionItem extends ActionItem {
        BaseActionItem(int aX, int aY){super(aX, aY);}
        protected  void DoAction(int aX, int aY){} 
        protected  boolean DoWheel(int aX) {
            iControlled.zoom(-aX/Settings.ZOOM_SENSITIVITY_FACTOR, 0, 0);
            iControlled.repaint();
            return true;
        }

        protected  boolean DoRelease(int aX, int aY) {return false;}
        protected  void DoPaint(Graphics2D aGC) {}   
    }
    
    class RectangularRoiCreator extends BaseActionItem {

        final RectangularShape iShape;
        boolean first = true;
        
        RectangularRoiCreator(RectangularShape aShape) {super(-1,-1); iShape = aShape;}

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
            iControlled.getManager().createRoiFromShape(iShape);
            iControlled.repaint();
            return false;
        }
        public void DoPaint(Graphics2D gc) {
            if (null != iShape)
                gc.draw(iShape);
        }
    };
    
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
                                            iControlled.zoom((aX-iX)/Settings.ZOOM_SENSITIVITY_FACTOR, 0, 0);
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
                            iControlled.loadFrame(iX+aX);
                            iX+=aX; //if the index corract and exception haven't been raised
                            iControlled.repaint();
                        }catch (IndexOutOfBoundsException ex) {
                            logger.info(ex);
                        }
                }}; 
            case MOUSE_ACTION_WHEEL: 
            case MOUSE_ACTION_ROI: 
            case MOUSE_ACTION_MENU:
            case MOUSE_ACTION_NONE:
            default: return new BaseActionItem(aX, aY);      
        }        
    }  
    
    private final MEDImageComponent iControlled;
    private ActionItem iAction;   
    private Overlay    iSelected;
    private ActionItem iWheel = NewAction(iWheelAction, 0, 0); 
    
    protected MEDImageComponent getObject() {return iControlled;}
    
    public Controller(MEDImageComponent aC) {
        iControlled = aC;
        register();
    }

    private final void register() {
        iControlled.addMouseListener(this);
        iControlled.addMouseMotionListener(this);
        iControlled.addMouseWheelListener(this);
        iControlled.addKeyListener(this);
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
            if (null != (iSelected = iControlled.getManager().findOverlay(e.getPoint()))) {
                if (0 == (iSelected.getCaps() & Overlay.HASCUSTOMMNU))
                    showPopupMenu_Roi(e.getX(), e.getY());
                
                
            } else 
                showPopupMenu_Context(e.getX(), e.getY());
        }
    }
    
    public void mouseDragged(MouseEvent e) {
        if (null != iAction) 
            iAction.action(e.getX(), e.getY());
    }

    private Overlay findActionTarget(Point aP) {
       Overlay ret = iControlled.getManager().findOverlay(aP);
       if (null != ret && true == ret.isSelectable())
           return ret;
       return null;
    }
    
    public void mousePressed(MouseEvent e) {
        if (null != iAction) {
            iAction.action(e.getX(), e.getY());
         //   return;
        }
        else 
            if (null != (iSelected = findActionTarget(e.getPoint()) )) {  // move ROI
                //iControlled.deleteRoi(iSelected);
                iAction = new BaseActionItem(e.getX(), e.getY()) {
                    protected void DoAction(int aX, int aY) {

                        iControlled.getManager().moveRoi(iSelected, aX-iX, aY-iY);
                        iControlled.repaint();//old.createIntersection(iSelected.iShape.getBounds2D())); 
                    }    
                    protected boolean DoRelease(int aX, int aY) {
                       // iControlled.addRoi(iSelected);
                        iSelected = null;
                        iControlled.repaint();
                        return false;
                      }  
                };
        } 
        else if (SwingUtilities.isLeftMouseButton(e)) {
            iAction = NewAction(iLeftAction, e.getX(), e.getY());
        }
        else if (SwingUtilities.isMiddleMouseButton(e)) {
            iAction = NewAction(iMiddleAction, e.getX(), e.getY());
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            //iRight.Activate(e.getX(), e.getY());
            iAction = NewAction(iRightAction, e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (null != iAction && !iAction.release(e.getX(), e.getY())) iAction = null;               
    }

    public void mouseMoved(MouseEvent e) {   
        
        Overlay r = findActionTarget(e.getPoint());
                        
        if (null != r ) { // TODO: cleave in two
            //if (r instanceof ROI)
           //     iSelected = r;//(ROI)r;
            if (r.isMovable())            
                iControlled.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            // iSelected = tmp;
        } else {
           // iSelected = null;
            iControlled.setCursor(Cursor.getDefaultCursor());
        }        
        ///logger.info ("-->mouse position" + e.getPoint());
    }

    public void keyPressed(KeyEvent e) {
    //  System.out.print("\n\t keyPressed");
        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

        }
        else if (e.getKeyCode() == KeyEvent.VK_ALT) {

        }
    }

    public void keyReleased(KeyEvent e) {
       // System.out.print("\n\t keyReleased");
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

    public void keyTyped(KeyEvent e) {
     // System.out.print("\n\t keyTyped");    
    }

    private static final String KCommandRoiCreateRect = "COMMAND_ROI_CREATE_RECT"; // NOI18N
    private static final String KCommandRoiCreateOval = "COMMAND_ROI_CREATE_OVAL"; // NOI18N
    private static final String KCommandRoiCreateFree = "COMMAND_ROI_CREATE_FREE"; // NOI18N
    private static final String KCommandRoiCreate = "COMMAND_ROI_OPERATIONS_CREATE"; // NOI18N
    private static final String KCommandRoiDelete = "COMMAND_ROI_OPERATIONS_DELETE"; // NOI18N
    private static final String KCommandRoiMove   = "COMMAND_ROI_OPERATIONS_MOVE"; // NOI18N
    private static final String KCommandRoiClone  = "COMMAND_ROI_OPERATIONS_CLONE"; // NOI18N
    private static final String KCommandRoiDeleteAll = "COMMAND_ROI_OPERATIONS_DELETE_ALL"; // NOI18N
    private static final String KCommandRoiFlipVert  = "COMMAND_ROI_OPERATIONS_FLIP_V"; // NOI18N
    private static final String KCommandRoiFlipHorz  = "COMMAND_ROI_OPERATIONS_FLIP_H"; // NOI18N
    private static final String KCommandRoiRotate90CW   = "COMMAND_ROI_OPERATIONS_ROTATE_90_CW"; // NOI18N
    private static final String KCommandRoiRotate90CCW  = "COMMAND_ROI_OPERATIONS_ROTATE_90_CCW"; // NOI18N
    private static final String KCommandRoiConvertToIso = "COMMAND_ROI_OPERATIONS_CONVERT_TO_ISO"; // NOI18N

    public void actionPerformed(ActionEvent e) {
        logger.info(e.getActionCommand() +  e.paramString()); // NOI18N
        
        switch (e.getActionCommand()) {
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
                            iControlled.getManager().createRoiFromShape(iPath);
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
                
            case KCommandRoiClone:   
                iControlled.getManager().cloneRoi((ROI)iSelected);
                iControlled.repaint();
                iSelected = null;
                break;
            case KCommandRoiMove: break;
                
            case KCommandRoiDelete: 
                iControlled.getManager().deleteOverlay(iSelected); 
                iSelected = null; 
                iControlled.repaint(); 
                break;
                
            case KCommandRoiFlipHorz:
                ((Overlay.IFlip)iSelected).flip(false);
                iControlled.repaint();
                break;
            case KCommandRoiFlipVert:
                ((Overlay.IFlip)iSelected).flip(false);
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
                iControlled.getManager().clear();//deleteAllOverlays(); 
                iControlled.repaint();
                break;
                
            default: 
                handleCustomCommand(iSelected);
                break;
        }
    }

    private void handleCustomCommand(Overlay aO) {
    
    }
    
    void showPopupMenu_Context(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI creation")); 
       
        { 
            JMenu m1 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ADD ROI"));

            JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("RECTANGULAR"));
            mi11.addActionListener(this);
            mi11.setActionCommand(KCommandRoiCreateRect);
            m1.add(mi11);

            JMenuItem mi12 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("OVAL"));
            mi12.addActionListener(this);
            mi12.setActionCommand(KCommandRoiCreateOval); 
            m1.add(mi12);

            JMenuItem mi13 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("FREE"));
            mi13.addActionListener(this);
            mi13.setActionCommand(KCommandRoiCreateFree);
            m1.add(mi13);

            mnu.add(m1);
        }
        {       
            JMenuItem m2 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("DELETE ALL"));
            m2.addActionListener(this);
            m2.setActionCommand(KCommandRoiDeleteAll);
            mnu.add(m2);
        }
        
        mnu.show(iControlled, aX, aY);
    }
    
    void showPopupMenu_Roi(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROI operations")); 
        
        if (!iSelected.isPermanent()) {
            JMenuItem mi11 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("DELETE"));
            mi11.addActionListener(this);
            mi11.setActionCommand(KCommandRoiDelete);
            mnu.add(mi11);
        }
        
        if (iSelected.isMovable()) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MOVE"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiMove); 
            mnu.add(mi);
        }
        
        if (iSelected.isCloneable()) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CLONE"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiClone); 
            mnu.add(mi);
        }
        
        if (iSelected instanceof Overlay.IFlip/*iSelected.canFlip()*/) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("FLIP HORZ"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiFlipHorz);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("FLIP VERT"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiFlipVert);
            mnu.add(mi);         
        }
        
        if (iSelected instanceof Overlay.IRotate/*iSelected.canRotate()*/) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROTATE 90 CW"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiRotate90CW);
            mnu.add(mi);
            mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("ROTATE 90 CCW"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiRotate90CCW);
            mnu.add(mi);
        }
         
        if (iSelected instanceof Overlay.IIsoLevel/*iSelected.canFlip()*/) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CONVERT TO ISO LEVEL"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiConvertToIso);
            mnu.add(mi);           
        } 
       
        mnu.show(iControlled, aX, aY);
    }

    public void paint(Graphics2D gc) {
        if (null != iAction) 
            iAction.paint(gc);
       
    }

    private static final Logger logger = LogManager.getLogger(Controller.class);
} 

