package com.ej.jmeter_from_java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class JmeterJava {
	
	//Add JMETER_HOME to .zsrc/.bash_profile and subsititute System.getEnv("JMETER_HOME")
	private static String jmeterHome = "/Users/ezjohnson/Downloads/apache-jmeter-5.4.1";
	
	private static List<String> jmxFiles = new ArrayList<String>();
	public static void main(String[] args) throws IOException {
		
		startTest("/Users/ezjohnson/Downloads/BeanShell.jmx", "/Users/ezjohnson/Downloads/BeanShell1.jtl");
		startTest("/Users/ezjohnson/Downloads/BeanShell.jmx", "/Users/ezjohnson/Downloads/BeanShell2.jtl");
		
//You can run in a loop, with all available jmx store in the list.
//		for(String currentJmxFile : jmxFiles) {
//			startTest("/Users/ezjohnson/Downloads/"+ currentJmxFile, "/Users/ezjohnson/Downloads/BeanShell"+System.currentTimeMillis()+".jtl");
//		}
	}
	
	public static void startTest(String fileNamePath, String jtlFilePath) throws IOException {
		
		// STEP-1: JMeter Engine
		StandardJMeterEngine jmeter = new StandardJMeterEngine();
		
		// STEP-2: Initialize Properties, logging, locale, etc.
		JMeterUtils.loadJMeterProperties("/Users/ezjohnson/Downloads/apache-jmeter-5.4.1/bin/jmeter.properties");
		JMeterUtils.setJMeterHome(jmeterHome);
		JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
		JMeterUtils.initLocale();	
		
		SaveService.loadProperties();
		
		// STEP-3: Load existing .jmx Test Plan
		HashTree testPlanTree = SaveService.loadTree(new File(fileNamePath));
		
		// STEP-4: Remove disabled test elements
		JMeter.convertSubTree(testPlanTree);
		
		// STEP-5: Add summariser
		Summariser summer = null;
		String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		if (summariserName.length() > 0) {
			summer = new Summariser(summariserName);
		}
		
		// STEP-6: Store execution results into a .jtl file
		String logFile = jtlFilePath;
		ResultCollector logger = new ResultCollector(summer);
		logger.setFilename(logFile);
		testPlanTree.add(testPlanTree.getArray()[0], logger);
		
		// STEP-7: Run JMeter Test
		jmeter.configure(testPlanTree);
		jmeter.run();
	}
}