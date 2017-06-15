%the code below is for implementing the artile from formule(6)~(11)

function levelset = AOS(Image, levelset, Force, time_step , iter, g) 
count = 1; 
figure();

while count < iter 
   
    Dphi = GetDifference(levelset);%this is |gradient u|
    absDphi = Upwind (levelset, Force);%according to (12) should be upwind scheme. Still working on this method. 
    Balloon_force=  Force .* g .*absDphi;%|grad u|*k*g 
    %so the model will be
    %phiu/phit=a*|gradient(u)|*div(b*gradient(u)/|gradient(u))
    %a=g,b=1=>geometric model
    %a=1,b=g=>geodesic model
    
    %in the article the levelset contour is used as ui^(n) denotes the
    %approximation of u(xi,tn). The time have been discretized
    %here as time_step. Matlab reads the picture and will give an automatic
    %discretization of the space(image).
    
    %b=g;%geodesic
    %a=1;%geodesic
    b=1;%geometric  ::doesn't work very well,cannot figure out why
    a=g;%geometric
    %x direction
    levelset1 = LinearSystem(2*time_step, levelset+time_step.*Balloon_force,Dphi, b,a); 
    %y direction
    levelset2 = LinearSystem(2*time_step, (levelset+time_step.*Balloon_force)', Dphi', b',a);%change direction x to y
    %time_step multiply 2 because in formule(11) there is a 2
    %article(11)
    levelset = (levelset1 + levelset2')./2; %get the new contour from contour1 and contour2's transpose
    
    count = count+1; 
    
    imshow( Image, [] ); hold on;  contour( levelset, [0,0], 'g'); hold off; drawnow %show the new contour
    title('final image');
end 
 
 
