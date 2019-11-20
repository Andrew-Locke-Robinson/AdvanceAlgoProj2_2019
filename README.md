# AdvanceAlgoProj2_2019
This repository is for the second project in Dr. Duans Advanced Algorithms Class
The program source code consists of 4 files, main.cpp, ImageConverter.cpp, ImageConverter.hpp, and half.hpp. The first three files were created solely by members of this team, while half.hpp was used fairly from sourceforge.net. All original documentation and licenses for half.hpp can be found in the HalfDocumnets directory.

linux
1. g++ -o program main.cpp ImageConverter.cpp #alternatively you may use the compile shell script
2. ./program 1 filename             #to convert to binary
3. ./program 2 filename             #binary to convert to ascii
3. ./program 3 headerfile svdfile k #compress image using k ranks to binary
4. ./program 4 compressedSVDImage   #restore svd compressed image

Example using provided documents
1. ./program 1 CAS.pgm               #creates CAS_b.pgm
2. ./program 2 CAS_b.pgm             #will create CAS_copy.pgm, which is a lossless reconstruction
3. ./program 3 header.txt SVD.txt 10 #creates image_b.pgm.SVD 
4. ./program 4 image_b.pgm.SVD       #creates image_10.pgn