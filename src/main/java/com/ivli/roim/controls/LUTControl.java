
package com.ivli.roim.controls;



import java.io.IOException;
import java.awt.Color;
//import ;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.color.*;
import java.awt.image.*;
import java.awt.Point;
import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;

import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
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
import com.ivli.roim.ImageView;
import com.ivli.roim.LutLoader;
import com.ivli.roim.PresentationLut;
import com.ivli.roim.Settings;
import com.ivli.roim.VOILut;
import com.ivli.roim.Window;


/**
 *
 * @author likhachev
 */
public class LUTControl extends JComponent implements ActionListener, MouseMotionListener, MouseListener, MouseWheelListener, WindowChangeListener {
    private static final int NUMBER_OF_SHADES = 255;
    
    private static final boolean EXTEND_WHEN_FOCUSED = false;
    
    private static final int INACTIVE_BAR_WIDTH    = 40 / (EXTEND_WHEN_FOCUSED ? 2 : 1); //when mouse is out
    private  final int ACTIVATED_BAR_WIDTH   = 40; //mouse inside
    private  final int TOP_GAP ;  //reserve some pixels at top & bottom 
    private  final int BOTTOM_GAP;
    private  static final int HORIZONTAL_BAR_EXCESS = 0;  //reserve border at left & right
    
    private static final int MARKER_CURSOR = java.awt.Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = java.awt.Cursor.N_RESIZE_CURSOR;
                    
    java.awt.Image arrow;
    
    boolean iShowPercent = true;
    ImageView iComponent;
    
    VOILut iVLut;
    PresentationLut iPLut;
    
    private BufferedImage iBuf;
    
    boolean    iActive;
    ActionItem iAction;
    
    Marker iTop    = new Marker(Color.green, "top");  //NOI18N
    Marker iBottom = new Marker(Color.red, "bottom"); //NOI18N
 
           
    public LUTControl(LUTControl aControl) {    
        
        try {
            arrow = javax.imageio.ImageIO.read(ClassLoader.getSystemResource("images/green_arrow_w.png")); //NOI18N
            
            } catch (IOException ex) {              
                logger.info(ex);                   
            } 
        
        TOP_GAP = BOTTOM_GAP = null != arrow ? arrow.getHeight(null)/2 : 4;   
         
        if (null != aControl) {
            iPLut = aControl.iPLut;  
            iVLut = aControl.iVLut;
        }
        
        addComponentListener(new listener());
    }    
    
       
    public void attach(PresentationLut aPlut, VOILut aVlut) {
       iPLut = aPlut;  
       iVLut = aVlut;              
    }
    
    public void addComponent(ImageView aComponent) {
        iComponent = aComponent;
    }
    
    public void setPresentationLUT(String aCM) {
        iPLut.setLUT(aCM);
        invalidateBuffer();
    }
    
    public void registerListeners(Component aO) {       
        aO.addMouseMotionListener(this);
        aO.addMouseListener(this);  
        aO.addMouseWheelListener(this);
    }
  
    public XYSeries makeXYSeries(XYSeries ret) {
        return iVLut.makeXYSeries(ret);
    }   

    private void invalidateBuffer() {iBuf = null;}
    
    private void windowChanged(Window aW) {              
        iVLut.setWindow(aW);        
        iTop.setPosition((int) imageToScreen(aW.getTop()));
        iBottom.setPosition((int) imageToScreen(aW.getBottom())); 
        invalidateBuffer();
        repaint();        
    }   
    
