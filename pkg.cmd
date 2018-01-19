@set PKG_DIR=.\pkg

goto _zip

@call bld.cmd dependency:copy-dependencies

@md %PKG_DIR%

@del /Y %PKG_DIR%\* 

:_cpy
@copy /B /Y .\spi\bitmap\target\dependency\*.*  %PKG_DIR%
@copy /B /Y .\spi\bitmap\target\*.jar           %PKG_DIR%
@copy /B /Y .\spi\dicom\*.jar                   %PKG_DIR%
@copy /B /Y .\spi\dicom\target\dependency\*.*   %PKG_DIR%

@copy /B /Y .\core\target\*.jar             %PKG_DIR%
@copy /B /Y .\core\target\dependency\*      %PKG_DIR%

@copy /B /Y .\view\target\*.jar             %PKG_DIR%
@copy /B /Y .\view\target\dependency\*      %PKG_DIR%

@copy /B /Y .\apps\dynamic\target\*.jar          %PKG_DIR%
@copy /B /Y .\apps\dynamic\target\dependency\*.* %PKG_DIR%
@copy /B /Y .\apps\tomo\target\*.jar             %PKG_DIR%
@copy /B /Y .\apps\tomo\target\dependency\*      %PKG_DIR%

:_cmd
@echo java -Djava.ext.dirs=.\ -cp dynamic-0.1.jar com.ivli.roim.DYNAMIC %%1 %%2 > %PKG_DIR%\dynamic.cmd
@echo java -Djava.ext.dirs=.\ -cp tomo-0.1.jar com.ivli.roim.TOMO  %%1 %%2      > %PKG_DIR%\tomo.cmd

:_zip
@set PATH="C:\Program Files\7-Zip\";%PATH%;
7z a %PKG_DIR%\dynamic.zip %PKG_DIR%\*.*

:_fine

