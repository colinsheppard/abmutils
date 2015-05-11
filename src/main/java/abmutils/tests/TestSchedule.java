package abmutils.tests;

import abmutils.schedule.*;

import org.apache.logging.log4j.*;
import org.junit.Test;

public class TestSchedule {
	static final Logger log = LogManager.getLogger(TestSchedule.class.getName());

	@Test
	public void test() {
		TestAgent agent1 = new TestAgent();
		TestAgent agent2 = new TestAgent();
		Schedule schedule = new Schedule();
		log.info("Testing Schedule Class");
		
		try {
			schedule.addEvent(agent1,"printHash", 10.0, 1.0);
			schedule.addEvent(agent1,"printHash", 1.0, 2.0);
			schedule.addEvent(agent1,"printHash", 1.0, 1.0);
			schedule.addEvent(agent2,"printHash", 5.0, 1.0);
			schedule.performScheduledTasks();
		} catch (Exception e) {
			log.error(e);
		} catch (Throwable e) {
			log.error(e);
		}
		try {
			schedule.addEvent(agent1,"printHash", 1.0, 2.0);
		} catch (Exception e) {
			log.error(e);
		}
		schedule.clear();
		schedule.resetTicks();
		try {
			schedule.addEvent(agent1,"sayHello", 1.0, 2.0);
			schedule.addEvent(agent1,"sayHello", 10.0, 2.0);
			schedule.addEvent(agent1,"sayHello", 100.0, 2.0);
			schedule.performScheduledTasks(50.0);
		} catch (Exception e) {
			log.error(e);
		} catch (Throwable e) {
			log.error(e);
		}
		log.info(schedule.getTick());
	}
}
