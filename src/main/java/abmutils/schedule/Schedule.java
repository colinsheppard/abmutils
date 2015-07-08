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

import abmutils.entities.Entity;

public class Schedule {
	static final Logger log = LogManager.getLogger(Schedule.class.getName());
	
	public static EventComparator comparator = new EventComparator();
	private TreeSet<Event> scheduleTree = new TreeSet<Event>(comparator);
	private long nextEventID = 0L;
	private double tick = 0.0;
	private LocalDate dateAtTickZero = LocalDate.parse("0000-01-01"); // Initialize to unrealistic date
	private long tickAsLong;
	private String tickAsDateString = null;
	private LocalDate tickAsLocalDate = null;
	private boolean tickChanged = true, clearSchedule = false;
	private static DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	public void addEvent(Entity agent, String methodStr, double eventTick, double priority) throws Exception{
		if(eventTick < tick)throw new Exception("Attempted to create an event in the past (tick "+eventTick+") but present moment is tick "+tick);
		Method method = null;
		try {
			Class<? extends Entity> class1 = agent.getClass();
			method = class1.getMethod(methodStr);
		} catch (SecurityException e) {
			log.error(e);
		} catch (NoSuchMethodException e) {
			log.error(e);
		}
		Event event = new Event(nextEventID++,agent,method,eventTick,priority);
		scheduleTree.add(event);
		agent.addEvent(event);
		agent.setMasterSchedule(this);
	}
	public void performScheduledTasks() throws Throwable{
		performScheduledTasks(Double.MAX_VALUE);
	}	
	public void performScheduledTasks(double untilTick) throws Throwable{ 
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
			event.agent.removeEvent(event);

			if(clearSchedule){
				this.clear();
				this.clearSchedule = false;
			}
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
	public void clearAtNextOpportunity() {
		this.clearSchedule = true;
	}
	public void clear() {
		for(Event event : scheduleTree){
			event.agent.clearJustMySchedule();
		}
		scheduleTree.clear();
	}
	public void resetTicks() {
		tick = 0.0;
	}
	public double getTick(){
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
		this.tickAsLocalDate = this.dateAtTickZero.plusDays((new Double(this.tick)).intValue());
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
	public double convertDateToTick(String dateStr){
		return new Double(Days.daysBetween(dateAtTickZero, LocalDate.parse(dateStr)).getDays());
	}
	public void removeEvent(Event event) {
		this.scheduleTree.remove(event);
	}
}