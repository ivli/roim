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

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author likhachev
 */
public class FileOpenDialog { 
    //TODO: following are the subject for configuration so user can choose what dialog is to be used
    //OS or swing and does it make sense to let open folders
    private final static boolean USE_SYSTEM_FILE_DIALOG = false;
    private final static boolean OPEN_FOLDERS = true;
    
    /*
     * let swing dialog speak a bit Russian
     */
    private static void adjustLAF() {
        if (Locale.getDefault().equals(new Locale("ru", "RU"))) { //NOI18N
            /*  add locale to JFileChooser */
            UIManager.put("FileChooser.lookInLabelText", "Папка");
            UIManager.put("FileChooser.cancelButtonText", "Отмена");
            UIManager.put("FileChooser.cancelButtonToolTipText", "Отмена");
            UIManager.put("FileChooser.openButtonText", "Открыть");
            UIManager.put("FileChooser.openButtonToolTipText", "Открыть");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Тип");
            UIManager.put("FileChooser.fileNameLabelText", "Файл");
            UIManager.put("FileChooser.detailsViewButtonToolTipText", "Подробно");
            UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Подробно");
            UIManager.put("FileChooser.upFolderToolTipText",    "На один уровень вверх"); 
            UIManager.put("FileChooser.upFolderAccessibleName", "На один уровень вверх"); 
            UIManager.put("FileChooser.homeFolderToolTipText",   "Домой"); 
            UIManager.put("FileChooser.homeFolderAccessibleName", "Домой"); 
            UIManager.put("FileChooser.fileNameHeaderText", "Имя"); 
            UIManager.put("FileChooser.fileSizeHeaderText", "Размер");
            UIManager.put("FileChooser.fileTypeHeaderText", "Тип"); 
            UIManager.put("FileChooser.fileDateHeaderText", "Дата"); 
            UIManager.put("FileChooser.fileAttrHeaderText", "Аттрибуты");
            UIManager.put("FileChooser.listViewButtonToolTipText", "Список"); 
            UIManager.put("FileChooser.listViewButtonAccessibleName", "Список");      
            UIManager.put("FileChooser.openDialogTitleText", "Выберите файл");
            UIManager.put("FileChooser.saveDialogTitleText", "Выберите файл");
            UIManager.put("FileChooser.saveAsButtonText", "Сохранить как");
            UIManager.put("FileChooser.saveButtonText", "Сохранить");
            UIManager.put("FileChooser.saveButtonToolTipText", "Сохранить");            
            UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");            
            UIManager.put("FileChooser.desktopAccessibleName", "Рабочий стол");
            UIManager.put("FileChooser.desktopToolTipText",    "Рабочий стол");            
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);   
        }
    }
    
    static { //ensure localisation 
        adjustLAF();
    }
    
    private final Frame  iFrame;
    private final String iTitle;
    private String iFileExtension; 
    private String iFileDescription;    
    private final boolean iOpen;
    private String iFilePath;
    private String iFileName;
    
    /**
     * creates an instance of the FileOpenDialog
     * @param aF  parent frame object 
     * @param aTitle caption text
     * @param aFileExtension file extensions to display like "dcm" or "jpg" 
     * @param aFileDescription file description text 
     * @param aPath path to start in
     * @param aOpen the reason: true means open file, false does save  
     */
    public FileOpenDialog(Frame aF, String aTitle, String aFileExtension, String aFileDescription, String aPath, boolean aOpen) {
        iFrame = aF;
        iTitle = aTitle;
        iFileExtension = aFileExtension;
        iFileDescription = aFileDescription;
        iFilePath = null != aPath ? aPath : System.getProperty("user.home");//Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, );
        iOpen = aOpen;
        iFileName = null;        
    }
    
    public FileOpenDialog(String aTitle, String aFileExtension, String aFileDescription, String aPath, boolean aOpen) {
        this(null, aTitle, aFileExtension, aFileDescription, aPath, aOpen);       
    }
    
    public FileOpenDialog(String aTitle, String aFileExtension, String aFileDescription, String aPath) {
        this(null, aTitle, aFileExtension, aFileDescription, aPath, true);       
    }
    
    public FileOpenDialog(String aTitle, String aFileExtension, String aFileDescription, boolean aOpen) {
        this(null, aTitle, aFileExtension, aFileDescription, null, aOpen);       
    }
       
    public FileOpenDialog(String aTitle, String aFileExtension, String aFileDescription) {    
        this (null, aTitle, aFileExtension, aFileDescription, null, true);
    }
    
    /**
     * creates an instance of the FileOpenDialog
     * @param aTitle caption text
     * @param aOpen the reason: true means open file, false does save
     */
    public FileOpenDialog(String aTitle, boolean aOpen) {    
        this (null, aTitle, null, null, null, aOpen);
    }
    
    public void setFileExtension(String aFileExtension, String aFileDescription) {
        iFileExtension = aFileExtension;
        iFileDescription = aFileDescription;
    }
    
    public void setPath(String aPath) {
        iFilePath = aPath;
    }
    
    public String getFileName() {
        return iFileName;
    }
    
    public File getFile() {
        return new File(iFileName);
    }
    
    /**
     * displays the dialog in application modal mode 
     * @return true if a file or folder has been selected otherwise false is returned   
     */
    public boolean doModal() {        
        if (USE_SYSTEM_FILE_DIALOG) {
            FileDialog fd = new FileDialog(iFrame, iTitle, iOpen ? FileDialog.LOAD : FileDialog.SAVE);
            
            fd.setFile(iFileExtension);
            
            // following doesn't work on windows see JDK-4031440 f**k 
            fd.setFilenameFilter((File dir, String name) -> name.endsWith(iFileExtension) );
                
            fd.setDirectory(iFilePath); 
            fd.setVisible(true);

            if (null != fd.getFile())
                iFileName = fd.getDirectory() + fd.getFile();                 
                        
            return null != iFileName;       
        } else {    
            JFileChooser jfc = new JFileChooser();   
            
            jfc.setCurrentDirectory(new File(iFilePath)); 
            
            jfc.setFileSelectionMode(OPEN_FOLDERS ? JFileChooser.FILES_AND_DIRECTORIES:JFileChooser.FILES_ONLY);
            
            if (null != iFileExtension && null != iFileDescription) {
                jfc.setFileFilter(new FileFilter(){ 
                    @Override
                    public  boolean accept(File f) {                                 
                        if (f.isDirectory()) 
                            return true;
                        else {
                            String ext = "";
                            String s = f.getName();
                            int i = s.lastIndexOf('.');

                            if (i > 0 &&  i < s.length() - 1) {
                                ext = s.substring(i+1).toLowerCase();
                            }    

                            if (ext.equalsIgnoreCase(iFileExtension))
                                return true;
                        }

                        return false;
                    }

                    @Override
                    public String getDescription() {
                        return iFileDescription;
                    }                           
                });        
            }
                                   
            if (JFileChooser.APPROVE_OPTION == (iOpen ? jfc.showOpenDialog(iFrame) : jfc.showSaveDialog(iFrame))) {
                iFileName = jfc.getSelectedFile().getAbsolutePath();                  
            } else {
                return false;
            }
            return true;
        }
    }    
}
