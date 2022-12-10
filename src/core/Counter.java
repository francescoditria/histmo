package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Counter extends Thread {

	public String fileName;
	public ArrayList hist;
	public int start;
	public int end;
	
	public void run()
	{

		String line;
		double num=0;
		int n=0;
		int i;
		int count;
		
		File file = new File(this.fileName);
        n=hist.size();
	    try {
	        Scanner scanner = new Scanner(file);
	        
	        
	        while (scanner.hasNextLine()) 
	        {
	            line=scanner.nextLine();
	            num=Double.parseDouble(line);
	            
	            for(i=start;i<end;i++)
	            {
	            	Bucket b=(Bucket) hist.get(i);
	            	if(num>=b.min && num<b.max)
	            	{
	            		b.freq++;
	            	}
	            	hist.set(i, b);
	            			            	
	            }
	            
	        }
	        scanner.close();
	    }
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }

	}

	
}
