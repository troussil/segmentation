function Dphi=GetDifference(image)
[Ix,Iy]=gradient(image);
Dphi=Ix.^2+Iy.^2+0.0001;