*Так же доступен: [in English](README.md), [по Русски](README.ru_ru.md)*

Roim - библиотека классов предназначенная быть каркасом для создания приложений по обработке данных медицинских исследований, преимущественно в области ядерной медицины. Библиотека целиком написанна на Java и спроектирована так, что бы быть максимально независимой от модальностей и оборудования, с помощью которого данные изображения получены. В настоящее время помимо собственно ядерной медицины (ЯМ) реализована ограниченная поддержка NM/DR/DXR/CT/PET/MR.


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


С пожеланиями всего самого лучшего,   
И.  



