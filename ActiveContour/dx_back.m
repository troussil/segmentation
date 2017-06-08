function fx=dx_back(f)
[nr,nc]=size(f); 
fx=zeros(nr,nc); 
fx(:,2:nc)=f(:,2:nc)-f(:,1:nc-1); 
fx(:,1)=f(:,2)-f(:,1); % for the first column, use the forward difference as the backward difference