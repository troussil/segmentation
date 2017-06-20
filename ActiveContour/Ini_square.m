function a = Ini_square(u, d) 
 [m, n] = size (u); 
for i =1:min(floor ((m+1)/2),floor((n+1)/2)) 
    
       a (i, i:n-i+1 ) = (i-d); 
       a (i:m-i+1,i) =(i-d); 
       a (m-i+1, i:n-i+1) = (i-d); 
       a (i:m-i+1,n-i+1) =(i-d); 
  
end  