    @Override
    public void windowChanged(WindowChangeEvent anE) {
        if (!iActive) {  
            if (anE.isRangeChanged()) {
                iVLut.getRange().setTop(anE.getMax());
                iVLut.getRange().setBottom(anE.getMin());
            }    
            
            windowChanged(anE.getWindow());                       
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
            final Window win = new Window(iVLut.getWindow());                     

            win.setLevel(win.getLevel() - e.getWheelRotation());                 

            if (iVLut.getRange().contains(win)) {
                if (null != iComponent) 
                    iComponent.setWindow(win);

                windowChanged(win);     
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
            
                final boolean iMoveTop = iTop.contains(ypos);    

                final boolean iMoveBoth = !(iMoveTop || iBottom.contains(ypos)) && iTop.getPosition() > ypos && iBottom.getPosition() < ypos;  

                protected void DoAction(int aX, int aY) {

                    final double delta = screenToImage(aY - iY);
                    final Window win = new Window(iVLut.getWindow());                     

                    if (iMoveTop) {               
                       win.setTop(win.getTop() - delta);
                      // iTop.move(iTop.getPosition() + aX - iX);
                    } else if (iMoveBoth) {
                        win.setLevel(win.getLevel() - delta);
                    } else {   
                      // iBottom.move(iBottom.getPosition() + aX - iX); 
                       win.setBottom(win.getBottom() - delta);
                    }
                    
                    if (iVLut.getRange().contains(win)) {
                        if (null != iComponent) 
                            iComponent.setWindow(win);

                        windowChanged(win);     
                    }
                }   
                protected boolean DoWheel(int aX) {
                    
                    final Window win = new Window(iVLut.getWindow());                     
                    
                    win.setLevel(win.getLevel() - aX);                 
                    
                    if (iVLut.getRange().contains(win)) {
                        if (null != iComponent) 
                            iComponent.setWindow(win);

                        windowChanged(win);     
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
           iActive = true;
        }
    }
   
    public void mouseEntered(MouseEvent e) {        
        iActive = true;
        if (null == iAction) {
            extend();
        }
    }
    
    public void mouseExited(MouseEvent e) {
        if (null == iAction) {
            shrink();
            iActive = false;
        }
    }
    
    public void mouseClicked(MouseEvent e) {    
        if (SwingUtilities.isRightMouseButton(e)) 
            showPopupMenu(e.getX(), e.getY());
       
    }
    

    private void updateBufferedImage() {
        final int width  = getWidth();
        final int height = getHeight() - (TOP_GAP + BOTTOM_GAP);               
        final double wtop = height * (iVLut.getWindow().getTop() / iVLut.getRange().getWidth());
        final double wbot = height * (iVLut.getWindow().getBottom() / iVLut.getRange().getWidth());
        
        final int size = (width * height - 1);
        DataBuffer data = new DataBufferUShort(width * height);
        
        
        
        for (int i = 0; i < height; ++i) {                 
            final short line_value; 
                        
            if (iVLut.isInverted()) {
                if (i >= wtop)
                    line_value = 0;
                else if (i <= wbot)
                    line_value = 255;  
                else             
                    line_value = (short)(NUMBER_OF_SHADES - ((i-wbot)*NUMBER_OF_SHADES/(wtop-wbot)));
                 } else {
                if (i >= wtop)
                    line_value = 255;
                else if (i <= wbot)
                    line_value = 0;  
                else             
                    line_value = (short)((i-wbot)*NUMBER_OF_SHADES/(wtop-wbot));          
            }                     
            
            final int lineNdx =  size - (i * width);
            
            for (int j = 0; j < width; ++j) {               
                data.setElem(lineNdx - j, line_value);            
            }                      
        }
       
        final WritableRaster wr = Raster.createWritableRaster(new ComponentSampleModel(data.getDataType(), width, height, 1, width, new int[] {0}),             
                                                        data, new Point(0,0));        
        
        iBuf = iPLut.transform(new BufferedImage(new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),                                                               
                                                               new int[] {8},
                                                               false,		// has alpha
                                                               false,		// alpha premultipled
                                                               Transparency.OPAQUE,
                                                               data.getDataType()), 
                                                 wr, true, null), null);     
    }
       
    void extend() {
        if (EXTEND_WHEN_FOCUSED) {
            setSize(ACTIVATED_BAR_WIDTH, getHeight());
            setLocation(getLocation().x - (ACTIVATED_BAR_WIDTH - INACTIVE_BAR_WIDTH), getLocation().y);
            getParent().setComponentZOrder(this, 0);

            invalidateBuffer();
            repaint();
        }                
    }
    
    void shrink() {
        setSize(INACTIVE_BAR_WIDTH, getHeight());
        getParent().setComponentZOrder(this, 1);
       
        invalidateBuffer();
        //repaint(); 
        getParent().revalidate();
    }
    
    public void paintComponent(Graphics g) {  
        if (null == iBuf) 
            updateBufferedImage();
        final Color clr = g.getColor();
        
        g.setColor(iVLut.isInverted() ? Color.WHITE : Color.BLACK);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(iBuf, 0, TOP_GAP, getWidth(), getHeight() - (TOP_GAP + BOTTOM_GAP), null);
        //g.drawImage(iBuf, 0, 0, getWidth(), getHeight(), null);
        g.draw3DRect(0, 0, getWidth(), getHeight(), true);
        
        
        
        
        iTop.draw((Graphics2D)g, getWidth(), getHeight());
        iBottom.draw((Graphics2D)g, getWidth(), getHeight());
        
        g.setColor(clr);
    }
    
    double imageToScreen(double aY) {
        final double screenRange = this.getHeight() - (TOP_GAP + BOTTOM_GAP);
        final double imageRange  = iVLut.getRange().getWidth();        
        return aY*screenRange/imageRange;       
    }
    
    double screenToImage(double aY) {
        final double screenRange = this.getHeight() - (TOP_GAP + BOTTOM_GAP);
        final double imageRange  = iVLut.getRange().getWidth();        
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
    

    class Marker  {
        int    iPos;
        Color  iCol;
        String iName;

        Marker(Color aColor, String aName) {
            iCol=aColor; 
            iName=aName;
        }
              
        void setPosition(int aPos) {
            logger.info(iName + ", " + aPos ); //NOI18N
            iPos = aPos;
        } 
        
        int getPosition() {
            return iPos;
        }             
        
        boolean contains(int aVal) {
            final int hh = (null != arrow) ? arrow.getHeight(null) / 2 : 4;
            return aVal < getPosition() + hh && aVal > getPosition() - hh;
        }
            
        void draw(Graphics2D aGC, int aWidth, int aHeight) {  
            aGC.setColor(iCol);       
            
            if (null != arrow) {
                int ypos = aHeight - (TOP_GAP + BOTTOM_GAP) - iPos;// + ((iName == "top") ? TOP_GAP : BOTTOM_GAP);
                aGC.drawImage(arrow, 0, ypos, null);
            } else {
                                       
                aGC.drawLine(HORIZONTAL_BAR_EXCESS, aHeight - iPos, aWidth-HORIZONTAL_BAR_EXCESS, aHeight - iPos);  
            }
            
                
            if (ACTIVATED_BAR_WIDTH == aWidth) {
                final double val = iShowPercent ? screenToImage(iPos) * 100.0 / iVLut.getRange().getWidth() : screenToImage(iPos);
                final String out = String.format("%.0f", Math.abs(val)); //NOI18N
                final int width = (int)(aGC.getFontMetrics().getStringBounds(out, aGC)).getWidth();                 
                aGC.drawString(out, aWidth/2 - width/2, (aHeight - iPos ) + ((iName == "top") ? (aGC.getFontMetrics().getAscent() + arrow.getHeight(null)/2) : - (aGC.getFontMetrics().getDescent()+ arrow.getHeight(null)/2)));
            }
        }         
    }
    
    
    private static final String KCommandTriggerLinear = "COMMAND_LUTCONTROL_TRIGGER_LINEAR"; // NOI18N
    private static final String KCommandTriggerDirect = "COMMAND_LUTCONTROL_TRIGGER_DIRECT"; // NOI18N
    private static final String KCommandShowDialog    = "COMMAND_LUTCONTROL_SHOW_DIALOG"; // NOI18N
    private static final String KCommandChangeLUT     = "COMMAND_LUTCONTROL_CHANGE_LUT"; // NOI18N
    
    
    public void actionPerformed(ActionEvent e) {
        assert (null != iComponent);
        
        logger.info(e.getActionCommand() +  e.paramString()); // NOI18N
        
        switch (e.getActionCommand()) {             
            case KCommandTriggerLinear: 
                iComponent.setLinear(!iComponent.isLinear());
                invalidateBuffer();
                iComponent.repaint();
                repaint();
                break;
            case KCommandTriggerDirect: 
                iComponent.setInverted(!iComponent.isInverted());
                invalidateBuffer();
                iComponent.repaint();
                repaint();
                break;
            case KCommandShowDialog:                 
                VOILUTPanel panel = new VOILUTPanel(this, iComponent.getImage().image());
                JDialog dialog = new JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);
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
                    iComponent.setLUT(cm);
                    invalidateBuffer();
                    iComponent.repaint();
                    repaint();
                } 
                break;          
            default: 
                //setLUT(e.getActionCommand());
                iComponent.setLUT(e.getActionCommand());
                invalidateBuffer();
                iComponent.repaint();
                repaint();
                break;
         }
    }
    
