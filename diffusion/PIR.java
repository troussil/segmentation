/*
 * test.java
 * 
 * Copyright 2017 marie-lou <marie-lou@marielou-Lenovo-Yoga-500-15IBD>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */


import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

	public class PIR {
	public static String PATH;
	
	public static void main (String args[]) throws IOException{
		String path="cercle.png";
		//PATH=path;
		double[][] tab=tool.getArray(path);
		//meanSmooth(tab,10);
		double sig=turkey.sigmae(tab);
		tab=turkey.border(tab,5,sig,path);
		
	}
        
    public static double[][] meanSmooth(double[][] Source,int intensity) throws IOException{
			
			int w =  Source[0].length;
			int h =  Source[1].length;
			//System.out.println("H= "+h+"   w="+w);
			double[][] Output = Source;
			
			for(int i=0;i<intensity;i++){
				for(int y = 1; y <h; y++){
					for(int x = 1; x <w; x++){
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
							Output[x][y]=(Source[x-1][y]+Source[x+1][y]+Source[x][y-1]+Source[x][y+1])/4;
						}
					}
				}
			Source=Output;
		}
		tool.getImage(Output,"meansmooth"+intensity+PATH);
		return Output;
	}

}


