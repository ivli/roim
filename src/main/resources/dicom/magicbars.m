## maketestdcm
## dicom image of size 256x320 comprised of bars [0-255] and magics 32x32 
## 

ls = uint16(linspace(0, 256, 256));
ls32 = repmat(ls, 32, 1); 
ls32f = fliplr(ls32);
m32 = magic(32);
m32 = uint32(m32 / max(max(m32)) * max(ls)); 
ls32m = ls32;
ls32m(1:32, 256-31:256) = m32;
ls32m(1:32, 1:32) = m32';
p = [ls32m;  ls32', ls32f', ls32', ls32f', ls32', ls32f', ls32', ls32f'; fliplr(ls32m)];
dicomwrite(uint16(p'), "magicbars.dcm");