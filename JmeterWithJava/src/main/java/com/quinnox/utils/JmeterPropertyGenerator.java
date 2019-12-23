package com.quinnox.utils;

import java.io.FileOutputStream;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

public class JmeterPropertyGenerator {
	public static void main(String[] args) throws Exception {
		StandardJMeterEngine jmeter = new StandardJMeterEngine();

		// JMeter initialization (properties, log levels, locale, etc)
		JMeterUtils.setJMeterHome("D:\\Downloads\\apache-jmeter-5.2\\apache-jmeter-5.2");
		JMeterUtils.loadJMeterProperties("C:\\Users\\mohitk\\Downloads\\apache-jmeter-5.2.1_src\\apache-jmeter-5.2.1\\bin\\jmeter.properties");
		JMeterUtils.initLocale();

		// JMeter Test Plan, basically JOrphan HashTree
		HashTree testPlanTree = new HashTree();

		// First HTTP Sampler - open example.com
		HTTPSamplerProxy examplecomSampler = new HTTPSamplerProxy();
		examplecomSampler.setDomain("localhost");
		examplecomSampler.setPort(2222);
		examplecomSampler.setPath("/rest/student/service/getAllStudents");
		examplecomSampler.setMethod("GET");
		examplecomSampler.setName("open student api");
		examplecomSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
		examplecomSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

		// Loop Controller
		LoopController loopController = new LoopController();
		loopController.setLoops(1);
		loopController.setFirst(true);
		loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
		loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
		loopController.initialize();

		// Thread Group
		org.apache.jmeter.threads.ThreadGroup threadGroup = new org.apache.jmeter.threads.ThreadGroup();
		threadGroup.setName("Example Thread Group");
		threadGroup.setNumThreads(1);
		threadGroup.setRampUp(1);
		threadGroup.setSamplerController(loopController);
		threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
		threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

		// Test Plan
		TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
		testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
		testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
		testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

		// Construct Test Plan from previously initialized elements
		testPlanTree.add(testPlan);
		HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
		threadGroupHashTree.add(examplecomSampler);

		// save generated test plan to JMeter's .jmx file format
		SaveService.saveTree(testPlanTree, new FileOutputStream("D:\\test.jmx"));

		// add Summarizer output to get test progress in stdout like:
		// summary = 2 in 1.3s = 1.5/s Avg: 631 Min: 290 Max: 973 Err: 0 (0.00%)
		Summariser summer = null;
		String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
		if (summariserName.length() > 0) {
			summer = new Summariser(summariserName);
		}

		// Store execution results into a .jtl file
		String logFile = "D:\\results2.jtl";
		ResultCollector logger = new ResultCollector(summer);
		logger.setFilename(logFile);
		testPlanTree.add(testPlanTree.getArray()[0], logger);

		// Run Test Plan
		jmeter.configure(testPlanTree);
		jmeter.run();

		System.out.println("Test plan created successfully...");
	}
}
