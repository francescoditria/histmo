package ui;

import core.Block;
import core.Bucket;

public class Report {

	public void show(Block block)
	{
		int i,j;

		System.out.println("\nReport");
		System.out.println("#Rows\t"+block.rows);
		System.out.println("#Distinct\t"+block.distinct);
        System.out.println("Min\t"+block.min);
        System.out.println("Max\t"+block.max);
        System.out.println("Avg\t"+block.avg);
        System.out.println("Std\t"+block.std);
        System.out.println("#Bucks\t"+block.nb);
        System.out.println("Error:\t"+block.error*100+"%");
        System.out.println("Confidence degree:\t"+block.confidence+"%");
	}
	
	public void showBuckets(Block block)
	{
		int i;
		for (i=0;i<block.nb;i++)
		{
			Bucket bucket=(Bucket) block.bucketList.get(i);
			System.out.println((i+1)+")\t"+bucket.min+"\t"+bucket.max+"\t"+bucket.freq);
		}

	}

}
