goto direct 
@md .\target

copy .\spi\target\spi-0.1.jar  .\target
copy .\roim\target\roim-0.1.jar  .\target

cd .\target

java -Djava.ext.dirs=.\  rom-0.1.jar com.ivli.roim.roim

goto fine

:direct

rem java -Djava.ext.dirs=.\spi\dicom\target;.\spi\bitmap\target;.\roim\target;.\spi\dicom\target\dependency;.\roim\target\dependency;.\apps\dynamic\target;.\apps\tomo\target; -cp ;.\apps\roim\target\roim-0.1.jar com.ivli.roim.Roim d:\images\composite.jpg

java -Djava.ext.dirs=.\view\target;.\view\target\dependency;.\spi\dicom\target;.\roim\target;.\spi\dicom\target\dependency;.\roim\target\dependency;.\apps\dynamic\target;.\apps\tomo\target; -cp ;.\apps\dynamic\target\dynamic-0.1.jar com.ivli.roim.DYNAMIC 
rem D:\images\dyn.dcm

:fine