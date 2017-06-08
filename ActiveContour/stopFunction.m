function g = stopFunction( u, k , sig) 
mask = fspecial( 'gaussian', 15, sig);
u = conv2( double(u), double(mask), 'same'); %smooth image by Gaussian convolution
%according to the article formule(14), here we do a Gaussian kernel of standard deviation u.
%imshow(u);

[ ux,uy ] = gradient(u); 
 g = 1 ./ (1 + (ux .^ 2 +  uy .^ 2)/(k.^2));%edge indicator