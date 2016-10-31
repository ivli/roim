/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ivli.roim.controls;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.event.MarkerChangeListener;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.general.SeriesChangeListener;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 *
 * @author likhachev
 */
public class ChartControl extends ChartPanel {            
    private static enum MENUS {
        NOP(              "NOP",                          "NOP"),
        ADD(              "MARKER_CMD_MARKER_ADD",        java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MARKER_ADD")),
        EXPORT_CSV(       "MARKER_CMD_EXPORT_CSV",        java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MARKER_EXPORT_CSV")),
        DELETE(           "MARKER_CMD_MARKER_DELETE",     java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MARKER_DELETE")),
        DELETE_ALL(       "MARKER_CMD_DELETE_ALL",        java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MARKER_DELETE_ALL")),
        MOVE_TO_MIN(      "MARKER_CMD_MOVE_TO_MIN",       java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN")),
       // MOVE_TO_MIN_LEFT( "MARKER_CMD_MOVE_TO_MIN_LEFT",  java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN_LEFT")),
       // MOVE_TO_MIN_RIGHT("MARKER_CMD_MOVE_TO_MIN_RIGHT", java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MIN_RIGHT")),
        MOVE_TO_MAX(      "MARKER_CMD_MOVE_TO_MAX",       java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX")),
       // MOVE_TO_MAX_LEFT( "MARKER_CMD_MOVE_TO_MAX_LEFT",  java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX_LEFT")),
       // MOVE_TO_MAX_RIGHT("MARKER_CMD_MOVE_TO_MAX_LEFT",  java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MAX_RIGHT")),
        MOVE_TO_MEDIAN(   "MARKER_CMD_MOVE_TO_MEDIAN",    java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.MOVE_TO_MEDIAN")),
        FIT_LEFT(         "MARKER_CMD_FIT_LEFT",          java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.FIT_LEFT")),
        FIT_RIGHT(        "MARKER_CMD_FIT_RIGHT",         java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.FIT_RIGHT"));
                        
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
    
    //private XYSeriesCollection iInterSet = new XYSeriesCollection(); 
    ///////////////////////////////////////  
    ChartControl(JFreeChart aChart) {
        super(aChart);        
        //aChart.getXYPlot().setDataset(INTERPOLATION_DATASET, iInterSet);        
    }
    
    void addMarker(DomainMarker aM) {
        getChart().getXYPlot().addDomainMarker(aM, Layer.FOREGROUND);        
        ValueMarker vm = new ValueMarker(.0);
        getChart().getXYPlot().addRangeMarker(vm, Layer.FOREGROUND);
        aM.setLinkedMarker(vm);        
    }
    
    void removeMarker(DomainMarker aM) {        
        ListIterator<Interpolation> it = iInterpolations.listIterator();
        
        while(it.hasNext()) {
            Interpolation i = it.next();
            if(i.iLhs == aM || i.iRhs == aM) {                
                it.remove();
                i.close();               
            }            
        }
                       
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
  
    Collection getDomainMarkersForSeries(XYSeries aS) {
        final XYPlot plot = getChart().getXYPlot();
        Collection mrak = plot.getDomainMarkers(Layer.FOREGROUND);
        return (new ArrayList<DomainMarker>(mrak)).stream()
                                                  .filter((DomainMarker m) -> {return (m.getSeries() == aS);})
                                                  .collect(Collectors.toList());                                                      
    }
    
    private ValueMarker findMarker(MouseEvent e) {
        final XYPlot plot = getChart().getXYPlot();
        
        Collection mark = plot.getDomainMarkers(Layer.FOREGROUND);
        
        if (null == mark || mark.isEmpty())
            return null;
          
        final double domainX = plot.getDomainAxis().java2DToValue(e.getX(), getScreenDataArea(), plot.getDomainAxisEdge());                            
       
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
       
        switch (MENUS.translate(e.getActionCommand())) {
            case ADD:               
                if (null != iSeries && iSeries instanceof XYSeries) {                    
                    addMarker(new DomainMarker(iDataItem.getXValue(), iSeries));                                                  
                } break;
            case EXPORT_CSV:               
                if (null != iSeries && iSeries instanceof XYSeries) { 
                    FileOpenDialog dlg = new FileOpenDialog(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("CHOICE_FILE_TO_OPEN"), 
                            "csv", //NOI18N 
                            "CSV file", //NOI18N 
                            false);
                    
                    if (dlg.DoModal()) {                        
                        try (Writer pwr = new PrintWriter(dlg.getFileName())) { //NOI18N                              
                            for (int i = 0; i < iSeries.getItemCount(); ++i) {
                                XYDataItem xy = iSeries.getDataItem(i);
                                pwr.append(String.format("%f\t%f\n", xy.getXValue(), xy.getYValue())); //NOI18N                        
                            }
                            pwr.flush(); 
                            pwr.close();
                        } catch (IOException ex) {
                            LOG.throwing(ex);
                        } 
                    }
                } break;
            case MOVE_TO_MAX:                                              
                iMarker.setValue(XYSeriesUtilities.getDomainValueOfMaximum(((DomainMarker)iMarker).getSeries())); break;//((DomainMarker)iMarker).moveToMaximum(DomainMarker.MOVETO.GLOBAL)); break;                           
            case MOVE_TO_MIN:                 
                iMarker.setValue(XYSeriesUtilities.getDomainValueOfMinimum(((DomainMarker)iMarker).getSeries())); break;            
            case MOVE_TO_MEDIAN:  {               
                iMarker.setValue(XYSeriesUtilities.getDomainValueOfMaximum(((DomainMarker)iMarker).getSeries())); 
                double medY = (iSeries.getMaxY() - iSeries.getMinY()) / 2.;
                double val = XYSeriesUtilities.getNearestX(iSeries, medY);

                if (Double.isFinite(val))
                    iMarker.setValue(val);
                }; break;                
            case DELETE:
                removeMarker((DomainMarker)iMarker);
                break;
            case DELETE_ALL:
                plot.getDomainMarkers(Layer.FOREGROUND).clear();
                iInterpolations.stream().forEach((i) -> {i.close();});           
                iInterpolations.clear();
                break;                
            case FIT_LEFT:                   
            case FIT_RIGHT: {                   
                final Collection mrak = plot.getDomainMarkers(Layer.FOREGROUND);
                final FITDIR dir = MENUS.FIT_LEFT == MENUS.translate(e.getActionCommand())? FITDIR.FIT_WEST:FITDIR.FIT_EAST;
                
                if (mrak.isEmpty()) {                  
                    return;                    
                } else if (mrak.size() == 1) { // Fine, got just one Marker interpolate till the last/first point                     
                    iInterpolations.add(new Interpolation((DomainMarker)mrak.iterator().next(), dir));                              
                } else {// got more than 1 markers thus range them and find east/west adjacent one to interpolate inbetween    
                    List<DomainMarker> list = 
                        (new ArrayList<DomainMarker>(mrak)).stream()
                                .sorted((DomainMarker aLhs, DomainMarker aRhs) -> {                                                
                                    return (int)(aLhs.getValue() - aRhs.getValue());})                                                                                            
                                .collect(Collectors.toList());                
                    
                    int ndx = list.indexOf(iMarker);  
                    
                    if (ndx == 0 && dir == FITDIR.FIT_WEST || ndx == list.size() - 1 && dir == FITDIR.FIT_EAST)
                        iInterpolations.add(new Interpolation((DomainMarker)iMarker, dir));
                        
                    
                    DomainMarker mark2 = list.get(dir == FITDIR.FIT_WEST ? --ndx : ++ndx);                        
                    
                    if (null != mark2)                                                      
                        iInterpolations.add(createInterpolation((DomainMarker)iMarker, mark2));    
                    else                                                             
                        iInterpolations.add(createInterpolation((DomainMarker)iMarker, null));
                }
            } break;                                
            default:
                break;
        }
        
        dropSelection();        
    }
    
    List<Interpolation> iInterpolations = new ArrayList<>();
    static int iInterpolationID = 0;
    
    static enum FITDIR {
            FIT_WEST,
            FIT_EAST,            
            FIT_RANGE
    }
        
    Interpolation createInterpolation(DomainMarker aLhs, DomainMarker aRhs) {
            Interpolation ret = new Interpolation(aLhs, aRhs);
            ret.iLhs = aLhs;
            ret.iRhs = aRhs;
         
            final double finval = aLhs.getSeries().getDataItem(aLhs.getSeries().getItemCount() - 1).getXValue();
                        
            ret.iSeries = new XYSeries(String.format(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INTERPOLATION%D"), iInterpolationID++));
            
            aLhs.getSeries().addChangeListener(ret);
                    
            XYSeriesUtilities.fit(aLhs.getSeries(), aLhs.getValue(), finval, true, ret.iSeries);
            
            
             getChart().getXYPlot().getRenderer().setSeriesPaint(getChart().getXYPlot().getDataset().indexOf(ret.iSeries.getKey()), 
                getChart().getXYPlot().getRenderer().getSeriesPaint(getChart().getXYPlot().getDataset().indexOf(aLhs.getSeries().getKey())));
                        
            getChart().getXYPlot().getRenderer().setSeriesStroke(getChart().getXYPlot().getDataset().indexOf(ret.iSeries.getKey()), INTERPOLATION_STROKE);
            
            ret.iLhs.addChangeListener(ret); 
            ret.iLhs.getSeries().addChangeListener(ret);
            
            if (null != aRhs) 
                ret.iRhs.addChangeListener(ret);
            
            return ret;
        }
    
    private final static BasicStroke INTERPOLATION_STROKE = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
    
    final class Interpolation implements MarkerChangeListener, SeriesChangeListener, AutoCloseable {
        DomainMarker iLhs; 
        DomainMarker iRhs;        
        XYSeries iSeries;                
           
        Interpolation(DomainMarker aLhs, DomainMarker aRhs, XYSeries aSeries) {
            iLhs = aLhs;
            iRhs = aRhs;
            iSeries = aSeries;
        }
        
        Interpolation(DomainMarker aLhs, FITDIR aDir) {           
            iLhs = aLhs; 
            iRhs = null;
                        
            final double finval = iLhs.getSeries().getDataItem(iLhs.getSeries().getItemCount() - 1).getXValue();
                        
            iSeries = new XYSeries(String.format(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INTERPOLATION%D"), iInterpolationID++));
            
            iLhs.getSeries().addChangeListener(this);
                    
            XYSeriesUtilities.fit(iLhs.getSeries(), iLhs.getValue(), finval, true, iSeries);
                                   
            ((XYSeriesCollection)getChart().getXYPlot().getDataset()).addSeries(iSeries);
              
            int ndx = getChart().getXYPlot().getDataset().indexOf(iLhs.getSeries().getKey());
            Paint p = getChart().getXYPlot().getRenderer().getSeriesPaint(ndx);
            
            getChart().getXYPlot().getRenderer().setSeriesPaint(getChart().getXYPlot().getDataset().indexOf(iSeries.getKey()), 
                    getChart().getXYPlot().getRenderer().getSeriesPaint(getChart().getXYPlot().getDataset().indexOf(iLhs.getSeries().getKey())));
          
            getChart().getXYPlot().getRenderer().setSeriesStroke(getChart().getXYPlot().getDataset().indexOf(iSeries.getKey()),
                    INTERPOLATION_STROKE);
           
            aLhs.addChangeListener(this);              
        }
                
        Interpolation(DomainMarker aLhs, DomainMarker aRhs) {
            iLhs = aLhs; 
            iRhs = aRhs;
            
            iSeries = new XYSeries(String.format(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("INTERPOLATION%D"), iInterpolationID++));            
                    
            XYSeriesUtilities.fit(iLhs.getSeries(), iLhs.getValue(), iRhs.getValue(), true, iSeries);
           
            ((XYSeriesCollection)getChart().getXYPlot().getDataset()).addSeries(iSeries);
           
            getChart().getXYPlot().getRenderer().setSeriesPaint(getChart().getXYPlot().getDataset().indexOf(iSeries.getKey()), 
                getChart().getXYPlot().getRenderer().getSeriesPaint(getChart().getXYPlot().getDataset().indexOf(iLhs.getSeries().getKey())));
                        
            getChart().getXYPlot().getRenderer().setSeriesStroke(getChart().getXYPlot().getDataset().indexOf(iSeries.getKey()), INTERPOLATION_STROKE);
            
            iLhs.getSeries().addChangeListener(this);
            aLhs.addChangeListener(this);
            aRhs.addChangeListener(this);             
        }             
                        
        public void close() {
            iLhs.removeChangeListener(this);
            if (iRhs != null)
                iRhs.removeChangeListener(this);   
            iSeries.clear();
        }
                
        public void markerChanged(MarkerChangeEvent mce) {
            iSeries.clear();        
            double left;
            double right;
            
            if (mce.getMarker() == iLhs){
                left = ((DomainMarker)mce.getMarker()).getValue();
                right = (null != iRhs) ? iRhs.getValue() : iLhs.getSeries().getDataItem(iLhs.getSeries().getItemCount() - 1).getXValue();
            } else if (mce.getMarker() == iRhs) {
                left = iLhs.getValue();
                right = ((DomainMarker)mce.getMarker()).getValue();
            } else 
                throw new IllegalArgumentException();
            
            iSeries = XYSeriesUtilities.fit(iLhs.getSeries(), left, right, true, iSeries);              
        }
        
        public void seriesChanged(SeriesChangeEvent sce) {           
            // it seems there's nothing to get done since marker change would fire an update of interpolation 
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
            JPopupMenu mnu = new JPopupMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MNU_MARKER_OPERATIONS"));             
            if (iSeries instanceof XYSeries) {               
                mnu.add(MENUS.ADD.makeItem(this)); 
                mnu.add(MENUS.EXPORT_CSV.makeItem(this));
                mnu.add(MENUS.DELETE_ALL.makeItem(this));                 
            } else if (iMarker instanceof DomainMarker)  {                
                JMenu mi1 = new JMenu(MENUS.MOVE_TO_MIN.iText);                             
                mi1.add(MENUS.MOVE_TO_MIN.makeItem(this));
              //  mi1.add(MENUS.MOVE_TO_MIN_LEFT.makeItem(this));
              //  mi1.add(MENUS.MOVE_TO_MIN_RIGHT.makeItem(this));
                mnu.add(mi1);
                JMenu mi2 = new JMenu(MENUS.MOVE_TO_MAX.iText);
                mi2.add(MENUS.MOVE_TO_MAX.makeItem(this));
              //  mi2.add(MENUS.MOVE_TO_MAX_LEFT.makeItem(this));
              //  mi2.add(MENUS.MOVE_TO_MAX_RIGHT.makeItem(this));
                
                mnu.add(mi2); 
                mnu.add(MENUS.MOVE_TO_MEDIAN.makeItem(this));
                
                //if(!getDomainMarkersForSeries()){
                    JMenu mi3 = new JMenu(java.util.ResourceBundle.getBundle("com/ivli/roim/Bundle").getString("MARKER_COMMAND.FIT"));                
                    mi3.add(MENUS.FIT_LEFT.makeItem(this));
                    mi3.add(MENUS.FIT_RIGHT.makeItem(this));                    
                    mnu.add(mi3);                 
                //}
                
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
            double domainX = plot.getDomainAxis().java2DToValue(e.getX(), getScreenDataArea(), plot.getDomainAxisEdge());
            iMarker.setValue(domainX); 
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
    
    private static final Logger LOG = LogManager.getLogger();
}
