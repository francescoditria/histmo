package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ui.Report;

public class Model {

	private String fileName;
	private Block block;
	
	public Model(String fileName)
	{
		this.fileName=fileName;
		this.reduce();
	}
	
	private void reduce()
	{
		Engine engine=new Engine(fileName);
		Report report=new Report();
		
		this.block=engine.scan();
		this.build();
		//report.show(block);
		//System.out.println("Ready");
		//this.showBuckets();
		//return this.block;
	}
	
	
	private void build()
	{
		System.out.println("Building model");
		float min=block.min;
		float max=block.max+1;
		int nb=block.nb;
		float w=(max-min)/nb; //bucket width
		
		block.bucketList.clear();
		int i;
		for (i=0;i<block.nb;i++)
		{
			Bucket bucket=new Bucket();
			bucket.min=block.min+i*w;
			bucket.min=(float) (Math.round(bucket.min * 100.0) / 100.0);
			bucket.max=bucket.min+w;
			bucket.max=(float) (Math.round(bucket.max * 100.0) / 100.0);
			//System.out.println(bucket.min+"\t"+bucket.max);
			block.bucketList.add(bucket);
		}
		
		int k=block.bucketList.size();
		Counter counter=new Counter();
		counter.fileName=this.fileName;
		counter.hist=block.bucketList;
		counter.start=0;
		counter.end=k/2;
		counter.start();
		
		Counter counter2=new Counter();
		counter2.fileName=this.fileName;
		counter2.hist=block.bucketList;
		counter2.start=(k/2)+1;
		counter2.end=k;
		counter2.start();
		
		while(counter.isAlive() || counter2.isAlive())
		{
		
		}
		//System.out.println("Model built");

		//this.showBuckets(block);
		this.merge();
		//this.showBuckets(block);
		
		this.errorTest();
		this.confidenceTest();
	}
	
