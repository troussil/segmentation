function dF = fLiveWireGetCostFcn(dImgP,dImgQ)
%calculate the local cost of p and q

%if nargin < 2,
    dWz = 0.43;
    dWg = 0.43;
    dWd = 0.14;
%end

% Calculat the cost function
% The gradient strength cost Fg
%dImg = double(dImg);
%[dY, dX] = gradient(dImg);
%dFg = sqrt(dX.^2 + dY.^2);
%dFg = 1 - dFg./max(dFg(:));
dImgQ = double(dImgQ);
[GmagQ, GdirQ] = imgradient(dImgQ);%it's to calculate the gradient and direction of image
dFg = 1 - GmagQ./max(GmagQ(:));

% The zero-crossing cost Fz
lFz = ~edge(dImgQ, 'zerocross');%zero crossing edge detector, 0 stands for "moving fast"

% The gradient direction Fd ??
dImgP = double(dImgP);
[GmagP, GdirP] = imgradient(dImgP);
GdirQ=abs(GdirQ);
GdirP=abs(GdirP);
dFd=(abs(GdirQ-pi)+abs(GdirP-pi))./pi;

% The Sum:
dF = dWz.*double(lFz)+ dWg.*dFg +dWd.*dFd;
