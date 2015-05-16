package abmutils.schedule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TreeSet;








// LOGGING
import org.apache.logging.log4j.*;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Schedule {
	static final Logger log = LogManager.getLogger(Schedule.class.getName());
	
	private static EventComparator comparator = new EventComparator();
	private TreeSet<Event> scheduleTree = new TreeSet<Event>(comparator);
	private Long nextEventID = 0L;
	private Double tick = 0.0;
	private LocalDate dateAtTickZero = LocalDate.parse("0000-01-01"); // Initialize to unrealistic date
	private Long tickAsLong = null;
	private String tickAsDateString = null;
	private LocalDate tickAsLocalDate = null;
	private Boolean tickChanged = true;
	private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	public void addEvent(Object agent, String methodStr, Double eventTick, Double priority) throws Exception{
		if(eventTick < tick)throw new Exception("Attempted to create an event in the past (tick "+eventTick+") but present moment is tick "+tick);
		Method method = null;
		try {
			Class<? extends Object> class1 = agent.getClass();
			method = class1.getMethod(methodStr);
		} catch (SecurityException e) {
			log.error(e);
		} catch (NoSuchMethodException e) {
			log.error(e);
		}
		Event event = new Event(nextEventID++,agent,method,eventTick,priority);
		scheduleTree.add(event);
	}
	public void performScheduledTasks() throws Throwable{
		performScheduledTasks(Double.MAX_VALUE);
	}	
	public void performScheduledTasks(Double untilTick) throws Throwable{ 
		Event event = scheduleTree.isEmpty() ? null : scheduleTree.first();
		while(event != null && event.tick <= untilTick){
			if(log.isTraceEnabled())log.trace("performing event-id: "+event.id+" for agent: "+event.agent+" at tick:"+event.tick+" and priority:"+event.priority);
			
			tick = event.tick;

			try {
				event.method.invoke(event.agent,(Object[])null);
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			} catch (InvocationTargetException e) {
				log.error(e.getTargetException());
			} catch (RuntimeException e) {
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
			java.util.Iterator<Event> iter = scheduleTree.iterator();
			while(iter.hasNext()){
				buf.append((iter.next()).dump());
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
	public String getTickAsDateString(){
		if(tickChanged){
			this.tickChanged = false;
			updateTickFormats();
		}
		return this.tickAsDateString;
	}
	public LocalDate getTickAsDate(){
		if(tickChanged){
			this.tickChanged = false;
			updateTickFormats();
		}
		return this.tickAsLocalDate;
	}
	public Long getTickAsLong(){
		if(tickChanged){
			this.tickChanged = false;
			updateTickFormats();
		}
		return this.tickAsLong;
	}
	public void updateTickFormats(){
		this.tickAsLocalDate = this.dateAtTickZero.plusDays(this.tick.intValue());
		this.tickAsLong = this.tickAsLocalDate.toDate().getTime();
		this.tickAsDateString = this.tickAsLocalDate.toString(dateFormatter);
	}
	public void setTickChangedToTrue(){
		this.tickChanged = true;
	}
	public void anchorToTick(LocalDate dateAtTickZero){
		this.dateAtTickZero = dateAtTickZero;
	}
	public static DateTimeFormatter getDateFormatter(){
		return Schedule.dateFormatter;
	}
	public Double convertDateToTick(String dateStr){
		return new Double(Days.daysBetween(dateAtTickZero, LocalDate.parse(dateStr)).getDays());
	}
}