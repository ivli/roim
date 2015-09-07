package com.ivli.roim.controls;

import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.awt.Point;
import java.awt.Frame;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jfree.data.xy.XYSeries;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ivli.roim.ActionItem;
import com.ivli.roim.Events.WindowChangeListener;
import com.ivli.roim.Events.WindowChangeEvent;

import com.ivli.roim.LutLoader;
import com.ivli.roim.Settings;
import com.ivli.roim.Window;
import com.ivli.roim.IWLManager;

/**
 *
 * @author likhachev
 */
public class LUTControl extends JComponent implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, WindowChangeListener {
    
    private static final boolean MARKERS_DISPLAY_WL_VALUES = false;
    private static final boolean WEDGE_EXTEND_WHEN_FOCUSED = false;      
    
    private static final int NUMBER_OF_SHADES = 255;
    
    
    private static final int INACTIVE_BAR_WIDTH = 32 / (WEDGE_EXTEND_WHEN_FOCUSED ? 2 : 1); //when mouse is out
    
    private final int ACTIVATED_BAR_WIDTH = 32; //mouse inside
    private final int TOP_GAP;  //reserve a half of marker height at window's top & bottom 
    private final int BOTTOM_GAP;
    private static final int LEFT_GAP = 2;  //reserve border at left & right
    private static final int RIGHT_GAP = 2;
    
    private static final int MARKER_CURSOR = java.awt.Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = java.awt.Cursor.N_RESIZE_CURSOR;
                        
    private boolean iShowPercent = false;
    ///private ImageView iView;
    
    private IWLManager     iWLM;
    private java.awt.Image iMarker;
    private BufferedImage  iBuf;
    
    private final boolean iActive;
    private ActionItem iAction;
    
    private final Marker iTop;///    = new Marker(Color.green, "top");  //NOI18N
    private final Marker iBottom;/// = new Marker(Color.red, "bottom"); //NOI18N
 
    private Window iLast;
     /* 
      * passive mode constructor, control is able only to display W/L not to 
      */
    public LUTControl(LUTControl aControl) {    
        iActive = false;
        iWLM = aControl.iWLM;
        
        TOP_GAP = BOTTOM_GAP = (null != iMarker) ? iMarker.getHeight(null) / 2 : 4; 
        iTop = iBottom = null; //no markers needed
    } 
    
