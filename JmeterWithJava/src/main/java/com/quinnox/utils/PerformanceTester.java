package com.quinnox.utils;

import java.io.File;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class PerformanceTester {

	/*
	 * static { PlatformLogger logger =
	 * PlatformLogger.getLogger("java.util.prefs");
	 * logger.setLevel(Level.SEVERE); }
	 */

	public static void main(String[] args) throws Exception {
		// Engine
		StandardJMeterEngine jm = new StandardJMeterEngine();
		// jmeter.properties
		JMeterUtils.loadJMeterProperties(
				"C:\\Users\\mohitk\\Downloads\\apache-jmeter-5.2.1_src\\apache-jmeter-5.2.1\\bin\\jmeter.properties");
		JMeterUtils.setJMeterHome("D:\\Downloads\\apache-jmeter-5.2\\apache-jmeter-5.2");
		JMeterUtils.initLocale();
		HashTree hashTree = new HashTree();

		SaveService.loadProperties();

		HashTree testPlanTree = SaveService.loadTree(new File("D:\\test.jmx"));

		Summariser summer = null;
		String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		if (summariserName.length() > 0) {
			summer = new Summariser(summariserName);
		}

		// Store execution results into a .jtl file
		String logFile = "D:\\newResult.jtl";
		ResultCollector logger = new ResultCollector(summer);
		logger.setFilename(logFile);
		testPlanTree.add(testPlanTree.getArray()[0], logger);

		// Run JMeter Test
		jm.configure(testPlanTree);
		jm.run();
		System.out.println();

	}

	/*
	 * // HTTP Sampler HTTPSampler httpSampler = new HTTPSampler();
	 * httpSampler.setDomain("www.google.com"); // httpSampler.setPort(80);
	 * //httpSampler.setPath("/"); httpSampler.setMethod("GET");
	 * 
	 * // Loop Controller TestElement loopCtrl = new LoopController();
	 * ((LoopController) loopCtrl).setLoops(1); ((LoopController)
	 * loopCtrl).addTestElement(httpSampler); ((LoopController)
	 * loopCtrl).setFirst(true);
	 * 
	 * // Thread Group SetupThreadGroup threadGroup = new SetupThreadGroup();
	 * threadGroup.setNumThreads(1); threadGroup.setRampUp(1);
	 * threadGroup.setSamplerController((LoopController) loopCtrl);
	 * 
	 * // Test plan TestPlan testPlan = new TestPlan("MY TEST PLAN");
	 * 
	 * 
	 * hashTree.add("testPlan", testPlan); hashTree.add("loopCtrl", loopCtrl);
	 * hashTree.add("threadGroup", threadGroup); hashTree.add("httpSampler",
	 * httpSampler);
	 * 
	 * jm.configure(hashTree);
	 * 
	 * jm.run();
	 */
}
