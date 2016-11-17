*Also available in: [English](README.md), [по Русски](README.ru_ru.md)*

Roim is a class library for processing biomedical imaging data. 
It is designed to be vendor neutral and support most of modalities. 
However, for the time being it is tested only with NM/DR/DXR/CT/PET/MR files.
 
It implements a set of algorithms, screen primitives and forms to simplify processes of extraction, 
processing and display information extracted off study files. 
The library relies on few external libraries namely DCM4CHE to handle DICOM data, 
aparapi for GPU operations and charts is based on JFreeChart. 

The library includes an application to illustrate basic concepts and principles behind it.  
This application is meant for processing dynamic studies such as renal clearance, hida or gastric emptying. 
The screen is split into two parts left one displays image either in list mode or as a composite image (a sum of all frames)
Here one can work with ROIs - set, delete, move. 
Right part is a cartesian chart showing activity curves over the ROIs. 
Also, using markers it is possible to fit acquired data with exponential curve using least squares method. 
And export data into a CSV file for processing in an external application.          

SIC: library heavily uses features introduced in JDK8 thus it is necessary to build and run.

With best regards,    
I.


