package abmutils.experiment;

import java.util.LinkedHashMap;

import abmutils.experiment.ParameterValue;
import abmutils.schedule.Schedule;

public abstract class Model implements Runnable {
	public LinkedHashMap<String, ParameterValue> parameters;
	public ExperimentalGroup experimentalGroup;
	public Schedule schedule = new Schedule();

	public Model() {
	}
	public void setParameters(LinkedHashMap<String, ParameterValue> params){
		this.parameters = params;
	}
	public void setExperimentalGroup(ExperimentalGroup experimentalGroup) {
		this.experimentalGroup = experimentalGroup;
	}
	public abstract void initialize();
	@Override
	public abstract void run();
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public Schedule getSchedule() {
		return this.schedule;
	}
	public Double getTick(){
		return this.schedule.getTick();
	}
}
