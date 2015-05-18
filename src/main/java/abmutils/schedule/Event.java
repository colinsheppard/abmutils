package abmutils.schedule;

import java.lang.reflect.Method;

import abmutils.entities.Entity;

public class Event {

	Long id;
	Double tick;
	Double priority;
	Entity agent;
	Method method;
	
	public Event(Long id, Entity agent, Method method,Double tick, Double priority) {
		this.id = id;
		this.tick = tick;
		this.priority = priority;
		this.agent = agent;
		this.method = method;
	}
	
	public String dump(){
		return this.toString();
	}
	
}
