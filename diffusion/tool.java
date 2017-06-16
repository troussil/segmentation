
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.awt.Color;

public class tool {
	
	
	/*****Retourne le gradient d'une image (tableau de double) sous forme de tableau de double *****/
	public static double[][] gradient(double[][] Source) throws IOException {
		int w =  Source[0].length;
		int h =  Source.length;
		double[][] Output = new double[h][w];
		
		for(int y = 0; y < w-1; y++){
			for(int x = 0; x < h-1; x++){
				double X= (Source[x][y+1]-Source[x][y]+Source[x+1][y+1]-Source[x+1][y])/2;
				double Y= (Source[x+1][y]-Source[x][y]+Source[x+1][y+1]-Source[x][y+1])/2;
				Output[x][y]=Math.sqrt((X*X)+(Y*Y));
				
			}
		}
		return Output;
	}
	
	
	/*******Ouvre une image et la retourne en niveau de gris comme un tableau de double**********/
	public static double[][] getArray(String path ) throws IOException{
				
			BufferedImage Source= ImageIO.read(new File(path));
			
			int w =  Source.getWidth();
			int h =  Source.getHeight();
			double[][] Output = new double[h][w];
			//double[][] Output2= new double[h+1][w+1];
			
			double[][] Out = new double[h+2][w+2];

			for(int y = -1; y < h+1; y++){
				for(int x =-1; x < w+1; x++){

					int p;
					if(y==-1){
						if(x==-1){
							p=Source.getRGB(x+1,y+1);
						}else if(x==w){
							p=Source.getRGB(x-1,y+1);
						}else{
							p=Source.getRGB(x,y+1);
						}
					}else if(y==h){
						if(x==-1){
							p=Source.getRGB(x+1,y-1);
						}else if(x==w){
							p=Source.getRGB(x-1,y-1);
						}else{
							p=Source.getRGB(x,y-1);
						}
					}else if(x==-1){
						p=Source.getRGB(x+1,y);
					}
					else if(x==w){
						p=Source.getRGB(x-1,y);
					}else{
						p = Source.getRGB(x,y);
					}
					int a = (p>>24)&0xff;
				    int r = (p>>16)&0xff;
				    int g = (p>>8)&0xff;
				    int b = p&0xff;

					//calcul intensité
					int avg = (r+g+b)/3;
					Out[y+1][x+1]=avg;
				}
			}
			//tool.getImage(Out,"ext.png");
		return Out;
		
        }
   
   
   
   
   /****** créer une probabilité selon l'intensté d'un pixel (255=1 / 0=0) *****/
   public static double[][] getProb(String path ) throws IOException{   
		double[][] prob= getArray(path);  
		int w =  prob[0].length;
		int h =  prob.length;
				
				for(int k=1;k<w;k++){
					for(int j=1;j<h;j++){
						prob[j][k]=prob[j][k]/255;
					}
				}
		return prob;
	}

	/*****Enregistre l'image correspondante au tableau de double donné (valable pour les ".png")**********/
	public static void getImage(double[][] Source ,String path) throws IOException{
			int w =  Source[0].length;
			int h =  Source.length;
			BufferedImage Output= new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
			System.out.println(w + "  " +h);	
			
			
			for(int y = 0; y < w; y++){
				for(int x = 0; x < h; x++){
					int val=(int) Source[x][y];
					int p =0;
					Color myWhite = new Color(val, val, val);
					int rgb = myWhite.getRGB();
					p= (255<<24) | (val<<16) | (val<<8) | val;
					Output.setRGB(y,x,rgb);

				}
			}
		ImageIO.write(Output, "png",new File(path));
		
        }
}
