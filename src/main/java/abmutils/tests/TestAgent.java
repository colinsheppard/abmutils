package abmutils.tests;

import org.apache.logging.log4j.*;

import abmutils.entities.Entity;

public class TestAgent extends Entity{
	static final Logger log = LogManager.getLogger(TestAgent.class.getName());
	public TestAgent(){
		
	}
	public void printHash(){
		log.info(this.hashCode());
	}
	public void sayHello(){
		log.info("hello");
	}
	@Override
	protected void dieActions() {
	}
}