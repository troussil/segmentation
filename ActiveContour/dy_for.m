function fy=dy_for(f)
[nr,nc]=size(f); 
fy=zeros(nr,nc); 
fy(1:nr-1,:)=f(2:nr,:)-f(1:nr-1,:); 
fy(nr,:)=f(nr,:)-f(nr-1,:);  % for the last row, use the backward difference as the forward difference 
