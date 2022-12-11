package ui;

import java.util.Scanner;

public class cli {

	
	public void showPrompt()
	{
		this.showIntro();
		this.showHelp();
		Scanner scan = new Scanner(System.in);
		Parser parser=new Parser();
		boolean f=true;
		while(f)
		{
		System.out.println("ready>");		
		String command = scan.nextLine();
		f=parser.parse(command);
		}
		this.exit();
		
	}
	
	private void showHelp()
	{
		System.out.println("\nCommands:");
		System.out.println("- generate <min>,<max>,<size>");
		System.out.println("\tgenerates a file with random <size> data in range [min,max] (testing scope only)");
		System.out.println("- model <filename>");
		System.out.println("\tbuilds a histogram model using file <filename>");
		System.out.println("- change <#buckets>");
		System.out.println("\tmanually changes the number of buckets in the generated histogram model");
		System.out.println("- query <min>,<max>");
		System.out.println("\tapproximate query processing in range [min,max]\n");

		
		
		
	}
	
	public void exit()
	{
		System.out.println("\nBye.");
	}

	
	public void showIntro()
	{
		System.out.println("Starting histmo");
		System.out.println("Type <bye> to quit");
		
	}
	
}
