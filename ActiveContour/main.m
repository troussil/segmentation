%this is the main program that you can run directly in matlab.
%Other files are just functions
clear
close all;
img=readImage('new.jpg');
sigma=1.2; %parameter in Gaussien Kernel origin:1.2
lambda=1;
g=stopFunction(img,lambda,sigma);

figure();
imshow(g);
title('edge indicator');
%initial LSF
LSF=Ini_square(img,5);
%dphi=GetDifference(LSF);
%test=dphi(:)./g(:);
figure();
%imagesc(img);=>to show a scalable image.
%colormap(gray);
imshow(img);
hold on;
[c,h]=contour(LSF,[0 0],'r');
title('initial contour');
%level set begins
levelset=AOS(img, LSF,-0.2,5, 150 , g);
