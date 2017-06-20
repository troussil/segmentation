

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.lang.reflect.*;

	public class main_diffusion {
	public static String PATH;
	
	public static void main (String args[]) throws  IOException,NoSuchMethodException,IllegalAccessException,InvocationTargetException{
		String path;
		double s;
		double l;
		int i;
		int function;
		

		
		//Initialisation parametres
		if(args.length >=5){
			path=args[0];
			function=Integer.parseInt(args[1]);
			i=Integer.parseInt(args[2]);
			s=Double.parseDouble(args[3]);
			l=Double.parseDouble(args[4]);
		}else{
			System.out.println(" Arguments manquants ");
			System.out.println(" requis :   String pathImage , int fonction , int intensité, int sigma, int lambda ");
			System.out.println(" Image: format .PNG");
			System.out.println(" fonction :    1->tukey biweight  2-> HUber minimax   3-> lorentzian error norm  ");
			System.out.println(" lambda=-1 ou sigma=-1   -> calcul automatique");
			return;
		}
		
		//nitalisation probabilité
		//La probabilité est calculée selon l'intensité lumineuse
		//donc la segmentation est efficace pour un objet clair sur fond sombre
		double [][] prob= tool.getProb(path);
		
		
		double[][] tab=tool.getArray(path);
		double[][] tab2=tool.getArray(path);
		
		tukey.segmentation(tab,prob,i,s,l,path,function);
		tukey.border(tab2,i,s,l,path,function);
		
	
		
		
	}
        
    public static double[][] meanSmooth(double[][] Source,int intensity) throws IOException{
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
							Output[x][y]= (Source[x-1][y]+
										Source[x+1][y]+
										Source[x][y+1]+
										Source[x][y-1])/4;
							}
					}
				}
			Source=Output;
		}
		tool.getImage(Output,"meansmooth"+intensity+PATH);
		return Output;
	}

}


