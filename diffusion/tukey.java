
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.lang.reflect.*;

public class tukey {
	
	
	/***** fonction biweight qui prends en entrée la difference d'intensité entre un pixel et son voisin*********/
	/***** TUKEY BIWEIGHT ******/
	public static double function1(double x,double sigma){
		//double sigma =0;
		
		if(Math.abs(x)<=sigma && sigma!=0) {
			
			double X= x *( 1 - (x/sigma)*(x/sigma))*( 1 - (x/sigma)*(x/sigma));
			
			return  X;
		} else if (sigma==0){
			//System.out.println("ERREUR SIGMA 0");
			 return 0;
		}else{
			
			return 0;
		}
	}
	/**** calcul sigma à partir de sigmae pour la fonction biweight ****/
	
	public static double sigma1(double sigmae){
		return Math.sqrt(5)*sigmae;
	}
		
		
		
		
		
	/***** HUBER MINIMAX ******/
	public static double function2(double x,double sigma){
		//double sigma =0;
		
		if(Math.abs(x)<=sigma && sigma!=0) {
			
			double X= x / sigma;
			
			return  X;
		}else{
			return Math.signum(x);
		}
	}
	
	/**** calcul sigma à partir de sigmae pour la fonction MINIMAX ****/
	public static double sigma2(double sigmae){
		return sigmae;
	}


	/***** PORIGINAL PERRONA MALIK******/
	public static double function3(double x,double sigma){
		//double sigma =0;
		
		if(x!=0 && sigma !=0) {
			
			double X= 2*x/((2*sigma*sigma)+(x*x));
			
			return  X;
		} else { return 0;}
	}
	/**** calcul sigma à partir de sigmae pour la fonction PERRONA MALIK ****/
	
	public static double sigma3(double sigmae){
		return sigmae/Math.sqrt(2);
	}
		




	/***** calcul su sigmae ******/
	public static double sigmae(double[][] img) throws IOException{
		int l=img.length*img[0].length;
		
		double[] source =new double[l];
		img=tool.gradient(img);
        
		int k=0;
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[0].length;j++){
				
				source[k]=img[i][j];
				k++;
			}
		}
		
		
		Arrays.sort(source);
		//System.out.println(Arrays.toString(source));
		double med;
		if(l%2==0) med= (source[l/2]+source[l/2-1])/2;
		else 	med=source[((l-1)/2)];
		
		System.out.println("med"+med);
		
		for(int i=0;i<l;i++){
					source[i]=Math.abs(source[i]-med);
		}
		
		
		Arrays.sort(source);
		
		if(l%2==0) med= (source[l/2]+source[l/2-1])/2;
		else 	med=source[((l-1)/2)];
		double sigmae = 1.4826*med;
		return sigmae;


	}
	
	
	
	/***** realise la diffusion selon une intensité donnée *******/
	public static double[][] smooth(double[][] Source,int intensity,double sigma,double lambda,String PATH,int function) throws  IOException,NoSuchMethodException,IllegalAccessException,InvocationTargetException{
			Method method = tukey.class.getDeclaredMethod("function" + function,double.class,double.class);
			Method methodSigma = tukey.class.getDeclaredMethod("sigma" + function,double.class);
			
			
			
			if(sigma==-1 || lambda==-1){
				double sigmae=sigmae(Source);
				sigma=(Double) methodSigma.invoke(0,sigmae);
				lambda = 1/((Double) method.invoke(0,sigmae,sigma));
				System.out.println("changer sigma");
			}
			
			
			int w =  Source[0].length;
			int h =  Source.length;
			double[][] Output = Source;
			
			for(int i=0;i<intensity-1;i++){
				for(int y = 1; y <w-1; y++){
					for(int x = 1; x <h-1; x++){
							//GENERAL
							Output[x][y]= Output[x][y]+lambda*((Double) method.invoke(0,Source[x-1][y]-Source[x][y],sigma)+
										(Double) method.invoke(0,Source[x+1][y]-Source[x][y],sigma)+
										(Double) method.invoke(0,Source[x][y+1]-Source[x][y],sigma)+
										(Double) method.invoke(0,Source[x][y-1]-Source[x][y],sigma))/4;
							
					}
				}
			Source=Output;
		}
		tool.getImage(Output,function+"Smooth"+intensity+"_"+sigma+"_"+PATH);
		return Output;
	}	
	
	

	
	
	/****** determine les contours de l'image ********/
	
	public static double[][] border (double[][] Source,int intensity,double sigma,double lambda,String PATH,int function) throws IOException,NoSuchMethodException,IllegalAccessException,InvocationTargetException{
		Method method = tukey.class.getDeclaredMethod("function" + function,double.class,double.class);
		Method methodSigma = tukey.class.getDeclaredMethod("sigma" + function,double.class);
	
		
		if(sigma==-1  || lambda==-1){
			double sigmae=sigmae(Source);
			sigma=(Double) methodSigma.invoke(0,sigmae);
			lambda = 1/((Double) method.invoke(0,sigmae,sigma));
			
		}
		
		
		Source = smooth(Source,intensity,sigma,lambda,PATH,function);
		double [][] grad=tool.gradient(Source);
		int w =  Source[0].length;
		int h =  Source.length;
		double[][] Output = Source;
		
		
			for(int y = 1; y <w-1; y++){
				for(int x = 1; x <h-1; x++){
					double l=255*(2*sigma*sigma)/((2*sigma*sigma)+grad[x][y]*grad[x][y]);
					Output[x][y]=l;
					
					
				}
			}
		tool.getImage(Output,function+"Border"+intensity+"_"+sigma+PATH);
		return Output;
	}



	public static double[][] segmentation(double[][] Source,double [][] proba,int intensity,double sigma,double lambda,String PATH,int function) throws IOException,NoSuchMethodException,IllegalAccessException,InvocationTargetException{
		Method method = tukey.class.getDeclaredMethod("function" + function,double.class,double.class);
		Method methodSigma = tukey.class.getDeclaredMethod("sigma" + function,double.class);
	
		
		if(sigma==-1  || lambda==-1){
			double sigmae=sigmae(Source);
			sigma=(Double) methodSigma.invoke(0,sigmae);
			lambda = 1/((Double) method.invoke(0,sigmae,sigma));
			
		}
		
		
		proba = smooth(proba,intensity,sigma,lambda,PATH,function);
		double [][] grad=tool.gradient(Source);
		int w =  Source[0].length;
		int h =  Source.length;
		
		
		
			for(int y = 1; y <w-1; y++){
				for(int x = 1; x <h-1; x++){
					if(proba[x][y]<0.6){
						Source[x][y]=0;
					}
					
					
				}
			}
		tool.getImage(Source,function+"sem"+intensity+"_"+sigma+PATH);
		return Source;
	}
}



