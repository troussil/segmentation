function fx=dx_for(f)
 
[nr,nc]=size(f); 
fx=zeros(nr,nc); 
fx(:,1:nc-1)=f(:,2:nc)-f(:,1:nc-1); 
 
fx(:,nc) = f(:,nc)-f(:,nc-1);  % for the last column, use the backward difference as the forward difference 