     /* 
      * complete object constructor
      */
     @SuppressWarnings("LeakingThisInConstructor")
    public LUTControl(IWLManager aView) {    
        assert(null != aView);
        
        iActive = true;
        iWLM = aView;                       
        
        try {
            iMarker = javax.imageio.ImageIO.read(ClassLoader.getSystemResource("images/green_arrow_w.png")); //NOI18N
            
        } catch (IOException ex) {              
            logger.info(ex);                 
        } 
             /* it's kinda feedback coupling used to addjust marker positions when control's size gets changed */
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {changeWindow(iWLM.getWindow());}                                               
            public void componentHidden(ComponentEvent e) {}
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {changeWindow(iWLM.getWindow());}                    
            });
        

        TOP_GAP = BOTTOM_GAP = null != iMarker ? iMarker.getHeight(null) / 2 : 4;   
        
        iTop    = new Marker(Color.green, "top");  //NOI18N
        iBottom = new Marker(Color.red, "bottom"); //NOI18N
        
        //aView.addWindowChangeListener(this);  
        
        addMouseMotionListener(this);
        addMouseListener(this);  
        addMouseWheelListener(this);   
    }
    
     
    public XYSeries makeXYSeries(XYSeries ret) {
        return iWLM.makeXYSeries(ret);
    }   

    private void invalidateBuffer() {        
        iBuf = null;
    }
    
    private void changeWindow(Window aW) {              
        iWLM.setWindow(aW);            
    }   
    
    @Override
    public void windowChanged(WindowChangeEvent anE) {   
        if (null != iTop && null != iBottom) { //theoretically we'd never get here when in passive mode
            iTop.setPosition((int) imageToScreen(anE.getWindow().getTop()));
            iBottom.setPosition((int) imageToScreen(anE.getWindow().getBottom())); 
            //iLast = anE.getWindow();
        }
        invalidateBuffer();
        repaint();              
    }   
           
    @Override
    public void mouseDragged(MouseEvent e) {
        if (null != iAction) 
            iAction.action(e.getX(), e.getY());
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (null != iAction)
            iAction.wheel(e.getWheelRotation());
        else {
            final Window win = new Window(iWLM.getWindow());                     

            win.setLevel(win.getLevel() - e.getWheelRotation());                 

            if (iWLM.getRange().contains(win)) {
                iWLM.setWindow(win);

                changeWindow(win);     
            }
        }            
    } 
    
    @Override
    public void mouseMoved(MouseEvent e) {
        final int ypos = getHeight() - e.getPoint().y;
                              
        if (iTop.contains(ypos) || iBottom.contains(ypos))
            setCursor(java.awt.Cursor.getPredefinedCursor(MARKER_CURSOR));
        else if (ypos < iTop.getPosition() && ypos > iBottom.getPosition())
            setCursor(java.awt.Cursor.getPredefinedCursor(WINDOW_CURSOR));
        else 
            setCursor(java.awt.Cursor.getDefaultCursor());                                     
    }
       
    @Override
    public void mousePressed(MouseEvent e) {        
        final int ypos = getHeight()- e.getPoint().y;
                      
        if (iTop.contains(ypos) || iBottom.contains(ypos) || iTop.getPosition() + 4 > ypos && iBottom.getPosition() - 4 < ypos) {  
           
            
            iAction = new ActionItem(e.getX(), e.getY()) {
                 
                boolean first = true;
                final boolean iMoveTop = iTop.contains(ypos);    

                final boolean iMoveBoth = !(iMoveTop || iBottom.contains(ypos)) && iTop.getPosition() > ypos && iBottom.getPosition() < ypos;  

                protected void DoAction(int aX, int aY) {
                    
                    
                    final double delta = screenToImage(aY - iY);
                    final Window win = new Window(iWLM.getWindow());                     

                    if (iMoveTop) {               
                       win.setTop(win.getTop() - delta);
                      // iTop.move(iTop.getPosition() + aX - iX);
                    } else if (iMoveBoth) {
                        win.setLevel(win.getLevel() - delta);
                    } else {   
                      // iBottom.move(iBottom.getPosition() + aX - iX); 
                       win.setBottom(win.getBottom() - delta);
                    }
                    
                    if (iWLM.getRange().contains(win)) {
                        if (null != iWLM) 
                            iWLM.setWindow(win);
                        if (first) {
                            iLast = iWLM.getWindow();
                            logger.info("saving last window, " + iLast);
                            first = false;
                        }
                        changeWindow(win);     
                    }
                }   
                protected boolean DoWheel(int aX) {
                    
                    final Window win = new Window(iWLM.getWindow());                     
                    
                    win.setLevel(win.getLevel() - aX);                 
                    
                    if (iWLM.getRange().contains(win)) {
                        if (null != iWLM) 
                            iWLM.setWindow(win);

                        changeWindow(win);     
                    }
                  
                    return false;
                }
            };                
        } 
        else if (SwingUtilities.isLeftMouseButton(e)) {
            //iAction = NewAction(iLeftAction, e.getX(), e.getY());
        }
        else if (SwingUtilities.isMiddleMouseButton(e)) {
            //iAction = NewAction(iMiddleAction, e.getX(), e.getY());
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            //iRight.Activate(e.getX(), e.getY());
            //iAction = NewAction(iRightAction, e.getX(), e.getY());
        }                
    }
    
    public void mouseReleased(MouseEvent e) {                    
        iAction = null;  
        if (!getBounds().contains(e.getPoint())){
           shrink(); 
           //iActive = true;
        }
    }
   
    public void mouseEntered(MouseEvent e) {        
        //iActive = true;
        if (null == iAction) {
            extend();
        }
    }
    
    public void mouseExited(MouseEvent e) {
        if (null == iAction) {
            shrink();
           // iActive = false;
        }
    }
    
    public void mouseClicked(MouseEvent e) {    
        if (SwingUtilities.isRightMouseButton(e)) 
            showPopupMenu(e.getX(), e.getY());
        else if (SwingUtilities.isLeftMouseButton(e)) {
                if(e.getClickCount() == 2){    
                    logger.info("gonna chane window to " + iLast);
                    changeWindow(iWLM.getRange().Window());
                }
        }
    }
    
     private void updateBufferedImage() {
        final int width  = getWidth()  - (LEFT_GAP + RIGHT_GAP);
        final int height = getHeight() - (TOP_GAP + BOTTOM_GAP);               
       
        
        final int size = (width * height - 1);
        DataBuffer data = new DataBufferUShort(width * height);
        
        final double ratio = iWLM.getRange().getRange() / height;
        
        for (int i = 0; i < height; ++i) {                                   
            final int lineNdx =  size - (i * width);
   
            for (int j = 0; j < width; ++j) {               
                data.setElem(lineNdx - j, (short)((double)i * ratio));            
            }                      
        }
       
        final WritableRaster wr = Raster.createWritableRaster(new ComponentSampleModel(data.getDataType(), width, height, 1, width, new int[] {0})             
                                                             , data, new Point(0,0)
                                                             );        
        
        iBuf = iWLM.transform(new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY)                                                               
                                                    , new int[] {8}
                                                    , false		// has alpha
                                                    , false		// alpha premultipled
                                                    , Transparency.OPAQUE
                                                    , data.getDataType())                                                                                                                                  
                                                , wr, true, null), null);
        
       // iBuf = iView.getPLut().transform(iBuf, null);     
    }
     
    public void paintComponent(Graphics g) {  
        if (null == iBuf) { 
            updateBufferedImage();
            if (null != iTop && null != iBottom) {
                iTop.setPosition((int) imageToScreen(iWLM.getWindow().getTop()));              
                iBottom.setPosition((int) imageToScreen(iWLM.getWindow().getBottom()));
            }
        }
        
        final Color clr = g.getColor();
        
        g.setColor(Color.LIGHT_GRAY);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(iBuf, LEFT_GAP, TOP_GAP, getWidth() - (LEFT_GAP + RIGHT_GAP), getHeight() - (TOP_GAP + BOTTOM_GAP), null);
        
        //g.drawImage(iBuf, 0, TOP_GAP, getWidth(), getHeight() - (TOP_GAP), 0, 0, INACTIVE_BAR_WIDTH, 255, null);
        
        g.draw3DRect(0, 0, getWidth(), getHeight(), true);       
        
        if (null != iTop && null != iBottom) {
            iTop.draw(g);
            iBottom.draw(g);
        }
        
        g.setColor(clr);
    }
     
    void extend() {
        if (WEDGE_EXTEND_WHEN_FOCUSED) {
            setSize(ACTIVATED_BAR_WIDTH, getHeight());
            setLocation(getLocation().x - (ACTIVATED_BAR_WIDTH - INACTIVE_BAR_WIDTH), getLocation().y);
            getParent().setComponentZOrder(this, 0);

            invalidateBuffer();
            repaint();
        }                
    }
    
    void shrink() {
        if(WEDGE_EXTEND_WHEN_FOCUSED) {
            setSize(INACTIVE_BAR_WIDTH, getHeight());
            getParent().setComponentZOrder(this, 1);
            invalidateBuffer();
            //repaint(); 
            getParent().revalidate();
        }
    }
  
    double imageToScreen(double aY) {
        final double screenRange = this.getHeight() - (TOP_GAP + BOTTOM_GAP);
        final double imageRange  = iWLM.getRange().getRange();        
        return aY*screenRange/imageRange;       
    }
    
    double screenToImage(double aY) {
        final double screenRange = this.getHeight() - (TOP_GAP + BOTTOM_GAP);
        final double imageRange  = iWLM.getRange().getRange();        
        return aY*imageRange/screenRange;    
    }       
    
    public Dimension getMinimumSize() {
        return new Dimension(ACTIVATED_BAR_WIDTH, NUMBER_OF_SHADES + (TOP_GAP + BOTTOM_GAP));
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(ACTIVATED_BAR_WIDTH, NUMBER_OF_SHADES + (TOP_GAP + BOTTOM_GAP));
    }
    
    public Dimension getMaximumSize() {
        return new Dimension(ACTIVATED_BAR_WIDTH, Short.MAX_VALUE);///NUMBER_OF_SHADES + 2*VERTICAL_BAR_EXCESS); //
    }
    
    final class Marker {
        int    iPos;
        Color  iCol;
        String iName;

        Marker(Color aColor, String aName) {
            iPos = 0;
            iCol = aColor; 
            iName = aName;
        }
              
        void setPosition(int aPos) {
            //logger.info(iName + ", " + aPos ); //NOI18N
            iPos = aPos;
        } 
        
        int getPosition() {
            return iPos;
        }             
        
        boolean contains(int aVal) {
            final int ypos = iPos;//getHeight() - (TOP_GAP + BOTTOM_GAP) - iPos;
            final int height = (null != iMarker) ? iMarker.getHeight(null) : 4;
            return aVal < ypos + height && aVal > ypos  /*- half_height*/;
        }
            
        void draw(Graphics aGC)/*, int aWidth, int aHeight)*/ {  
            aGC.setColor(Color.BLACK);       
           
            if (iActive) {
                if (null != iMarker) {
                    int ypos = getHeight() - (TOP_GAP + BOTTOM_GAP) - iPos;// + ((iName == "top") ? TOP_GAP : BOTTOM_GAP);
                    aGC.drawImage(iMarker, 0, ypos, null);
                } else {                                       
                    aGC.drawLine(0, getHeight() - iPos, getWidth() , getHeight() - iPos);  
                }
            }
                
            if (MARKERS_DISPLAY_WL_VALUES) {
                final double val = iShowPercent ? screenToImage(iPos) * 100.0 / iWLM.getRange().getRange() : screenToImage(iPos);
                final String out = String.format("%.0f", Math.abs(val)); //NOI18N
                final Rectangle2D sb = aGC.getFontMetrics().getStringBounds(out, aGC);    
                final int height = (null != iMarker) ? iMarker.getHeight(null) : 4;
                aGC.drawString(out, (int)(getWidth()/2 - sb.getWidth()/2) 
                              , (int)(getHeight() - iPos - height / 2 + sb.getHeight() / 2 )
                              );
            }
           
        }         
    }
    
    
    private static final String KCommandTriggerLinear = "COMMAND_LUTCONTROL_TRIGGER_LINEAR"; // NOI18N
    private static final String KCommandTriggerDirect = "COMMAND_LUTCONTROL_TRIGGER_DIRECT"; // NOI18N
    private static final String KCommandShowDialog    = "COMMAND_LUTCONTROL_SHOW_DIALOG"; // NOI18N
    private static final String KCommandChangeLUT     = "COMMAND_LUTCONTROL_CHANGE_LUT"; // NOI18N
    
    
    public void actionPerformed(ActionEvent e) {
        assert (null != iWLM);
        
        logger.info(e.getActionCommand() +  e.paramString()); // NOI18N
        
        switch (e.getActionCommand()) {             
            case KCommandTriggerLinear: 
                iWLM.setLinear(!iWLM.isLinear());
                invalidateBuffer();
                //iView.repaint();
                repaint();
                break;
            case KCommandTriggerDirect: 
                iWLM.setInverted(!iWLM.isInverted());
                invalidateBuffer();
                ///iView.repaint();
                repaint();
                break;
            case KCommandShowDialog:    /**    TODO: refactor the dialog     **/     
                
                VOILUTPanel panel = new VOILUTPanel(this, null);//iView.getImage().image());
                
                javax.swing.JDialog dialog = new javax.swing.JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setContentPane(panel);
                dialog.validate();
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);                 
                break;
            case KCommandChangeLUT:
                FileDialog fd = new FileDialog((Frame)null , java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("NEWJFRAME.CHOOSE_A_FILE"), FileDialog.LOAD);
                fd.setDirectory(Settings.LUT_DIRECTORY);
                fd.setFile(Settings.LUT_FILE_SUFFIX);
                fd.setVisible(true);
                String cm ;        
                if (null != fd.getFile() && null != (cm = fd.getDirectory() + fd.getFile())) {
                    iWLM.setLUT(cm);
                    invalidateBuffer();
                   // iView.repaint();
                    repaint();
                } 
                break;          
            default: 
                //setLUT(e.getActionCommand());
                iWLM.setLUT(e.getActionCommand());
                invalidateBuffer();
               // iView.repaint();
                repaint();
                break;
         }
    }
    
    void showPopupMenu(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("WL_CONTEXT_MENU_TITLE"));        
        JCheckBoxMenuItem mi11 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_LOGARITHMIC"));
        mi11.addActionListener(this);
        mi11.setState(!iWLM.isLinear());
        mi11.setActionCommand(KCommandTriggerLinear);
        
        mnu.add(mi11);

        JCheckBoxMenuItem mi12 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_DIRECT_INVERSE"));
        mi12.addActionListener(this);
        mi12.setActionCommand(KCommandTriggerDirect); 
        mi12.setState(iWLM.isInverted());
        mnu.add(mi12);

        JMenuItem mi13 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.SHOW_DIALOG"));
        mi13.addActionListener(this);
        mi13.setActionCommand(KCommandShowDialog); 
        mnu.add(mi13);
        
        JMenu m1 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.OPEN_BUILTIN_LUT"));
               
        for (String s : LutLoader.getBuiltinLUT()) {
            JMenuItem mit = new JMenuItem(s);
            mit.addActionListener(this);
            mit.setActionCommand(s); 
            m1.add(mit);
        }   
        
        mnu.add(m1);
        
        JMenuItem mi14 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.OPEN_LUT_ONDISC"));
        mi14.addActionListener(this);
        mi14.setActionCommand(KCommandChangeLUT); 
        mnu.add(mi14);
        
        mnu.show(this, aX, aY);
    }   
    
    
    private static final Logger logger = LogManager.getLogger(LUTControl.class);
}

