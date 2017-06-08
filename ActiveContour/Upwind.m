%this .m file is for implementing article(12)
function d = Upwind(u,sign)
xf=dx_for(u);
yf=dy_for(u);
xb=dx_back(u);
yb=dy_back(u);

if sign<=0    %if k<=0 
    d=sqrt(max(xb,0).^2+min(xf,0).^2+max(yb,0).^2+min(yf,0).^2);
else           %if k>0
    d=sqrt(min(xb,0).^2+max(xf,0).^2+min(yb,0).^2+max(yf,0).^2);  
end


        
    