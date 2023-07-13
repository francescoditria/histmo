package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Engine {

	private String fileName;

	public Engine(String fileName)
	{
		this.fileName=fileName;
	}
	
	public Block scan()
	{
		System.out.println("Scanning database");
		int rows=0;
		int cols=1;
		int i,j;
        float min=0,max=0,sum=0;
        ArrayList listDist=new ArrayList();
        
		File file = new File(fileName);

		try 
	    {
	        Scanner scanner = new Scanner(file);
	        String line=scanner.nextLine();
	        min=Float.parseFloat(line);
	        max=Float.parseFloat(line);
	        sum=min;
	        rows++;
	        listDist.add(min);
	        
	        while (scanner.hasNextLine()) 
	        {
	        	rows++;
	            line=scanner.nextLine();
	            float num=Float.parseFloat(line);
	            sum+=num;
	            if(num>max) max=num;
	            if(num<min) min=num;
	            if(!listDist.contains(num))
	            	listDist.add(num);
 
	        }
	        scanner.close();
            

	    }
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }

		Block block=new Block();
		block.rows=rows;
		block.cols=cols;
		block.min=min;
		block.max=max;
		block.distinct=listDist.size();
		block.avg= (float) (Math.round(sum/rows * 100.0) / 100.0);
		block.std=(float) (Math.round(this.standardDev(block.avg,block.rows) * 100.0) / 100.0);


		////
		float cv=(float) (Math.round(block.std/block.avg * 100.0) / 100.0);
        float sp = 0;
        if(cv<=0.1) sp=10;
        if(cv>0.1 && cv<0.9) sp=(int) ((cv-0.1)/(0.9-0.1)*(90-10)+10);
        if(cv>=0.9) sp=90;
	    //int nb=0;
	    int nb=(int) ((block.distinct)*sp/100);//this.length*this.sp/100;
	    //float nb2=(int) ((block.max-block.min)*sp/100);//this.length*this.sp/100;
	    //System.out.println("NB="+nb+" NB2="+nb2+" sp="+sp);
		
	    block.nb=nb;
		return block;
	}

	
	private float standardDev(float avg, int rows)
	{
		String line;
		float num=0;
		float n=0;
		float sum=0;
		
		File file = new File(this.fileName);
	    try {
	        Scanner scanner = new Scanner(file);
	        
	        while (scanner.hasNextLine()) 
	        {
	            line=scanner.nextLine();
	            num=Float.parseFloat(line);
	            sum+=Math.pow(num-avg,2);
	            
	        }
	        scanner.close();
	    }
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }
	    n=(float) Math.sqrt(sum/rows);
		return n;
	}

	

	public void count(Bucket[] testBucket,int ntest)
	{
		File file = new File(this.fileName);
		String line;
		double num=0;
		double resultCount=0;
		double resultSum=0;
		double result=0;
		int i;
		
	    try {
	        Scanner scanner = new Scanner(file);
	        
	        while (scanner.hasNextLine()) 
	        {
	            line=scanner.nextLine();
	            num=Double.parseDouble(line);
	            
	            for(i=0;i<ntest;i++)
	            {
	            	if(num>=testBucket[i].min && num<=testBucket[i].max) 
	            	{
	            		testBucket[i].freq++;
	            		//testBucket[i].sum+=num;
	            	}
	            }
	        }
	        
	        scanner.close();

	    } catch (FileNotFoundException e) {e.printStackTrace();}	    	    	  

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
	            if(num>=inf && num<sup) 
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
