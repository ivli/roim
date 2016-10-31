Roim is a class library designed to help implementing software for processing nuclear medicine (hereinafter <b>NM</b>) data. 

It is designed to be vendor neutral and support most of modalities. 
However, for the time being it is tested only with NM/DR/DXR/CT/PET/MR files. 
It implements a set of controls, screen primitives (Overlays) and algorithms to extract, 
process and display biomedical information off study files in DICOM format. 
  
The library relies on few external libraries namely DCM4CHE to handle DICOM data, aparapi for GPU operations and charting is based on JFreeChart. 

Finally the library includes an application to illustrate basic concepts and principles behind the design.  
This application is meant for processing dynamic studies such as renal clearance, hida or gastric emptying. 
The screen is split into two parts left one displays image either in list mode or as a composite image (a sum of all frames)
Here one can work with ROIs - set, delete, move. 
Right part is a cartesian chart showing activity curves over the ROIs. 
Also, using markers it is possible to fit acquired data with exponential curve using least squares method. 
And export data into a CSV file for processing in an external application.          

SIC: library hevily use features of JDK8 so it is necessary to build and run

With best regards,
I. 

