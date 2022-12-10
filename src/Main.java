import ui.cli;
import core.Generator;
import core.Model;
//equi-width unidimensional histogram model

public class Main {

	public static void main(String[] args) 
	{
		
		cli cli=new cli();
		cli.showPrompt();
		
		
		/*
		//csv unidimensional file		
		String fileName="C:\\Users\\ditria\\workspace\\jhisto\\src\\data.txt";
		Generator generator=new Generator(fileName,1,100,1000);
		Model model = new Model();
		model.newModel(fileName);
		//model.changeModel(9);
		//model.changeModel(5);
		
		model.querymin=50;
		model.querymax=80;
		
		model.start();
		
		double res,res2;
		double start_time, end_time,difference;
		start_time= System.currentTimeMillis();
		res=model.exec("count", model.querymin, model.querymax);
		end_time = System.currentTimeMillis();
		difference = (end_time - start_time)/1000;
		System.out.println("Approx\t"+res+" "+difference);
		*/
		
	}
	
	private void main_old()
	{
		//csv unidimensional file		
		String fileName="C:\\Users\\ditria\\workspace\\jhisto\\src\\data.txt";
		Generator generator=new Generator(fileName,1,1000,1000000);
		Model model = new Model();
		model.newModel(fileName);
		//model.changeModel(9);
		//model.changeModel(5);
		
		model.querymin=50;
		model.querymax=80;
		
		model.start();
		
		double res,res2;
		double start_time, end_time,difference;
		start_time= System.currentTimeMillis();
		res=model.exec("count", model.querymin, model.querymax);
		end_time = System.currentTimeMillis();
		difference = (end_time - start_time)/1000;
		System.out.println("Approx\t"+res+" "+difference);
		
		
		//real		
		
	}

}
