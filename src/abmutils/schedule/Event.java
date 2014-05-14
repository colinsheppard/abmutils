package abmutils.schedule;

import java.lang.reflect.Method;

class Event {

	Long id;
	Double tick;
	Double priority;
	Object agent;
	Method method;
	
	public Event(Long id, Object agent, Method method,Double tick, Double priority) {
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
