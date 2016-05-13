/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;

import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 *
 * @author likhachev
 */
public class CurvePanel extends org.jfree.chart.ChartPanel {        
    class MENUIITEM {
        final String iText;
        final String iCommand;
        MENUIITEM(String aC, String aT) {
            iCommand = aC;
            iText = aT;
        }
    }
    
    static enum MENUS {
        NOP(              "NOP",                          "NOP"),
        ADD(              "MARKER_CMD_MARKER_ADD",        java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MARKER_ADD")),
        DELETE(           "MARKER_CMD_MARKER_DELETE",     java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MARKER_DELETE")),
        DELETE_ALL(       "MARKER_CMD_DELETE_ALL",        java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MARKER_DELETE_ALL")),
        MOVE_TO_MIN(      "MARKER_CMD_MOVE_TO_MIN",       java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN")),
        MOVE_TO_MIN_LEFT( "MARKER_CMD_MOVE_TO_MIN_LEFT",  java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN_LEFT")),
        MOVE_TO_MIN_RIGHT("MARKER_CMD_MOVE_TO_MIN_RIGHT", java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN_RIGHT")),
        MOVE_TO_MAX(      "MARKER_CMD_MOVE_TO_MAX",       java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX")),
        MOVE_TO_MAX_LEFT( "MARKER_CMD_MOVE_TO_MAX_LEFT",  java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX_LEFT")),
        MOVE_TO_MAX_RIGHT("MARKER_CMD_MOVE_TO_MAX_LEFT",  java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX_RIGHT")),
        FIT_LEFT(         "MARKER_CMD_FIT_LEFT",          java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.FIT_LEFT")),
        FIT_RIGHT(        "MARKER_CMD_FIT_RIGHT",         java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.FIT_RIGHT"));
                        
        MENUS(String aC, String aT) {
            iCommand = aC;
            iText = aT;
        }

        final String iText;
        final String iCommand;
        
        JMenuItem makeItem(ActionListener aL) {            
            JMenuItem ret = new JMenuItem(iText);
            ret.setActionCommand(iCommand);
            ret.addActionListener(aL); 
            return ret;
        }
        
        static MENUS translate(String aS) {
            for (MENUS m : MENUS.values())
                if (m.iCommand.equals(aS))
                    return m;
            return MENUS.NOP;
        }
    }
            
    public CurvePanel(JFreeChart aChart) {
        super(aChart);
    }
    
    public void addMarker(DomainMarker aM) {
        getChart().getXYPlot().addDomainMarker(aM, Layer.FOREGROUND);        
        ValueMarker vm = new ValueMarker(.0);
        getChart().getXYPlot().addRangeMarker(vm, Layer.FOREGROUND);
        aM.setLinkedMarker(vm);        
    }
    
    public void removeMarker(DomainMarker aM) {
        getChart().getXYPlot().removeRangeMarker(aM.getLinkedMarker(), Layer.FOREGROUND);
        getChart().getXYPlot().removeDomainMarker(aM, Layer.FOREGROUND);                 
    }
            
    private ChartEntity findEntity(MouseEvent e) {              
        if (null != getChart()) {
            final Insets insets = getInsets();
            final int x = (int) ((e.getX() - insets.left) / this.getScaleX());
            final int y = (int) ((e.getY() - insets.top) / this.getScaleY());

            if (this.getChartRenderingInfo() != null) {
                EntityCollection entities = this.getChartRenderingInfo().getEntityCollection();

                if (entities != null) 
                    return entities.getEntity(x, y);                                                
            }
        }
        return null;
    }
            
    private ValueMarker findMarker(MouseEvent e) {
        final XYPlot plot = getChart().getXYPlot();
        
        java.util.Collection mark = plot.getDomainMarkers(Layer.FOREGROUND);
        
        if (null == mark || mark.isEmpty())
            return null;
        
        final double domainX = plot.getDomainAxis().java2DToValue(e.getX(), 
                                                                  getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                                                                  plot.getDomainAxisEdge());
        
        final double Epsilon = plot.getDataRange(plot.getDomainAxis()).getLength() * .01d;
        
        for (Object o : mark) {
            if (o instanceof DomainMarker) {           
                //DomainMarker m = (DomainMarker)o;
                double val = ((DomainMarker)o).getValue();
                if (val >= domainX - Epsilon && val <= domainX + Epsilon) {
                    //getContentPane().setCursor(new Cursor(Cursor.HAND_CURSOR)); 
                    return (ValueMarker)o;
                }
            }
        }
        return null;
    }  
    
    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        final XYPlot plot = getChart().getXYPlot();
        
        boolean fit_left = false;
         
        switch (MENUS.translate(e.getActionCommand())) {
            case ADD:               
                if (null != iSeries && iSeries instanceof XYSeries) {                    
                    addMarker(new DomainMarker(iDataItem.getXValue(), iSeries));                                                  
                }   break;
            case MOVE_TO_MAX:                                              
                ((DomainMarker)iMarker).moveToMaximum(DomainMarker.MOVETO.GLOBAL); break;    
            case MOVE_TO_MAX_LEFT:    
                ((DomainMarker)iMarker).moveToMaximum(DomainMarker.MOVETO.LEFT); break;
            case MOVE_TO_MAX_RIGHT:                                              
                ((DomainMarker)iMarker).moveToMaximum(DomainMarker.MOVETO.RIGHT); break;            
            case MOVE_TO_MIN:                 
                ((DomainMarker)iMarker).moveToMinimum(DomainMarker.MOVETO.GLOBAL); break;
            case MOVE_TO_MIN_LEFT:                 
                ((DomainMarker)iMarker).moveToMinimum(DomainMarker.MOVETO.LEFT); break;
            case MOVE_TO_MIN_RIGHT:                 
                ((DomainMarker)iMarker).moveToMinimum(DomainMarker.MOVETO.RIGHT); break;
            case DELETE:
                removeMarker((DomainMarker)iMarker);
                break;
            case DELETE_ALL:
                plot.getDomainMarkers(Layer.FOREGROUND).clear();
                break;
                
            case FIT_LEFT:   
                 fit_left = true;
            case FIT_RIGHT: {                      
                double x1 = iMarker.getValue();                
                List<DomainMarker> list = new ArrayList<>( plot.getDomainMarkers(Layer.FOREGROUND) );

                if (list.size() < 2) {
                    //TODO: message box 2 markers are necessary
                    return;                    
                }    
                
                Collections.sort(list, (DomainMarker o1, DomainMarker o2) -> {
                    if (o1.equals(o2))
                        return 0;
                    if (o1.getValue() > o2.getValue())
                        return 1;
                    else
                        return -1;
                });                
                
                DomainMarker mark2 = null;
                
                for(int i = 0; i < list.size(); ++i) {
                    if (list.get(i) == iMarker) {   
                        int ndx = fit_left ? i-1 : i+1;
                        if (ndx >= 0 && ndx < list.size())                            
                            mark2 = list.get(ndx);
                        
                        break;
                    }                        
                }
                
                if (null != mark2) {
                    logger.info(String.format("Marker found: %f", mark2.getValue()));                
                    if (null == iPol)
                        iPol = new ArrayList<>();
                    iPol.add(new Interpolation((DomainMarker)iMarker, mark2));    
                } else
                    return;
   
            } break;                                
            default:
                break;
        }
        
        dropSelection();        
    }
    
    List<Interpolation> iPol = null;
    static int iId = 0;
    
    final class Interpolation implements MarkerChangeListener {
        DomainMarker iLhs; 
        DomainMarker iRhs;
        XYSeries   iSrc;        
        
        Interpolation(DomainMarker aLhs, DomainMarker aRhs) {
            iLhs = aLhs; 
            iRhs = aRhs;
            iSrc = new XYSeries(String.format("INTERPOLATION%d", iId++));
            fillIn();
            ((XYSeriesCollection)(getChart().getXYPlot().getDataset())).addSeries(iSrc);
            aLhs.addChangeListener(this);
            aRhs.addChangeListener(this);
        }
        
        void update() {            
            iSrc.clear();
            fillIn();
        }
        
        void fillIn() {
            iSrc.add(iLhs.getValue(), iLhs.getLinkedMarker().getValue());
            iSrc.add((iLhs.getValue() + iRhs.getValue())/2.0, (iLhs.getLinkedMarker().getValue() + iRhs.getLinkedMarker().getValue()) /2.0);
            iSrc.add(iRhs.getValue(), iRhs.getLinkedMarker().getValue());
        }
        
        public void markerChanged(MarkerChangeEvent mce) {
            update();
        }
    }
            
    public void mouseMoved(MouseEvent e) {
        super.mouseMoved(e);

        if (null == iEntity && null == iMarker) {                    
            ChartEntity entity = findEntity(e);
            if (entity instanceof XYItemEntity) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));                 
            } else if (entity instanceof PlotEntity) {

                if (null != findMarker(e))
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                else
                    setCursor(Cursor.getDefaultCursor());
            } else {
                setCursor(Cursor.getDefaultCursor());
            }
        }
    }              
            
    @Override
    public void mousePressed(MouseEvent e) {  
        ChartEntity entity = findEntity(e);

        if (entity instanceof XYItemEntity) {
            selectItem(entity);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else if (null != (iMarker = findMarker(e))) {                
            if (SwingUtilities.isLeftMouseButton(e))                 
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));                           
        } else
            super.mousePressed(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {        
        if (SwingUtilities.isRightMouseButton(e) && (iMarker instanceof DomainMarker || iSeries instanceof XYSeries)) {
            JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MNU_MARKER_OPERATIONS"));             
            if (iSeries instanceof XYSeries) {               
                mnu.add(MENUS.ADD.makeItem(this));                    
                mnu.add(MENUS.DELETE_ALL.makeItem(this));                         
            } else if (iMarker instanceof DomainMarker)  {                
                JMenu mi1 = new JMenu(MENUS.MOVE_TO_MIN.iText);                             
                mi1.add(MENUS.MOVE_TO_MIN.makeItem(this));
                mi1.add(MENUS.MOVE_TO_MIN_LEFT.makeItem(this));
                mi1.add(MENUS.MOVE_TO_MIN_RIGHT.makeItem(this));
                mnu.add(mi1);
                JMenu mi2 = new JMenu(MENUS.MOVE_TO_MAX.iText);
                mi2.add(MENUS.MOVE_TO_MAX.makeItem(this));
                mi2.add(MENUS.MOVE_TO_MAX_LEFT.makeItem(this));
                mi2.add(MENUS.MOVE_TO_MAX_RIGHT.makeItem(this));
                mnu.add(mi2); 
                JMenu mi3 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/controls/Bundle").getString("MARKER_COMMAND.FIT"));                
                mi3.add(MENUS.FIT_LEFT.makeItem(this));
                mi3.add(MENUS.FIT_RIGHT.makeItem(this));
                mnu.add(mi3);                 
                mnu.add(MENUS.DELETE.makeItem(this));                             
            }
            
            mnu.show(this, e.getX(), e.getY());
        } else {
            super.mouseReleased(e);
            dropSelection();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {        
        final XYPlot plot = getChart().getXYPlot();
        
        if (null != iEntity) {                   
            moveTo(plot.getRangeAxis().java2DToValue(e.getY(), 
                    getChartRenderingInfo().getPlotInfo().getDataArea(),                             
                        plot.getRangeAxisEdge()));
        }  
        if (null != iMarker) {
            iMarker.setValue(plot.getDomainAxis().java2DToValue(e.getX(), 
                            getChartRenderingInfo().getPlotInfo().getDataArea(),                                                                                                                 
                                plot.getDomainAxisEdge()));      
        }
        else
            super.mouseDragged(e);
    }
  
    void dropSelection() {
        iEntity = null;
        iSeries = null;
        iDataItem = null;
        iMarker = null;  
        setCursor(Cursor.getDefaultCursor());
    }
    
    private ChartEntity iEntity = null;
    private XYSeries    iSeries  = null;
    private XYDataItem  iDataItem  = null;
    private ValueMarker iMarker  = null;
   
    protected void selectItem(ChartEntity anEntity) {
        iEntity = anEntity;
        iSeries = ((XYSeriesCollection)((XYItemEntity)iEntity).getDataset()).getSeries(((XYItemEntity)iEntity).getSeriesIndex());        
        iDataItem = iSeries.getDataItem(((XYItemEntity)iEntity).getItem());  
    }
            
    protected void moveTo(double aNewY) {        
        iDataItem.setY(aNewY);
        iSeries.delete(iSeries.indexOf(iDataItem.getX()), iSeries.indexOf(iDataItem.getX()));
        iSeries.add(iDataItem);         
    }   
    
    
    private static final Logger logger = LogManager.getLogger(CurvePanel.class);
}
