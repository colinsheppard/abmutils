package abmutils.entities;

import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import abmutils.schedule.*;

public abstract class Entity {
	public static AtomicInteger atomicID = new AtomicInteger();
	public int id;
	private Schedule masterSchedule;
	private TreeSet<Event> mySchedule = new TreeSet<Event>(Schedule.comparator);

	public Entity() {
		id = atomicID.addAndGet(1);
	}
	public final void die(){
		// to be called from the model when an entity dies
		clearMySchedule();
		dieActions();
	}
	protected abstract void dieActions();
	public void setMasterSchedule(Schedule schedule){
		this.masterSchedule = schedule;
	}
	public void addEvent(Event event){
		mySchedule.add(event);
	}
	public void removeNextEvent(){
		Event event = mySchedule.first();
		if(event!=null)mySchedule.remove(event);
		masterSchedule.removeEvent(event);
	}
	public void removeEvent(Event event){
		mySchedule.remove(event);
	}
	public void clearMySchedule(){
		for(Event event : mySchedule){
			masterSchedule.removeEvent(event);
		}
		mySchedule.clear();
	}
}
