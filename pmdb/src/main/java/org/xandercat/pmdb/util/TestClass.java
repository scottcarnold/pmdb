package org.xandercat.pmdb.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestClass {

	private static final Logger LOGGER = LogManager.getLogger(TestClass.class);
	
	public static void main(String[] args) {
		System.out.println("Test 1");
		LOGGER.warn("Test 2");
		System.out.println("Test 3");
		System.exit(0);
	}
	
	public TestClass() {
		System.out.println("test");
	}
}
