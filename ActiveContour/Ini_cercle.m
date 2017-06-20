function a = Ini_cercle(u) 
 [m, n] = size (u); 
 center=[m/2,n/2];
 radius = min(center)-5; 
 a = zeros( m,n );                                 
 for i = 1 : m
    for j = 1 : n
        distance = sqrt( sum( ( center - [ i, j ] ).^2 ) );  
        %distance=sqrt((m/2-i).^2+(n/2-j).^2);
        a( i, j ) =radius-distance;                      
    end
 end