	private void merge()
	{
		
		ArrayList hist=block.bucketList;
		int n=hist.size();
		//System.out.println("Merging #"+n+" buckets");
		int i,j=0;
		int lastMerged=0;
		Bucket bucket1;
		Bucket bucket2;
		int newFreq=0;
		ArrayList mergedHist=new ArrayList();
		
		for(i=0;i<n-1;i++)
		{
			bucket1=(Bucket) hist.get(i);
			//System.out.println("\nCurrent Bucket\n\t"+i+"\tMin="+bucket1.min+"\tMax="+bucket1.max+"\t#"+bucket1.freq);
			newFreq=bucket1.freq;
			j=i;
			boolean merged=false;
			do
			{
				j++;
				bucket2=(Bucket) hist.get(j);
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
		//System.out.println("Merged #"+n+" buckets");
		block.nb=n;
		
	}

	
	public void showModel()
	{
		Report report=new Report();
		report.showBuckets(block);
	}
	
	
	public void change(int nb)
	{
		this.block.nb=nb;
		System.out.println("\nNew model");
		this.build();
		
		//Report report=new Report();
		//report.show(block);
	}
	
	
	public void showData()
	{
		Report report=new Report();
		report.show(block);
	}
	
	
	private void errorTest()
	{
	
		System.out.println("Testing model");
		int ntest=1000;
		int i;
		int r1;
		int r2;
		Bucket[] testBucket = new Bucket[ntest];
		double final_error=0;
		double max_error=0;
		for(i=0;i<ntest;i++)
		{
			do
			{
				Random r = new Random();
				r1 = (int) (r.nextInt((int) (block.max-block.min)) + block.min);
				r2 = (int) (r.nextInt((int) (block.max-block.min)) + block.min);
			} while (r1>=r2);
			//System.out.println("Test Bucket "+i);
			Bucket b=new Bucket();
			b.min=r1;
			b.max=r2;
			b.freq=0;
			testBucket[i]=b;						
		}

		Engine engine=new Engine(this.fileName);
		engine.count(testBucket,ntest);
		
        for(i=0;i<ntest;i++)
        {
        	double exact=testBucket[i].freq;
        	double approx=this.exec("count",testBucket[i].min,testBucket[i].max,block.bucketList);
			double error=Math.abs(exact-approx)/exact;
			final_error+=error;		
			if(error>max_error) max_error=error;
        }
        double mer=(final_error/ntest);
        double roundMer=Math.floor(mer * 100) / 100;
		//System.out.println("Mean Relative Error:\t"+roundMer+"%");
		//this.error=roundMer;
        block.errorMax=Math.floor(max_error * 100) / 100;
        block.error=roundMer;
        
		
	}
	
	private double exec(String function, double inf, double sup,ArrayList hist)
	{
		
		int n=hist.size();
		int i;
		Bucket bucket;
		double result=0;
		double sum=0;
		double count=0;
		double avg=0;
		
		for(i=0;i<n;i++)
		{
			bucket=(Bucket) hist.get(i);
			if(bucket.min>=inf && bucket.max<=sup)
			{
			    //System.out.println("caso 1"+" "+bucket.min+"\t"+bucket.max);
			    count+=bucket.freq;
			    sum+=(bucket.min+bucket.max)/2*bucket.freq;
			}
			
			else if(bucket.min<inf && bucket.max>sup)
			{
			    //System.out.println("caso 2"+" "+bucket.min+"\t"+bucket.max);
				double perc=(sup-inf)/(bucket.max-bucket.min)*100;
				double value=bucket.freq*perc/100;
				count+=value;
				sum+=(sup+inf)/2*value;
			}
			
			else if(bucket.min<inf && bucket.max>inf)
			{
			    //System.out.println("caso 3"+" "+bucket.min+"\t"+bucket.max);
				double perc=(bucket.max-inf)/(bucket.max-bucket.min)*100;
				double value=bucket.freq*perc/100;
				count+=value;
			    sum+=(bucket.max+inf)/2*value;

			}
				
			else if(bucket.max>sup && bucket.min<sup)
			{
			    //System.out.println("caso 4"+" "+bucket.min+"\t"+bucket.max);
				double perc=(sup-bucket.min)/(bucket.max-bucket.min)*100;
				double value=bucket.freq*perc/100;
				count+=value;
				sum+=(sup+bucket.min)/2*value;
			}
				
			
		}

		avg=sum/count;
		//System.out.println("Approx Result\t"+result);
		if(function.equals("count")) result=count;
		if(function.equals("sum")) result=sum;
		if(function.equals("avg")) result=avg;
		
		return result;
	}

	
	
	private void confidenceTest()
	{
		//System.out.println("\nTesting model");
		int ntest=1000;
		int i;
		int r1;
		int r2;
		Bucket[] testBucket = new Bucket[ntest];
		double final_error=0;
		for(i=0;i<ntest;i++)
		{
			do
			{
				Random r = new Random();
				r1 = (int) (r.nextInt((int) (block.max-block.min)) + block.min);
				r2 = (int) (r.nextInt((int) (block.max-block.min)) + block.min);
			} while (r1>=r2);
			//System.out.println("Test Bucket "+i);
			Bucket b=new Bucket();
			b.min=r1;
			b.max=r2;
			b.freq=0;
			testBucket[i]=b;						
		}
		
		
	

		//////
		Engine engine=new Engine(this.fileName);
		engine.count(testBucket,ntest);
		////////
		
		
	    double conf=0;
	    double confMax=0;
        for(i=0;i<ntest;i++)
        {
        	double exact=testBucket[i].freq;
        	double approx=this.exec("count",testBucket[i].min,testBucket[i].max,block.bucketList);
			double error=Math.abs(exact-approx)/exact;
			if(error<=block.error)
			{
				conf++;
			}
			if(error<=block.errorMax)
			{
				confMax++;
			}
        }

		//System.out.println("Confidence degree:\t"+conf);
		block.confidence=(Math.round((conf/ntest*100) * 100.0) / 100.0);
		block.confidenceMax=(Math.round((confMax/ntest*100) * 100.0) / 100.0);
		//System.out.println("Confidence degree:\t"+cd+"%");
        
	}
	
	public void process(String function,float min,float max)
	{
    	
    	double start_time= System.currentTimeMillis();
    	double approx=this.exec(function,min,max,block.bucketList);
    	approx=(Math.round(approx * 100.0) / 100.0);
    	double end_time = System.currentTimeMillis();
		double difference = (end_time - start_time)/1000;
		//System.out.println("Approx\t"+approx+" ["+difference+" secs] ["+block.error+"% error] ["+block.confidence+"% confidence degree]");
		double approx1=Math.round(approx/(block.error+1));
		double approx2=Math.round(approx/(1-block.error));
		System.out.println("Approx\t["+approx1+","+approx2+"] [p="+block.confidence+"%]"+" ["+difference+" secs]");
		
		/*
		double approx1Max=Math.round(approx/(block.errorMax+1));
		double approx2Max=Math.round(approx/(1-block.errorMax));
		System.out.println("["+approx1Max+","+approx2Max+"] [p="+block.confidenceMax+"%]");
		 */
		
		this.processReal(function, min, max);
	}

	private void processReal(String function,float min,float max)
	{
    	Engine engine=new Engine(this.fileName);
    	double start_time= System.currentTimeMillis();
    	double approx=engine.exec_real(function, min, max);
    	double end_time = System.currentTimeMillis();
		double difference = (end_time - start_time)/1000;
		System.out.println("Real\t"+approx+" ["+difference+" secs]");
		
		
	}

}
