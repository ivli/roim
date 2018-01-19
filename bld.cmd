@path="D:\apache-maven-3.1.1\bin\";%path%;

rem "C:\Program Files\NetBeans 8.1\java\maven\bin\"

@if "%1"=="" goto noarg

@mvn %1 %2 %3 %4   -DskipTests 
goto fine

:noarg
@mvn install -DskipTests 

:fine