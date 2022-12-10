package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Manager extends Thread {
	
	double querymin;
	double querymax;
	String fileName;
	
	public void run()
	{
		double start_time= System.currentTimeMillis();
		double res2=this.exec_real("count", this.querymin, this.querymax);
		double end_time = System.currentTimeMillis();
		double difference = (end_time - start_time)/1000;
		System.out.println("Real\t"+res2+" "+difference);
		//this.stop();	
	}
	
	public double exec_real(String function, double inf, double sup)
	{

		//System.out.println(this.fileName);
		File file = new File(this.fileName);
		String line;
		double num=0;
		double resultCount=0;
		double resultSum=0;
		double result=0;
		
	    try {
	        Scanner scanner = new Scanner(file);
	        
	        while (scanner.hasNextLine()) 
	        {
	            line=scanner.nextLine();
	            num=Double.parseDouble(line);
	            if(num>=inf && num<=sup) 
	            {
	            		resultCount++;
	            		resultSum+=num;
	            		
	            }
	        }
	        scanner.close();

	    } catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }	    	    	  
	    
	    
	    //System.out.println("Real Result\t"+result);
    	if(function.equalsIgnoreCase("count"))
    		result= resultCount;
    	if(function.equalsIgnoreCase("sum"))
    		result= resultSum;
    	if(function.equalsIgnoreCase("avg"))
    		result=resultSum/resultCount;
		
    	return result;
	}



}
