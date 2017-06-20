import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import javax.imageio.*;
import java.io.File;
import java.awt.Color;
import java.util.*;
// http://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
// http://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values

public class TestMapEstimation {

	public static int t1=75,t2=75;
	public static int height,width,nbPixels;
	public static int beta=5;
	public static String nameFile="imgSource\\otsu_test";
	public static String extension=".jpg";
	public static boolean FordFulkerson = true; //false -> push relabel
	public static boolean automaticThresholding=true;

	public static void main (String[] args) throws IOException {
		int[][] img1,afterCut;
		long startTime,endTime;
		OptimizedAdjMatrix optAdjMat;

		File fichImg=new File(nameFile+extension);
		BufferedImage img = ImageIO.read(fichImg);


		img1=ImgProcessing.coloredImgToArray(img);
		System.out.println("Image 1, height "+img1.length+" width "+img1[0].length);
		File res;

		/*BufferedImage grayScale = ImgProcessing.arrayToImg(img1);
		res = new File("imgRes\\circle5_grayscale.jpg");
		ImageIO.write(grayScale,"jpg",res);*/
		
		int[][] imgThresholded;
		if(automaticThresholding){
			t1=ImgProcessing.medianThreshold(img1);
			t2=t1;
			System.out.println("Value of Otsu threshold : "+t1 +"; median threshold="+ImgProcessing.medianThreshold(img1));
		} else {
			System.out.println("Value of threshold "+t1+"--"+t2);
		}
		
		imgThresholded=ImgProcessing.threshold(img1,t1,t2);
		BufferedImage imgAfterThreshold = ImgProcessing.arrayToImg(imgThresholded);
		res = new File("imgRes\\circle5_thresolded.jpg");
		ImageIO.write(imgAfterThreshold,"jpg",res);

		optAdjMat=new OptimizedAdjMatrix(img1,beta,t1,t2);
		FordFulkerson=true;
		startTime=System.currentTimeMillis();
		if(FordFulkerson) {
			afterCut = optAdjMat.minCut();  //afterCut is the new image
		} else {
			afterCut = optAdjMat.pushRelabelAlgo();
		}
		endTime=System.currentTimeMillis();

		BufferedImage imgAfterCut = ImgProcessing.arrayToImg(afterCut);
		res = new File("imgRes\\circle5_segmented.jpg");
		ImageIO.write(imgAfterCut,"jpg",res);

		long time=endTime-startTime;
		System.out.println("Running time of the cut: "+time);
	}


}
