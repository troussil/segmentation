function x = TDMA(varargin)
%This function used THOMAS Algorithm to solve the strictly diagonally dominant tridiagonal matrix.
%To solve the equation (11)
%For A*x=R,where A is a diagonal tridiagonal matrix. It will output x.
a = varargin{1};
b = varargin{2};
c = varargin{3};
d = varargin{4};
%allow to get the 4 parameters
%a is middle, b is upper, c is lower, d is R(result)
% Initialization
m = zeros(size(a));
l = zeros(size(c));
y = zeros(size(d));
n = size(a,1);


m(1,:) = a(1,:);

y(1,:) = d(1,:); 

for i = 2 : n
   i_1 = i-1;
   l(i_1,:) = c(i_1,:)./m(i_1,:);
   m(i,:) = a(i,:) - l(i_1,:).*b(i_1,:);
   
   y(i,:) = d(i,:) - l(i_1,:).*y(i_1,:); 
    
end
x(n,:) = y(n,:)./m(n,:);
for i = n-1 : -1 : 1
   x(i,:) = (y(i,:) - b(i,:).*x(i+1,:))./m(i,:);
end
