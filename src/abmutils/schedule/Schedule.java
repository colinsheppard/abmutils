package abmutils.schedule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeSet;

// LOGGING
import org.apache.logging.log4j.*;

public class Schedule {
	static final Logger log = LogManager.getLogger(Schedule.class.getName());
	
	private EventComparator comparator = new EventComparator();
	private TreeSet<Event> scheduleTree = new TreeSet<Event>(comparator);
	private Long nextEventID = 0L;
	private Double tick = 0.0;
	private Boolean debug = true;
	
	public void addEvent(Object agent, String methodStr, Double eventTick, Double priority) throws Exception{
		if(eventTick < tick)throw new Exception("Attempted to create an event in the past (tick "+eventTick+") but present moment is tick "+tick);
		Method method = null;
		try {
			method = agent.getClass().getMethod(methodStr, null);
		} catch (SecurityException e) {
			log.error(e);
		} catch (NoSuchMethodException e) {
			log.error(e);
		}
		Event event = new Event(nextEventID++,agent,method,eventTick,priority);
		scheduleTree.add(event);
	}
	public void performScheduledTasks(){
		performScheduledTasks(Double.MAX_VALUE);
	}	
	public void performScheduledTasks(Double untilTick){ 
		Event event = scheduleTree.isEmpty() ? null : scheduleTree.first();
		while(event != null && event.tick <= untilTick){
			if(debug)log.trace("performing event-id: "+event.id+" for agent: "+event.agent+" at tick:"+event.tick+" and priority:"+event.priority);
			
			tick = event.tick;

			try {
				event.method.invoke(event.agent,null);
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e);
			}

			// Remove the current event as is from the schedule
			scheduleTree.remove(event);

			// Reschedule the event if necessary
//			event.reschedule();

			// Grab the next event from the schedule
			event = scheduleTree.isEmpty() ? null : scheduleTree.first();
		
		}
		if(untilTick < Double.MAX_VALUE && untilTick > tick) tick = untilTick;
	}
	public String dump(boolean readable, boolean exporting, boolean reference) {
		StringBuilder buf = new StringBuilder();
		if (exporting) {
			buf.append("LogoSchedule");
			if (!reference) {
				buf.append(":");
			}
		}
		if (!(reference && exporting)) {
			buf.append(" [ ");
			java.util.Iterator iter = scheduleTree.iterator();
			while(iter.hasNext()){
				buf.append(((Event)iter.next()).dump());
				buf.append(" ");
			}
			buf.append("]");
		}
		return buf.toString();
	}
	public void clear() {
		scheduleTree.clear();
	}
	public void resetTicks() {
		tick = 0.0;
	}
	public Double getTick(){
		return tick;
	}
}