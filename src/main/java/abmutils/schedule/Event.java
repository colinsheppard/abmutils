package abmutils.schedule;

import java.lang.reflect.Method;

import abmutils.entities.Entity;

public class Event {

	long id;
	double tick;
	double priority;
	Entity agent;
	Method method;
	
	public Event(long id, Entity agent, Method method,double tick, double priority) {
		this.id = id;
		this.tick = tick;
		this.priority = priority;
		this.agent = agent;
		this.method = method;
	}
	public String toString(){
//		return this.tick + ": " + this.agent + " --> " + this.method.getName();
		return this.tick + "(" + this.priority + ") " + this.method.getName();
	}
	public double getEventTick(){
		return this.tick;
	}
	public String getMethodName(){
		return this.method.getName();
	}
	public String dump(){
		return this.toString();
	}
	
}
