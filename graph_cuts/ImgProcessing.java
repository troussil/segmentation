import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.*;
import java.io.File;
import java.awt.Color;

public class ImgProcessing {


	public static int[][] imgToArray(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      int[][] result = new int[height][width];

      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            int color = image.getRGB(j, i);
            int blue = color & 0xff;
			int green = (color & 0xff00) >> 8;
			int red = (color & 0xff0000) >> 16;
			if((blue+green+red)/3>128){
				result[i][j]=1;
			} else {
				result[i][j]=0;
			}
         }
      }

      return result;
   }

   public static int[][] coloredImgToArray(BufferedImage image) {
      int width = image.getWidth();
      int height = image.getHeight();
      int[][] result = new int[height][width];

      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
            int color = image.getRGB(j, i);
            int blue = color & 0xff;
			int green = (color & 0xff00) >> 8;
			int red = (color & 0xff0000) >> 16;
			int mean = (blue+green+red)/3;
			//System.out.println(mean);
			result[i][j]=mean;
         }
      }

      return result;
   }

   public static BufferedImage arrayToImg(int[][] array){
	   int height=array.length;
	   int width=array[0].length;
	   BufferedImage res = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				if(array[i][j]==0){
					res.setRGB(j,i,Color.black.getRGB());
				} else if(array[i][j]==1) {
					res.setRGB(j,i,Color.white.getRGB());
				} else {
					res.setRGB(j,i,Color.cyan.getRGB());
				}
			}
		}

		return res;
	}

	public static void noise25_Percent(int[][] array){
		for(int i=0;i<array.length;i++){
			for(int j=0; j<array[0].length;j++){
				int change=(int)(4*Math.random());
				if(change==0){		//1 chance sur 4
					if(array[i][j]==0){
						array[i][j]=1;
					} else {
						array[i][j]=0;
					}
				}
			}
		}
	}
	
	public static int[][] threshold(int[][] img,int t1,int t2){
		int[][] res=new int[img.length][img[0].length];
		if(t1==t2 && t1>0)
			t1=t1-1;
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[0].length;j++){
				if(img[i][j]<=t1){
					res[i][j]=0;
				} else if(img[i][j]>=t2){
					res[i][j]=1;
				} else {
					res[i][j]=2;
				}
			}
		}
		return res;	
	}
	
	public static int otsuThreshold(int[][] img){
		double[] hist=probaHistogram(img);
		int pixelsNb=img.length*img[0].length;
		double sum = 0;
		double sumB = 0;
		double wB = 0;
		double wF = 0;
		double mB;
		double mF;
		double max = 0;
		double between;
		int threshold = 0;
		for (int i = 0; i < 256; i++) //normally it will be 255 but sometimes we want to change step
			sum += i * hist[i];
		System.out.println(sum);
		for (int i = 0; i < 256; i++) {
		  wB += hist[i];
		  if (wB == 0)
			continue;
		  wF = (1 - wB);
		  if (wF <= 10e-5)
			break;
		  sumB += (double) i * hist[i];
		  mB = sumB / wB;
		  mF = (sum - sumB) / wF;
		  System.out.println(mB+" "+mF+" w: "+wB+" "+wF);
		  between = wB*wF*(mB - mF)*(mB-mF);
		  if (between > max) {
			max = between;
			threshold = i;
		  }
		}
		return threshold;	
	}
	
	public static int medianThreshold(int[][] img){
		int pixelsNb=img.length*img[0].length;
		int[] hist=histogram(img);
		int sum=0;
		for(int i=0;i<256;i++){
			sum+=hist[i];
			if(sum>=pixelsNb/2)
				return i;
		}
		return -1;
	}
	
	public static double[] probaHistogram(int[][] img){
		int[] res=histogram(img);
		int nbPixels=img.length*img[0].length;
		double[] proba=new double[256];
		for(int i=0;i<256;i++){
			proba[i]=(double)res[i]/(double)nbPixels;
			//System.out.println("Intensite :"+i+"="+res[i]+" "+proba[i]);
		}
		return proba;
	}
	
	public static int[] histogram(int[][] img){
		int[] res=new int[256];
		for(int i=0;i<256;i++){
			res[i]=0;
		}
		for(int i=0;i<img.length;i++){
			for(int j=0;j<img[i].length;j++){
				int val=img[i][j];
				if(val>=0 && val<256){
					res[val]++;
				}
			}
		}
		return res;
	}

}
