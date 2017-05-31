
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class tool {
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
		
	public static double[][] getArray(String path ) throws IOException{
				
			BufferedImage Source= ImageIO.read(new File(path));
			
			int w =  Source.getWidth();
			int h =  Source.getHeight();
			double[][] Output = new double[h][w];
			
			for(int y = 1; y < h; y++){
				for(int x = 1; x < w; x++){
					int p = Source.getRGB(x,y);

					int a = (p>>24)&0xff;
				    int r = (p>>16)&0xff;
				    int g = (p>>8)&0xff;
				    int b = p&0xff;

					//calcul intensité
					int avg = (r+g+b)/3;
					Output[y][x]=avg;
				}
			}
		return Output;
        }
        

	public static void getImage(double[][] Source ,String path) throws IOException{
			int w =  Source[0].length;
			int h =  Source.length;
			BufferedImage Output= new BufferedImage(w,h, BufferedImage.TYPE_INT_ARGB);
			System.out.println(w + "  " +h);	
			
			
			for(int y = 0; y < w; y++){
				for(int x = 0; x < h; x++){
					int val=(int) Source[x][y];
					int p =0;
					p= (255<<24) | (val<<16) | (val<<8) | val;
					Output.setRGB(y,x,p);

				}
			}
		ImageIO.write(Output, "png",new File(path));
		
        }
}
