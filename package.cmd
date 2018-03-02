md package
copy /B .\apps\dynamic\target\dependency\*.* .\package
copy /B .\apps\dynamic\target\*.jar  .\package
copy /B .\apps\tomo\target\dependency\*.* .\package
copy /B .\apps\tomo\target\*.jar  .\package


copy /B .\spi\dicom\target\*.jar  .\package
copy /B .\spi\dicom\target\dependency\*.* .\package

copy /B .\spi\bitmap\target\*.jar  .\package
copy /B .\spi\bitmap\target\dependency\*.* .\package