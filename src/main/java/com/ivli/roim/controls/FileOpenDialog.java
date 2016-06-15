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

import com.ivli.roim.view.Settings;
import java.awt.FileDialog;
import java.io.File;
import java.util.Locale;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author likhachev
 */
public class FileOpenDialog { 
    private final static boolean USE_SYSTEM_FILE_DIALOG = false;
    
    private static final void addjustLAF() {
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
 
    private final String iTitle;
    private final String iFileExtension; 
    private final String iFileDescription;
    private String iFileName;
    
    public FileOpenDialog(String aTitle, String aFileExtension, String aFileDescription) {
        iTitle = aTitle;
        iFileExtension = aFileExtension;
        iFileDescription = aFileDescription;
        iFileName = null;
    }
    
    public String getFileName() {
        return iFileName;
    }
    
    public boolean DoModal(JFrame aF, boolean aOpen) {
        
        if (USE_SYSTEM_FILE_DIALOG) {
            FileDialog fd = new FileDialog(aF, iTitle, aOpen ? FileDialog.LOAD : FileDialog.SAVE);
            
            fd.setFile(iFileExtension);//Settings.get(Settings.DEFAULT_FILE_SUFFIX_DICOM, "*.dcm")); // NOI18N
            
            // following doesn't work on windows see JDK-4031440 f**k 
            fd.setFilenameFilter((File dir, String name) -> name.endsWith(iFileExtension) );
                
            fd.setDirectory(Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, System.getProperty("user.home"))); // NOI18N
            fd.setVisible(true);

            if (null != fd.getFile()) {
                iFileName = fd.getDirectory() + fd.getFile(); 
                Settings.set(Settings.KEY_DEFAULT_FOLDER_DICOM, fd.getDirectory());
            }
            return null != iFileName;       
        } else {    
            JFileChooser jfc = new JFileChooser();   
            jfc.setCurrentDirectory(new File(Settings.get(Settings.KEY_DEFAULT_FOLDER_DICOM, System.getProperty("user.home")))); // NOI18N
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setFileFilter(new FileFilter(){ 
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

                        if (ext.equalsIgnoreCase(Settings.DEFAULT_FILE_SUFFIX_DICOM))
                            return true;
                    }

                    return false;
                }
            
                public String getDescription() {
                    return iFileDescription;
                }                           
            });        
           
            if (JFileChooser.APPROVE_OPTION == (aOpen ? jfc.showOpenDialog(aF) : jfc.showSaveDialog(aF))) {
                iFileName = jfc.getSelectedFile().getAbsolutePath();  
                Settings.set(Settings.KEY_DEFAULT_FOLDER_DICOM, jfc.getSelectedFile().getParent());
            } else {
                return false;
            }
            return true;
        }
    }    
}
