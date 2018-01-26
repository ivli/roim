goto dynamic

:dynamic
java -Djava.ext.dirs=.\core\target;.\view\target;.\view\target\dependency;.\spi\dicom\target;.\spi\dicom\target\dependency;.\roim\target\dependency;.\apps\dynamic\target;.\apps\dynamic\target\dependency; -cp ;.\apps\dynamic\target\dynamic-1.0.1.jar com.ivli.roim.DYNAMIC 
rem java -Djava.ext.dirs=.\apps\dynamic\target;.\apps\dynamic\target\dependency; -cp .\apps\dynamic\target\dynamic-1.0.1.jar com.ivli.roim.DYNAMIC 
goto fine

:tomo
java -Djava.ext.dirs=.\core\target;.\view\target;.\view\target\dependency;.\spi\dicom\target;.\spi\dicom\target\dependency;.\roim\target\dependency;.\apps\tomo\target;.\apps\tomo\target\dependency; -cp ;.\apps\tomo\target\dynamic-1.0.1.jar com.ivli.roim.TOMO 

:fine