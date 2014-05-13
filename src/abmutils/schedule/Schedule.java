package abmutils.schedule;

import java.lang.reflect.Method;
import java.util.TreeSet;

public class Schedule {
	EventComparator comparator = new EventComparator();
	TreeSet<Event> scheduleTree = new TreeSet<Event>(comparator);
	Long nextEventID = 0L;
	
	public void addEvent(Object agent, Method method, Double tick, Double priority){
		Event event = new Event(nextEventID++,agent,method,tick,priority);
		scheduleTree.add(event);
		
		Double eventTick = null;
		switch(addType){
		case DEFAULT:
			primName = "add";
			if(args.length<3)throw new ExtensionException("time:add must have 3 arguments: schedule agent task tick/time");
			break;
		case SHUFFLE:
			primName = "add-shuffled";
			if(args.length<3)throw new ExtensionException("time:add-shuffled must have 3 arguments: schedule agent task tick/time");
			break;
		case REPEAT:
			primName = "repeat";
			if(args.length<4)throw new ExtensionException("time:repeat must have 4 or 5 arguments: schedule agent task tick/time number (period-type)");
			break;
		case REPEAT_SHUFFLED:
			primName = "repeat-shuffled";
			if(args.length<4)throw new ExtensionException("time:repeat-shuffled must have 4 or 5 arguments: schedule agent task tick/time number (period-type)");
			break;
		}
		if (!(args[0].get() instanceof Agent) && !(args[0].get() instanceof AgentSet) && !((args[0].get() instanceof String) && args[0].get().toString().toLowerCase().equals("observer"))) 
			throw new ExtensionException("time:"+primName+" expecting an agent, agentset, or the string \"observer\" as the first argument");
		if (!(args[1].get() instanceof CommandTask)) throw new ExtensionException("time:"+primName+" expecting a command task as the second argument");
		if(args[2].get().getClass().equals(Double.class)){
			eventTick = args[2].getDoubleValue();
		}else if(args[2].get().getClass().equals(LogoTime.class)){
			if(!this.isAnchored())throw new ExtensionException("A LogoEvent can only be scheduled to occur at a LogoTime if the discrete event schedule has been anchored to a LogoTime, see time:anchor-schedule");
			eventTick = this.timeToTick(getTimeFromArgument(args, 2));
		}else{
			throw new ExtensionException("time:"+primName+" expecting a number or logotime as the third argument");
		}
		if (eventTick < ((ExtensionContext)context).workspace().world().ticks()) throw new ExtensionException("Attempted to schedule an event for tick "+ eventTick +" which is before the present 'moment' of "+((ExtensionContext)context).workspace().world().ticks());
		Double repeatInterval = null;
		if(addType == AddType.REPEAT || addType == AddType.REPEAT_SHUFFLED){
			if (!args[3].get().getClass().equals(Double.class)) throw new ExtensionException("time:repeat expecting a number as the fourth argument");
			repeatInterval = args[3].getDoubleValue();
			if (repeatInterval <= 0) throw new ExtensionException("time:repeat the repeat interval must be a positive number");
			if(args.length == 5){
				if(!this.isAnchored())throw new ExtensionException("A LogoEvent can only be scheduled to repeat using a period type if the discrete event schedule has been anchored to a LogoTime, see time:anchor-schedule");
				TimeExtension.PeriodType pType = stringToPeriodType(getStringFromArgument(args, 4));
				repeatInterval = this.timeAnchor.getDifferenceBetween(this.tickType, this.timeAnchor.plus(pType, repeatInterval))/this.tickValue;
				printToConsole(context, "from:"+pType+" to:"+this.tickType+" inteval:"+repeatInterval);
			}
		}
		Boolean shuffleAgentSet = (addType == AddType.SHUFFLE || addType == AddType.REPEAT_SHUFFLED);

		org.nlogo.agent.AgentSet agentSet = null;
		if (args[0].get() instanceof org.nlogo.agent.Agent){
			org.nlogo.agent.Agent theAgent = (org.nlogo.agent.Agent)args[0].getAgent();
			agentSet = new ArrayAgentSet(theAgent.getAgentClass(),1,false,(World) theAgent.world());
			agentSet.add(theAgent);
		}else if(args[0].get() instanceof AgentSet){
			agentSet = (org.nlogo.agent.AgentSet) args[0].getAgentSet();
		}else{
			// leave agentSet as null to signal observer should be used
		}
		LogoEvent event = (new TimeExtension()).new LogoEvent(agentSet,args[1].getCommandTask(),eventTick,repeatInterval,shuffleAgentSet);
		if(debug)printToConsole(context,"scheduling event: "+event.dump(false, false, false));
		scheduleTree.add(event);
	}
	public void performScheduledTasks(Argument args[], Context context) throws ExtensionException, LogoException {
		performScheduledTasks(args,context,Double.MAX_VALUE);
	}	
	public void performScheduledTasks(Argument args[], Context context, Double untilTick) throws ExtensionException, LogoException {
		ExtensionContext extcontext = (ExtensionContext) context;
		TickCounter tickCounter = extcontext.workspace().world().tickCounter;
		Object[] emptyArgs = new Object[0]; // This extension is only for CommandTasks, so we know there aren't any args to pass in
		LogoEvent event = scheduleTree.isEmpty() ? null : scheduleTree.first();
		ArrayList<org.nlogo.agent.Agent> theAgents = new ArrayList<org.nlogo.agent.Agent>();
		while(event != null && event.tick <= untilTick){
			if(debug)printToConsole(context,"performing event-id: "+event.id+" for agent: "+event.agents+" at tick:"+event.tick);
			tickCounter.tick(event.tick-tickCounter.ticks());

			if(event.agents == null){
				org.nlogo.nvm.Context nvmContext = new org.nlogo.nvm.Context(extcontext.nvmContext().job,
																				(org.nlogo.agent.Agent)extcontext.getAgent().world().observer(),
																				extcontext.nvmContext().ip,
																				extcontext.nvmContext().activation);
				event.task.perform(nvmContext, emptyArgs);
			}else if(event.shuffleAgentSet){
				Iterator iter = event.agents.shufflerator(extcontext.nvmContext().job.random);
				while(iter.hasNext()){
					org.nlogo.nvm.Context nvmContext = new org.nlogo.nvm.Context(extcontext.nvmContext().job,iter.next(),extcontext.nvmContext().ip,extcontext.nvmContext().activation);
//					if(extcontext.nvmContext().stopping)return;
					event.task.perform(nvmContext, emptyArgs);
//					if(nvmContext.stopping)return;
				}
			}else{
				org.nlogo.agent.Agent[] source = null;
				org.nlogo.agent.Agent[] copy = null;
				if(event.agents instanceof ArrayAgentSet){
					source = event.agents.toArray();
					copy = new org.nlogo.agent.Agent[event.agents.count()];
					System.arraycopy(source, 0, copy, 0, source.length);
				}else if(event.agents instanceof TreeAgentSet){
					copy = event.agents.toArray();
				}
				for(org.nlogo.agent.Agent theAgent : copy){
					if(theAgent == null || theAgent.id == -1)continue;
					org.nlogo.nvm.Context nvmContext = new org.nlogo.nvm.Context(extcontext.nvmContext().job,theAgent,extcontext.nvmContext().ip,extcontext.nvmContext().activation);
//					if(extcontext.nvmContext().stopping)return;
					event.task.perform(nvmContext, emptyArgs);
//					if(nvmContext.stopping)return;
				}
			}

			// Remove the current event as is from the schedule
			scheduleTree.remove(event);

			// Reschedule the event if necessary
			event.reschedule();

			// Grab the next event from the schedule
			event = scheduleTree.isEmpty() ? null : scheduleTree.first();
		}
		if(untilTick < Double.MAX_VALUE && untilTick > tickCounter.ticks()) tickCounter.tick(untilTick-tickCounter.ticks());
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
				buf.append(((Event)iter.next()).dump(true, true, true));
				buf.append(" ");
			}
			buf.append("]");
		}
		return buf.toString();
	}
	public String getExtensionName() {
		return "time";
	}
	public String getNLTypeName() {
		return "schedule";
	}
	public boolean recursivelyEqual(Object arg0) {
		return equals(arg0);
	}
	public void clear() {
		scheduleTree.clear();
	}
}