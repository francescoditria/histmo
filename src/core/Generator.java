package core;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class Generator {

	
	public Generator(String fileName, int min, int max, int size)
	{
		int i;
		int num;
		Random r = new Random();
		
		System.out.println("Generating testing data");
		PrintWriter writer;
		try 
		{
			writer = new PrintWriter(fileName, "UTF-8");			
			for(i=0;i<size;i++)
			{				
				num = (int) (r.nextInt((int) (max-min)) + min);
				writer.println(num);		
			}
			writer.close();

		}
		catch (FileNotFoundException e) {e.printStackTrace();} 
		catch (UnsupportedEncodingException e) {e.printStackTrace();}

		
	}
	
	
}
