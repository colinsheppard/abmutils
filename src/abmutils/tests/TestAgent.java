package abmutils.tests;

import org.apache.logging.log4j.*;

public class TestAgent {
	static final Logger log = LogManager.getLogger(TestAgent.class.getName());
	public TestAgent(){
		
	}
	public void printHash(){
		log.info(this.hashCode());
	}
	public void sayHello(){
		log.info("hello");
	}
}