package core;

import java.util.ArrayList;

public class Block {

	public int rows;
	public int cols;
	public float min;
	public float max;
	public int distinct;
	public float avg;
	public float std;
	public int nb;
	public ArrayList bucketList=new ArrayList();
	public double error;
	public double errorMax;
	public double confidence;
	public double confidenceMax;
	
	
	public void show()
	{

		System.out.println("\nReport");
		System.out.println("#Rows\t"+this.rows);
		System.out.println("#Distinct\t"+this.distinct);
        System.out.println("Min\t"+this.min);
        System.out.println("Max\t"+this.max);
        System.out.println("Avg\t"+this.avg);
        System.out.println("Std\t"+this.std);
        System.out.println("#Bucks\t"+this.nb);
        System.out.println("Error:\t"+this.error*100+"%");
        System.out.println("Confidence degree:\t"+this.confidence+"%");
	}
	
	public void showModel()
	{
		int i;
		for (i=0;i<this.nb;i++)
		{
			Bucket bucket=(Bucket) this.bucketList.get(i);
			System.out.println((i+1)+")\t"+this.min+"\t"+this.max+"\t"+bucket.freq);
		}

	}

}
