package ui;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import core.Generator;
import core.Model;

public class Parser {


	Model model;
	
	public boolean parse(String command)
	{

		command=command.toLowerCase();
		String path=System.getProperty("user.dir")+File.separator;
		String fileName=path+"data.csv";
		
		Pattern pattern;
		Matcher matcher;
		String operation= new String();
		
		operation="bye";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			return false;
		}
		
		
		operation="generate (.+),(.+),(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			String min=matcher.group(1);
			String max=matcher.group(2);
			String size=matcher.group(3);
			Generator generator=new Generator(fileName,Integer.parseInt(min),Integer.parseInt(max),Integer.parseInt(size));
			System.out.println("Testing file "+fileName);
			return true;
		}
		

		operation="model (.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			fileName=matcher.group(1);
			model = new Model(fileName);
			return true;
		}

		
		operation="change (.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			String newsize=matcher.group(1);
			int nb=Integer.parseInt(newsize);
			model.change(nb);
			return true;
		}
		
		operation="show stats";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			model.showData();
			return true;
		}

		operation="show model";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			model.showModel();
			return true;
		}

		operation="count (.+),(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			String min=matcher.group(1);
			String max=matcher.group(2);
			model.process("count", Float.parseFloat(min), Float.parseFloat(max));
			return true;
		}

		operation="sum (.+),(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			String min=matcher.group(1);
			String max=matcher.group(2);
			model.process("sum", Float.parseFloat(min), Float.parseFloat(max));
			return true;
		}
		
		operation="avg (.+),(.+)";
		pattern=Pattern.compile(operation);
		matcher=pattern.matcher(command);
		while(matcher.find()){
			String min=matcher.group(1);
			String max=matcher.group(2);
			model.process("avg", Float.parseFloat(min), Float.parseFloat(max));
			return true;
		}

		return true;
	}
	
	
}
