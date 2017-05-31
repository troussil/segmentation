
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class turkey {
	public static double lambda=1;
	
	
	
	public static double biweight(double x, double sigma){
		//X DIFFERENCE ENTRE PIXEL ET SON VOISIN
		if(Math.abs(x)<=sigma) {
			return  (x * Math.pow( 1 - Math.pow(x/sigma,2),2));
		} else{
			 return 0;
		}
	}

	public static double sigmae(double[][] img) throws IOException{
		int l=img.length*img[0].length;
		double[] source =new double[l];
		img=tool.gradient(img);
			/*	for(int y = 1; y <16; y++){
					for(int x = 1; x <16; x++){
						System.out.print(img[x][y]+"  ");
					}
					System.out.println("  ");
				}*/
               
               
		int k=0;
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[0].length;j++){
				
				source[k]=img[i][j];
				k++;
			}
		}
		
		
		Arrays.sort(source);
		double med;
		if(l%2==0) med= (source[l/2]+source[l/2-1])/2;
		else 	med=source[((l-1)/2)];
		for(int i=0;i<l;i++){
					source[i]=Math.abs(source[i]-med);
		}
		
		Arrays.sort(source);
		System.out.println(med);
		if(l%2==0) med= (source[l/2]+source[l/2-1])/2;
		else 	med=source[((l-1)/2)];
		double sigmae = 1.4826*med;
		return sigmae;


	}
		
	
	
	
	public static double[][] smooth(double[][] Source,int intensity,double sigma,String PATH) throws IOException{
			
			int w =  Source[0].length;
			int h =  Source.length;
			double[][] Output = Source;
			
			for(int i=0;i<intensity-1;i++){
				for(int y = 1; y <w-2; y++){
					for(int x = 1; x <h-2; x++){
						//CALCUL DES BORDS ET COINS
						if(x==1 &&  y==1){
							Output[x][y]=(Source[x+1][y]+Source[x][y+1])/2;
						}else if(x==1 &&  y==(h-1)){
							Output[x][y]=(Source[x+1][y]+Source[x][y-1])/2;
							
						}else if(x==w-1 &&  y==1){
							Output[x][y]=(Source[x-1][y]+Source[x][y+1])/2;
						}else if(x==w-1 &&  y==h-1){
							Output[x][y]=(Source[x-1][y]+Source[x][y-1])/2;
						}else if(x==1){
							Output[x][y]=(Source[x+1][y]+Source[x][y-1]+Source[x][y+1])/3;
						}else if(x==w-1){
							Output[x][y]=(Source[x-1][y]+Source[x][y-1]+Source[x][y+1])/3;
						}else if(y==1){
							Output[x][y]=(Source[x+1][y]+Source[x-1][y]+Source[x][y+1])/3;
						}else if(y==h-1){
							
						
							Output[x][y]=(Source[x+1][y]+Source[x-1][y]+Source[x][y+-1])/3;
						}else {
							//GENERAL
							Output[x][y]= Output[x][y]+(biweight(Source[x-1][y]-Output[x][y],sigma)+
										biweight(Source[x+1][y]-Output[x][y],sigma)+
										biweight(Source[x][y+1]-Output[x][y],sigma)+
										biweight(Source[x][y-1]-Output[x][y],sigma))/4;
							System.out.println( Source[x][y+1]-Output[x][y]+ "   "+biweight(Source[x][y+1]-Output[x][y],sigma));
						}
					}
				}
			Source=Output;
		}
		tool.getImage(Output,"turkeySmooth"+intensity+"_"+sigma+"_"+PATH);
		return Output;
	}	
	
	
	
	
	
	
	public static double[][] border(double[][] Source,int intensity,double sigma,String PATH) throws IOException{
			//System.out.println(Arrays.toString(Source[160]));
			int w =  Source[0].length;
			int h =  Source.length;
			//System.out.println("H= "+h+"   w="+w);
			double[][] Output = Source;
			
			for(int i=0;i<intensity-1;i++){
				for(int y = 0; y <w-1; y++){
					for(int x = 0; x <h-1 ;x++){
						//CALCUL DES BORDS ET COINS
						int border=0;
						if(x==0 &&  y==0){
							Output[x][y]=(Source[x+1][y]+Source[x][y+1])/2;
						}else if(x==0 &&  y==(h-1)){
							Output[x][y]=(Source[x+1][y]+Source[x][y-1])/2;
							
						}else if(x==w-1 &&  y==0){
							Output[x][y]=(Source[x-1][y]+Source[x][y+1])/2;
						}else if(x==w-1 &&  y==h-1){
							Output[x][y]=(Source[x-1][y]+Source[x][y-1])/2;
						}else if(x==0){
							Output[x][y]=(Source[x+1][y]+Source[x][y-1]+Source[x][y+1])/3;
						}else if(x==w-1){
							Output[x][y]=(Source[x-1][y]+Source[x][y-1]+Source[x][y+1])/3;
						}else if(y==0){
							Output[x][y]=(Source[x+1][y]+Source[x-1][y]+Source[x][y+1])/3;
						}else if(y==h-1){
							Output[x][y]=(Source[x+1][y]+Source[x-1][y]+Source[x][y+-1])/3;
						}else {
							//GENERAL
							Output[x][y]=(int) (Output[x][y]+lambda*(biweight(Source[x-1][y]-Output[x][y],sigma)+
										biweight(Source[x+1][y]-Output[x][y],sigma)+
										biweight(Source[x][y+1]-Output[x][y],sigma)+
										biweight(Source[x][y-1]-Output[x][y],sigma))/4);

						}
					}
				}
			Source=Output;
		}
			for(int y = 1; y <w-1; y++){
				for(int x = 1; x <h-1; x++){
				
					if(biweight(Source[x-1][y]-Output[x][y],sigma)==0 || biweight(Source[x+1][y]-Output[x][y],sigma)==0 || biweight(Source[x][y+1]-Output[x][y],sigma)==0 || biweight(Source[x][y-1]-Output[x][y],sigma)==0){
						Output[x][y]=0;
					}else{
						Output[x][y]=255;
					}
				}
			}
		tool.getImage(Output,"turkeyBorder"+intensity+"_"+sigma+PATH);
		return Output;
	}
}



