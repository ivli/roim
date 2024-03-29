[![Build Status](https://travis-ci.com/ivli/roim.svg?branch=master)](https://travis-ci.com/ivli/roim)
[![Java CI](https://github.com/ivli/roim/actions/workflows/default.yaml/badge.svg)](https://github.com/ivli/roim/actions/workflows/default.yaml)
[![codecov](https://codecov.io/gh/ivli/roim/branch/master/graph/badge.svg?token=oWNQyO3jA6)](https://codecov.io/gh/ivli/roim)

*Also available in: [English](README.md), [по Русски](README.ru_ru.md)*


***Roim*** is a class library for processing biomedical imaging data. 
It is designed to be vendor neutral and support most of modalities. 
However, for the time being it is tested only with ***NM/DR/DXR/CT/PET/MR*** files.
 
It implements a set of algorithms, screen primitives and forms to simplify processes of extraction, 
processing and display information extracted off study files. 
The library relies on few external libraries namely [DICOM](https://en.wikipedia.org/wiki/DICOM) to handle DICOM data, 
[Aparapi](https://aparapi.github.io/) for GPU operations and charts is based on [JFreeChart](http://www.jfree.org/jfreechart/). 

The library includes an application to illustrate basic concepts and principles behind it.  
This application is meant for processing dynamic studies such as renal clearance, hida or gastric emptying. 
The screen is split into two parts left one displays image either in list mode or as a composite image (a sum of all frames)
Here one can work with ROIs - set, delete, move. 
Right part is a cartesian chart showing activity curves over the ROIs. 
Also, using markers it is possible to fit acquired data with exponential curve using least squares method. 
And export data into a CSV file for processing in an external application.          

***SIC***: Java8 is necessary.

The library and program have been successfully tested on Windows and Linux (Ubuntu) and it has almost 100% i18n for English and Russian.  

Please visit Roim [project](http://ivli.github.io/roim/) or [WiKi] (https://github.com/ivli/roim/wiki/ROIM/) page to get more details on design, uses and how it can help in creating your applications.



With best regards,    
[I.](http://www.ilikhachev.com)

