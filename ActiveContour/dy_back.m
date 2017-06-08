function fy=dy_back(f)
[nr,nc]=size(f);
fy=zeros(nr,nc);
fy(2:nr,:)=f(2:nr,:)-f(1:nr-1,:); 
fy(1,:)=f(2,:)-f(1,:);%for the first row,use the forward difference as the backward difference