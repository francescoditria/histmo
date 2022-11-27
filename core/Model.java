package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Model {

	private String fileName;
	private double min;
	private double max;
	private double avg;
	private double std;
	private int length;
	private int nb;
    private double cv;
    private int sp;

	private ArrayList<Bucket> hist=new ArrayList<Bucket>();
	private ArrayList<Bucket> mergedHist=new ArrayList<Bucket>();
	
	public Model(String fileName)
	{
	    this.fileName=fileName;
		this.load();
		this.build();
		this.merge();
		this.hist.clear();
		this.showModel();
		this.test();
	}
	
	private void test()
	{
		System.out.println("\nTesting model");
		int ntest=10;
		int i;
		int r1;
		int r2;
		double final_error=0;
		for(i=0;i<ntest;i++)
		{
			do{
			Random r = new Random();
			r1 = (int) (r.nextInt((int) (this.max-this.min)) + this.min);
			r2 = (int) (r.nextInt((int) (this.max-this.min)) + this.min);
			} while (r1>r2 || r1==r2);
			//System.out.println();
			double exact=exec_real("count",r1,r2);
			double approx=exec("count",r1,r2);
			double error=Math.abs(exact-approx)/exact;
			final_error+=error;
			//System.out.println("\nmin:"+r1+"\tmax:"+r2+"\texact:"+exact+"\tapprox:"+approx);
			
		}
		//double mer=(Math.round((final_error*100/ntest) * 100.0) / 100.0);
		double mer=(final_error*100/ntest);
		System.out.println("Mean Relative Error:\t"+mer+"%");
		//return mer;
		
		//confidence degree
		double conf=0;
		for(i=0;i<ntest;i++)
		{
			do{
			Random r = new Random();
			r1 = (int) (r.nextInt((int) (this.max-this.min)) + this.min);
			r2 = (int) (r.nextInt((int) (this.max-this.min)) + this.min);
			} while (r1>r2 || r1==r2);
			//System.out.println();
			double exact=exec_real("count",r1,r2);
			double approx=exec("count",r1,r2);
			double error=Math.abs(exact-approx)/exact;
			if(error<=mer)
			{
				conf++;
			}
			
		}
		//System.out.println("Confidence degree:\t"+conf);
		double cd=(Math.round((conf/ntest*100) * 100.0) / 100.0);
		System.out.println("Confidence degree:\t"+cd+"%");
		
		
		
		
	}
	
	public double exec_real(String function, double inf, double sup)
	{

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
	
	public double exec(String function, double inf, double sup)
	{
		
		int n=this.mergedHist.size();
		int i;
		Bucket bucket;
		double result=0;
		
		for(i=0;i<n;i++)
		{
			bucket=this.mergedHist.get(i);
			if(bucket.min>=inf && bucket.max<=sup)
			{
			    //System.out.println("caso 1"+" "+bucket.min+"\t"+bucket.max);
			    result+=bucket.freq;
			}
			
			else if(bucket.min<inf && bucket.max>sup)
			{
			    //System.out.println("caso 2"+" "+bucket.min+"\t"+bucket.max);
				double perc=(sup-inf)/(bucket.max-bucket.min)*100;
				double value=bucket.freq*perc/100;
				result+=value;
			}
			
			else if(bucket.min<inf && bucket.max>inf)
			{
			    //System.out.println("caso 3"+" "+bucket.min+"\t"+bucket.max);
				double perc=(bucket.max-inf)/(bucket.max-bucket.min)*100;
				double value=bucket.freq*perc/100;
				result+=value;
			}
				
			else if(bucket.max>sup && bucket.min<sup)
			{
			    //System.out.println("caso 4"+" "+bucket.min+"\t"+bucket.max);
				double perc=(sup-bucket.min)/(bucket.max-bucket.min)*100;
				double value=bucket.freq*perc/100;
				result+=value;
			}
				
			
		}

		
		//System.out.println("Approx Result\t"+result);
		return result;
	}
	
	private void load()
	{
		System.out.println("\nLoading data");
		String line;
		double min=0;
		double max=0;
		double num=0;
		double sum=0;
		
		int n=0;
		
		File file = new File(this.fileName);
	    try {
	        Scanner scanner = new Scanner(file);
	        line=scanner.nextLine();
	        min=Double.parseDouble(line);
	        max=Double.parseDouble(line);
	        sum=min;
	        n++;
	        
	        while (scanner.hasNextLine()) 
	        {
	        	n++;
	            line=scanner.nextLine();
	            num=Double.parseDouble(line);
	            sum+=num;
	            if(num>max) max=num;
	            if(num<min) min=num;
	        }
	        scanner.close();
		    this.min=min;
		    this.max=max;
		    this.length=n;
		    this.avg= Math.round(sum/n * 100.0) / 100.0;
			this.std=Math.round(this.standardDev() * 100.0) / 100.0;
			this.cv=Math.round(this.std/this.avg * 100.0) / 100.0;

	        this.sp = 0;
	        if(this.cv<=0.1) this.sp=10;
	        if(this.cv>0.1 && this.cv<0.9) this.sp=(int) ((this.cv-0.1)/(1-0.1)*(90-10)+10);
	        if(this.cv>=0.9) this.sp=90;

		    //int nb=0;
		    this.nb=(int) ((this.max-this.min)*this.sp/100);//this.length*this.sp/100;
		    System.out.println("Sample Percentage\t"+this.sp);
		    System.out.println("Initial Number of Buckets\t"+this.nb);
	        
	        
	    }
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }	    	    	  

	}

	
	private double standardDev()
	{
		String line;
		double num=0;
		double n=0;
		double sum=0;
		
		File file = new File(this.fileName);
	    try {
	        Scanner scanner = new Scanner(file);
	        
	        while (scanner.hasNextLine()) 
	        {
	            line=scanner.nextLine();
	            num=Double.parseDouble(line);
	            sum+=Math.pow(num-this.avg,2);
	            
	        }
	        scanner.close();
	    }
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }
	    n=Math.sqrt(sum/this.length);
		return n;
	}

	private void build()
	{
		System.out.println("\nBuilding model");
		int i,j;
		int n=this.length;
		double min=this.min;
		double max=this.max+1;
		double range=max-min;
		double width;
		
		double minb;
		double maxb;
		int freq;
		int mer=0;
		i=this.nb;
		//for(i=2;i<n;i++)
		//{
			width=range/i;
			System.out.println("\n#buckets="+i+"\tWidth="+width);
			for(j=1;j<=i;j++)
			{
				minb=min+(width*(j-1));
				maxb=minb+width;
				freq=0;//this.countFreq(minb,maxb);
				Bucket bucket=new Bucket();
				bucket.index=j;
				bucket.min=minb;
				bucket.max=maxb;
				bucket.freq=freq;
				//System.out.println("\tBucket "+j+"\tMin="+minb+"\tMax="+maxb+"\t#"+freq);
				hist.add(bucket);							
			}
			this.countFreq2(hist);
			
	}

	private void countFreq2(ArrayList<Bucket> hist)
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
	            
	            for(i=0;i<n;i++)
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

		//return n;
        for(i=0;i<n;i++)
        {
           	Bucket b=(Bucket) hist.get(i);
           	System.out.println(i+ ") "+b.min+" "+b.max+" "+b.freq);
        }
	}

	
	
	private int countFreq(double min,double max)
	{
		String line;
		double num=0;
		int n=0;
		
		File file = new File(this.fileName);
	    try {
	        Scanner scanner = new Scanner(file);
	        
	        while (scanner.hasNextLine()) 
	        {
	            line=scanner.nextLine();
	            num=Double.parseDouble(line);
	            if(num>=min && num<max)
	            	n++;
	        }
	        scanner.close();
	    }
	    catch (FileNotFoundException e) 
	    {
	        e.printStackTrace();
	    }

		return n;
	}

	
	private void merge()
	{

		int n=hist.size();
		System.out.println("\nMerging #"+n+" buckets");
		int i,j=0;
		int lastMerged=0;
		Bucket bucket1;
		Bucket bucket2;
		int newFreq=0;
		this.mergedHist.clear();
		
		for(i=0;i<n-1;i++)
		{
			bucket1=hist.get(i);
			//System.out.println("\nCurrent Bucket\n\t"+i+"\tMin="+bucket1.min+"\tMax="+bucket1.max+"\t#"+bucket1.freq);
			newFreq=bucket1.freq;
			j=i;
			boolean merged=false;
			do
			{
				j++;
				bucket2=hist.get(j);
				//System.out.println("Comparing to Bucket\n\t"+j+"\tMin="+bucket2.min+"\tMax="+bucket2.max+"\t#"+bucket2.freq);
					
				if(bucket1.freq==bucket2.freq)
				{
					bucket1.max=bucket2.max;
					newFreq+=bucket2.freq;
					//System.out.println("\tAdding "+j);
					lastMerged=j;
					//hist.remove(j);
					
				}
				else
				{
					bucket1.freq=newFreq;
					//System.out.println("\n\tMerged Bucket "+i+"\tMin="+bucket1.min+"\tMax="+bucket1.max+"\t#"+bucket1.freq);
					mergedHist.add(bucket1);
					i=j-1;
					newFreq=0;
					merged=true;
				}
				
			} while (merged==false && j<n-1);
		}
		//System.out.println("\ni="+i+"\tlastMerged="+lastMerged);
		if(lastMerged<i)
		{
			mergedHist.add(hist.get(i));			
		}

		n=mergedHist.size();
		System.out.println("Merged #"+n+" buckets");
		this.nb=n;

		
	}
	
	
	public void showModel()
	{

		int n=mergedHist.size();
		if(n<1) return;
		
		Bucket bucket;
		int i;
		
		System.out.println("\nHistogram Model");
	    System.out.println("\tMin="+this.min);
        System.out.println("\tMax="+this.max);
        System.out.println("\tLength="+this.length);
        System.out.println("\tAvg="+this.avg);
        System.out.println("\tStd="+this.std);
        System.out.println("\tCoefficient of Variation="+this.cv);
        System.out.println("\tSample Percentage="+this.sp);
        System.out.println("\tNumber of Buckets="+this.nb);

		for(i=0;i<n;i++)
		{
			bucket=mergedHist.get(i);
			//System.out.println("\t"+i+"\tMin="+bucket.min+"\tMax="+bucket.max+"\t#"+bucket.freq);
			
		}
		
		
	}
	
	
	public void changeModel(int nb)
	{
		this.nb=nb;
		this.build();
		this.merge();
		this.hist.clear();
		this.showModel();
	
	}
	
}
