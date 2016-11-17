*Так же доступен: [in English](README.md), [по Русски](README.ru_ru.md)*

Roim - библиотека классов предназначенная быть каркасом для создания приложений по обработке данных медицинских исследований, преимущественно в области ядерной медицины. Библиотека целиком написанна на Java и спроектирована так, что бы быть максимально независимой от модальностей и оборудования, с помощью которого данные изображения получены. В настоящее время помимо собственно ядерной медицины (ЯМ) реализована ограниченная поддержка ***NM/DR/DXR/CT/PET/MR.

Имеется реализация экранных примитивов таких как области интереса (ОИ), профили, аннотации и т.д. которые могут быть использованы для извлечения данных и построения графиков зависимостей, например накопления от времени имп./сек. (т.к. в случае исследования ЯМ интенсивность пиксела напрямую зависит от накопления активности в данной области).     
Библиотека поддерживает данные в формате [DICOM](<https://ru.wikipedia.org/wiki/DICOM>) для доступа к котрорым используется [DCM4CHE](<http://www.dcm4che.org>).  
[Aparapi](<https://aparapi.github.io/>) задействуется для обработки данных с использованием GPU а отрисовка графиков основывается на [JFreeChart](<http://www.jfree.org/jfreechart/>). 

The library includes an application to illustrate basic concepts and principles behind it.  
This application is meant for processing dynamic studies such as renal clearance, hida or gastric emptying. 
The screen is split into two parts left one displays image either in list mode or as a composite image (a sum of all frames)
Here one can work with ROIs - set, delete, move. 
Right part is a cartesian chart showing activity curves over the ROIs. 
Also, using markers it is possible to fit acquired data with exponential curve using least squares method. 
And export data into a CSV file for processing in an external application.          

SIC: для работы необхдимо наличие JDK8.


С пожеланиями всего самого лучшего,   
И.  



