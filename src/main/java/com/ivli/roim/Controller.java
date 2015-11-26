
package com.ivli.roim;

import com.ivli.roim.core.Window;
import java.awt.Graphics2D;
import java.awt.Cursor;
import java.awt.Point;

import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.event.*;
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
class Controller implements ActionListener {
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
    
    protected int iLeftAction   = Settings.MOUSE_DEFAULT_ACTION_LEFT;
    protected int iMiddleAction = Settings.MOUSE_DEFAULT_ACTION_MIDDLE;
    protected int iRightAction  = Settings.MOUSE_DEFAULT_ACTION_RIGHT;
    protected int iWheelAction  = Settings.MOUSE_DEFAULT_ACTION_WHEEL;

    private double iZoomStep = Settings.ZOOM_STEP_FACTOR;
    
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
            super(-1,-1); 
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
                        iControlled.getLUTMgr().setWindow(new Window(iControlled.getLUTMgr().getWindow().getLevel() + iY - aY, iControlled.getLUTMgr().getWindow().getWidth() + aX - iX));
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
    private final ActionItem iWheel = NewAction(iWheelAction, 0, 0); 
    private final MouseHandler iMouse = new MouseHandler();
    private final KeyHandler   iKeys  = new KeyHandler();
    //protected ImageView getObject() {return iControlled;}
    
    public Controller(ImageView aC) {
        iControlled = aC;
        iControlled.addMouseListener(iMouse);
        iControlled.addMouseMotionListener(iMouse);
        iControlled.addMouseWheelListener(iMouse);
        iControlled.addKeyListener(iKeys);
    }

    public void paint(Graphics2D gc) {
        if (null != iAction) 
            iAction.paint(gc);
    }
     
    private Overlay findActionTarget(Point aP) {
       Overlay ret = iControlled.getROIMgr().findOverlay(aP);
       if (null != ret && true == ret.isSelectable())
           return ret;
       return null;
    }
    
    class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {           
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
                if (null != (iSelected = iControlled.getROIMgr().findOverlay(e.getPoint()))) {
                    if (iSelected.hasMenu()) {
                        final JPopupMenu mnu = buildObjectSpecificPopupMenu(iSelected);


                         mnu.show(iControlled, e.getX(), e.getY());
                    }
                } else 
                    showPopupMenu_Context(e.getX(), e.getY());
            }
        }

        public void mouseDragged(MouseEvent e) {
            if (null != iAction) 
                iAction.action(e.getX(), e.getY());
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

                            iControlled.getROIMgr().moveRoi(iSelected, aX-iX, aY-iY);
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
    }
    
    class KeyHandler implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
        //  System.out.print("\n\t keyPressed");
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

            }
            else if (e.getKeyCode() == KeyEvent.VK_ALT) {

            }
        }

        @Override
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

        @Override
        public void keyTyped(KeyEvent e) {
         // System.out.print("\n\t keyTyped");    
        }
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
    private static final String KCommandRoiClone  = "COMMAND_ROI_OPERATIONS_CLONE"; // NOI18N
    private static final String KCommandRoiDeleteAll = "COMMAND_ROI_OPERATIONS_DELETE_ALL"; // NOI18N
    private static final String KCommandRoiFlipVert  = "COMMAND_ROI_OPERATIONS_FLIP_V"; // NOI18N
    private static final String KCommandRoiFlipHorz  = "COMMAND_ROI_OPERATIONS_FLIP_H"; // NOI18N
    private static final String KCommandRoiRotate90CW   = "COMMAND_ROI_OPERATIONS_ROTATE_90_CW"; // NOI18N
    private static final String KCommandRoiRotate90CCW  = "COMMAND_ROI_OPERATIONS_ROTATE_90_CCW"; // NOI18N
    private static final String KCommandRoiConvertToIso = "COMMAND_ROI_OPERATIONS_CONVERT_TO_ISO"; // NOI18N
    private static final String KCommandProfileShow     = "COMMAND_ROI_OPERATIONS_PROFILE_SHOW_ON-OFF"; // NOI18N

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
                        if (null != iShape) {                         
                           final java.awt.Rectangle cr = gc.getClipBounds();
                           final java.awt.Rectangle bn = iShape.getBounds();                           
                           gc.drawLine(cr.x, bn.y, cr.x+cr.width, bn.y);                           
                           gc.drawLine(cr.x, bn.y+bn.height, cr.x+cr.width, bn.y+bn.height);
                        }
                    }
                };
            break;
                
            case KCommandRoiClone:   
                iControlled.getROIMgr().cloneRoi((ROI)iSelected);
                iControlled.repaint();
                iSelected = null;
                break;
            case KCommandRoiMove: break;
                
            case KCommandRoiDelete: 
                iControlled.getROIMgr().deleteOverlay(iSelected); 
                iSelected = null; 
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
                iControlled.getROIMgr().clear();//deleteAllOverlays(); 
                iControlled.repaint();
                break;
            case KCommandProfileShow:
                ((Profile)iSelected).showHistogram();
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
        
        mnu.show(iControlled, aX, aY);
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
        
        if (iSelected.isMovable()) {
            JMenuItem mi = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_ROI_OPERATIONS.MOVE"));
            mi.addActionListener(this);
            mi.setActionCommand(KCommandRoiMove); 
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

