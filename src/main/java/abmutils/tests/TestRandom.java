package abmutils.tests;

import org.apache.logging.log4j.*;
import org.junit.Test;

import abmutils.random.Random;

public class TestRandom {
	static final Logger log = LogManager.getLogger(TestRandom.class.getName());

	@Test
	public void test() {
		Random random = new Random();
		random.setSeed(2L);
		for (int i = 0; i < 1000; i++) {
			System.out.print(random.exponential(10.0).nextValue()+",");
		}
		System.out.print("\n");
		for (int i = 0; i < 1000; i++) {
			System.out.print(random.gaussian().nextValue()+",");
		}
		System.out.print("\n");
		for (int i = 0; i < 1000; i++) {
			System.out.print(random.poisson(5.0).nextValue()+",");
		}
		System.out.print("\n");
		for (int i = 0; i < 1000; i++) {
			System.out.print(random.uniform(0.0, 100.0).nextValue()+",");
		}
		System.out.print("\n");
		for (int i = 0; i < 1000; i++) {
			System.out.print(random.randomInt(10, 20).nextValue()+",");
		}
		System.out.print("\n");
	}
}
