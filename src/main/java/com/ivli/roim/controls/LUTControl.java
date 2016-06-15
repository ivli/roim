package com.ivli.roim.controls;

import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Cursor;

import java.awt.geom.Rectangle2D;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.EventListenerList;

import com.ivli.roim.view.ActionItem;
import com.ivli.roim.view.ImageView;
import com.ivli.roim.io.LutReader;
import com.ivli.roim.view.Settings;
import com.ivli.roim.core.Curve;
import com.ivli.roim.core.ImageFrame;
import com.ivli.roim.core.Window;
import com.ivli.roim.core.Range;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;
/**
 *
 * @author likhachev
 */
public class LUTControl extends JComponent implements  WindowChangeListener, FrameChangeListener, ActionListener, 
                                                          MouseMotionListener, MouseListener, MouseWheelListener {    
    private static final boolean MARKERS_DISPLAY_WL_VALUES = false;
    private static final boolean MARKERS_DISPLAY_PERCENT   = false;
    private static final boolean WEDGE_EXTEND_WHEN_FOCUSED = false;      
    
    private static final int NUMBER_OF_SHADES = 255;    
    private static final int VGAP_DEFAULT = 4;
    private static final int INACTIVE_BAR_WIDTH  = 32 / (WEDGE_EXTEND_WHEN_FOCUSED ? 2 : 1); //when mouse is out    
    private static final int ACTIVATED_BAR_WIDTH = 32; //mouse inside
    private static final int LEFT_GAP  = 2;  //reserve border at left & right
    private static final int RIGHT_GAP = 2;
    
    private final int TOP_GAP;  //reserve a half of marker height at window's top & bottom 
    private final int BOTTOM_GAP;
        
    private static final int MARKER_CURSOR = Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = Cursor.N_RESIZE_CURSOR;                           
   
    protected final EventListenerList iList;
      
    private Range iRange;    
    private final Marker iTop;
    private final Marker iBottom;
    private ActionItem iAction;
    private BufferedImage iBuf;
      
    private boolean iCanShowDialog;
    private ImageView  iView;                 
    
    
    public static LUTControl create(ImageView aV) {
        LUTControl ret = new LUTControl();
        ret.construct(aV);
        aV.addFrameChangeListener(ret);
        aV.addWindowChangeListener(ret); 
        return ret;
    }
    
    protected LUTControl() { 
        iTop = new Marker(true);  
        iBottom = new Marker(false);  
        iList = new EventListenerList();
        TOP_GAP = iTop.getMarkerHeight()/2;
        BOTTOM_GAP = iBottom.getMarkerHeight()/2; //to the case images of different height are used 
        iRange  = null;        
        iCanShowDialog = true;        
    }
          
    protected void attach(LUTControl aParent) {                          
        construct(aParent.iView);
        
        iCanShowDialog = false;
        
        aParent.addWindowChangeListener(this);
        
        super.addAncestorListener(new AncestorListener() {
            public void ancestorAdded(AncestorEvent event) {}

            public void ancestorRemoved(AncestorEvent event){        
                LOG.debug("Deregistered");
                aParent.removeWindowChangeListener(LUTControl.this);            
            }

            public void ancestorMoved(AncestorEvent event){}         
        });
    } 
      
    private void construct(ImageView aW) {    
        iView = aW;    
        if (null != iView.getImage())
            iRange = iView.getFrame().getRange();   
       // else
       //     iRange = new Range();
        /* use feedback loop to addjust marker positions when size changed */
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {                
                iRange = iView.getRange();
                iTop.setPosition((int) imageToScreen(iView.getWindow().getTop()));
                iBottom.setPosition((int) imageToScreen(iView.getWindow().getBottom())); 
                makeBuffer();
                invalidateBuffer();
                repaint();
            }                                               
            public void componentHidden(ComponentEvent e) {
               // if (null != iParent)
               //     iParent.removeWindowChangeListener(e.getSource());
            }
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {
                iRange = iView.getRange();
                iTop.setPosition((int) imageToScreen(iView.getWindow().getTop()));
                iBottom.setPosition((int) imageToScreen(iView.getWindow().getBottom())); 
                makeBuffer();
                invalidateBuffer();
                repaint();
            }                    
        });
              
        addMouseMotionListener(this);
        addMouseListener(this);  
        addMouseWheelListener(this);   
    }
  
    /* */     
    public Curve getCurve() {    
        return iView.getLUTMgr().getCurve();
    }   
        
    public void addWindowChangeListener(WindowChangeListener aL) {        
        iList.add(WindowChangeListener.class, aL);    
    }
   
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iList.remove(WindowChangeListener.class, aL);       
    }
    
    private void directChangeWindow(Window aW) {           
        iTop.setPosition((int) imageToScreen(aW.getTop()));
        iBottom.setPosition((int) imageToScreen(aW.getBottom()));              
        invalidateBuffer();
              
        iView.setWindow(aW);        
        notifyWindowChange();
        iView.repaint();
    }
            
    protected void notifyWindowChange() {
        WindowChangeEvent evt = new WindowChangeEvent(this, iView.getWindow());
        for (WindowChangeListener l : iList.getListeners(WindowChangeListener.class))            
            l.windowChanged(evt);            
    }
           
    @Override
    public void windowChanged(WindowChangeEvent anE) {       
        if (null != iTop && null != iBottom ) { 
            iTop.setPosition((int) imageToScreen(anE.getWindow().getTop()));
            iBottom.setPosition((int) imageToScreen(anE.getWindow().getBottom()));                       
            
            invalidateBuffer();
            repaint();     
        }         
    }   
    
    @Override
    public void frameChanged(FrameChangeEvent anE) {           
        if (null != iTop && null != iBottom) {   
            Window nw;
            
            if (true) {
                nw = new Window(iRange = iView.getRange());
                //nw = new Window(iRange = anE.getRange());
            } else {            
                //nw = new Window(iView.getWindow());
                nw = new Window(iRange);
                nw.setBottom(screenToImage(iBottom.getPosition()));
                nw.setTop(screenToImage(iTop.getPosition()));
                               
                final double or = iRange.range();           
                final double nr = (iRange = iView.getRange()).range();       
                nw.scale(nr / or);                
            } 
            
            directChangeWindow(nw);
            makeBuffer();     
        }         
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
            final Window win = new Window(iView.getWindow());                     

            win.setLevel(win.getLevel() - e.getWheelRotation());                 

            if (iRange.contains(win)) {
                directChangeWindow(win);
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
        final int ypos = getHeight() - e.getPoint().y;

        if (iTop.contains(ypos) || iBottom.contains(ypos) || iTop.getPosition() + 4 > ypos && iBottom.getPosition() - 4 < ypos) {  
            iAction = new ActionItem(e.getX(), e.getY()) {

                boolean first = true;
                final boolean iMoveTop = iTop.contains(ypos);    

                final boolean iMoveBoth = !(iMoveTop || iBottom.contains(ypos)) && iTop.getPosition() > ypos && iBottom.getPosition() < ypos;  

                protected void DoAction(int aX, int aY) {
                    final double delta = screenToImage(aY - iY);
                    final Window win = new Window(iView.getWindow());                     

                    if (iMoveTop) {               
                       win.setTop(win.getTop() - delta);
                      // iTop.move(iTop.getPosition() + aX - iX);
                    } else if (iMoveBoth) {
                        win.setLevel(win.getLevel() - delta);
                    } else {   
                      // iBottom.move(iBottom.getPosition() + aX - iX); 
                       win.setBottom(win.getBottom() - delta);
                    }

                    if (iRange.contains(win)) 
                        directChangeWindow(win); 

                }   

                protected boolean DoWheel(int aX) {
                    final Window win = new Window(iView.getWindow());                     

                    win.setLevel(win.getLevel() - aX);                 

                    if (iRange.contains(win)) {
                        if (null != iView) 
                            directChangeWindow(win);
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
           
        }
    }

    public void mouseEntered(MouseEvent e) {        
        //iActive = true;
        if (null == iAction) {
           
        }
    }

    public void mouseExited(MouseEvent e) {
        if (null == iAction) {           
        }
    }

    public void mouseClicked(MouseEvent e) {    
        if (SwingUtilities.isRightMouseButton(e)) 
            showPopupMenu(e.getX(), e.getY());
        else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)                                            
            directChangeWindow(new Window(iRange));         
    }

    private  ImageFrame iBackFrame;
    
    private void makeBuffer() {
        final int width  = getWidth()  - (LEFT_GAP + RIGHT_GAP);
        final int height = getHeight() - (TOP_GAP + BOTTOM_GAP);               
        
        if (width <=0 || height <=0) {
           iBackFrame = null;
           return;
        }
            
        iBackFrame = new ImageFrame(width, height);
        final double ratio = iRange.range() / height;
        
        for (int i = 0; i < width; ++i) {                  
            for (int j = 0; j < height; ++j) {                                     
                iBackFrame.setPixel(i, j, (int)((height - j) * ratio));
            }                      
        }
    }
        
    private void updateBufferedImage() {
        iBuf = iView.transform(iBackFrame);                                                                                                                                                                                                 
    }
      
    public void paintComponent(Graphics g) {          
        if (null == iBuf && null != iBackFrame)  
            updateBufferedImage();
           
        final Color clr = g.getColor();
        
        g.setColor(Color.LIGHT_GRAY);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(iBuf, LEFT_GAP, TOP_GAP, getWidth() - (LEFT_GAP + RIGHT_GAP), getHeight() - (TOP_GAP + BOTTOM_GAP), null);      
        
        g.draw3DRect(0, 0, getWidth(), getHeight(), true);       
        
        if (null != iTop && null != iBottom) {
            iTop.draw(g);
            iBottom.draw(g);
        }
        
        g.setColor(clr);
    }
     
    private void invalidateBuffer() {        
        iBuf = null;
    }       
  
    private double imageToScreen(double aY) {              
        return aY * ((this.getHeight() - (TOP_GAP + BOTTOM_GAP)) / iRange.range());       
    }
    
    private double screenToImage(double aY) {             
        return aY * (iRange.range() /(this.getHeight() - (TOP_GAP + BOTTOM_GAP)));  
    }       
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(ACTIVATED_BAR_WIDTH, NUMBER_OF_SHADES + (TOP_GAP + BOTTOM_GAP));
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ACTIVATED_BAR_WIDTH, NUMBER_OF_SHADES + (TOP_GAP + BOTTOM_GAP));
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(ACTIVATED_BAR_WIDTH, Short.MAX_VALUE);
    }
    
    final class Marker {
        int     iPos;        
        boolean iTop;
        Image  iKnob;

        Marker(boolean aTop) {           
            iPos = 0;                       
            iTop = aTop;
            
            try {               
                if (iTop) 
                    iKnob = javax.imageio.ImageIO.read(ClassLoader.getSystemResource("images/knob_bot.png")); //NOI18N
                else
                    iKnob = javax.imageio.ImageIO.read(ClassLoader.getSystemResource("images/knob_bot.png")); //NOI18N                                   
                
             } catch (IOException ex) {              
                 LOG.error("FATAL!!!", ex); //NOI18N               
             }         
        }
        
        int getMarkerHeight() {
            return iKnob.getHeight(null);
        }
            
        void setPosition(int aPos) {           
            iPos = aPos;
        } 
        
        int getPosition() {
            return iPos;
        }             
        
        boolean contains(int aVal) {
            final int ypos = iPos;//getHeight() - (TOP_GAP + BOTTOM_GAP) - iPos;
            final int height = (null != iKnob) ? iKnob.getHeight(null) : 4;
            return aVal < ypos + height && aVal > ypos  /*- half_height*/;
        }
            
        void draw(Graphics aGC) {                                                   
            final int ypos = getHeight() - (TOP_GAP + BOTTOM_GAP) - iPos;// + ((iName == "top") ? TOP_GAP : BOTTOM_GAP);
            aGC.drawImage(iKnob, 0, ypos, null);                
                
            if (MARKERS_DISPLAY_WL_VALUES) {
                final double val = MARKERS_DISPLAY_PERCENT ? screenToImage(iPos) * 100.0 / iRange.range() : screenToImage(iPos);
                final String out = String.format("%.0f", Math.abs(val)); //NOI18N
                final Rectangle2D sb = aGC.getFontMetrics().getStringBounds(out, aGC);    
                final int height = (null != iKnob) ? iKnob.getHeight(null) : 4;
                
                aGC.setColor(Color.BLACK);    
                aGC.drawString(out, (int)(getWidth()/2 - sb.getWidth()/2), 
                              (int)(getHeight() - iPos - height / 2 + sb.getHeight() / 2 )
                              );
            }           
        }         
    }        
       
    private static final String KCommandTriggerLinear = "COMMAND_LUTCONTROL_TRIGGER_LINEAR"; // NOI18N
    private static final String KCommandTriggerDirect = "COMMAND_LUTCONTROL_TRIGGER_DIRECT"; // NOI18N
    private static final String KCommandShowDialog    = "COMMAND_LUTCONTROL_SHOW_DIALOG";    // NOI18N
    private static final String KCommandChangeLUT     = "COMMAND_LUTCONTROL_CHANGE_LUT";     // NOI18N    
    
    public void actionPerformed(ActionEvent e) {
        assert (null != iView);
        
        LOG.info(e.getActionCommand() +  e.paramString()); // NOI18N
        
        switch (e.getActionCommand()) {             
            case KCommandTriggerLinear: 
                iView.setLinear(!iView.isLinear());
                invalidateBuffer();
                //iView.repaint();
                repaint();
                break;
            case KCommandTriggerDirect: 
                iView.setInverted(!iView.isInverted());
                invalidateBuffer();
                ///iView.repaint();
                repaint();
                break;
            case KCommandShowDialog:    /**    TODO: refactor the dialog     **/                     
                VOILUTPanel panel = new VOILUTPanel(this, this.iView);
                
                javax.swing.JDialog dialog = new javax.swing.JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setContentPane(panel);
                dialog.validate();
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);                 
                break;
            case KCommandChangeLUT:
                FileDialog fd = new FileDialog((Frame)null , java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.CHOOSE_LUT_FILE"), FileDialog.LOAD);
                fd.setDirectory(Settings.get(Settings.KEY_DEFAULT_FOLDER_LUT, System.getProperty("user.home")));
                fd.setFile(Settings.get(Settings.KEY_FILE_SUFFIX_LUT, Settings.DEFAULT_FILE_SUFFIX_LUT));
                fd.setVisible(true);
                  
                if (null != fd.getFile()) {
                    final String lutFile = fd.getDirectory() + fd.getFile();
                    Settings.set(Settings.KEY_LASTFILE_LUT, lutFile);
                    iView.setLUT(lutFile);
                    invalidateBuffer();                  
                    repaint();
                } 
                break;          
            default: 
                //setLUT(e.getActionCommand());
                iView.setLUT(e.getActionCommand());
                invalidateBuffer();
                iView.repaint();
                repaint();
                break;
         }
    }
    
    void showPopupMenu(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu("WL_CONTEXT_MENU_TITLE");     //NOI18N    
        JCheckBoxMenuItem mi11 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_LOGARITHMIC"));
        mi11.addActionListener(this);
        mi11.setState(!iView.isLinear());
        mi11.setActionCommand(KCommandTriggerLinear);
        
        mnu.add(mi11);

        JCheckBoxMenuItem mi12 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_DIRECT_INVERSE"));
        mi12.addActionListener(this);
        mi12.setActionCommand(KCommandTriggerDirect); 
        mi12.setState(iView.isInverted());
        mnu.add(mi12);
        
        if (iCanShowDialog) {
            JMenuItem mi13 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.SHOW_DIALOG"));
            mi13.addActionListener(this);
            mi13.setActionCommand(KCommandShowDialog); 
            mnu.add(mi13);
        }
        
        JMenu m1 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.OPEN_BUILTIN_LUT"));
               
        for (String s : LutReader.getInstalledLUT()) {
            JMenuItem mit = new JMenuItem(s);
            mit.addActionListener(this);
            mit.setActionCommand(s); 
            m1.add(mit);
        }   
        
        mnu.add(m1);
        
        JMenuItem mi14 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.CHOOSE_LUT_FILE"));
        mi14.addActionListener(this);
        mi14.setActionCommand(KCommandChangeLUT); 
        mnu.add(mi14);
        
        mnu.show(this, aX, aY);
    }   

    private static final Logger LOG = LogManager.getLogger();
}