    void showPopupMenu(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("WL_CONTEXT_MENU_TITLE"));        
        JCheckBoxMenuItem mi11 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_LOGARITHMIC"));
        mi11.addActionListener(this);
        mi11.setState(!iVLut.isLinear());
        mi11.setActionCommand(KCommandTriggerLinear);
        
        mnu.add(mi11);

        JCheckBoxMenuItem mi12 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_DIRECT_INVERSE"));
        mi12.addActionListener(this);
        mi12.setActionCommand(KCommandTriggerDirect); 
        mi12.setState(iVLut.isInverted());
        mnu.add(mi12);

        JMenuItem mi13 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.SHOW_DIALOG"));
        mi13.addActionListener(this);
        mi13.setActionCommand(KCommandShowDialog); 
        mnu.add(mi13);
        
        JMenu m1 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.OPEN_BUILTIN_LUT"));
        
        final String [] bl = LutLoader.getBuiltinLUT();
        for (String s:bl) {
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
        
        mnu.show(iComponent, aX, aY);
    }   
                       
    class listener implements ComponentListener {
    
        public void componentResized(ComponentEvent e) {           
            windowChanged(iVLut.getWindow());
        }

        public void componentHidden(ComponentEvent e) {}

        public void componentMoved(ComponentEvent e) {}

        public void componentShown(ComponentEvent e) {
            windowChanged(iVLut.getWindow());
        }
    }
    private static final Logger logger = LogManager.getLogger(LUTControl.class);
}

