clear;
close all;
I = imread('a.png');
if size(I, 3) == 3, I = rgb2gray(I); end
dImg = double(I);
%dF  = fLiveWireGetCostFcn(dImg,dImg-1); % The cost function of the live-wire algorithm, see Ref
iPX = zeros(size(dImg), 'int8'); % The path map that shows the cheapest path to the sast anchor point.
iPY = zeros(size(dImg), 'int8'); % The path map that shows the cheapest path to the sast anchor point.
dX = 50; dY = 50;
iXPath = dX; iYPath = dY;
[iPX, iPY] = fLiveWireCalcP(dImg, dX, dY);
dX = 51; dY = 55;
[iXPath, iYPath] = fLiveWireGetPath(iPX, iPY, dX, dY);
[iPX, iPY] = fLiveWireCalcP(dImg, dX, dY);
