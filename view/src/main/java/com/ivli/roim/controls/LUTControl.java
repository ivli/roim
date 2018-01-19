/*
 * Copyright (C) 2016 likhachev
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

package com.ivli.roim.controls;

import java.awt.Color;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Dialog;
import java.awt.Cursor;
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
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import com.ivli.roim.view.ActionItem;
import com.ivli.roim.io.LutReader;
import com.ivli.roim.view.Settings;
import com.ivli.roim.core.Window;
import com.ivli.roim.events.FrameChangeEvent;
import com.ivli.roim.events.FrameChangeListener;
import com.ivli.roim.events.WindowChangeEvent;
import com.ivli.roim.events.WindowChangeListener;
import com.ivli.roim.view.IImageView;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.awt.image.BufferedImageOp;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

/**
 *
 * @author likhachev
 */
public class LUTControl extends JComponent implements WindowChangeListener, FrameChangeListener, ActionListener, 
                                                          MouseMotionListener, MouseListener, MouseWheelListener {    
    
    private static final int NUMBER_OF_GREYS = 255;    
    private final int BAR_WIDTH = 32; 
    private final int LEFT_GAP  = 2; //reserve border at left & right
    private final int RIGHT_GAP = 2;   
        
    private static final int MARKER_CURSOR = Cursor.HAND_CURSOR;    
    private static final int WINDOW_CURSOR = Cursor.N_RESIZE_CURSOR;                           
   
    protected final EventListenerList iListeners;
    
    private boolean iShowToolTips       = false;
    private boolean iToolTipsInPercents = true;
            
    private Marker iTop, iBottom;
    private ActionItem   iAction;
    private BufferedImage iBuf;
        
    private IImageView iView; 
        
   /// protected Window iView.getWindow() = new Window(255./2., 255.);
        
    protected void setMarkerPositions() {                       
        iTop.setPosition((int) imageToScreen(iView.getWindow().getTop()));
        iBottom.setPosition((int) imageToScreen(iView.getWindow().getBottom())); 
        repaint();       
    }
    
    private LUTControl() {        
        iListeners = new EventListenerList();
        iTop       = new Marker("images/knob_horz.png", Marker.Orientation.VERTICAL); //NOI18N
        iBottom    = new Marker("images/knob_horz.png", Marker.Orientation.VERTICAL); //NOI18N                                    
    }
        
    public static LUTControl create() {
        LUTControl ret = new LUTControl();
        ret.construct();  
                
        return ret;
    }
          
    public LUTControl attach(IImageView aV) {
        if (null != iView){
            iView.removeListenerFromAllLists(this);
        }
        
        iView = aV;
        ///iView.getWindow() = aV.getWindow();
        
        iView.addFrameChangeListener(this);
        iView.addWindowChangeListener(this);  
        return this;
    } 
        
    private void construct() {                    
        addComponentListener(new ComponentListener() {    
            public void componentResized(ComponentEvent e) {                                
                iTop.setPosition((int) imageToScreen(iView.getWindow().getTop()));
                iBottom.setPosition((int) imageToScreen(iView.getWindow().getBottom())); 
                makeBuffer();
                invalidateBuffer();
                repaint();
            }                                               
            public void componentHidden(ComponentEvent e) {}                         
            public void componentMoved(ComponentEvent e) {}
            public void componentShown(ComponentEvent e) {
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
  
    protected void notifyWindowChange() {
        WindowChangeEvent evt = new WindowChangeEvent(this, iView.getWindow());
        
        for (WindowChangeListener l : iListeners.getListeners(WindowChangeListener.class))            
            l.windowChanged(evt);            
    }
           
    @Override
    public void windowChanged(WindowChangeEvent anE) {           
//        iView.getWindow() = anE.getWindow();
        iTop.setPosition((int) imageToScreen(iView.getWindow().getTop()));
        iBottom.setPosition((int) imageToScreen(iView.getWindow().getBottom()));                       
        
        invalidateBuffer();
        repaint();               
    }   
    
    @Override
    public void frameChanged(FrameChangeEvent anE) {       
///        iView.getWindow() = iView.getWindow();
        invalidateBuffer();
        makeBuffer();         
    }   

    @Override
    public void mouseDragged(MouseEvent e) {
        if (null != iAction) {
            iAction.action(e.getX(), e.getY());           
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (null != iAction)
            iAction.wheel(e.getWheelRotation());
        else {
            //final Window win = new Window(iView.getWindow());                     
            iView.getWindow().setLevel(iView.getWindow().getLevel() - e.getWheelRotation());    
            setMarkerPositions();
            //if (null != iView)
            //    iView.setWindow(iView.getWindow());
            notifyWindowChange();
        }            
    } 
    
    @Override
    public void mouseMoved(MouseEvent e) {
        final int ypos = getHeight() - e.getPoint().y;

        if (iTop.contains(ypos) || iBottom.contains(ypos)) {
            setCursor(java.awt.Cursor.getPredefinedCursor(MARKER_CURSOR));
        }
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
                    //Window win = iView.getWindow();                     

                    if (iMoveTop) {               
                        iView.getWindow().setTop(iView.getWindow().getTop() - delta);                        
                        updateMarkerAnnotation(new Point(aX, aY), iView.getWindow().getTop());
                    } else if (iMoveBoth) {
                        iView.getWindow().setLevel(iView.getWindow().getLevel() - delta);
                        updateMarkerAnnotation(new Point(aX, aY), iView.getWindow().getWidth());
                    } else {                         
                        iView.getWindow().setBottom(iView.getWindow().getBottom() - delta);
                        updateMarkerAnnotation(new Point(aX, aY), iView.getWindow().getBottom());
                    }
                    //iView.getWindow() = win;
                    setMarkerPositions();
                    if (null != iView)
                        iView.setWindow(iView.getWindow());
                    notifyWindowChange();
                }   

                @Override
                protected boolean DoWheel(int aX) {                                       

                    iView.getWindow().setLevel(iView.getWindow().getLevel() - aX);    
                    setMarkerPositions();
                    if (null != iView)
                        iView.setWindow(iView.getWindow());
                    notifyWindowChange();
                    
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

    @Override
    public void mouseReleased(MouseEvent e) {                    
        iAction = null;  
        if (!getBounds().contains(e.getPoint())){
           repaint();
        }
    }
    
    private Popup popup = null;
    private JToolTip iTip = new JToolTip();
    //TOD: aWhat's value means: 1 - top, 2 - bottom, 3 - both :-))) tooooo stupid
    private void updateMarkerAnnotation(Point aPosition, double aVal) {
        if (null != popup) {
            popup.hide();
            popup = null;
        }
        
        if (iShowToolTips && iActive && null != aPosition) {
          /**
            String text = !iToolTipsInPercents ? String.format("%.0f", aVal): //NOI18N
                String.format("%.0f%%", aVal / (iView.getMax() - iView.getMin()) * 100.); //NOI18N
          **/  

            String text = String.format("%.0f", aVal);
            iTip.setTipText(text);
            PopupFactory popupFactory = PopupFactory.getSharedInstance();
            Point pt = new Point(getLocationOnScreen());            
            
            SwingUtilities.convertPointToScreen(aPosition, this);
            int x = pt.x;
            int y = aPosition.y - iTip.getHeight();
            popup = popupFactory.getPopup(this, iTip, x, y);
            popup.show();
        }
    }
    
    private boolean iActive = false;
    
    @Override
    public void mouseEntered(MouseEvent e) {        
        iActive = true;
        //updateMarkerAnnotation();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        iActive = false;
        updateMarkerAnnotation(null, .0);
    }

    @Override
    public void mouseClicked(MouseEvent e) {    
        if (SwingUtilities.isRightMouseButton(e)) 
            showPopupMenu(e.getX(), e.getY());
        else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {                                           
            setMarkerPositions();
            if (null != iView)
                iView.setWindow(iView.getWindow());
            notifyWindowChange();
        }
    }

    private BufferedImage iBackFrame;
    
    private void makeBuffer() {
        final int width  = getWidth() - (LEFT_GAP + RIGHT_GAP);
        final int height = getHeight() - (iTop.getMarkerSize() + iBottom.getMarkerSize())/2;               
        
        final double r = ratio() / (double)height;
       
        iBackFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
   
        for (int j = 0; j < height; ++j) { 
            final int pixel = (int)((height - j) * r);
            for (int i = 0; i < width; ++i)                   
                iBackFrame.setRGB(i, j, pixel);                     
        }
    }
        
    private void updateBufferedImage() {
//       BufferedImageOp op = new BufferedImageOp(iView.getTransform());
       iBuf = iView.getTransform().transform(iBackFrame, null);                                                                                                                                                                                                 
    }
      
    public void paintComponent(Graphics g) {          
        if (null == iBuf && null != iBackFrame)  
            updateBufferedImage();
           
        final Color clr = g.getColor();
        
        g.setColor(Color.LIGHT_GRAY);
        
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(iBuf, LEFT_GAP, iTop.getMarkerSize()/2, getWidth() - (LEFT_GAP + RIGHT_GAP), getHeight() - (iTop.getMarkerSize() + iBottom.getMarkerSize())/2, null);      
        
        g.draw3DRect(0, 0, getWidth(), getHeight(), true);       
        
        iTop.draw(g, getBounds());
        iBottom.draw(g, getBounds());
        
        
        g.setColor(clr);
    }
     
    private void invalidateBuffer() {        
        iBuf = null;
    }       
  
    double ratio() {
        if (null == iView) 
            return (double)NUMBER_OF_GREYS;
        else
            return (double)(iView.getMax() - iView.getMin());
    }
    
    private double imageToScreen(double aY) {            
        return aY / (ratio() / (getHeight() - (iTop.getMarkerSize() + iBottom.getMarkerSize())/2.0));       
    }
    
    private double screenToImage(double aY) {             
        return aY * (ratio() / (getHeight() - (iTop.getMarkerSize() + iBottom.getMarkerSize())/2.0));  
    }       
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(BAR_WIDTH, NUMBER_OF_GREYS + ((iTop.getMarkerSize() + iBottom.getMarkerSize())/2));
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(BAR_WIDTH, NUMBER_OF_GREYS + ((iTop.getMarkerSize() + iBottom.getMarkerSize())/2));
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(BAR_WIDTH, Short.MAX_VALUE);
    }
   
    private static final String KCOMMAND_TRIGGER_LINEAR = "LUTCONTROL.KCOMMAND_TRIGGER_LINEAR"; //NOI18N
    private static final String KCOMMAND_TRIGGER_DIRECT = "LUTCONTROL.KCOMMAND_TRIGGER_DIRECT"; //NOI18N
    private static final String KCOMMAND_SHOW_DIALOG    = "LUTCONTROL.KCOMMAND_SHOW_DIALOG";    //NOI18N
    private static final String KCOMMAND_CHANGE_LUT     = "LUTCONTROL.KCOMMAND_CHANGE_LUT";     //NOI18N    
    private static final String KCOMMAND_ANNOTATE_MARKER = "LUTCONTROL.KCOMMAND_ANNOTATE_MARKER"; //NOI18N 
    private static final String KCOMMAND_ANNOTATE_PERCENTS = "LUTCONTROL.KCOMMAND_ANNOTATE_PERCENTS"; //NOI18N 
    
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {    
            case KCOMMAND_ANNOTATE_MARKER: 
                iShowToolTips = !iShowToolTips;
                break;
            case KCOMMAND_ANNOTATE_PERCENTS: 
                toolTipsInPercents(!iToolTipsInPercents);
                break;
            case KCOMMAND_TRIGGER_LINEAR: 
                iView.setLinear(!iView.isLinear());
                invalidateBuffer();
                //iView.repaint();
                repaint();
                break;
            case KCOMMAND_TRIGGER_DIRECT: 
                iView.setInverted(!iView.isInverted());
                invalidateBuffer();
                ///iView.repaint();
                repaint();
                break;
            case KCOMMAND_SHOW_DIALOG:    /**    TODO: refactor the dialog     **/                     
                VOILUTPanel panel = new VOILUTPanel(this);
                
                javax.swing.JDialog dialog = new javax.swing.JDialog(null, Dialog.ModalityType.APPLICATION_MODAL);
                dialog.setContentPane(panel);
                dialog.validate();
                dialog.pack();
                dialog.setResizable(false);
                dialog.setVisible(true);                 
                break;
            case KCOMMAND_CHANGE_LUT:
                FileOpenDialog fd = new FileOpenDialog(null,
                    java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.CHOOSE_LUT_FILE"),
                    Settings.get(Settings.KEY_FILE_SUFFIX_LUT, Settings.DEFAULT_FILE_SUFFIX_LUT),
                    "",
                    Settings.get(Settings.KEY_DEFAULT_FOLDER_LUT, System.getProperty("user.home")),
                    true
                );
              
                if (fd.doModal()) {
                    final String lutFile = fd.getFileName();
                    Settings.set(Settings.KEY_LASTFILE_LUT, lutFile);
                    iView.setLUT(lutFile);
                    invalidateBuffer();
                    repaint();
                } 
                break;          
            default:                 
                invalidateBuffer(); 
                iView.setLUT(e.getActionCommand());
                invalidateBuffer();                
                repaint();
                break;
         }
    }
    
    void showPopupMenu(int aX, int aY) {
        final JPopupMenu mnu = new JPopupMenu("WL_CONTEXT_MENU_TITLE"); //NOI18N    
        JCheckBoxMenuItem mi11 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_LOGARITHMIC"));
        mi11.addActionListener(this);
       //TODO!!! mi11.setState(!iView.isLinear());
        mi11.setActionCommand(KCOMMAND_TRIGGER_LINEAR);
        
        mnu.add(mi11);

        JCheckBoxMenuItem mi12 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.TRIGGER_DIRECT_INVERSE"));
        mi12.addActionListener(this);
        mi12.setActionCommand(KCOMMAND_TRIGGER_DIRECT); 
       //TODO!!! mi12.setState(iView.isInverted());
        mnu.add(mi12);
        
        /*
        if (iCanHaveChildren) {
            JMenuItem mi13 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.SHOW_DIALOG"));
            mi13.addActionListener(this);
            mi13.setActionCommand(KCOMMAND_SHOW_DIALOG); 
            mnu.add(mi13);
        }
        */
        
        JMenu m1 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.OPEN_BUILTIN_LUT"));
 
        for (String s : LutReader.getInstalledLUT()) {            
            IndexColorModel icm = LutReader.open(s);
            BufferedImage buf = new BufferedImage(256, 16, TYPE_INT_RGB);
            WritableRaster r = buf.getRaster();
            
            for (int i = 0; i < 255; ++i) {
                int[] components = {0, 0, 0};
                for(int j = 0; j < 15; ++j)
                    r.setPixel(i, j, icm.getComponents(i, components, 0));
            }
            
            JMenuItem mit = new JMenuItem(s, new ImageIcon(buf));            
            mit.addActionListener(this);
            mit.setActionCommand(s); 
            m1.add(mit);
        }   
        
        mnu.add(m1);
        
        JMenuItem mi14 = new JMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.CHOOSE_LUT_FILE"));
        mi14.addActionListener(this);
        mi14.setActionCommand(KCOMMAND_CHANGE_LUT); 
        mnu.add(mi14);
                
        JCheckBoxMenuItem mi15 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.KCOMMAND_ANNOTATE_MARKER"));
        mi15.addActionListener(this);
        mi15.setActionCommand(KCOMMAND_ANNOTATE_MARKER); 
        mi15.setState(iShowToolTips);
        mnu.add(mi15);
        
        JCheckBoxMenuItem mi16 = new JCheckBoxMenuItem(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("LUT_MENU.KCOMMAND_ANNOTATE_PERCENTS"));
        mi16.addActionListener(this);
        mi16.setActionCommand(KCOMMAND_ANNOTATE_PERCENTS); 
        mi16.setState(iToolTipsInPercents);
        mnu.add(mi16);
        
        mnu.show(this, aX, aY);
    }   

    /**
     * @param iShowToolTips the iShowToolTips to set
     */
    public void enableToolTips(boolean iShowToolTips) {
        this.iShowToolTips = iShowToolTips;
    }

    /**
     * @param iTooleTipsInPercents the iTooleTipsInPercents to set
     */
    public void toolTipsInPercents(boolean iToolTipsInPercents) {
        this.iToolTipsInPercents = iToolTipsInPercents;
    }
    
    /*  */   
    public void addWindowChangeListener(WindowChangeListener aL) {
         iListeners.add(WindowChangeListener.class, aL);  
    }
    
    public void removeWindowChangeListener(WindowChangeListener aL) {
        iListeners.remove(WindowChangeListener.class, aL);
    }
   
    
    private static final org.apache.logging.log4j.Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
}

